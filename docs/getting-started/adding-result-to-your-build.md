---
layout: page
title: Adding Result to Your Build
description: How to add Result as a dependency to your build
hero_image: https://images.unsplash.com/photo-1616628188502-413f2fe46e5e?crop=entropy&cs=srgb&fm=jpg&ixid=M3wxOTcwMjR8MHwxfHNlYXJjaHw1fHx0b29sJTIwaGFuZHxlbnwwfHx8fDE2ODUwMjA3MDh8MA&ixlib=rb-4.0.3&q=85
hero_height: is-small
menubar: result_docs
---

# Adding Result to Your Build

The library requires JDK 1.8 or higher. Other than that, it has no external dependencies and it is very lightweight. Adding it to your build should be very easy.

## Artifact coordinates

* Group ID: `com.leakyabstractions`
* Artifact ID: `result`
* Version: `1.0.0.0`

## Maven

To use `Result`, we can add a [**Maven**](https://maven.apache.org/) dependency to our project.

```xml
<dependencies>
    <dependency>
        <groupId>com.leakyabstractions</groupId>
        <artifactId>result</artifactId>
        <version>1.0.0.0</version>
        <type>pom</type>
    </dependency>
</dependencies>
```

## Gradle

We can also add `Result` as a [**Gradle**](https://gradle.org/) dependency.

```
dependencies {
    implementation 'com.leakyabstractions:result:1.0.0.0'
}
```

This is the most common configuration when we are building an application that uses `Result` internally. If we were building a library that exposed `Result` in its public API, we should use instead:

```
dependencies {
    api 'com.leakyabstractions:result:1.0.0.0'
}
```

> For more information on when to use `api` and `implementation`, read the [Gradle documentation on API and implementation separation](https://docs.gradle.org/current/userguide/java\_library\_plugin.html#sec:java\_library\_separation).
