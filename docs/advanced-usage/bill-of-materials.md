---
layout: page
description: Declare dependencies without having to worry about version numbers
hero_height: is-small
menubar: result_docs
---

# Bill of Materials

This project contains [Bill of Materials (BOM) POM](https://reflectoring.io/maven-bom/) for [Result libraries](https://dev.leakyabstractions.com/result/), which is a special POM file that groups dependency versions that are known to be valid and tested to work together, reducing the chances to have version mismatches.

The basic idea is that, instead of specifying a version number for every Result library that you want to use in your project, you can use this BOM POM to get a full set of consistent versions to use.

### Adding Result BOM to Your Build <a href="#adding-resul-bom-to-your-build" id="adding-resul-bom-to-your-build"></a>

Artifact coordinates:

* Group ID: `com.leakyabstractions`
* Artifact ID: `result-bom`
* Version: `1.0.0.0`

To [import the BOM using Maven](https://maven.apache.org/guides/introduction/introduction-to-dependency-mechanism.html#bill-of-materials-bom-poms), use the following:

```
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
```

To [import the BOM using Gradle](https://docs.gradle.org/current/userguide/platforms.html#sub:bom\_import), use the following:

```
dependencies {
    // Import the BOM
    implementation platform('com.leakyabstractions:result-bom:1.0.0.0')

    // Define dependencies without version numbers
    implementation 'com.leakyabstractions:result'
    implementation 'com.leakyabstractions:result-jackson'
    testImplementation 'com.leakyabstractions:result-assertj'
}
```
