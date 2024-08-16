
{#assertj}
## Fluent Assertions

> ***How to assert Result objects fluently***

You can use fluent assertions for Result objects to enhance the readability and expressiveness of your unit tests. These assertions are based on [AssertJ](https://assertj.github.io/), an open-source Java library that offers a fluent API for writing assertions in test cases.

{blurb, class: information}

AssertJ features a comprehensive and intuitive set of strongly-typed assertions for unit testing. It is a popular choice among Java developers due to its effective features and compatibility with various testing frameworks like [JUnit](https://junit.org/) and [TestNG](https://testng.org/).

{/blurb}


### How to Use this Add-On

Add this Maven dependency to your build:

| Group ID                | Artifact ID      | Version   |
|-------------------------|------------------|-----------|
| `com.leakyabstractions` | `result-assertj` | `1.0.0.0` |


[Maven Central](https://central.sonatype.com/artifact/com.leakyabstractions/result-assertj/) provides snippets for different build tools to declare this dependency.


### Asserting Result Objects

You can use `ResultAssertions.assertThat` in your tests to create fluent assertions for result objects.

{pagebreak}

{title: "Asserting Result objects"}
```java
import static com.leakyabstractions.result.assertj.ResultAssertions.assertThat;

@Test
void testAssertThat() {
  // When
  final Result<Integer, String> result = success(0);
  // Then
  assertThat(0).isZero();
  assertThat(result).hasSuccess(0);
}
```

If, for any reason, you cannot statically import `assertThat`, you can use `ResultAssert.assertThatResult` instead.

{title: "Using alternative static import"}
```java
import static com.leakyabstractions.result.assertj.ResultAssert.assertThatResult;
import static org.assertj.core.api.Assertions.assertThat;

@Test
void testAssertThatResult() {
  // When
  final Result<Integer, String> result = success(0);
  // Then
  assertThat(0).isZero();
  assertThatResult(result).hasSuccess(0);
}
```


### Conclusion

We covered how to use fluent assertions for Results. This approach allows you to write clear and expressive tests, enhancing the maintainability of your unit tests while ensuring that Result objects behave as expected.

{aside}

The full source code for the examples is [available on GitHub](https://github.com/LeakyAbstractions/result-assertj/tree/main/result-assertj/src/test/java/example).

{/aside}

{pagebreak}
