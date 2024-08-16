
{#lazy}
## Lazy Results

> ***How to defer expensive calculations with Results***

Lazy results optimize performance by deferring costly operations until absolutely necessary. They behave like regular results, but only execute the underlying operation when an actual check for success or failure is performed.


### How to Use this Add-On

Add this Maven dependency to your build:

| Group ID                | Artifact ID   | Version   |
|-------------------------|---------------|-----------|
| `com.leakyabstractions` | `result-lazy` | `1.0.0.0` |

[Maven Central](https://central.sonatype.com/artifact/com.leakyabstractions/result-lazy) provides snippets for different build tools to declare this dependency.


### Creating Lazy Results

We can use `LazyResults.ofSupplier` to create a lazy result.

{title: "Creating lazy Results"}
```java
Supplier<Result<Integer, String>> supplier = () -> success(123);
Result<Integer, String> lazy = LazyResults.ofSupplier(supplier);
```

While suppliers can return a fixed success or failure, lazy results shine when they encapsulate time-consuming or resource-intensive operations.

{title: "Encapsulating expensive calculations"}
```java
/* Represents the operation we may omit */
Result<Long, Exception> expensiveCalculation(AtomicLong timesExecuted) {
  long counter = timesExecuted.incrementAndGet();
  return success(counter);
}
```

This sample method simply increments and returns a counter for brevity. However, in a typical scenario, this would involve an I/O operation.

{pagebreak}


### Skipping Expensive Calculations

The advantage of lazy results is that they defer invoking the provided [`Supplier`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/function/Supplier.html) for as long as possible. Despite this, you can screen and transform them like any other result without losing their laziness.

{title: "Skipping expensive calculations"}
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

In this example, the expensive calculation is omitted because the lazy result is never fully evaluated. This test demonstrates that a lazy result can be transformed while maintaining laziness, ensuring that the expensive calculation is deferred.

{aside}

These methods will preserve laziness:

- `filter`
- `recover`
- `mapSuccess`
- `mapFailure`
- `map`
- `flatMapSuccess`
- `flatMapFailure`
- `flatMap`

{/aside}

{pagebreak}


### Triggering Result Evaluation

Finally, when it's time to check whether the operation succeeds or fails, the lazy result will execute it. This is triggered by using any of the *terminal* methods, such as `hasSuccess`.

{title: "Triggering Result evaluation"}
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

{aside}

Terminal methods will immediately evaluate the lazy result:

- `hasSuccess`
- `hasFailure`
- `getSuccess`
- `getFailure`
- `orElse`
- `orElseMap`
- `streamSuccess`
- `streamFailure`

{/aside}

{pagebreak}


### Handling Success and Failure Eagerly

By default, `ifSuccess`, `ifFailure`, and `ifSuccessOrElse` are treated as terminal methods. This means they eagerly evaluate the result and then perform an action based on its status.

{title: "Handling success and failure eagerly"}
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

In this test, we don't explicitly *unwrap the value* or *check the status*, but since we want to *consume the success value*, we need to evaluate the lazy result first.

{pagebreak}

Furthermore, even if we wanted to handle the failure scenario, we would still need to evaluate the lazy result.

{title: "Handling success and failure eagerly"}
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

In this other test, we use `ifFailure` instead of `ifSuccess`. Since the lazy result is evaluated to a success, the failure consumer is never executed.

{aside}

These methods are treated as terminal when used with regular consumer functions:

- `ifSuccess`
- `ifFailure`
- `ifSuccessOrElse`

{/aside}

{pagebreak}


### Handling Success and Failure Lazily

When these conditional actions may also be skipped along with the expensive calculation, we can encapsulate them into a [`LazyConsumer`](https://javadoc.io/doc/com.leakyabstractions/result-lazy/latest/com/leakyabstractions/result/lazy/LazyConsumer.html) instead of a regular [`Consumer`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/function/Consumer.html). All we need to do is to create the consumer using `LazyConsumer.of`. Lazy consumers will preserve the laziness until a terminal method is eventually used on the result.

{title: "Handling success and failure lazily"}
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

Here, we use a lazy consumer with `ifSuccess` so the expensive calculation is skipped because the lazy result is never fully evaluated.


### Conclusion

We learned how to defer expensive calculations until absolutely necessary. By leveraging lazy results, you can optimize performance by avoiding unnecessary computations and only evaluating the operation's outcome when needed.

{aside}

The full source code for the examples is [available on GitHub](https://github.com/LeakyAbstractions/result-lazy/tree/main/result-lazy/src/test/java/example).

{/aside}

{pagebreak}
