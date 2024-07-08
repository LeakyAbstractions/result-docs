---
description: How to reject success values and accept failure values
---

# Screening Results

{% hint style="info" %}

Screening mechanisms provide greater flexibility in handling edge cases and enable more robust error recovery
strategies.

{% endhint %}

The following methods allow you to run inline tests on the wrapped value of a result to dynamically transform a success
into a failure or a failure into a success.


## Validating Success

The [`Result::filter`][RESULT_FILTER] method allows you to transform a success into a failure based on certain
conditions. It takes two parameters:

1. A [`Predicate`][PREDICATE] to determine if the success value is acceptable.
2. A mapping [`Function`][FUNCTION] that will produce a failure value if the value is deemed unacceptable.

{% hint style="success" %}

This can be used to enforce additional validation constraints on success values.

{% endhint %}

```java
@Test
void testFilter() {
  // Given
  Result<Integer, String> result = success(1);
  // When
  Result<Integer, String> filtered = result.filter(x -> x % 2 == 0, x -> "It's odd");
  // Then
  assertTrue(filtered.hasFailure());
}
```

In this example, we use a lambda expression to validate that the success value inside `result` is even. Since the number
is odd, it transforms the result into a failure.

{% hint style="danger" %}

Note that it is illegal for the mapping function to return `null`.

{% endhint %}


## Recovering From Failure

The [`Result::recover`][RESULT_RECOVER] method allows you to transform a failure into a success based on certain
conditions. It also receives two parameters:

1. A [`Predicate`][PREDICATE] to determine if the failure value is recoverable.
2. A mapping [`Function`][FUNCTION] that will produce a success value from the acceptable failure value.

{% hint style="success" %}

This method is useful for implementing fallback mechanisms or recovery strategies, ensuring the application logic
remains resilient and adaptable.

{% endhint %}

```java
@Test
void testRecover() {
  // Given
  Result<Integer, String> result = failure("OK");
  // When
  Result<Integer, String> filtered = result.recover("OK"::equals, String::length);
  // Then
  assertTrue(filtered.hasSuccess());
}
```

In this example, we use method references to check if the failure value equals `OK` and then transform the result into a
success.


[FUNCTION]:                     https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/function/Function.html
[PREDICATE]:                    https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/function/Predicate.html
[RESULT_FILTER]:                https://javadoc.io/doc/com.leakyabstractions/result-api/latest/com/leakyabstractions/result/api/Result.html#filter-java.util.function.Predicate-java.util.function.Function-
[RESULT_RECOVER]:               https://javadoc.io/doc/com.leakyabstractions/result-api/latest/com/leakyabstractions/result/api/Result.html#recover-java.util.function.Predicate-java.util.function.Function-
