package com.example;

import com.leakyabstractions.result.api.Result;
import com.leakyabstractions.result.core.Results;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Getting Started / Creating Results")
class GettingStartedCreatingResultsTest {

    @DisplayName("Results.success")
    @Test
    void testSuccess() {
        // When
        Result<Integer, ?> result = Results.success(200);
        // Then
        assertTrue(result::hasSuccess);
        assertFalse(result::hasFailure);
    }

    @DisplayName("Results.failure")
    @Test
    void testFailure() {
        // When
        Result<?, String> result = Results.failure("The operation failed");
        // Then
        assertTrue(result::hasFailure);
        assertFalse(result::hasSuccess);
    }

    @DisplayName("Results.ofNullable")
    @Test
    void testOfNullable() {
        // Given
        String string1 = "The operation succeeded";
        String string2 = null;
        // When
        Result<String, Integer> result1 = Results.ofNullable(string1, 404);
        Result<String, Integer> result2 = Results.ofNullable(string2, 404);
        // Then
        assertTrue(result1::hasSuccess);
        assertTrue(result2::hasFailure);
    }

    @DisplayName("Results.ofOptional")
    @Test
    void testOfOptional() {
        // Given
        Optional<BigDecimal> optional1 = Optional.of(BigDecimal.ONE);
        Optional<BigDecimal> optional2 = Optional.empty();
        // When
        Result<BigDecimal, Integer> result1 = Results.ofOptional(optional1, -1);
        Result<BigDecimal, Integer> result2 = Results.ofOptional(optional2, -1);
        // Then
        assertTrue(result1::hasSuccess);
        assertTrue(result2::hasFailure);
    }

    String task1() {
        return "OK";
    }

    String task2() throws Exception {
        throw new Exception("Whoops!");
    }

    @DisplayName("Results.ofCallable")
    @Test
    void testOfCallable() {
        // When
        Result<String, Exception> result1 = Results.ofCallable(this::task1);
        Result<String, Exception> result2 = Results.ofCallable(this::task2);
        // Then
        assertTrue(result1::hasSuccess);
        assertTrue(result2::hasFailure);
    }
}
