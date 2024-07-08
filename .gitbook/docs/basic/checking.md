---
description: How to find out if the operation succeded or failed
---

# Checking Success or Failure

As we discovered earlier, we can easily determine if a given `Result` instance is successful or not.


## Checking Success

We can use [`Result::hasSuccess`][RESULT_HAS_SUCCESS] to obtain a `boolean` value that represents whether a result is
successful.

```java
@Test
void testHasSuccess() {
  // Given
  Result<?, ?> result1 = success(1024);
  Result<?, ?> result2 = failure(1024);
  // When
  boolean result1HasSuccess = result1.hasSuccess();
  boolean result2HasSuccess = result2.hasSuccess();
  // Then
  assertTrue(result1HasSuccess);
  assertFalse(result2HasSuccess);
}
```


## Checking Failure

We can also use [`Result::hasFailure`][RESULT_HAS_FAILURE] to find out if a result contains a failure value.

```java
@Test
void testHasFailure() {
  // Given
  Result<?, ?> result1 = success(512);
  Result<?, ?> result2 = failure(512);
  // When
  boolean result1HasFailure = result1.hasFailure();
  boolean result2HasFailure = result2.hasFailure();
  // Then
  assertFalse(result1HasFailure);
  assertTrue(result2HasFailure);
}
```

[RESULT_HAS_FAILURE]:           https://javadoc.io/doc/com.leakyabstractions/result-api/latest/com/leakyabstractions/result/api/Result.html#hasFailure--
[RESULT_HAS_SUCCESS]:           https://javadoc.io/doc/com.leakyabstractions/result-api/latest/com/leakyabstractions/result/api/Result.html#hasSuccess--
