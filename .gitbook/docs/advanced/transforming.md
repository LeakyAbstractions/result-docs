---
description: How to transform values wrapped inside Results
---

# Transforming Results

Transforming result objects is a key feature that enables you to compose complex operations in a clean and functional
style. There are two primary techniques used for these transformations.


## Mapping Results

Mapping involves applying a function to the value inside a result to produce a new result object.


### Mapping Success Values

We can use [`Result::mapSuccess`][RESULT_MAP_SUCCESS] to apply a function to the success value of a result, transforming
it into a new success value. If the result is a failure, it remains unchanged.

```java
@Test
void testMapSuccess() {
  // Given
  Result<String, ?> result = success("HELLO");
  // When
  Result<Integer, ?> mapped = result.mapSuccess(String::length);
  // Then
  assertEquals(5, mapped.orElse(null));
}
```

In this example, we wrap a `String` inside a `Result` object and invoke `mapSuccess` to calculate its length and wrap it
inside a new `Result` object.


### Mapping Failure Values

Next up, we can use [`Result::mapFailure`][RESULT_MAP_FAILURE] to apply a function to the failure value, transforming it
into a new one. If the result is a success, it remains unchanged.

```java
@Test
void testMapFailure() {
  // Given
  Result<?, BigDecimal> result = failure(ONE);
  // When
  Result<?, Boolean> mapped = result.mapFailure(TWO::equals);
  // Then
  assertFalse(mapped.getFailure().orElse(null));
}
```

Here, we invoke `mapFailure` to transform the failure type of the result from `String` to `Boolean` for demonstration
purposes.


### Mapping Both Success and Failure

The [`Result::map`][RESULT_MAP] method simultaneously handles both success and failure cases by applying two separate
functions: one for transforming the success value and one for transforming the failure value.

```java
@Test
void testMap() {
  // Given
  Result<String, BigDecimal> result1 = success("HELLO");
  Result<String, BigDecimal> result2 = failure(ONE);
  // When
  Result<Integer, Boolean> mapped1 = result1.map(String::length, TWO::equals);
  Result<Integer, Boolean> mapped2 = result2.map(String::length, TWO::equals);
  // Then
  assertEquals(5, mapped1.orElse(null));
  assertFalse(mapped2.getFailure().orElse(null));
}
```


## Flat-Mapping Results

Flat-mapping is used to chain operations that return results themselves, flattening the nested structures into a single
result object. This allows you to transform a success into a failure, or a failure into a success.

To illustrate flat-mapping concepts, the next examples will follow a familiar "pet store" theme. This involves three
Java types: `Pet`, `PetError`, and `PetStore`. These types will help us demonstrate the effective use of flat-mapping
methods.

```java
enum PetError {NOT_FOUND, NO_CONFIG}

record Pet(long id, String name) {

  static final Pet DEFAULT = new Pet(0, "Default pet");
  static final Pet ROCKY = new Pet(1, "Rocky");
  static final Pet GARFIELD = new Pet(2, "Garfield");
}

record PetStore(Pet... pets) {

  PetStore() {
    this(Pet.ROCKY, Pet.GARFIELD);
  }

  Result<Pet, PetError> find(long id) {
    Optional<Pet> found = stream(pets).filter(pet -> pet.id() == id).findAny();
    return Results.ofOptional(found, NOT_FOUND);
  }

  Result<Pet, PetError> getDefaultPet(PetError error) {
    return error == NO_CONFIG ? success(Pet.DEFAULT) : failure(error);
  }

  Result<Long, PetError> getDefaultPetId(PetError error) {
    return getDefaultPet(error).mapSuccess(Pet::id);
  }
}
```

With these types defined, we'll explore how to use various flat-mapping methods to transform result objects and manage
pet-related operations in our imaginary pet store.


### Flat-Mapping Successful Results

