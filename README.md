# modlauncher-injector-junit
![GitHub Workflow Status (branch)](https://img.shields.io/github/workflow/status/KyoriPowered/adventure/build/master) [![MIT License](https://img.shields.io/badge/license-MIT-blue)](LICENSE.txt) [![Maven Central](https://img.shields.io/maven-central/v/org.spongepowered/modlauncher-injector-junit?label=stable)](https://search.maven.org/search?q=g:org.spongepowered%20AND%20a:modlauncher-injector-junit) ![Sonatype Nexus (Snapshots)](https://img.shields.io/nexus/s/org.spongepowered/modlauncher-injector-junit?label=dev&server=https%3A%2F%2Foss.sonatype.org)

A helper library to allow running [JUnit] tests in a [ModLauncher] context.

* [Source]
* [Issues]
* [Community Discord]

## Prerequisites
* Java 8

## Building from Source
In order to build you simply need to run the `./gradlew build` command. You can find the compiled JAR file in `./build/libs` labeled similarly to 'modlauncher-injector-junit-x.x.x-SNAPSHOT.jar'.

## Contributing
Are you a talented programmer looking to contribute some code? We'd love the help!
* Open a pull request with your changes, following our [guidelines](CONTRIBUTING.md).

## Usage

modlauncher-injector-junit publishes releases on Maven Central and Sponge's own repository. 
Snapshots are published on Sonatype OSSRH and Sponge's repository.

If you're using [Gradle] to manage project dependencies, simply include the following in your `build.gradle` file:
```gradle
repositories {
  // releases
  mavenCentral()
  // snapshots
  maven {
    url "https://repo.spongepowered.org/repository/maven-public/"
  }
}

dependencies {
  implementation "org.spongepowered:modlauncher-injector-junit:1.0.0-SNAPSHOT"
}
```

If you're using [Maven] to manage project dependencies, simply include the following in your `pom.xml` file:
```xml
<dependency>
  <groupId>org.spongepowered</groupId>
  <artifactId>modlauncher-injector-junit</artifactId>
  <version>1.0.0-SNAPSHOT</version>
</dependency>
```

## Credits
* Sponge and contributors.
* Contributors of [ModLauncher] and [JUnit].
* All the people behind [Java](http://www.oracle.com/technetwork/java/index.html), [Maven], and [Gradle].

[Gradle]: https://gradle.org
[Maven]: https://maven.apache.org/
[Source]: https://github.com/SpongePowered/modlauncher-injector-junit
[Issues]: https://github.com/SpongePowered/modlauncher-injector-junit/issues
[License]: https://opensource.org/licenses/MIT
[Community Discord]: https://discord.gg/sponge
[ModLauncher]: https://github.com/McModLauncher/modlauncher
[JUnit]: https://junit.org/junit5/
