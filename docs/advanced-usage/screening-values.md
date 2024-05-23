---
layout: page
title: Screening Values
subtitle: Conditionally rejecting success values and accepting failure values
description: Conditionally rejecting success values and accepting failure values
hero_height: is-small
menubar: result_docs
---

# Screening Values


## Rejecting Successes


We can run an inline test on our wrapped success value with [`filter()`][FILTER]. This method takes a
[predicate][PREDICATE] and a mapping [function][FUNCTION] as arguments and returns a possibly-new `Result` object:

- If the result has a success value that doesn't satisfy the predicate, `filter()` will return a new result
  object containing the failure value produced by the mapping function.

- Otherwise, the result will be returned as-is.

```java
  @Test
  void filter_successful_result() {
    // Given
    final Result<Pet, PetError> result1 = createPet("Rantanplan", AVAILABLE);
    final Result<Pet, PetError> result2 = createPet("Garfield", SOLD);
    final Result<Pet, PetError> result3 = createPet(null, AVAILABLE);
    final Predicate<Pet> predicate = p -> p.status == AVAILABLE;
    final Function<Pet, PetError> mapper = p -> new PetError(p.name + " is not available");
    // When
    final Result<Pet, PetError> filtered1 = result1.filter(predicate, mapper);
    final Result<Pet, PetError> filtered2 = result2.filter(predicate, mapper);
    final Result<Pet, PetError> filtered3 = result3.filter(predicate, mapper);
    // Then
    assertEquals("Rantanplan", filtered1.getSuccess().get().name);
    assertEquals("Garfield is not available", filtered2.getFailure().get().message);
    assertEquals("Missing pet name", filtered3.getFailure().get().message);
  }
```

The filter method is normally used to reject wrapped success values based on a predefined rule.

Note that it is illegal for a mapping function to return `null`.

```java
  @Test
  void filter_successful_result_to_null() {
    // Given
    final Result<Pet, PetError> result = createPet("Rantanplan", AVAILABLE);
    // Then
    assertThatThrownBy(() -> result.filter(s -> false, s -> null))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("failure value returned by mapper");
  }
```


## Recovering From Failures

We can run an inline test on our wrapped failure value with [`recover()`][RECOVER]. This method takes a
[predicate][PREDICATE] and a mapping [function][FUNCTION] as arguments and returns a possibly-new `Result` object:

- If the result has a failure value that satisfies the predicate, `recover()` will return a new result object
  containing the success value produced by the mapping function.

- Otherwise, the result will be returned as-is.

```java
  @Test
  void recover_failed_result() {
    // Given
    final Result<Pet, PetError> result1 = failure(new PetError("Not found"));
    final Result<Pet, PetError> result2 = createPet("Foo", SOLD);
    final Predicate<PetError> predicate = x -> x.message.equals("Not found");
    final Function<PetError, Pet> mapper = x -> new Pet("Fallback pet", AVAILABLE);
    // When
    final Result<Pet, PetError> mapped1 = result1.recover(predicate, mapper);
    final Result<Pet, PetError> mapped2 = result2.recover(predicate, mapper);
    // Then
    assertEquals("Fallback pet", mapped1.orElse(null).name);
    assertEquals("Foo", mapped2.orElse(null).name);
  }
```

Note that it is illegal for a mapping function to return `null`.

```java
  @Test
  void recover_failed_result_to_null() {
    // Given
    final Result<Pet, PetError> result = failure(new PetError("Not found"));
    // Then
    assertThatThrownBy(() -> result.recover(s -> true, s -> null))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("success value returned by mapper");
  }
```

[FILTER]: https://dev.leakyabstractions.com/result/javadoc/1.0.0.0/com/leakyabstractions/result/Result.html#filter-java.util.function.Predicate,java.util.function.Function-
[RECOVER]: https://dev.leakyabstractions.com/result/javadoc/1.0.0.0/com/leakyabstractions/result/Result.html#recover-java.util.function.Predicate,java.util.function.Function-
[PREDICATE]: https://docs.oracle.com/javase/8/docs/api/java/util/function/Predicate.html
[FUNCTION]: https://docs.oracle.com/javase/8/docs/api/java/util/function/Function.html
