package com.leovegas;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.IOException;
import java.nio.file.*;
import java.util.Map;

import static java.nio.file.StandardOpenOption.*;

import java.nio.file.attribute.PosixFilePermission;
import java.util.Arrays;
import java.util.HashSet;
import java.util.stream.Collectors;

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
     * @throws MojoExecutionException If an execution error occurs.
     * @throws MojoFailureException If an execution error occurs.
     */
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        if (!Files.exists(HOOK_DIR_PATH)) {
            throw new MojoExecutionException("Not a git repository");
        }
        generateDefaultHooks();
        generateResourceHooks();
    }



    /**
     * Generates default Git hooks using predefined scripts.
     *
     * @throws MojoExecutionException If an execution error occurs.
     */
    private void generateDefaultHooks() throws MojoExecutionException, MojoFailureException {
        if (hooks == null) {
            return;
        }
        for (Map.Entry<String, String> hook : hooks.entrySet()) {
            String hookName = hook.getKey();
            getLog().info("Generating " + hookName + " from Maven conf");
            if(GitHookType.isValidHookName(hookName)){
            generateHookFile(hookName, SHEBANG + '\n' + getDefaultScript());
            }
            else {
                throw new MojoFailureException("'" + hookName + "' is not a valid hook file name.");
            }
        }
    }

    /**
     * Generates resource-based Git hooks by reading external script files.
     *
     * @throws MojoExecutionException If an execution error occurs.
     */
    private void generateResourceHooks() throws MojoExecutionException, MojoFailureException {
        if (resourceHooks == null) {
            return;
        }
        for (Map.Entry<String, String> hook : resourceHooks.entrySet()) {
            String hookName = hook.getKey();

            if (GitHookType.isValidHookName(hookName)) {

                Path hookFilePath = Paths.get(hook.getValue());
                Path local = Paths.get("");

                if (!hookFilePath.toAbsolutePath().startsWith(local.toAbsolutePath())) {
                    throw new MojoExecutionException("only file inside the project can be used to generate git hooks");
                }
                try {
                    getLog().info("Generating " + hookName + " from " + hookFilePath.toString());
                    generateHookFile(hookName, Files.lines(hookFilePath).collect(Collectors.joining("\n")));
                } catch (IOException e) {
                    throw new MojoExecutionException("could not access hook resource : " + hookFilePath, e);
                }
            }
            else {
                throw new MojoFailureException("'" + hookName + "' is not a valid hook file name.");
            }
        }
    }

    /**
     * Generates a hook file with the provided name and content.
     *
     * @param hookName        Name of the hook.
     * @param asStringScript  Script content as a string.
     * @throws MojoExecutionException If an execution error occurs.
     */
    private void generateHookFile(String hookName, String asStringScript) throws MojoExecutionException {
        try {
            Path path = HOOK_DIR_PATH.resolve(hookName);
            Files.write(
                    path,
                    asStringScript.getBytes(),
                    CREATE, TRUNCATE_EXISTING
            );
            Files.setPosixFilePermissions(
                    path,
                    new HashSet<>(Arrays.asList(
                            PosixFilePermission.OWNER_EXECUTE,
                            PosixFilePermission.OWNER_READ,
                            PosixFilePermission.OWNER_WRITE
                    ))
            );
        } catch (IOException e) {
            throw new MojoExecutionException("could not write hook with name : " + hookName, e);
        }
    }

    /**
     * Returns the default inline script for checking dependency updates.
     *
     * @return The default inline script.
     */
    private String getDefaultScript() {
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
