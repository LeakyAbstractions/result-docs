---
layout: page
title: Checking Success or Failure
description: How to find out if the operation succeded or failed
hero_height: is-small
menubar: result_docs
---

# Checking Success or Failure

As we saw previously, we can easily determine if a given `Result` instance is successful or failed.

## Checking Success

When we invoke [`Result::success`](https://dev.leakyabstractions.com/result/javadoc/1.0.0.0/com/leakyabstractions/result/Result.html#hasSuccess--) we will get a `boolean` representing whether a result contains a success value.

```java
@Test
void check_success() {
  // Given
  final Result<?, ?> result1 = success(1024);
  final Result<?, ?> result2 = failure(1024);
  // When
  final boolean result1HasSuccess = result1.hasSuccess();
  final boolean result2HasSuccess = result2.hasSuccess();
  // Then
  assertTrue(result1HasSuccess);
  assertFalse(result2HasSuccess);
}
```

## Checking Failure

We can also use [`Result::hasFailure`](https://dev.leakyabstractions.com/result/javadoc/1.0.0.0/com/leakyabstractions/result/Result.html#hasFailure--) to find out if a result contains a failure value.

```java
@Test
void check_failure() {
  // Given
  final Result<?, ?> result1 = success(512);
  final Result<?, ?> result2 = failure(512);
  // When
  final boolean result1HasFailure = result1.hasFailure();
  final boolean result2HasFailure = result2.hasFailure();
  // Then
  assertFalse(result1HasFailure);
  assertTrue(result2HasFailure);
}
```
