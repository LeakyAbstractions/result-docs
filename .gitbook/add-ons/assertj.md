---
description: How to assert Result objects fluently
---

# Fluent Assertions

You can use fluent assertions for Result objects to enhance the readability and expressiveness of your unit tests. These
assertions are based on [AssertJ][ASSERTJ], an open-source Java library that offers a fluent API for writing assertions
in test cases.

{% hint style="info" %}

[AssertJ][ASSERTJ] features a comprehensive and intuitive set of strongly-typed assertions for unit testing. It is a
popular choice among Java developers due to its effective features and compatibility with various testing frameworks
like [JUnit][JUNIT] and [TestNG][TESTNG].

{% endhint %}


## How to Use this Add-On

Add this Maven dependency to your build:

| Group ID                | Artifact ID      | Latest Version |
|-------------------------|------------------|----------------|
| `com.leakyabstractions` | `result-assertj` | ![][LATEST]    |

{% hint style="success" %}

[Maven Central][RELEASES] provides snippets for different build tools to declare this dependency.

{% endhint %}


## Asserting Result Objects

You can use [`ResultAssertions::assertThat`][ASSERT_THAT] in your tests to create fluent assertions for result objects.

```java
import static com.leakyabstractions.result.assertj.ResultAssertions.assertThat;

@Test
void testAssertThat() {
  // Given
  final int zero = 0;
  // When
  final Result<Integer, String> result = success(zero);
  // Then
  assertThat(zero).isZero();
  assertThat(result).hasSuccess(zero);
}
```

If, for any reason, you cannot statically import `assertThat`, you can use
[`ResultAssert::assertThatResult`][ASSERT_THAT_RESULT] instead.

```java
import static com.leakyabstractions.result.assertj.ResultAssert.assertThatResult;
import static org.assertj.core.api.Assertions.assertThat;

@Test
void testAssertThatResult() {
  // Given
  final int zero = 0;
  // When
  final Result<Integer, String> result = success(zero);
  // Then
  assertThat(zero).isZero();
  assertThatResult(result).hasSuccess(zero);
}
```


## Conclusion

We covered how to use fluent assertions for Results. This approach allows you to write clear and expressive tests,
enhancing the maintainability of your unit tests while ensuring that Result objects behave as expected.

{% hint style="success" %}

The full source code for the examples is [available on GitHub][EXAMPLES].

{% endhint %}


[ASSERTJ]:                      https://assertj.github.io/
[ASSERT_THAT]:                  https://javadoc.io/doc/com.leakyabstractions/result-assertj/latest/com/leakyabstractions/result/assertj/ResultAssertions.html#assertThat-com.leakyabstractions.result.api.Result-
[ASSERT_THAT_RESULT]:           https://javadoc.io/doc/com.leakyabstractions/result-assertj/latest/com/leakyabstractions/result/assertj/ResultAssert.html#assertThatResult-com.leakyabstractions.result.api.Result-
[EXAMPLES]:                     https://github.com/LeakyAbstractions/result-assertj/tree/main/result-assertj/src/test/java/example
[JUNIT]:                        https://junit.org/
[LATEST]:                       https://img.shields.io/endpoint?url=https://dev.leakyabstractions.com/result-assertj/latest.json
[RELEASES]:                     https://central.sonatype.com/artifact/com.leakyabstractions/result-assertj/
[TESTNG]:                       https://testng.org/
