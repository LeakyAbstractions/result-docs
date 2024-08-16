
{#jackson}
## Jackson Module

> ***How to serialize Result objects with Jackson***

When using Result objects with [Jackson](https://github.com/FasterXML/jackson) we might run into some problems. The [Jackson datatype module for Result](https://github.com/LeakyAbstractions/result-jackson/) solves them by making Jackson treat results as if they were ordinary objects.

{blurb, class: information}

Jackson is a Java library for [JSON](https://www.json.org/) parsing and generation. It is widely used for converting Java objects to JSON and vice versa, making it essential for handling data in web services and RESTful APIs.

{/blurb}


### How to Use this Add-On

Add this Maven dependency to your build:

| Group ID                | Artifact ID      | Version   |
|-------------------------|------------------|-----------|
| `com.leakyabstractions` | `result-jackson` | `1.0.0.0` |

[Maven Central](https://central.sonatype.com/artifact/com.leakyabstractions/result-jackson) provides snippets for different build tools to declare this dependency.


### Test Scenario

Let's start by creating a class `ApiResponse` containing one ordinary and one `Result` field.

{title: "Test Scenario"}
```java
public class ApiResponse {

  @JsonProperty String version;
  @JsonProperty Result<String, String> result;

  // Constructors, getters and setters omitted
}
```

{pagebreak}


### Problem Overview

Then we will take a look at what happens when we try to serialize and deserialize `ApiResponse` objects.


#### Serialization Problem

Now, let's instantiate an `ApiResponse` object.

{title: "Serialization problem"}
```java
ApiResponse response = new ApiResponse();
response.setVersion("v1");
response.setResult(success("Perfect"));
```

And finally, let's try serializing it using an [object mapper](https://www.baeldung.com/jackson-object-mapper-tutorial).

{title: "Using an object mapper"}
```java
ObjectMapper objectMapper = new ObjectMapper();
String json = objectMapper.writeValueAsString(response);
```

We'll see that now we get an `InvalidDefinitionException`.

{title: "Invalid definition exception", line-numbers: false}
```text
Java 8 optional type `java.util.Optional<java.lang.String>` not supported by default:
add Module "com.fasterxml.jackson.datatype:jackson-datatype-jdk8" to enable handling
```

While this may look strange, it's the expected behavior. When Jackson examined the result object, it invoked `getSuccess` and received an optional string value. But Jackson will not handle JDK 8 datatypes like `Optional` unless you register [the appropriate modules](https://github.com/FasterXML/jackson-modules-java8).

{pagebreak}

{title: "Testing serialization problem"}
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

{title: "Expected serialization", line-numbers: false}
```json
{
  "version": "v1",
  "result": {
    "failure": null,
    "success": "Perfect"
  }
}
```


#### Deserialization Problem

Now, let's reverse our previous example, this time trying to deserialize a JSON object into an `ApiResponse`.

{title: "Deserialization problem"}
```java
String json = "{\"version\":\"v2\",\"result\":{\"success\":\"OK\"}}";
ObjectMapper objectMapper = new ObjectMapper();
objectMapper.readValue(json, ApiResponse.class);
```

{pagebreak}

We'll see that we get another `InvalidDefinitionException`. Let's inspect the stack trace.

{title: "Invalid definition exception", line-numbers: false}
```text
Cannot construct instance of `com.leakyabstractions.result.api.Result`
 (no Creators, like default constructor, exist):
 abstract types either need to be mapped to concrete types,
 have custom deserializer, or contain additional type information
```

This behavior again makes sense. Essentially, Jackson cannot create new result objects because `Result` is an interface, not a concrete type.

{title: "Testing deserialization problem"}
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


### Solution Implementation

What we want, is for Jackson to treat `Result` values as JSON objects that contain either a `success` or a `failure` value. Fortunately, there's a Jackson module that can solve this problem.


#### Registering the Jackson Datatype Module for Result

Once we have added Result-Jackson as a dependency, all we need to do is register `ResultModule` with our object mapper.

{title: "Registering the Jackson datatype module for Result"}
```java
ObjectMapper objectMapper = new ObjectMapper();
objectMapper.registerModule(new ResultModule());
```

{pagebreak}

Alternatively, you can also make Jackson auto-discover the module.

{title: "Auto-discovering the module"}
```java
objectMapper.findAndRegisterModules();
```

Regardless of the chosen registration mechanism, once the module is registered all functionality is available for all normal Jackson operations.


#### Serializing Results

Now, let's try and serialize our `ApiResponse` object again:

{title: "Serializing a successful Result"}
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

If we look at the serialized response, we'll see that this time the `result` field contains a null `failure` value and a non-null `success` value:

{title: "Actual serialization", line-numbers: false}
```json
{
  "version": "v3",
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

{title: "Actual serialization", line-numbers: false}
```json
{
  "version": "v4",
  "result": {
    "failure": "Oops",
    "success": null
  }
}
```

#### Deserializing Results

Now, let's repeat our tests for deserialization. If we read our `ApiResponse` again, we'll see that we no longer get an `InvalidDefinitionException`.

{pagebreak}

{title: "Deserializing a successful Result"}
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

Finally, let's repeat the test again, this time with a failed result. We'll see that yet again we don't get an exception, and in fact, have a failed result.

{title: "Deserializing a failed Result"}
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


### Conclusion

We learned how to serialize and deserialize Result objects using Jackson, demonstrating how the provided datatype module enables Jackson to treat Results as ordinary objects.

{aside}

The full source code for the examples is [available on GitHub](https://github.com/LeakyAbstractions/result-jackson/tree/main/result-jackson/src/test/java/example).

{/aside}

{pagebreak}
