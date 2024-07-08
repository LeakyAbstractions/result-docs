---
description: How to serialize Result objects with Micronaut
---

# Micronaut Serialization

When using Result objects with [Micronaut][MICRONAUT], we might run into some problems. The
[Micronaut serialization][MICRONAUT_SERIALIZATION] support for Result solves them by making Micronaut treat results as
[`Serdeable`][SERDEABLE] (so they can be serialized and deserialized).

{% hint style="info" %}

[Micronaut][MICRONAUT] is a modern, JVM-based framework for building lightweight microservices and serverless
applications. It focuses on fast startup times and low memory usage. Although not as widely adopted as
[Spring Boot][SPRING_BOOT], it has gained popularity for its performance and innovative features.

{% endhint %}


## How to Use this Add-On

Add this Maven dependency to your build:

| Group ID                | Artifact ID              | Latest Version |
|-------------------------|--------------------------|----------------|
| `com.leakyabstractions` | `result-micronaut-serde` | ![][LATEST]    |

{% hint style="success" %}

[Maven Central][RELEASES] provides snippets for different build tools to declare this dependency.

{% endhint %}


## Test Scenario

Let's start by creating a record `ApiOperation` containing one ordinary and one Result field.

```java
/** Represents an API operation */
@Serdeable
public record ApiOperation(String name, Result<String, String> result) {
}
```


## Problem Overview

We will take a look at what happens when we try to serialize and deserialize `ApiOperation` objects with Micronaut.


### Serialization Problem

Now, let's create a Micronaut controller that returns an instance of `ApiOperation` containing a successful result.

```java
@Controller("/operations")
public class ApiController {

    @Get("/last")
    ApiOperation lastOperation() {
        return new ApiOperation("setup", Results.success("Perfect"));
    }
}
```

And finally, let's run the application and try the `/operations/last` endpoint we just created.

```bash
curl 'http://localhost:8080/operations/last'
```

We'll see that we get a Micronaut `CodecException` caused by a [`SerdeException`][SERDE_EXCEPTION].

```
No serializable introspection present for type Success.
 Consider adding Serdeable. Serializable annotate to type Success.
 Alternatively if you are not in control of the project's source code,
 you can use @SerdeImport(Success.class) to enable serialization of this type.
```

Although this may look strange, it's actually what we should expect. Even though we annotated `ApiOperation` as
[`@Serdeable`][SERDEABLE], Micronaut doesn't know how to serialize result objects yet, so the data structure cannot be
serialized.

```java
@Test
void testSerializationProblem(ObjectMapper objectMapper) {
  // Given
  ApiOperation op = new ApiOperation("setup", success("Perfect"));
  // Then
  SerdeException error = assertThrows(SerdeException.class,
      () -> objectMapper.writeValueAsString(op));
  assertTrue(error.getMessage().startsWith(
      "No serializable introspection present for type Success."));
}
```

This is Micronaut's default serialization behavior. But we'd like to serialize the `result` field like this:

```json
{
  "name": "setup",
  "result": {
    "failure": null,
    "success": "Perfect"
  }
}
```


### Deserialization Problem

Now, let's reverse our previous example, this time trying to receive an `ApiOperation` as the body of a `POST` request.

```java
@Controller("/operations")
public class ApiController {

    @Post("/notify")
    Map<String, String> notify(@Body ApiOperation op) {
        return op.result()
                .mapSuccess(s -> Map.of("message", op.name() + " succeeded: " + s))
                .orElseMap(f -> Map.of("error", op.name() + " failed: " + f));
    }
}
```

We'll see that now we get an [`IntrospectionException`][INTROSPECTION_EXCEPTION]. Let's inspect the stack trace.

```
No bean introspection available for type
 [interface com.leakyabstractions.result.api.Result].
 Ensure the class is annotated with
 io.micronaut.core.annotation.Introspected
```

This behavior again makes sense. Essentially, Micronaut cannot create new result objects, because `Result` is not
annotated as [`@Introspected`][INTROSPECTED] or [`@Serdeable`][SERDEABLE].

```java
@Test
void testDeserializationProblem(ObjectMapper objectMapper) {
  // Given
  String json = """
      {"name":"renew","result":{"success":"OK"}}""";
  // Then
  IntrospectionException error = assertThrows(IntrospectionException.class,
      () -> objectMapper.readValue(json, ApiOperation.class));
  String errorMessage = error.getMessage(); // Extract error message
  // Verify error message
  assertTrue(errorMessage.startsWith("No bean introspection available " +
      "for type [interface com.leakyabstractions.result.api.Result]."));
} // End
```


## Solution Implementation

What we want, is for Micronaut to treat Result values as JSON objects that contain either a `success` or a `failure`
value. Fortunately, there's an easy way to solve this problem.


