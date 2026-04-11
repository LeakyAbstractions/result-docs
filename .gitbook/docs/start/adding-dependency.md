---
description: How to add Result as a dependency to your build
---

# Adding Result to Your Build

This library adheres to [Pragmatic Versioning][PRAGVER] to communicate the backwards compatibility of each version.

The latest releases are available in [![Maven Central repository][LOGO_MAVEN_CENTRAL]][RELEASES]

Result supports both [**Maven**][MAVEN] and [**Gradle**][GRADLE] for seamless integration into your Java build workflow.

<table data-card-size="large" data-view="cards">
<thead>
<tr>
<th></th>
<th data-hidden data-card-target data-type="content-ref"></th>
<th data-hidden data-card-cover data-type="image">Cover image</th>
<th data-hidden data-card-cover-dark data-type="image">Cover image (dark)</th>
</tr>
</thead>
<tbody>
<tr>
<td>

[**Apache Maven**][MAVEN] is a convention-based Java build tool that uses XML configuration to manage dependencies,
compile code, and package applications in a standardized lifecycle.

</td>
<td>

[Maven](#maven)

</td>
<td data-object-fit="contain">

[Cover image][LOGO_MAVEN]

</td>
<td>

[Cover image dark][LOGO_MAVEN_DARK]

</td>
</tr>
<tr>
<td>

[**Gradle**][GRADLE] is a flexible and high-performance build tool that uses a Groovy or Kotlin DSL to define builds,
offering advanced customization and fast incremental builds.

</td>
<td>

[Gradle](#gradle)

</td>
<td data-object-fit="contain">

[Cover image][LOGO_GRADLE]

</td>
<td>

[Cover image dark][LOGO_GRADLE_DARK]

</td>
</tr>
</tbody>
</table>

## Artifact Coordinates

Add this Maven dependency to your build:

| Group ID                | Artifact ID | Latest Version |
|-------------------------|-------------|----------------|
| `com.leakyabstractions` | `result`    | ![][LATEST]    |

{% hint style="success" %}

[Maven Central][RELEASES] provides snippets for different build tools to declare this dependency.

{% endhint %}


## Maven

Add Result as a Maven dependency to your project.

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

Add Result as a Gradle dependency to your project.

```groovy
dependencies {
    implementation("com.leakyabstractions:result:1.0.0.0")
}
```

{% hint style="info" %}

This is the most common configuration for projects using Result internally. If we were building a library that exposed
Result in its public API, [we should use `api` instead of `implementation`][GRADLE_API_CONFIG].

{% endhint %}


## Conclusion

We learned how to add the library to your project using either Maven or Gradle. By including the correct dependencies,
you're now ready to start leveraging the power of Results in your applications.


[GRADLE]:                       https://gradle.org/
[GRADLE_API_CONFIG]:            https://docs.gradle.org/current/userguide/java_library_plugin.html#sec:java_library_separation
[LATEST]:                       https://img.shields.io/endpoint?url=https://dev.leakyabstractions.com/result/latest.json
[LOGO_GRADLE]:                  ../../.gitbook/assets/logo-gradle.svg
[LOGO_GRADLE_DARK]:             ../../.gitbook/assets/logo-gradle.dark.svg
[LOGO_MAVEN]:                   ../../.gitbook/assets/logo-maven.svg
[LOGO_MAVEN_CENTRAL]:           ../../.gitbook/assets/logo-maven-central.svg
[LOGO_MAVEN_DARK]:              ../../.gitbook/assets/logo-maven.dark.svg
[MAVEN]:                        https://maven.apache.org/
[PRAGVER]:                      https://pragver.github.io/
[RELEASES]:                     https://central.sonatype.com/artifact/com.leakyabstractions/result/
