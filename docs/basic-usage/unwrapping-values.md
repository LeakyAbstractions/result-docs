---
layout: page
title: Unwrapping Values
description: How to get wrapped values out of Result objects
hero_height: is-small
menubar: result_docs
---

# Unwrapping Values

In a nutshell, a `Result` object is just a container that wraps either a success or a failure value for us. Sometimes we may need to simply get that value out of the container.

As useful as it may seem at first, we will soon realize that we won't need to do it that often.

## Unwrapping Success Value

The most basic way to retrieve the success value wrapped inside a `Result` instance is [`Result::getSuccess`](https://dev.leakyabstractions.com/result/javadoc/1.0.0.0/com/leakyabstractions/result/Result.html#getSuccess--). This method will return an optional success value, depending on whether the result is successful or failed.

```java
@Test
void get_success() {
  // Given
  final Result<?, ?> result = success("SUCCESS");
  // Then
  final Optional<?> success = result.getSuccess();
  final Optional<?> failure = result.getFailure();
  // Then
  assertEquals("SUCCESS", success.orElse(null));
  assertTrue(failure::isEmpty);
}
```

## Unwrapping Failure Value

Similarly, we can use [`Result::getFailure`](https://dev.leakyabstractions.com/result/javadoc/1.0.0.0/com/leakyabstractions/result/Result.html#getFailure--) to obtain the failure value held by a `Result` object.

```java
@Test
void get_failure() {
  // Given
  final Result<?, ?> result = failure("FAILURE");
  // Then
  final Optional<?> success = result.getSuccess();
  final Optional<?> failure = result.getFailure();
  // Then
  assertTrue(success::isEmpty);
  assertEquals("FAILURE", failure.orElse(null));
}
```

{% include notification.html
  status="is-info"
  icon="fas fa-info-circle"
  message="

Note that, unlike [`Optional::get`](https://docs.oracle.com/javase/8/docs/api/java/util/Optional.html#get--),
these methods are null-safe.
However, in practice, we will not be using them a lot, especially since there are more convenient ways to get the success value out of a result.

" %}


## Using Alternative Success Value

For example, we can use [`Result::orElse`](https://dev.leakyabstractions.com/result/javadoc/1.0.0.0/com/leakyabstractions/result/Result.html#orElse-S-) and provide an alternative success value that will be returned in case the result is failed.

```java
@Test
void get_success_or_an_alternative_value() {
  // Given
  final Result<String, ?> result1 = success("IDEAL");
  final Result<String, ?> result2 = failure(1024);
  final String alternative = "OTHER";
  // When
  final String value1 = result1.orElse(alternative);
  final String value2 = result2.orElse(alternative);
  // Then
  assertEquals("IDEAL", value1);
  assertEquals("OTHER", value2);
}
```

{% include notification.html
  status="is-info"
  icon="fas fa-info-circle"
  message="Note that the alternative success value can be `null`." %}

## Mapping Failure Value

The [`Result::orElseMap`](https://dev.leakyabstractions.com/result/javadoc/1.0.0.0/com/leakyabstractions/result/Result.html#orElseMap-java.util.function.Function-) method is similar to `orElse()`. However, instead of using an alternative success value, it takes a mapping function. This function will be applied to the failure value to produce an alternative success value:

```java
@Test
void get_success_or_map_failure_value() {
  // Given
  final Result<String, Integer> result1 = success("OK");
  final Result<String, Integer> result2 = failure(1024);
  final Result<String, Integer> result3 = failure(-256);
  final Function<Integer, String> mapper = x -> x > 0 ? "HI" : "LO";
  // When
  final String value1 = result1.orElseMap(mapper);
  final String value2 = result2.orElseMap(mapper);
  final String value3 = result3.orElseMap(mapper);
  // Then
  assertEquals("OK", value1);
  assertEquals("HI", value2);
  assertEquals("LO", value3);
}
```

{% include notification.html
  status="is-info"
  icon="fas fa-info-circle"
  message="Note that, although the mapping function may return `null`, it is not the best practice." %}

## Streaming Success/Failure Value

Finally, we can use [`Result::streamSuccess`](https://dev.leakyabstractions.com/result/javadoc/1.0.0.0/com/leakyabstractions/result/Result.html#streamSuccess--) and [`Result::streamFailure`](https://dev.leakyabstractions.com/result/javadoc/1.0.0.0/com/leakyabstractions/result/Result.html#streamFailure--) to wrap the value held by an instance of `Result` into a possibly-empty `Stream` object.

```java
@Test
void stream_success() {
  // Given
  final Result<?, ?> result1 = success("Yes");
  final Result<?, ?> result2 = failure("No");
  // When
  final Stream<?> stream1 = result1.streamSuccess();
  final Stream<?> stream2 = result2.streamSuccess();
  // Then
  assertEquals("Yes", stream1.findFirst().orElse(null));
  assertNull(stream2.findFirst().orElse(null));
}

@Test
void stream_failure() {
  // Given
  final Result<?, ?> result1 = success("Yes");
  final Result<?, ?> result2 = failure("No");
  // When
  final Stream<?> stream1 = result1.streamFailure();
  final Stream<?> stream2 = result2.streamFailure();
  // Then
  assertNull(stream1.findFirst().orElse(null));
  assertEquals("No", stream2.findFirst().orElse(null));
}
```
