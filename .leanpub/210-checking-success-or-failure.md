
{#checking-success-or-failure}
## Checking Success or Failure

> ***How to find out if the operation succeded or failed***

As we discovered earlier, we can easily determine if a given `Result` instance is successful or not.


### Checking Success

We can use `hasSuccess`{i: hasSuccess} to obtain a `boolean` value that represents whether a result is successful.

{title: "Checking success"}
```java
@Test
void testHasSuccess() {
  // When
  boolean result1HasSuccess = success(1024).hasSuccess();
  boolean result2HasSuccess = failure(1024).hasSuccess();
  // Then
  assertTrue(result1HasSuccess);
  assertFalse(result2HasSuccess);
}
```


### Checking Failure

We can also use `hasFailure`{i: hasFailure} to find out if a result contains a failure value.

{title: "Checking failure"}
```java
@Test
void testHasFailure() {
  // When
  boolean result1HasFailure = success(512).hasFailure();
  boolean result2HasFailure = failure(512).hasFailure();
  // Then
  assertFalse(result1HasFailure);
  assertTrue(result2HasFailure);
}
```


### Conclusion

We discussed how to determine the state of a Result object using `hasSuccess` and `hasFailure`. These methods provide a straightforward way to identify the outcome of an operation, helping you make decisions based on the outcome.

{pagebreak}
