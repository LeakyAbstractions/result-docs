
{#unwrapping-values}
## Unwrapping Values

> ***How to get values out of Result objects***

In essence, a `Result` object is just a container that wraps a success or a failure value for us. Therefore, sometimes you are going to want to get that value out of the container.

As useful as this may seem, we will soon realize that we won't be doing it very often.


### Unwrapping Success

The most basic way to retrieve the success value wrapped inside a result is by using `getSuccess`{i: getSuccess}. This method will return an optional success value, depending on whether the result was actually successful or not.

{title: "Unwrapping success"}
```java
@Test
void testGetSuccess() {
  // Given
  Result<?, ?> result1 = success("SUCCESS");
  Result<?, ?> result2 = failure("FAILURE");
  // Then
  Optional<?> success1 = result1.getSuccess();
  Optional<?> success2 = result2.getSuccess();
  // Then
  assertEquals("SUCCESS", success1.get());
  assertTrue(success2::isEmpty);
}
```

{pagebreak}


### Unwrapping Failure

Similarly, we can use `getFailure`{i: getFailure} to obtain the failure value held by a `Result` object.

{title: "Unwrapping failure"}
```java
@Test
void testGetFailure() {
  // Given
  Result<?, ?> result1 = success("SUCCESS");
  Result<?, ?> result2 = failure("FAILURE");
  // Then
  Optional<?> failure1 = result1.getFailure();
  Optional<?> failure2 = result2.getFailure();
  // Then
  assertTrue(failure1::isEmpty);
  assertEquals("FAILURE", failure2.get());
}
```

Unlike Optional's `get`, these methods are null-safe. However, in practice, we will not be using them frequently. Especially, since there are more convenient ways to get the success value out of a result.


### Using Alternative Success

We can use `orElse`{i: orElse} to provide an alternative success value that must be returned when the result is unsuccessful.

{title: "Using alternative success"}
```java
@Test
void testGetOrElse() {
  // Given
  Result<String, String> result1 = success("IDEAL");
  Result<String, String> result2 = failure("ERROR");
  String alternative = "OTHER";
  // When
  String value1 = result1.orElse(alternative);
  String value2 = result2.orElse(alternative);
  // Then
  assertEquals("IDEAL", value1);
  assertEquals("OTHER", value2);
}
```

Note that alternative success values can be `null`.


### Mapping Failure

The `orElseMap`{i: orElseMap} method is similar to Optional's `orElseGet`, but it takes a mapping [`Function`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/function/Function.html) instead of a [`Supplier`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/function/Supplier.html). The function will receive the failure value to produce the alternative success value.

{title: "Mapping failure"}
```java
@Test
void testGetOrElseMap() {
  // Given
  Result<String, Integer> result1 = success("OK");
  Result<String, Integer> result2 = failure(1024);
  Result<String, Integer> result3 = failure(-256);
  Function<Integer, String> mapper = x -> x > 0 ? "HI" : "LO";
  // When
  String value1 = result1.orElseMap(mapper);
  String value2 = result2.orElseMap(mapper);
  String value3 = result3.orElseMap(mapper);
  // Then
  assertEquals("OK", value1);
  assertEquals("HI", value2);
  assertEquals("LO", value3);
}
```

Although probably not the best practice, the mapping function may return `null`.

{pagebreak}


### Streaming Success or Failure

Finally, we can use `streamSuccess`{i: streamSuccess} and `streamFailure`{i: streamFailure} to wrap the value held by an instance of `Result` into a possibly-empty [`Stream`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/stream/Stream.html) object.

{title: "Streaming success or failure"}
```java
@Test
void testStreamSuccess() {
  // Given
  Result<?, ?> result1 = success("Yes");
  Result<?, ?> result2 = failure("No");
  // When
  Stream<?> stream1 = result1.streamSuccess();
  Stream<?> stream2 = result2.streamSuccess();
  // Then
  assertEquals("Yes", stream1.findFirst().orElse(null));
  assertNull(stream2.findFirst().orElse(null));
}

@Test
void testStreamFailure() {
  // Given
  Result<?, ?> result1 = success("Yes");
  Result<?, ?> result2 = failure("No");
  // When
  Stream<?> stream1 = result1.streamFailure();
  Stream<?> stream2 = result2.streamFailure();
  // Then
  assertNull(stream1.findFirst().orElse(null));
  assertEquals("No", stream2.findFirst().orElse(null));
}
```


### Conclusion

We explored various ways to retrieve values from results. Using these methods you can efficiently access the underlying data within a Result object, whether it's a success or a failure.

{pagebreak}
