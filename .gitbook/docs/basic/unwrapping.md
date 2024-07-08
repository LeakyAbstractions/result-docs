---
description: How to get values out of Result objects
---

# Unwrapping Values

In essence, a `Result` object is just a container that wraps a success or a failure value for us. Therefore, sometimes
you are going to want to get that value out of the container.

{% hint style="info" %}

As useful as this may seem, we will soon realize that we won't be doing it very often.

{% endhint %}


## Unwrapping Success

The most basic way to retrieve the success value wrapped inside a result is by using
[`Result::getSuccess`][RESULT_GET_SUCCESS]. This method will return an optional success value, depending on whether the
result was actually successful or not.

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


## Unwrapping Failure

Similarly, we can use [`Result::getFailure`][RESULT_GET_FAILURE] to obtain the failure value held by a `Result` object.

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

{% hint style="success" %}

Unlike [`Optional::get`][OPTIONAL_GET], these methods are null-safe. However, in practice, we will not be using them
frequently. Especially, since there are more convenient ways to get the success value out of a result.

{% endhint %}


## Using Alternative Success

We can use [`Result::orElse`][RESULT_OR_ELSE] to provide an alternative success value that must be returned when the
result is unsuccessful.

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

{% hint style="info" %}

Note that alternative success values can be `null`.

{% endhint %}


## Mapping Failure

The [`Result::orElseMap`][RESULT_OR_ELSE_MAP] method is similar to [`Optional::orElseGet`][OPTIONAL_OR_ELSE_GET], but it
takes a mapping [`Function`][FUNCTION] instead of a [`Supplier`][SUPPLIER]. The function will receive the failure value
to produce the alternative success value.

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

{% hint style="info" %}

Although probably not the best practice, the mapping function may return `null`.

{% endhint %}


## Streaming Success or Failure

Finally, we can use [`Result::streamSuccess`][RESULT_STREAM_SUCCESS] and
[`Result::streamFailure`][RESULT_STREAM_FAILURE] to wrap the value held by an instance of `Result` into a possibly-empty
[`Stream`][STREAM] object.

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

[FUNCTION]:                     https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/function/Function.html
[OPTIONAL_GET]:                 https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/Optional.html#get()
[OPTIONAL_OR_ELSE_GET]:         https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/Optional.html#orElseGet(java.util.function.Supplier)
[RESULT_GET_FAILURE]:           https://javadoc.io/doc/com.leakyabstractions/result-api/latest/com/leakyabstractions/result/api/Result.html#getFailure--
[RESULT_GET_SUCCESS]:           https://javadoc.io/doc/com.leakyabstractions/result-api/latest/com/leakyabstractions/result/api/Result.html#getSuccess--
[RESULT_OR_ELSE]:               https://javadoc.io/doc/com.leakyabstractions/result-api/latest/com/leakyabstractions/result/api/Result.html#orElse-S-
[RESULT_OR_ELSE_MAP]:           https://javadoc.io/doc/com.leakyabstractions/result-api/latest/com/leakyabstractions/result/api/Result.html#orElseMap-java.util.function.Function-
[RESULT_STREAM_FAILURE]:        https://javadoc.io/doc/com.leakyabstractions/result-api/latest/com/leakyabstractions/result/api/Result.html#streamFailure--
[RESULT_STREAM_SUCCESS]:        https://javadoc.io/doc/com.leakyabstractions/result-api/latest/com/leakyabstractions/result/api/Result.html#streamSuccess--
[STREAM]:                       https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/stream/Stream.html
[SUPPLIER]:                     https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/function/Supplier.html
