
{#spring-boot-demo}
### Spring Boot Demo Project

> ***Take a look at a Spring Boot-based REST API leveraging Result objects***

This demo project demonstrates how to handle and serialize `Result` objects within a [Spring Boot](https://spring.io/projects/spring-boot) application. It provides a working example of a "pet store" web service that exposes a REST API for managing pets.


#### Generating the Project

The project was generated via [Spring Initializr](https://start.spring.io/) including features: *web* and *cloud-feign*.


#### Adding Serialization Support

Then [Jackson datatype module for Result objects](#jackson) was manually added as a dependency to serialize and deserialize `Result` objects.

{title: "build.gradle", line-numbers: false}
```groovy
dependencies {
  // ...
  implementation platform('com.leakyabstractions:result-bom:1.0.0.0')
  implementation 'com.leakyabstractions:result'
  implementation 'com.leakyabstractions:result-jackson'
}
```

We use a `@Bean` to register the datatype module.

{title: "JacksonConfig.java"}
```java
@Configuration
public class JacksonConfig {
  @Bean
  public Module registerResultModule() {
    return new ResultModule();
  }
}
```

{pagebreak}


#### API Responses

API responses contain a `Result` field, encapsulating the outcome of the requested operation.

{title: "ApiResponse.java"}
```java
public class ApiResponse<S> {

  @JsonProperty String version;
  @JsonProperty Instant generatedOn;
  @JsonProperty Result<S, ApiError> result;
}
```

Results have different success types, depending on the specific endpoint. Failures will be encapsulated as instances of `ApiError`.


#### Controllers

Controllers return instances of `ApiResponse` that will be serialized to JSON by Spring Boot.

{title="PetController.java"}
```java
@RestController
public class PetController {
  // ...
  @GetMapping("/pet")
  ApiResponse<Collection<Pet>> list(@RequestHeader("X-Type") RepositoryType type) {
    log.info("List all pets in {} pet store", type);
    return response(locate(type)
      .flatMapSuccess(PetRepository::listPets)
      .ifSuccess(x -> log.info("Listed {} pet(s) in {}", x.size(), type))
      .ifFailure(this::logError));
  }
}
```

Since failures are expressed as `ApiError` objects, endpoints invariably return HTTP status `200`.

{pagebreak}


#### Running the Application

The application can be built and run with Gradle.

{title: "Running the application", line-numbers: false}
```bash
./gradlew bootRun
```

This will start a stand-alone server on port 8080.


#### Testing the Server

Once started, you can interact with the API.

{title: "Testing the server", line-numbers: false}
```bash
curl -s -H 'x-type: local' http://localhost:8080/pet/0
```

You should see a JSON response like this:

{title: "JSON response", line-numbers: false}
```json
{
  "version": "1.0",
  "result": {
    "success":{
      "id": 0,
      "name": "Rocky",
      "status": "AVAILABLE"
    }
  }
}
```


#### Using Swagger-UI

You can navigate to `http://localhost:8080/` to inspect the API using an interactive UI

{aside}

The full source code for the example application is [available on GitHub](https://github.com/LeakyAbstractions/result-example-spring-boot).

{/aside}

{pagebreak}