Use [`Result::flatMapSuccess`][RESULT_FLATMAP_SUCCESS] to chain an operation that returns a result object. This method
applies a mapping function to the success value, replacing the original result with the new one returned by the
function. If the result is a failure, it remains unchanged.

```java
@Test
void testFlatMapSuccess() {
  // Given
  PetStore store = new PetStore();
  Result<Long, PetError> result = success(100L);
  // When
  Result<Pet, PetError> mapped = result.flatMapSuccess(store::find);
  // Then
  assertEquals(NOT_FOUND, mapped.getFailure().orElse(null));
}
```

This example starts with a successful result containing a wrong pet ID (not found in the pet store). When we flat-map it
with the store's `find` method reference, the final result contains a pet error.


### Flat-Mapping Failed Results

Use [`Result::flatMapFailure`][RESULT_FLATMAP_FAILURE] to chain a result-bearing operation. This method also replaces
the original result with the new one returned by the mapping function. If the result is a success, it remains unchanged.

```java
@Test
void testFlatMapFailure() {
  // Given
  PetStore store = new PetStore();
  Result<Long, PetError> result = failure(NO_CONFIG);
  // When
  Result<Long, PetError> mapped = result.flatMapFailure(store::getDefaultPetId);
  // Then
  assertEquals(Pet.DEFAULT.id(), mapped.orElse(null));
}
```

Here we start with a failed result containing a pet error. When we flat-map it with the store's `getDefaultPetId` method
reference, the final result contains the ID of the default pet in the store.


### Flat-Mapping Both Success and Failure

The [`Result::flatMap`][RESULT_FLATMAP] method handles both success and failure cases by applying the appropriate
function based on the status of the original result.

```java
@Test
void testFlatMap() {
  // Given
  PetStore store = new PetStore();
  Result<Long, PetError> result1 = success(100L);
  Result<Long, PetError> result2 = failure(NO_CONFIG);
  // When
  Result<Pet, PetError> mapped1 = result1.flatMap(store::find, store::getDefaultPet);
  Result<Pet, PetError> mapped2 = result2.flatMap(store::find, store::getDefaultPet);
  // Then
  assertEquals(NOT_FOUND, mapped1.getFailure().orElse(null));
  assertEquals(Pet.DEFAULT, mapped2.orElse(null));
}
```

This example starts with a successful result containing a wrong pet ID (not found in the pet store). When we flat-map it
with the store's `find` method reference, the final result contains a pet error.

Here we start with a failed result containing a pet error. When we flat-map it with the store's `getDefaultPetId` method
reference, the final result contains the ID of the default pet in the store.


## Conclusion

We demonstrated how to transform results in a concise and functional manner, enhancing the clarity and flexibility of
your error-handling and data-processing logic.


[RESULT_FLATMAP]:               https://javadoc.io/doc/com.leakyabstractions/result-api/latest/com/leakyabstractions/result/api/Result.html#flatMap-java.util.function.Function-java.util.function.Function-
[RESULT_FLATMAP_FAILURE]:       https://javadoc.io/doc/com.leakyabstractions/result-api/latest/com/leakyabstractions/result/api/Result.html#flatMapFailure-java.util.function.Function-
[RESULT_FLATMAP_SUCCESS]:       https://javadoc.io/doc/com.leakyabstractions/result-api/latest/com/leakyabstractions/result/api/Result.html#flatMapSuccess-java.util.function.Function-
[RESULT_MAP]:                   https://javadoc.io/doc/com.leakyabstractions/result-api/latest/com/leakyabstractions/result/api/Result.html#map-java.util.function.Function-java.util.function.Function-
[RESULT_MAP_FAILURE]:           https://javadoc.io/doc/com.leakyabstractions/result-api/latest/com/leakyabstractions/result/api/Result.html#mapFailure-java.util.function.Function-
[RESULT_MAP_SUCCESS]:           https://javadoc.io/doc/com.leakyabstractions/result-api/latest/com/leakyabstractions/result/api/Result.html#mapSuccess-java.util.function.Function-
