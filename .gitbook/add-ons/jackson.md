---
description: How to serialize Result objects with Jackson
---

# Jackson Module

When using [Result objects][RESULT] with [Jackson][JACKSON] we might run into some problems. The
[Jackson datatype module for Result][RESULT_JACKSON] solves them by making Jackson treat results as if they were
ordinary objects.

{% hint style="info" %}

[Jackson][JACKSON] is a Java library for [JSON] parsing and generation. It is widely used for converting Java objects to
JSON and vice versa, making it essential for handling data in web services and RESTful APIs.

{% endhint %}


## How to Use this Add-On

Add this Maven dependency to your build:

| Group ID                | Artifact ID      | Latest Version |
|-------------------------|------------------|----------------|
| `com.leakyabstractions` | `result-jackson` | ![][LATEST]    |

{% hint style="success" %}

[Maven Central][RELEASES] provides snippets for different build tools to declare this dependency.

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


### Serialization Problem

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

We'll see that now we get an [`InvalidDefinitionException`][INVALID_DEFINITION_EXCEPTION].

```
Java 8 optional type `java.util.Optional<java.lang.String>`
 not supported by default:
 add Module "com.fasterxml.jackson.datatype:jackson-datatype-jdk8"
 to enable handling
```

While this may look strange, it's the expected behavior. When Jackson examined the result object, it invoked
[`Result::getSuccess`][RESULT_GET_SUCCESS] and received an optional string value. But Jackson will not handle JDK 8
datatypes like `Optional` unless you register [the appropriate modules][JACKSON_MODULES_JAVA8].

```java
@Test
void testSerializationProblem() {
  // Given
  ApiResponse response = new ApiResponse("v1", success("Perfect"));
  // Then
  ObjectMapper objectMapper = new ObjectMapper();
  InvalidDefinitionException error = assertThrows(InvalidDefinitionException.class,
      () -> objectMapper.writeValueAsString(response));
  assertTrue(error.getMessage().startsWith(
      "Java 8 optional type `java.util.Optional<java.lang.String>` not supported"));
}
```

This is Jackson's default serialization behavior. But we'd like to serialize the `result` field like this:

```json
{
  "version": "v1",
  "result": {
    "failure": null,
    "success": "Perfect"
  }
}
```


### Deserialization Problem

Now, let's reverse our previous example, this time trying to deserialize a JSON object into an `ApiResponse`.

```java
String json = "{\"version\":\"v2\",\"result\":{\"success\":\"OK\"}}";
ObjectMapper objectMapper = new ObjectMapper();
objectMapper.readValue(json, ApiResponse.class);
```

We'll see that we get another [`InvalidDefinitionException`][INVALID_DEFINITION_EXCEPTION]. Let's inspect the stack
trace.

```
Cannot construct instance of `com.leakyabstractions.result.api.Result`
 (no Creators, like default constructor, exist):
 abstract types either need to be mapped to concrete types,
 have custom deserializer, or contain additional type information
```

This behavior again makes sense. Essentially, Jackson cannot create new result objects because `Result` is an interface,
not a concrete type.

```java
@Test
void testDeserializationProblem() {
  // Given
  String json = "{\"version\":\"v2\",\"result\":{\"success\":\"OK\"}}";
  // Then
  ObjectMapper objectMapper = new ObjectMapper();
  InvalidDefinitionException error = assertThrows(InvalidDefinitionException.class,
      () -> objectMapper.readValue(json, ApiResponse.class));
  assertTrue(error.getMessage().startsWith(
      "Cannot construct instance of `com.leakyabstractions.result.api.Result`"));
}
```


## Solution Implementation

What we want, is for Jackson to treat `Result` values as JSON objects that contain either a `success` or a `failure`
value. Fortunately, there's a Jackson module that can solve this problem.


### Registering the Jackson Datatype Module for Result

Once we have [added Result-Jackson as a dependency][ADD_DEPENDENCY], all we need to do is register `ResultModule` with
our object mapper.

```java
ObjectMapper objectMapper = new ObjectMapper();
objectMapper.registerModule(new ResultModule());
```

Alternatively, you can also make Jackson auto-discover the module.

```java
objectMapper.findAndRegisterModules();
```

Regardless of the chosen registration mechanism, once the module is registered all functionality is available for all
normal Jackson operations.


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
void deserializeSuccessfulResult() throws Exception {
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
void deserializeFailedResult() throws Exception {
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

You have learned how to use results with [Jackson][JACKSON] without any problems by leveraging the
[Jackson datatype module for Result][RESULT_JACKSON], demonstrating how it enables Jackson to treat Result objects as
ordinary fields.

{% hint style="success" %}

The full source code for the examples is [available on GitHub][EXAMPLES].

{% endhint %}


[ADD_DEPENDENCY]:               #how-to-use-this-add-on
[EXAMPLES]:                     https://github.com/LeakyAbstractions/result-jackson/tree/main/result-jackson/src/test/java/example
[INVALID_DEFINITION_EXCEPTION]: https://javadoc.io/static/com.fasterxml.jackson.core/jackson-databind/2.17.2/com/fasterxml/jackson/databind/exc/InvalidDefinitionException.html
[JACKSON]:                      https://github.com/FasterXML/jackson
[JACKSON_MODULES_JAVA8]:        https://github.com/FasterXML/jackson-modules-java8
[JSON]:                         https://www.json.org/
[LATEST]:                       https://img.shields.io/endpoint?url=https://dev.leakyabstractions.com/result-jackson/latest.json
[OBJECT_MAPPER]:                https://www.baeldung.com/jackson-object-mapper-tutorial
[RELEASES]:                     https://central.sonatype.com/artifact/com.leakyabstractions/result-jackson
[RESULT]:                       https://github.com/LeakyAbstractions/result/
[RESULT_GET_SUCCESS]:           https://javadoc.io/doc/com.leakyabstractions/result-api/latest/com/leakyabstractions/result/api/Result.html#getSuccess--
[RESULT_JACKSON]:               https://github.com/LeakyAbstractions/result-jackson/
