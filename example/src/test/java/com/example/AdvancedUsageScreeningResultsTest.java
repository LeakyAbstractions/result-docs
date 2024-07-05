package com.example;

import com.leakyabstractions.result.api.Result;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.leakyabstractions.result.core.Results.failure;
import static com.leakyabstractions.result.core.Results.success;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Advanced Usage / Screening Results")
class AdvancedUsageScreeningResultsTest {

    @DisplayName("Result.filter")
    @Test
    void testFilter() {
        // Given
        Result<Integer, String> result = success(1);
        // When
        Result<Integer, String> filtered = result.filter(x -> x % 2 == 0, x -> "It's odd");
        // Then
        assertTrue(filtered.hasFailure());
    }

    @DisplayName("Result.recover")
    @Test
    void testRecover() {
        // Given
        Result<Integer, String> result = failure("OK");
        // When
        Result<Integer, String> filtered = result.recover("OK"::equals, String::length);
        // Then
        assertTrue(filtered.hasSuccess());
    }
}
