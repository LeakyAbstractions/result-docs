
{#screening-results}
## Screening Results

> ***How to reject success values and accept failure values***

Screening mechanisms provide greater flexibility in handling edge cases and enable more robust error recovery strategies.

The following methods allow you to run inline tests on the wrapped value of a result to dynamically transform a success into a failure or a failure into a success.


### Validating Success

The `filter`{i: filter} method allows you to transform a success into a failure based on certain conditions. It takes two parameters:

1. A [`Predicate`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/function/Predicate.html) to determine if the success value is acceptable.
2. A mapping [`Function`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/function/Function.html) that will produce a failure value if the value is deemed unacceptable.

This can be used to enforce additional validation constraints on success values.

{title: "Validating success"}
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

In this example, we use a lambda expression to validate that the success value inside `result` is even. Since the number is odd, it transforms the result into a failure.

{blurb, class: warning}

Note that it is illegal for the mapping function to return `null`.

{/blurb}

{pagebreak}


### Recovering From Failure

The `recover`{i: recover} method allows you to transform a failure into a success based on certain conditions. It also receives two parameters:

1. A `Predicate` to determine if the failure value is recoverable.
2. A mapping `Function` that will produce a success value from the acceptable failure value.

This method is useful for implementing fallback mechanisms or recovery strategies, ensuring the application logic remains resilient and adaptable.

{title: "Recovering from failure"}
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

In this example, we use method references to check if the failure value equals `OK` and then transform the result into a success.


### Conclusion

We covered how to filter out unwanted success values and accept failure values using `filter` and `recover`. These methods enable you to refine results based on specific criteria, ensuring that only the relevant values are processed down the line.

{pagebreak}
