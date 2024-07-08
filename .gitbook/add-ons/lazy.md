---
description: How to defer expensive calculations with Results
---

# Lazy Results

Lazy results optimize performance by deferring costly operations until absolutely necessary. They behave like regular
results, but only execute the underlying operation when an actual check for success or failure is performed.


## How to Use this Add-On

Add this Maven dependency to your build:

| Group ID                | Artifact ID   | Latest Version |
|-------------------------|---------------|----------------|
| `com.leakyabstractions` | `result-lazy` | ![][LATEST]    |

{% hint style="success" %}

[Maven Central][RELEASES] provides snippets for different build tools to declare this dependency.

{% endhint %}


## Creating Lazy Results

We can use [`LazyResults::ofSupplier`][LAZY_RESULTS_OF_SUPPLIER] to create a lazy result.

```java
Supplier<Result<Integer, String>> supplier = () -> success(123);
Result<Integer, String> lazy = LazyResults.ofSupplier(supplier);
```

While [suppliers][SUPPLIER] can return a fixed success or failure, lazy results shine when they encapsulate
time-consuming or resource-intensive operations.

```java
/* Represents the operation we may omit */
Result<Long, Exception> expensiveCalculation(AtomicLong timesExecuted) {
  long counter = timesExecuted.incrementAndGet();
  return success(counter);
}
```

This sample method simply increments and returns a counter for brevity. However, in a typical scenario, this would
involve an I/O operation.


## Skipping Expensive Calculations

The advantage of lazy results is that they defer invoking the provided [`Supplier`][SUPPLIER] for as long as possible.
Despite this, you can screen and transform them like any other result without losing their laziness.

```java
@Test
void shouldSkipExpensiveCalculation() {
  AtomicLong timesExecuted = new AtomicLong();
  // Given
  Result<Long, Exception> lazy = LazyResults
      .ofSupplier(() -> expensiveCalculation(timesExecuted));
  // When
  Result<String, Exception> transformed = lazy.mapSuccess(Object::toString);
  // Then
  assertNotNull(transformed);
  assertEquals(0L, timesExecuted.get());
}
```

In this example, the expensive calculation is omitted because the lazy result is never fully evaluated. This test
demonstrates that a lazy result can be transformed while maintaining laziness, ensuring that the expensive calculation
is deferred.

{% hint style="info" %}

These methods will preserve laziness:

- [`Result::filter`][RESULT_FILTER]
- [`Result::recover`][RESULT_RECOVER]
- [`Result::mapSuccess`][RESULT_MAP_SUCCESS]
- [`Result::mapFailure`][RESULT_MAP_FAILURE]
- [`Result::map`][RESULT_MAP]
- [`Result::flatMapSuccess`][RESULT_FLATMAP_SUCCESS]
- [`Result::flatMapFailure`][RESULT_FLATMAP_FAILURE]
- [`Result::flatMap`][RESULT_FLATMAP]

{% endhint %}


## Triggering Result Evaluation

Finally, when it's time to check whether the operation succeeds or fails, the lazy result will execute it. This is
triggered by using any of the *terminal* methods, such as [`Result::hasSuccess`][RESULT_HAS_SUCCESS].

```java
@Test
void shouldExecuteExpensiveCalculation() {
  AtomicLong timesExecuted = new AtomicLong();
  // Given
  Result<Long, Exception> lazy = LazyResults
      .ofSupplier(() -> expensiveCalculation(timesExecuted));
  // When
  Result<String, Exception> transformed = lazy.mapSuccess(Object::toString);
  boolean success = transformed.hasSuccess();
  // Then
  assertTrue(success);
  assertEquals(1L, timesExecuted.get());
}
```

Here, the expensive calculation is executed because the lazy result is finally evaluated.

{% hint style="info" %}

Terminal methods will immediately evaluate the lazy result:

- [`Result::hasSuccess`][RESULT_HAS_SUCCESS]
- [`Result::hasFailure`][RESULT_HAS_FAILURE]
- [`Result::getSuccess`][RESULT_GET_SUCCESS]
- [`Result::getFailure`][RESULT_GET_FAILURE]
- [`Result::orElse`][RESULT_OR_ELSE]
- [`Result::orElseMap`][RESULT_OR_ELSE_MAP]
- [`Result::streamSuccess`][RESULT_STREAM_SUCCESS]
- [`Result::streamFailure`][RESULT_STREAM_FAILURE]

