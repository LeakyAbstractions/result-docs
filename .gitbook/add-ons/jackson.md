---
description: How to serialize Result objects with Jackson 2.x and 3.x
---

# Jackson Datatype Modules for Result

When using Result objects with [**Jackson**][JACKSON] we might run into some problems. The Jackson datatype modules for
Result solve them by making Jackson treat results as if they were ordinary objects.

{% hint style="info" %}

[**Jackson**][JACKSON] is a Java library for [JSON] parsing and generation. It is widely used for converting Java
objects to JSON and vice versa, making it essential for handling data in web services and RESTful APIs.

{% endhint %}


## How to Use These Add-Ons

Choose the Maven dependency that matches your Jackson version.


### Jackson 2.x

Add this Maven dependency to your build:

| Group ID                | Artifact ID       | Latest Version                |
|-------------------------|-------------------|-------------------------------|
| `com.leakyabstractions` | `result-jackson`  | ![][LATEST_RESULT_JACKSON]    |


### Jackson 3.x

Add this one instead:

| Group ID                | Artifact ID       | Latest Version                 |
|-------------------------|-------------------|--------------------------------|
| `com.leakyabstractions` | `result-jackson3` | ![][LATEST_RESULT_JACKSON3]    |

{% hint style="success" %}

Maven Central provides snippets for different build tools to declare these dependencies.

- [Jackson 2.x datatype module for Result][RELEASES_RESULT_JACKSON]
- [Jackson 3.x datatype module for Result][RELEASES_RESULT_JACKSON3]

{% endhint %}


## Test Scenario

Let's start by creating a class `ApiResponse` containing one ordinary and one `Result` field.

```java
/** Represents an API response */
public class ApiResponse {

  @JsonProperty
  String version;

  @JsonProperty
  Result<String, String> result;

  // Constructors, getters and setters omitted
}
```


## Problem Overview

Then we will take a look at what happens when we try to serialize and deserialize `ApiResponse` objects.


### Serialization Problem (Jackson 2.x Only)

Now, let's instantiate an `ApiResponse` object.

```java
ApiResponse response = new ApiResponse();
response.setVersion("v1");
response.setResult(success("Perfect"));
```

And finally, let's try serializing it using an [object mapper][OBJECT_MAPPER].

```java
ObjectMapper objectMapper = new ObjectMapper();
String json = objectMapper.writeValueAsString(response);
```

With Jackson 2.x, this will produce an error: [`InvalidDefinitionException`][INVALID_DEFINITION_EXCEPTION].

```
Java 8 optional type `java.util.Optional<java.lang.String>`
 not supported by default:
 add Module "com.fasterxml.jackson.datatype:jackson-datatype-jdk8"
 to enable handling
```

The reason is Jackson encounters `Optional` values internally and it will not handle it unless you register
[the appropriate modules][JACKSON_MODULES_JAVA8].


### Deserialization Problem (Both Jackson 2.x and 3.x)

Now, let's reverse our previous example, this time trying to deserialize a JSON object into an `ApiResponse`.

```java
String json = "{\"version\":\"v2\",\"result\":{\"success\":\"OK\"}}";
ObjectMapper objectMapper = new ObjectMapper();
objectMapper.readValue(json, ApiResponse.class);
```

This will produce an error: [`InvalidDefinitionException`][INVALID_DEFINITION_EXCEPTION]. Let's inspect the stack trace.

```
Cannot construct instance of `com.leakyabstractions.result.api.Result`
 (no Creators, like default constructor, exist):
 abstract types either need to be mapped to concrete types,
 have custom deserializer, or contain additional type information
```

This behavior again makes sense. Essentially, Jackson cannot create new result objects because `Result` is an interface,
not a concrete type.


## Solution Implementation

The Jackson datatype modules for Result provide serializers and deserializers so that Jackson treats results as if they
were regular objects.


### Registering the Jackson Datatype Module for Result

First of all, we need to [add the the appropriate datatype module as a dependency][ADD_DEPENDENCY].


#### Jackson 2.x

Then, all we need to do is register `ResultModule` with our [object mapper][OBJECT_MAPPER].

```java
ObjectMapper objectMapper = new ObjectMapper();
objectMapper.registerModule(new ResultModule());
```

Alternatively, you can also make Jackson 2.x auto-discover the module.

```java
objectMapper.findAndRegisterModules();
```


#### Jackson 3.x

Just like the previous example, we need to add `ResultModule` to our [JSON mapper][JSON_MAPPER].

```java
JsonMapper.Builder builder = JsonMapper.builder();
builder.addModule(new ResultModule());
ObjectMapper objectMapper = builder.build();
```

Or simply use auto-discovery:

```java
builder.findAndAddModules();
```

{% hint style="info" %}

Regardless of the chosen registration mechanism, once the appropriate dataype module is registered all functionality is
available for all normal Jackson operations.

