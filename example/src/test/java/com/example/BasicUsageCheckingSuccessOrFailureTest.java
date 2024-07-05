package com.example;

import com.leakyabstractions.result.api.Result;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.leakyabstractions.result.core.Results.failure;
import static com.leakyabstractions.result.core.Results.success;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Basic Usage / Checking Success or Failure")
class BasicUsageCheckingSuccessOrFailureTest {

    @DisplayName("Result.hasSuccess")
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

    @DisplayName("Result.hasFailure")
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
}
