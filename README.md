# Git Hook Installer Maven Plugin

The Git Hook Installer Maven Plugin is a Maven plugin that allows you to easily install Git hooks in a project repository. Git hooks are scripts that Git executes before or after certain Git events, such as committing or pushing code.

## Why Git Hooks?
Git hooks provide a way to automate tasks and checks in your repository. With hooks, you can ensure consistent code quality, enforce coding standards, run tests before committing, and perform various other actions automatically. By integrating Git hooks into your project, you streamline your development process and maintain higher code standards.
#### If you want more details, please read this article 
- [Understanding Git Hooks](https://codeburst.io/understanding-git-hooks-in-the-easiest-way-bad9afcbb1b3)
- [Git Hooks](https://www.atlassian.com/git/tutorials/git-hooks)


## Protect your Version Control System
It's always a good idea to check your changes before committing them: run unit tests, perform the build, etc. However, such check-lists may be easily overlooked, especially in big projects. To get rid of the human factor, they should be somehow forced and automated. The best way is to implement such verification on the project infrastructure level.


## Table of Contents

- [Installation](#installation)
- [Usage](#usage)
- [Configuration](#configuration)
- [Examples](#examples)

## Installation

To use the Git Hook Installer Maven Plugin, you need to add it as a plugin in your Maven project's `pom.xml` file.

```xml
<build>
<plugins>
    <plugin>
        <groupId>com.leovegas</groupId>
        <artifactId>git-hook-install-maven-plugin</artifactId>
        <version>1.0.0</version>
        <executions>
            <execution>
                <goals>
                    <goal>install</goal>
                </goals>
            </execution>
        </executions>
        <configuration>
            <!-- Define your hooks here -->
        </configuration>
    </plugin>
</plugins>
</build>
```

Replace 1.0.0 with the actual version of the plugin you want to use.

## Usage
The plugin will automatically execute the hook installation process during your Maven build. It installs Git hooks as defined in the plugin's configuration.

You can install default hooks or hooks from external resources within your project.

## Configuration
Configure the plugin using these options:

- `hooks`: A list of default hooks to install. Simply include hook names within the `<configuration>` section.
- `resourceHooks`: Install hooks from external resources. Specify the hook name and the path to the external hook resource.

## Examples

#### Install Default Hooks
To install default hooks, include hook names within the `<configuration>` section:

```xml
<configuration>
    <hooks>
        <pre-commit/>
    </hooks>
</configuration>
```
#### It is the default hooks script.
```xml
# Change directory to the project's root
cd "$(git rev-parse --show-toplevel)"

# Check for updated dependencies and microservice versions
dependency_updates=$(mvn versions:display-property-updates versions:display-parent-updates -DgenerateBackupPoms=false \
    | grep '\->' \
    | awk -F ' ' '{if ($2 != $4) print $0}')

# Check if dependency updates are available
if [[ -n "$dependency_updates" ]]; then
    echo "WARNING: The following dependencies or microservices have updates available:"
        echo "$dependency_updates"
fi
```
#### Let's explain what this code is
<details>
  <summary>Click to expand!</summary>
  
1. `cd "$(git rev-parse --show-toplevel)"` : This line changes the current working directory of the shell to the root directory of the Git repository where the script is located. It uses git rev-parse --show-toplevel to get the top-level directory of the Git repository.
2.  `dependency_updates=$(mvn versions:display-property-updates versions:display-parent-updates -DgenerateBackupPoms=false ...`: This line runs Maven commands to check for updated versions of dependencies and parent POMs (Project Object Model) within the project. Here's what each part of the command does:
`mvn`: This is the command to run Maven, a build and dependency management tool for Java projects.
`versions:display-property-updates`: This Maven plugin goal displays updates for properties in the project's POM file.
`versions:display-parent-updates`: This Maven plugin goal displays updates for the parent POM.
`-DgenerateBackupPoms=false`: This flag prevents Maven from creating backup POM files.
The output of these commands is captured in the `dependency_updates` variable.
3. `| grep '\->' | awk -F ' ' '{if ($2 != $4) print $0}'` : This sequence of commands processes the output of the previous Maven commands. Here's what each part does:
`| grep '\->'`: This pipes the output to `grep`, which filters lines containing `'->' (arrow)` indicating update information.
`| awk -F ' ' '{if ($2 != $4) print $0}'`: This further processes the lines using `awk`. It checks if the second field (current version) is different from the fourth field (available version) and prints the whole line if an update is available.
4. if `[[ -n "$dependency_updates" ]]; then ...`: This line checks if the variable `dependency_updates` is not empty (meaning there are updates available). If updates are available, the following block of code is executed
5. `echo "WARNING: The following dependencies or microservices have updates available:"`: This line prints a warning message indicating that there are updates available.
</details>


#### Install Hooks from Resources

To install hooks from resources, use the `resourceHooks` configuration:

```xml
<configuration>
    <resourceHooks>
        <pre-commit>src/main/resources/pre-commit-hook.sh</pre-commit>
    </resourceHooks>
</configuration>
```

In this example, pre-commit-hook.sh is located in the src/main/resources directory.

