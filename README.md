# Git Hook Install Maven Plugin
The Git Hook Install Maven Plugin is a custom Maven plugin that allows you to automate the installation of Git hooks in your project's repository. Git hooks are scripts that Git executes before or after certain actions such as commits, merges, and pushes. This plugin simplifies the process of generating and installing these hooks by providing a seamless integration with Maven.

## Why Git Hooks?
Git hooks provide a way to automate tasks and checks in your repository. With hooks, you can ensure consistent code quality, enforce coding standards, run tests before committing, and perform various other actions automatically. By integrating Git hooks into your project, you streamline your development process and maintain higher code standards.

## Protect your Version Control System
It's always a good idea to check your changes before committing them: run unit tests, perform the build, etc. However, such check-lists may be easily overlooked, especially in big projects. To get rid of the human factor, they should be somehow forced and automated. The best way is to implement such verification on the project infrastructure level.

## Features
Automatically generates and installs Git hooks based on configuration provided in the Maven project.
Supports both inline hooks with predefined scripts and resource hooks with external script files.
Provides a default script that checks for updated dependencies and microservice versions before certain Git actions.

## So why should I use this plugin?
Because it deals with the problem of providing hook configuration to the repository, and automates their installation.

## Usage
The plugin provides the only goal "install". It's mapped on "initialize" phase by default. To use the default flow add these lines to the plugin definition:

1. Add Plugin Configuration: In your project's pom.xml, add the plugin configuration under the build section:
```
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
                <!-- Define hooks and resourceHooks here -->
            </configuration>
        </plugin>
    </plugins>
</build>
```
Configure Hooks: Define your hooks in the plugin configuration's <hooks> and <resourceHooks> sections. Inline hooks have predefined scripts, while resource hooks link to external script files.
#### NOTE: If you define it this way, the default version control script will be added to the specified hook.
```
<hooks>
  <hook-name/>
  ...
</hooks>
```
NOTE: The plugin rewrites hooks.

## Usage Example
```
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
		<pre-commit/>
            </configuration>
        </plugin>
    </plugins>
</build>

```


External hook files can also been used :
```
...
<configuration>
    <hooks>
        <resourceHooks>
            <pre-push>hooks/pre-push.sh</pre-push>
           <!-- Add more resource hooks as needed -->
        </resourceHooks>
    </hooks>
</configuration>
...
```
