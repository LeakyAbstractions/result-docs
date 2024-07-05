package com.example;

import com.leakyabstractions.result.api.Result;
import com.leakyabstractions.result.core.Results;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static com.example.AdvancedUsageTransformingResultsTest.PetError.NOT_FOUND;
import static com.example.AdvancedUsageTransformingResultsTest.PetError.NO_CONFIG;
import static com.leakyabstractions.result.core.Results.failure;
import static com.leakyabstractions.result.core.Results.success;
import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.TWO;
import static java.util.Arrays.stream;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@DisplayName("Advanced Usage / Transforming Results")
class AdvancedUsageTransformingResultsTest {

    @DisplayName("Result.mapSuccess")
    @Test
    void testMapSuccess() {
        // Given
        Result<String, ?> result = success("HELLO");
        // When
        Result<Integer, ?> mapped = result.mapSuccess(String::length);
        // Then
        assertEquals(5, mapped.orElse(null));
    }

    @DisplayName("Result.mapFailure")
    @Test
    void testMapFailure() {
        // Given
        Result<?, BigDecimal> result = failure(ONE);
        // When
        Result<?, Boolean> mapped = result.mapFailure(TWO::equals);
        // Then
        assertFalse(mapped.getFailure().orElse(null));
    }

    @DisplayName("Result.map")
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

    @DisplayName("Result.flatMapSuccess")
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

    @DisplayName("Result.flatMapFailure")
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

    @DisplayName("Result.flatMap")
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
}
