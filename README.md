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