{% endhint %}


## Handling Success and Failure Eagerly

By default, [`Result::ifSuccess`][RESULT_IF_SUCCESS], [`Result::ifFailure`][RESULT_IF_FAILURE], and
[`Result::ifSuccessOrElse`][RESULT_IF_SUCCESS_OR_ELSE] are treated as terminal methods. This means they eagerly evaluate
the result and then perform an action based on its status.

```java
@Test
void shouldHandleSuccessEagerly() {
  AtomicLong timesExecuted = new AtomicLong();
  AtomicLong consumerExecuted = new AtomicLong();
  Consumer<Long> consumer = x -> consumerExecuted.incrementAndGet();
  // Given
  Result<Long, Exception> lazy = LazyResults
      .ofSupplier(() -> expensiveCalculation(timesExecuted));
  // When
  lazy.ifSuccess(consumer);
  // Then
  assertEquals(1L, timesExecuted.get());
  assertEquals(1L, consumerExecuted.get());
}
```

In this test, we don't explicitly *unwrap the value* or *check the status*, but since we want to
*consume the success value*, we need to evaluate the lazy result first.

Furthermore, even if we wanted to handle the failure scenario, we would still need to evaluate the lazy result.

```java
@Test
void shouldHandleFailureEagerly() {
  AtomicLong timesExecuted = new AtomicLong();
  AtomicLong consumerExecuted = new AtomicLong();
  Consumer<Exception> consumer = x -> consumerExecuted.incrementAndGet();
  // Given
  Result<Long, Exception> lazy = LazyResults
      .ofSupplier(() -> expensiveCalculation(timesExecuted));
  // When
  lazy.ifFailure(consumer);
  // Then
  assertEquals(1L, timesExecuted.get());
  assertEquals(0L, consumerExecuted.get());
}
```

In this other test, we use [`Result::ifFailure`][RESULT_IF_FAILURE] instead of [`Result::ifSuccess`][RESULT_IF_SUCCESS].
Since the lazy result is evaluated to a success, the failure consumer is never executed.

{% hint style="info" %}

These methods are treated as terminal when used with regular consumer functions:

- [`Result::ifSuccess`][RESULT_IF_SUCCESS]
- [`Result::ifFailure`][RESULT_IF_FAILURE]
- [`Result::ifSuccessOrElse`][RESULT_IF_SUCCESS_OR_ELSE]

{% endhint %}


## Handling Success and Failure Lazily

When these conditional actions may also be skipped along with the expensive calculation, we can encapsulate them into a
[`LazyConsumer`][LAZY_CONSUMER] instead of a regular [`Consumer`][CONSUMER]. All we need to do is to create the consumer
using [`LazyConsumer::of`][LAZY_CONSUMER_OF]. Lazy consumers will preserve the laziness until a terminal method is
eventually used on the result.

```java
@Test
void shouldHandleSuccessLazily() {
  AtomicLong timesExecuted = new AtomicLong();
  AtomicLong consumerExecuted = new AtomicLong();
  Consumer<Long> consumer = LazyConsumer
      .of(x -> consumerExecuted.incrementAndGet());
  // Given
  Result<Long, Exception> lazy = LazyResults
      .ofSupplier(() -> expensiveCalculation(timesExecuted));
  // When
  lazy.ifSuccess(consumer);
  // Then
  assertEquals(0L, timesExecuted.get());
  assertEquals(0L, consumerExecuted.get());
}
```

Here, we use a lazy consumer with [`Result::ifSuccess`][RESULT_IF_SUCCESS] so the expensive calculation is skipped
because the lazy result is never fully evaluated.

{% hint style="success" %}

The full source code for the examples is [available on GitHub][EXAMPLES].

{% endhint %}


