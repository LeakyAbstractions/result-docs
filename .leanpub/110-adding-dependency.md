
{#adding-dependency}
## Adding Result to Your Build

> ***How to add Result as a dependency to your build***

This library adheres to [Pragmatic Versioning](https://pragver.github.io/) to communicate the backwards compatibility of each version.

The latest releases are available in Maven Central.


### Artifact Coordinates

Add this Maven dependency to your build:

| Group ID                | Artifact ID | Version   |
|-------------------------|-------------|-----------|
| `com.leakyabstractions` | `result`    | `1.0.0.0` |

[Maven Central](https://central.sonatype.com/artifact/com.leakyabstractions/result) provides snippets for different build tools to declare this dependency.


### Maven

To use `Result`, we can add a [**Maven**](https://maven.apache.org/) dependency to our project.

{title: "Adding Result as a Maven dependency", line-numbers: false}
```xml
<dependencies>
    <dependency>
        <groupId>com.leakyabstractions</groupId>
        <artifactId>result</artifactId>
        <version>1.0.0.0</version>
    </dependency>
</dependencies>
```

{pagebreak}


### Gradle

We can also add `Result` as a [**Gradle**](https://gradle.org/) dependency.

{title: "Adding Result as a Gradle dependency", line-numbers: false}
```groovy
dependencies {
    implementation("com.leakyabstractions:result:1.0.0.0")
}
```

This is the most common configuration for projects using `Result` internally. If we were building a library that exposed `Result` in its public API, [we should use `api` instead of `implementation`](https://docs.gradle.org/current/userguide/java_library_plugin.html#sec:java_library_separation).


### Conclusion

We learned how to add the library to your project using either Maven or Gradle. By including the correct dependencies, you're now ready to start leveraging the power of Results in your applications.

{pagebreak}