{% endhint %}


### Serializing Results

Now, let's try and serialize our `ApiResponse` object again:

```java
@Test
void serializeSuccessfulResult() throws Exception {
  // Given
  ApiResponse response = new ApiResponse("v3", success("All good"));
  // When
  ObjectMapper objectMapper = new ObjectMapper();
  objectMapper.registerModule(new ResultModule());
  String json = objectMapper.writeValueAsString(response);
  // Then
  assertTrue(json.contains("v3"));
  assertTrue(json.contains("All good"));
}
```

If we look at the serialized response, we'll see that this time the `result` field contains a null `failure` value and a
non-null `success` value:

```json
{
  "version": "v3",
  "result": {
    "failure": null,
    "success": "All good"
  }
}
```

Next, we can try serializing a failed result.

```java
@Test
void serializeFailedResult() throws Exception {
  // Given
  ApiResponse response = new ApiResponse("v4", failure("Oops"));
  // When
  ObjectMapper objectMapper = new ObjectMapper();
  objectMapper.findAndRegisterModules();
  String json = objectMapper.writeValueAsString(response);
  // Then
  assertTrue(json.contains("v4"));
  assertTrue(json.contains("Oops"));
} // End
```

We can verify that the serialized response contains a non-null `failure` value and a null `success` value.

```json
{
  "version": "v4",
  "result": {
    "failure": "Oops",
    "success": null
  }
}
```

### Deserializing Results

Now, let's repeat our tests for deserialization. If we read our `ApiResponse` again, we'll see that we no longer get an
[`InvalidDefinitionException`][INVALID_DEFINITION_EXCEPTION].

```java
@Test
void deserializeSuccessfulResult() {
  // Given
  String json = "{\"version\":\"v5\",\"result\":{\"success\":\"Yay\"}}";
  // When
  ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
  ApiResponse response = objectMapper.readValue(json, ApiResponse.class);
  // Then
  assertEquals("v5", response.getVersion());
  assertEquals("Yay", response.getResult().orElse(null));
}
```

Finally, let's repeat the test again, this time with a failed result. We'll see that yet again we don't get an
exception, and in fact, have a failed result.

```java
@Test
void deserializeFailedResult() {
  // Given
  String json = "{\"version\":\"v6\",\"result\":{\"failure\":\"Nay\"}}";
  // When
  ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
  ApiResponse response = objectMapper.readValue(json, ApiResponse.class);
  // Then
  assertEquals("v6", response.getVersion());
  assertEquals("Nay", response.getResult().getFailure().orElse(null));
}
```


## Conclusion

We learned how to serialize and deserialize Result objects using both **Jackson 2.x** and **Jackson 2.x**, demonstrating
how the provided datatype module enables Jackson to treat Results as ordinary objects.

The integration is nearly identical across versions; the main differences are limited to dependency coordinates and how
the object mapper is constructed and configured.

{% hint style="success" %}

The full source code for the examples is available on GitHub.

- [Jackson 2.x examples][EXAMPLES_RESULT_JACKSON]
- [Jackson 3.x examples][EXAMPLES_RESULT_JACKSON3]

{% endhint %}


[ADD_DEPENDENCY]:               #how-to-use-this-add-on
[EXAMPLES_RESULT_JACKSON]:      https://github.com/LeakyAbstractions/result-jackson/tree/main/result-jackson/src/test/java/example
[EXAMPLES_RESULT_JACKSON3]:     https://github.com/LeakyAbstractions/result-jackson3/tree/main/result-jackson3/src/test/java/example
[INVALID_DEFINITION_EXCEPTION]: https://javadoc.io/static/com.fasterxml.jackson.core/jackson-databind/2.17.2/com/fasterxml/jackson/databind/exc/InvalidDefinitionException.html
[JACKSON]:                      https://github.com/FasterXML/jackson/
[JACKSON_MODULES_JAVA8]:        https://github.com/FasterXML/jackson-modules-java8/
[JSON]:                         https://www.json.org/
[JSON_MAPPER]:                  https://javadoc.io/static/tools.jackson.core/jackson-databind/3.0.0/tools.jackson.databind/tools/jackson/databind/json/JsonMapper.html
[LATEST_RESULT_JACKSON]:        https://img.shields.io/endpoint?url=https://dev.leakyabstractions.com/result-jackson/latest.json
[LATEST_RESULT_JACKSON3]:       https://img.shields.io/endpoint?url=https://dev.leakyabstractions.com/result-jackson3/latest.json
[OBJECT_MAPPER]:                https://www.baeldung.com/jackson-object-mapper-tutorial
[RELEASES_RESULT_JACKSON]:      https://central.sonatype.com/artifact/com.leakyabstractions/result-jackson/
[RELEASES_RESULT_JACKSON3]:     https://central.sonatype.com/artifact/com.leakyabstractions/result-jackson3/
