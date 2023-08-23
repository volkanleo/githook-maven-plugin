package com.leovegas;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;

/**
 * Maven Mojo for installing Git hooks in a project repository.
 */
@Mojo(name = "install", defaultPhase = LifecyclePhase.INITIALIZE)
public final class GitHookInstallMojo extends AbstractMojo {

    private static final String SHEBANG = "#!/bin/sh";
    private static final Path HOOK_DIR_PATH = Paths.get(".git/hooks");

    @Parameter(name = "hooks")
    private Map<String, String> hooks;

    @Parameter(name = "resourceHooks")
    private Map<String, String> resourceHooks;

    /**
     * Executes the Git hook installation process.
     *
     * @throws MojoExecutionException If an error occurs during execution.
     * @throws MojoFailureException   If the execution fails due to incorrect input or configuration.
     */
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        if (!Files.exists(HOOK_DIR_PATH)) {
            throw new MojoExecutionException("Not a git repository");
        }
        installDefaultHooks();
        installResourceGitHook();
    }

    /**
     * Generates default hooks specified in the Maven configuration.
     *
     * @throws MojoExecutionException If an error occurs during hook generation.
     * @throws MojoFailureException   If an invalid hook file name is encountered.
     */
    private void installDefaultHooks() throws MojoExecutionException, MojoFailureException {
        if (hooks == null) {
            return;
        }
        for (Map.Entry<String, String> hook : hooks.entrySet()) {
            String hookName = hook.getKey();
            getLog().info("Generating " + hookName + " from Maven conf");
            if (GitHookType.isValidHookName(hookName)) {
                generateDefaultHookFile(hookName, SHEBANG + '\n' + getDefaultHookScript());
            } else {
                throw new MojoFailureException("'" + hookName + "' is not a valid hook file name.");
            }
        }
    }

    /**
     * Generates a Git hook file with the specified content.
     *
     * @param hookName        The name of the hook file.
     * @param asStringScript  The content of the hook file.
     * @throws MojoExecutionException If an error occurs during hook file generation.
     */
    private void generateDefaultHookFile(String hookName, String asStringScript) throws MojoExecutionException {
        try {
            Path path = HOOK_DIR_PATH.resolve(hookName);
            Files.write(
                    path,
                    asStringScript.getBytes(),
                    CREATE, TRUNCATE_EXISTING
            );
            setCustomFilePermissions(path);
        } catch (IOException e) {
            throw new MojoExecutionException("could not write hook with name : " + hookName, e);
        }
    }

    /**
     * Generates hooks from resources specified in the Maven configuration.
     *
     * @throws MojoExecutionException If an error occurs during hook generation.
     * @throws MojoFailureException   If an invalid hook file name is encountered.
     */
    private void installResourceGitHook() throws MojoFailureException {
        if (resourceHooks == null) {
            return;
        }
        for (final Map.Entry<String, String> hook : resourceHooks.entrySet()) {
            final String hookName = hook.getKey();
            if (GitHookType.isValidHookName(hookName)) {
                installGitHook(hookName, hook.getValue());
            } else {
                throw new MojoFailureException("'" + hookName + "' is not a valid hook file name.");
            }
        }
    }

    /**
     * Take the file in the provided location and install it as a Git hook of the provided type.
     *
     * @param hookName the type of hook to install.
     * @param filePath the location of the file to install as a hook.
     */
    private void installGitHook(final String hookName, final String filePath) {
        Path gitHookPath = HOOK_DIR_PATH.resolve(hookName);
        getLog().info("Generating " + hookName + " from " + gitHookPath.toString());
        if (Objects.nonNull(filePath) && Paths.get(filePath).toFile().isFile()) {
            copyFromFile(filePath, gitHookPath);
        }
    }

    /**
     * Copies the specified file from the file system into the default hooks directory.
     *
     * @param filePath path to the file to use as the hook.
     * @param gitHookPath the location to move the file to.
     */
    private void copyFromFile(final String filePath, final Path gitHookPath) {
        try {
            Files.copy(Paths.get(filePath), gitHookPath, StandardCopyOption.REPLACE_EXISTING);
            setCustomFilePermissions(gitHookPath);
        } catch (final IOException e) {
            getLog().warn("Could not move file into .git/hooks directory", e);
        }
    }

    /**
     * Sets custom POSIX file permissions on the specified file path.
     *
     * @param path The path of the file for which permissions need to be set.
     * @throws IOException If an I/O error occurs while setting the permissions.
     */
    private static void setCustomFilePermissions(Path path) throws IOException {
        Files.setPosixFilePermissions(
                path,
                new HashSet<>(Arrays.asList(
                        PosixFilePermission.OWNER_EXECUTE,
                        PosixFilePermission.OWNER_READ,
                        PosixFilePermission.OWNER_WRITE
                ))
        );
    }

    /**
     * Returns the default script for Git hooks.
     *
     * @return The default hook script.
     */
    private String getDefaultHookScript() {
        return "# Change directory to the project's root\n" +
                "cd \"$(git rev-parse --show-toplevel)\"\n\n" +
                "# Check for updated dependencies and microservice versions\n" +
                "dependency_updates=$(mvn versions:display-property-updates versions:display-parent-updates -DgenerateBackupPoms=false \\\n" +
                "    | grep '\\->' \\\n" +
                "    | awk -F ' ' '{if ($2 != $4) print $0}')\n\n" +
                "# Check if dependency updates are available\n" +
                "if [[ -n \"$dependency_updates\" ]]; then\n" +
                "    echo \"WARNING: The following dependencies or microservices have updates available:\"\n" +
                "    echo \"$dependency_updates\"\n" +
                "fi";
    }
}
