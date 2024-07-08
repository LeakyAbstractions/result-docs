---
description: How to add Result as a dependency to your build
---

# Adding Result to Your Build

This library adheres to [Pragmatic Versioning][PRAGVER] to communicate the backwards compatibility of each version.

The latest releases are available in [Maven Central][RELEASES].


## Artifact Coordinates

Add this Maven dependency to your build:

| Group ID                | Artifact ID | Latest Version |
|-------------------------|-------------|----------------|
| `com.leakyabstractions` | `result`    | ![][LATEST]    |

{% hint style="success" %}

[Maven Central][RELEASES] provides snippets for different build tools to declare this dependency.

{% endhint %}


## Maven

To use `Result`, we can add a [**Maven**][MAVEN] dependency to our project.

```xml
<dependencies>
    <dependency>
        <groupId>com.leakyabstractions</groupId>
        <artifactId>result</artifactId>
        <version>1.0.0.0</version>
    </dependency>
</dependencies>
```


## Gradle

We can also add `Result` as a [**Gradle**][GRADLE] dependency.

```gradle
dependencies {
    implementation("com.leakyabstractions:result:1.0.0.0")
}
```

{% hint style="info" %}

This is the most common configuration for projects using `Result` internally. If we were building a library that exposed
`Result` in its public API, [we should use `api` instead of `implementation`][GRADLE_API_CONFIG].

{% endhint %}


[GRADLE]:                       https://gradle.org/
[GRADLE_API_CONFIG]:            https://docs.gradle.org/current/userguide/java_library_plugin.html#sec:java_library_separation
[LATEST]:                       https://img.shields.io/endpoint?url=https://dev.leakyabstractions.com/result/latest.json
[MAVEN]:                        https://maven.apache.org/
[PRAGVER]:                      https://pragver.github.io/
[RELEASES]:                     https://central.sonatype.com/artifact/com.leakyabstractions/result
