
{#creating-results}
## Creating Results

> ***How to instantiate new Result objects***

There are several ways to create result objects.


### Successful Results

A successful result contains a non-null value produced by an operation when everything works as intended. We can use `Results.success`{i: success} to create a new instance.

{title: "Successful Results"}
```java
@Test
void testSuccess() {
  // When
  Result<Integer, ?> result = Results.success(200);
  // Then
  assertTrue(result::hasSuccess);
  assertFalse(result::hasFailure);
}
```

Note that we can invoke `hasSuccess` or `hasFailure` to check whether a result is successful or failed (more on this in the [next chapter](#checking-success-or-failure "Checking Success or Failure")).

### Failed Results

On the other hand, a failed result holds a value representing the problem that prevented the operation from completing. We can use `Results.failure`{i: failure} to create a new one.

{title: "Failed Results"}
```java
@Test
void testFailure() {
  // When
  Result<?, String> result = Results.failure("The operation failed");
  // Then
  assertTrue(result::hasFailure);
  assertFalse(result::hasSuccess);
}
```

Failure values cannot be `null` either.


### Results Based on Nullable Values

When we need to create results that depend on a possibly null value, we can use `Results.ofNullable`{i: ofNullable}. If the first argument is `null` then the second one will be used to create a failed result.

{title: "Results based on nullable values"}
```java
@Test
void testOfNullable() {
  // Given
  String string1 = "The operation succeeded";
  String string2 = null;
  // When
  Result<String, Integer> result1 = Results.ofNullable(string1, 404);
  Result<String, Integer> result2 = Results.ofNullable(string2, 404);
  // Then
  assertTrue(result1::hasSuccess);
  assertTrue(result2::hasFailure);
}
```

The second argument can be either a failure value or a function that produces a failure value.


### Results Based on Optionals

We can also use `Results.ofOptional`{i: ofOptional} to create results that depend on an [`Optional`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/Optional.html) value. If the first argument is an empty optional, then the second one will be used to create a failed result.

{title: "Results based on Optionals"}
```java
@Test
void testOfOptional() {
  // Given
  Optional<BigDecimal> optional1 = Optional.of(BigDecimal.ONE);
  Optional<BigDecimal> optional2 = Optional.empty();
  // When
  Result<BigDecimal, Integer> result1 = Results.ofOptional(optional1, -1);
  Result<BigDecimal, Integer> result2 = Results.ofOptional(optional2, -1);
  // Then
  assertTrue(result1::hasSuccess);
  assertTrue(result2::hasFailure);
}
```

The second argument can be a [`Supplier`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/function/Supplier.html) too.


### Results Based on Callables

Finally, if we have a task that may either return a success value or throw an exception, we can encapsulate it as a result using `Results.ofCallable`{i: ofCallable} so we don't need to use a *try-catch* block.

{title: "Results based on Callables"}
```java
String task1() {
  return "OK";
}

String task2() throws Exception {
  throw new Exception("Whoops!");
}

@Test
void testOfCallable() {
  // When
  Result<String, Exception> result1 = Results.ofCallable(this::task1);
  Result<String, Exception> result2 = Results.ofCallable(this::task2);
  // Then
  assertTrue(result1::hasSuccess);
  assertTrue(result2::hasFailure);
}
```

This method enables compatibility with legacy or third-party code that uses exceptions to indicate operation failure.


### Conclusion

We've covered how to create new instances of `Result` using various factory methods provided by the `Results` class. Each method serves a specific purpose, allowing you to select the most suitable one based on the situation.

{pagebreak}
