
{#conditional-actions}
## Conditional Actions

> ***How to handle success and failure scenarios***

We'll now delve into a set of methods that allow you to take conditional actions based on the state of a result. They provide a cleaner and more expressive way to handle success and failure scenarios, eliminating the need for lengthy *if/else* blocks.


### Handling Success

We can use `ifSuccess`{i: ifSuccess} to specify an action that must be executed if the result represents a successful outcome. This method takes a consumer function that will be applied to the success value wrapped by the result.

{title: "Handling success"}
```java
@Test
void testIfSuccess() {
  // Given
  List<Object> list = new ArrayList<>();
  Result<Integer, String> result = success(100);
  // When
  result.ifSuccess(list::add);
  // Then
  assertEquals(100, list.getFirst());
}
```

In this example, `ifSuccess` ensures that the provided action (adding the success value to the list) is only executed if the parsing operation is successful.

{pagebreak}


### Handling Failure

On the other hand, we can use `ifFailure`{i: ifFailure} method to define an action that must be taken when the result represents a failure. This method also takes a [`Consumer`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/function/Consumer.html) that will be applied to the failure value inside the result.

{title: "Handling failure"}
```java
@Test
void testIfFailure() {
  // Given
  List<Object> list = new ArrayList<>();
  Result<Integer, String> result = failure("ERROR");
  // When
  result.ifFailure(list::add);
  // Then
  assertEquals("ERROR", list.getFirst());
}
```

Here, `ifFailure` ensures that the provided action (adding the failure value to the list) is only executed if the parsing operation fails.

{pagebreak}


### Handling Both Scenarios

Finally, `ifSuccessOrElse`{i: ifSuccessOrElse} allows you to specify two separate actions: one for when the operation succeeded and another for when it failed. This method takes two consumer functions: the first for handling the success case and the second for handling the failure case.

{title: "Handling both scenarios"}
```java
@Test
void testIfSuccessOrElse() {
  // Given
  List<Object> list1 = new ArrayList<>();
  List<Object> list2 = new ArrayList<>();
  Result<Long, String> result1 = success(100L);
  Result<Long, String> result2 = failure("ERROR");
  // When
  result1.ifSuccessOrElse(list1::add, list1::add);
  result2.ifSuccessOrElse(list2::add, list2::add);
  // Then
  assertEquals(100L, list1.getFirst());
  assertEquals("ERROR", list2.getFirst());
}
```

In this example, `ifSuccessOrElse` simplifies conditional logic by providing a single method to handle both success and failure scenarios, making the code more concise and readable.


### Conclusion

We explained how to handle success and failure scenarios using these three methods. They provide a powerful way to perform conditional actions based on the state of a Result, streamlining your error handling and making your code more readable and maintainable.

{pagebreak}
