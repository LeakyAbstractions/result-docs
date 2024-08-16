
{#micronaut}
## Micronaut Serialization

> ***How to serialize Result objects with Micronaut***

When using Result objects with [Micronaut](https://micronaut.io/), we might run into some problems. The [Micronaut serialization support for Result](https://github.com/LeakyAbstractions/result-micronaut-serde/) solves them by making Micronaut treat results as [`Serdeable`](https://javadoc.io/doc/io.micronaut.serde/micronaut-serde-api/latest/io/micronaut/serde/annotation/Serdeable.html) (so they can be serialized and deserialized).

{blurb, class: information}

Micronaut is a modern, JVM-based framework for building lightweight microservices and serverless applications. It focuses on fast startup times and low memory usage. Although not as widely adopted as [Spring Boot](https://spring.io/projects/spring-boot), it has gained popularity for its performance and innovative features.

{/blurb}


### How to Use this Add-On

Add this Maven dependency to your build:

| Group ID                | Artifact ID              | Version   |
|-------------------------|--------------------------|-----------|
| `com.leakyabstractions` | `result-micronaut-serde` | `1.0.0.0` |

[Maven Central](https://central.sonatype.com/artifact/com.leakyabstractions/result-micronaut-serde/) provides snippets for different build tools to declare this dependency.


### Test Scenario

Let's start by creating a record `ApiOperation` containing one ordinary and one Result field.

{title: "Test Scenario"}
```java
/** Represents an API operation */
@Serdeable
public record ApiOperation(String name, Result<String, String> result) {
}
```

{pagebreak}


### Problem Overview

We will take a look at what happens when we try to serialize and deserialize `ApiOperation` objects with Micronaut.


#### Serialization Problem

Now, let's create a Micronaut controller that returns an instance of `ApiOperation` containing a successful result.

{title: "Serialization problem"}
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

{title: "Running the application", line-numbers: false}
```bash
curl 'http://localhost:8080/operations/last'
```

We'll see that we get a Micronaut `CodecException` caused by a `SerdeException`.

{title: "Serde exception", line-numbers: false}
```text
No serializable introspection present for type Success.
 Consider adding Serdeable. Serializable annotate to type Success.
 Alternatively if you are not in control of the project's source code,
 you can use @SerdeImport(Success.class) to enable serialization of this type.
```

Although this may look strange, it's actually what we should expect. Even though we annotated `ApiOperation` as `@Serdeable`, Micronaut doesn't know how to serialize result objects yet, so the data structure cannot be serialized.

{pagebreak}

{title: "Testing serialization problem"}
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

{title: "Expected serialization", line-numbers: false}
```json
{
  "name": "setup",
  "result": {
    "failure": null,
    "success": "Perfect"
  }
}
```


#### Deserialization Problem

Now, let's reverse our previous example, this time trying to receive an `ApiOperation` as the body of a `POST` request.

{title: "Deserialization problem"}
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

{pagebreak}

We'll see that now we get an `IntrospectionException`. Let's inspect the stack trace.

{title: "Introspection exception", line-numbers: false}
```text
No bean introspection available for type
 [interface com.leakyabstractions.result.api.Result].
 Ensure the class is annotated with
 io.micronaut.core.annotation.Introspected
```

This behavior again makes sense. Essentially, Micronaut cannot create new result objects, because `Result` is not annotated as [`@Introspected`](https://javadoc.io/doc/io.micronaut/micronaut-core/latest/io/micronaut/core/annotation/Introspected.html) or `@Serdeable`.

{title: "Testing deserialization problem"}
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


### Solution Implementation

What we want, is for Micronaut to treat Result values as JSON objects that contain either a `success` or a `failure` value. Fortunately, there's an easy way to solve this problem.


#### Adding the Serde Imports to the Classpath

All we need to do now is add Result-Micronaut-Serde as a Maven dependency. Once the [`@SerdeImport`](https://javadoc.io/doc/io.micronaut.serde/micronaut-serde-api/latest/io/micronaut/serde/annotation/SerdeImport.html) is in the classpath, all functionality is available for all normal Micronaut operations.

{pagebreak}


#### Serializing Results

Now, let's try and serialize our `ApiOperation` object again.

{title: "Serializing a successful Result"}
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

{title: "Actual serialization", line-numbers: false}
```json
{
  "name": "clean",
  "result": {
    "failure": null,
    "success": "All good"
  }
}
```

{pagebreak}

Next, we can try serializing a failed result.

{title: "Serializing a failed Result"}
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

{title: "Actual serialization", line-numbers: false}
```json
{
  "name": "build",
  "result": {
    "failure": "Oops",
    "success": null
  }
}
```


#### Deserializing Results

Now, let's repeat our tests for deserialization. If we read our `ApiOperation` again, we'll see that we no longer get an `IntrospectionException`.

{pagebreak}

{title: "Deserializing a successful Result"}
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

Finally, let's repeat the test again, this time with a failed result. We'll see that yet again we don't get an exception, and in fact, have a failed result.

{title: "Deserializing a failed Result"}
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


### Conclusion

We learned how to serialize and deserialize Result objects using Micronaut, demonstrating how the provided `@SerdeImport` enables Micronaut to treat Results as `Serdeable` objects.

{aside}

The full source code for the examples is [available on GitHub](https://github.com/LeakyAbstractions/result-micronaut-serde/tree/main/result-micronaut-serde/src/test/java/example).

{/aside}

{pagebreak}
