---
description: How to declare dependencies without having to worry about version numbers
---

# Bill of Materials

Tracking multiple add-on versions for your project can quickly become cumbersome. In that situation, you can use the
convenient [Result Library Bill of Materials][RESULT_BOM] to centralize and align their versions. This ensures
compatibility and simplifies dependency maintenance.

{% hint style="info" %}

[Maven][MAVEN]'s Bill of Materials POMs are special POM files that group dependency versions known to be valid and
tested to work together, reducing the chances of having version mismatches.

{% endhint %}

The basic idea is that instead of specifying a version number for each Result library in your project, you can use this
BOM to get a complete set of consistent versions.


## How to Use this Add-On

Add this Maven dependency to your build:

| Group ID                | Artifact ID  | Latest Version |
|-------------------------|--------------|----------------|
| `com.leakyabstractions` | `result-bom` | ![][LATEST]    |


### Maven

To [import the BOM using Maven][MAVEN_IMPORT], use the following:

```xml
<!-- Import the BOM -->
<dependencyManagement>
  <dependencies>
    <dependency>
      <groupId>com.leakyabstractions</groupId>
      <artifactId>result-bom</artifactId>
      <version>1.0.0.0</version>
      <scope>import</scope>
      <type>pom</type>
    </dependency>   
  </dependencies>
</dependencyManagement>

<!-- Define dependencies without version numbers -->
<dependencies>
  <dependency>
    <groupId>com.leakyabstractions</groupId>
    <artifactId>result</artifactId>
  </dependency>
  <dependency>
    <groupId>com.leakyabstractions</groupId>
    <artifactId>result-assertj</artifactId>
    <scope>test</scope>
  </dependency>
</dependencies>
```


### Gradle

To [import the BOM using Gradle][GRADLE_IMPORT], use the following:

```gradle
dependencies {
  // Import the BOM
  implementation platform("com.leakyabstractions:result-bom:1.0.0.0")

  // Define dependencies without version numbers
  implementation("com.leakyabstractions:result")
  testImplementation("com.leakyabstractions:result-assertj")
}
```


[GRADLE_IMPORT]:                https://docs.gradle.org/current/userguide/platforms.html#sub:bom_import
[LATEST]:                       https://img.shields.io/endpoint?url=https://dev.leakyabstractions.com/result-bom/latest.json
[MAVEN]:                        https://maven.apache.org/
[MAVEN_IMPORT]:                 https://maven.apache.org/guides/introduction/introduction-to-dependency-mechanism.html#bill-of-materials-bom-poms
[RESULT_BOM]:                   https://github.com/LeakyAbstractions/result-bom