### Adding the Serde Imports to the Classpath

All we need to do now is [add Result-Micronaut-Serde as a Maven dependency][ADD_DEPENDENCY]. Once the
[`@SerdeImport`][SERDE_IMPORT] is in the classpath, all functionality is available for all normal Micronaut operations.


### Serializing Results

Now, let's try and serialize our `ApiOperation` object again.

```java
@Test
void serializeSuccessfulResult(ObjectMapper objectMapper)
    throws IOException {
  // Given
  ApiOperation op = new ApiOperation("clean", success("All good"));
  // When
  String json = objectMapper.writeValueAsString(op);
  // Then
  assertEquals("""
      {"name":"clean","result":{"success":"All good"}}""", json);
}
```

If we look at the serialized response, we'll see that this time the `result` field contains a `success` field.

```json
{
  "name": "clean",
  "result": {
    "failure": null,
    "success": "All good"
  }
}
```

Next, we can try serializing a failed result.

```java
@Test
void serializeFailedResult(ObjectMapper objectMapper)
    throws IOException {
  // Given
  ApiOperation op = new ApiOperation("build", failure("Oops"));
  // When
  String json = objectMapper.writeValueAsString(op);
  // Then
  assertEquals("""
      {"name":"build","result":{"failure":"Oops"}}""", json);
}
```

We can verify that the serialized response contains a non-null `failure` value and a null `success` value:

```json
{
  "name": "build",
  "result": {
    "failure": "Oops",
    "success": null
  }
}
```


### Deserializing Results

Now, let's repeat our tests for deserialization. If we read our `ApiOperation` again, we'll see that we no longer get an
[`IntrospectionException`][INTROSPECTION_EXCEPTION].

```java
@Test
void deserializeSuccessfulResult(ObjectMapper objectMapper)
    throws IOException {
  // Given
  String json = """
      {"name":"check","result":{"success":"Yay"}}""";
  // When
  ApiOperation response = objectMapper.readValue(json, ApiOperation.class);
  // Then
  assertEquals("check", response.name());
  assertEquals("Yay", response.result().orElse(null));
}
```

Finally, let's repeat the test again, this time with a failed result. We'll see that yet again we don't get an
exception, and in fact, have a failed result.

```java
@Test
void deserializeFailedResult(ObjectMapper objectMapper)
    throws IOException {
  // Given
  String json = """
      {"name":"start","result":{"failure":"Nay"}}""";
  // When
  ApiOperation response = objectMapper.readValue(json, ApiOperation.class);
  // Then
  assertEquals("start", response.name());
  assertEquals("Nay", response.result().getFailure().orElse(null));
}
```


## Conclusion

We learned how to serialize and deserialize Result objects using [Micronaut][MICRONAUT], demonstrating how the provided
[`@SerdeImport`][SERDE_IMPORT] enables Micronaut to treat Results as [`Serdeable`][SERDEABLE] objects.

{% hint style="success" %}

The full source code for the examples is [available on GitHub][EXAMPLES].

{% endhint %}


[ADD_DEPENDENCY]:               #how-to-use-this-add-on
[EXAMPLES]:                     https://github.com/LeakyAbstractions/result-micronaut-serde/tree/main/result-micronaut-serde/src/test/java/example
[INTROSPECTED]:                 https://javadoc.io/doc/io.micronaut/micronaut-core/latest/io/micronaut/core/annotation/Introspected.html
[INTROSPECTION_EXCEPTION]:      https://javadoc.io/doc/io.micronaut/micronaut-core/latest/io/micronaut/core/beans/exceptions/IntrospectionException.html
[LATEST]:                       https://img.shields.io/endpoint?url=https://dev.leakyabstractions.com/result-micronaut-serde/latest.json
[MICRONAUT]:                    https://micronaut.io/
[MICRONAUT_SERIALIZATION]:      https://micronaut-projects.github.io/micronaut-serialization/latest/guide/
[RELEASES]:                     https://central.sonatype.com/artifact/com.leakyabstractions/result-jackson
[RESULT_MICRONAUT_SERDE]:       https://github.com/LeakyAbstractions/result-micronaut-serde/
[SERDEABLE]:                    https://javadoc.io/doc/io.micronaut.serde/micronaut-serde-api/latest/io/micronaut/serde/annotation/Serdeable.html
[SERDE_EXCEPTION]:              https://javadoc.io/doc/io.micronaut.serde/micronaut-serde-api/latest/io/micronaut/serde/exceptions/SerdeException.html
[SERDE_IMPORT]:                 https://javadoc.io/doc/io.micronaut.serde/micronaut-serde-api/latest/io/micronaut/serde/annotation/SerdeImport.html
[SPRING_BOOT]:                  https://spring.io/projects/spring-boot