[CONSUMER]:                     https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/function/Consumer.html
[EXAMPLES]:                     https://github.com/LeakyAbstractions/result-lazy/tree/main/result-lazy/src/test/java/example
[LATEST]:                       https://img.shields.io/endpoint?url=https://dev.leakyabstractions.com/result-lazy/latest.json
[LAZY_CONSUMER]:                https://javadoc.io/doc/com.leakyabstractions/result-lazy/latest/com/leakyabstractions/result/lazy/LazyConsumer.html
[LAZY_CONSUMER_OF]:             https://javadoc.io/doc/com.leakyabstractions/result-lazy/latest/com/leakyabstractions/result/lazy/LazyConsumer.html#of-java.util.function.Consumer-
[LAZY_RESULTS_OF_SUPPLIER]:     https://javadoc.io/doc/com.leakyabstractions/result-lazy/latest/com/leakyabstractions/result/lazy/LazyResults.html#ofSupplier-java.util.function.Supplier-
[RELEASES]:                     https://central.sonatype.com/artifact/com.leakyabstractions/result-lazy
[RESULT_FILTER]:                https://javadoc.io/doc/com.leakyabstractions/result-api/latest/com/leakyabstractions/result/api/Result.html#filter-java.util.function.Predicate-java.util.function.Function-
[RESULT_FLATMAP]:               https://javadoc.io/doc/com.leakyabstractions/result-api/latest/com/leakyabstractions/result/api/Result.html#flatMap-java.util.function.Function-java.util.function.Function-
[RESULT_FLATMAP_FAILURE]:       https://javadoc.io/doc/com.leakyabstractions/result-api/latest/com/leakyabstractions/result/api/Result.html#flatMapFailure-java.util.function.Function-
[RESULT_FLATMAP_SUCCESS]:       https://javadoc.io/doc/com.leakyabstractions/result-api/latest/com/leakyabstractions/result/api/Result.html#flatMapSuccess-java.util.function.Function-
[RESULT_GET_FAILURE]:           https://javadoc.io/doc/com.leakyabstractions/result-api/latest/com/leakyabstractions/result/api/Result.html#getFailure--
[RESULT_GET_SUCCESS]:           https://javadoc.io/doc/com.leakyabstractions/result-api/latest/com/leakyabstractions/result/api/Result.html#getSuccess--
[RESULT_HAS_FAILURE]:           https://javadoc.io/doc/com.leakyabstractions/result-api/latest/com/leakyabstractions/result/api/Result.html#hasFailure--
[RESULT_HAS_SUCCESS]:           https://javadoc.io/doc/com.leakyabstractions/result-api/latest/com/leakyabstractions/result/api/Result.html#hasSuccess--
[RESULT_IF_FAILURE]:            https://javadoc.io/doc/com.leakyabstractions/result-api/latest/com/leakyabstractions/result/api/Result.html#ifFailure-java.util.function.Consumer-
[RESULT_IF_SUCCESS]:            https://javadoc.io/doc/com.leakyabstractions/result-api/latest/com/leakyabstractions/result/api/Result.html#ifSuccess-java.util.function.Consumer-
[RESULT_IF_SUCCESS_OR_ELSE]:    https://javadoc.io/doc/com.leakyabstractions/result-api/latest/com/leakyabstractions/result/api/Result.html#ifSuccessOrElse-java.util.function.Consumer-java.util.function.Consumer-
[RESULT_MAP]:                   https://javadoc.io/doc/com.leakyabstractions/result-api/latest/com/leakyabstractions/result/api/Result.html#map-java.util.function.Function-java.util.function.Function-
[RESULT_MAP_FAILURE]:           https://javadoc.io/doc/com.leakyabstractions/result-api/latest/com/leakyabstractions/result/api/Result.html#mapFailure-java.util.function.Function-
[RESULT_MAP_SUCCESS]:           https://javadoc.io/doc/com.leakyabstractions/result-api/latest/com/leakyabstractions/result/api/Result.html#mapSuccess-java.util.function.Function-
[RESULT_OR_ELSE]:               https://javadoc.io/doc/com.leakyabstractions/result-api/latest/com/leakyabstractions/result/api/Result.html#orElse-S-
[RESULT_OR_ELSE_MAP]:           https://javadoc.io/doc/com.leakyabstractions/result-api/latest/com/leakyabstractions/result/api/Result.html#orElseMap-java.util.function.Function-
[RESULT_RECOVER]:               https://javadoc.io/doc/com.leakyabstractions/result-api/latest/com/leakyabstractions/result/api/Result.html#recover-java.util.function.Predicate-java.util.function.Function-
[RESULT_STREAM_FAILURE]:        https://javadoc.io/doc/com.leakyabstractions/result-api/latest/com/leakyabstractions/result/api/Result.html#streamFailure--
[RESULT_STREAM_SUCCESS]:        https://javadoc.io/doc/com.leakyabstractions/result-api/latest/com/leakyabstractions/result/api/Result.html#streamSuccess--
[SUPPLIER]:                     https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/function/Supplier.html
