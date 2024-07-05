package com.example;

import com.leakyabstractions.result.api.Result;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import static com.leakyabstractions.result.core.Results.failure;
import static com.leakyabstractions.result.core.Results.success;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Basic Usage / Unwrapping Values")
class BasicUsageUnwrappingValuesTest {

    @DisplayName("Result.getSuccess")
    @Test
    void testGetSuccess() {
        // Given
        Result<?, ?> result1 = success("SUCCESS");
        Result<?, ?> result2 = failure("FAILURE");
        // Then
        Optional<?> success1 = result1.getSuccess();
        Optional<?> success2 = result2.getSuccess();
        // Then
        assertEquals("SUCCESS", success1.get());
        assertTrue(success2::isEmpty);
    }

    @DisplayName("Result.getFailure")
    @Test
    void testGetFailure() {
        // Given
        Result<?, ?> result1 = success("SUCCESS");
        Result<?, ?> result2 = failure("FAILURE");
        // Then
        Optional<?> failure1 = result1.getFailure();
        Optional<?> failure2 = result2.getFailure();
        // Then
        assertTrue(failure1::isEmpty);
        assertEquals("FAILURE", failure2.get());
    }

    @DisplayName("Result.orElse")
    @Test
    void testGetOrElse() {
        // Given
        Result<String, String> result1 = success("IDEAL");
        Result<String, String> result2 = failure("ERROR");
        String alternative = "OTHER";
        // When
        String value1 = result1.orElse(alternative);
        String value2 = result2.orElse(alternative);
        // Then
        assertEquals("IDEAL", value1);
        assertEquals("OTHER", value2);
    }

    @DisplayName("Result.orElseMap")
    @Test
    void testGetOrElseMap() {
        // Given
        Result<String, Integer> result1 = success("OK");
        Result<String, Integer> result2 = failure(1024);
        Result<String, Integer> result3 = failure(-256);
        Function<Integer, String> mapper = x -> x > 0 ? "HI" : "LO";
        // When
        String value1 = result1.orElseMap(mapper);
        String value2 = result2.orElseMap(mapper);
        String value3 = result3.orElseMap(mapper);
        // Then
        assertEquals("OK", value1);
        assertEquals("HI", value2);
        assertEquals("LO", value3);
    }

    @DisplayName("Result.streamSuccess")
    @Test
    void testStreamSuccess() {
        // Given
        Result<?, ?> result1 = success("Yes");
        Result<?, ?> result2 = failure("No");
        // When
        Stream<?> stream1 = result1.streamSuccess();
        Stream<?> stream2 = result2.streamSuccess();
        // Then
        assertEquals("Yes", stream1.findFirst().orElse(null));
        assertNull(stream2.findFirst().orElse(null));
    }

    @DisplayName("Result.streamFailure")
    @Test
    void testStreamFailure() {
        // Given
        Result<?, ?> result1 = success("Yes");
        Result<?, ?> result2 = failure("No");
        // When
        Stream<?> stream1 = result1.streamFailure();
        Stream<?> stream2 = result2.streamFailure();
        // Then
        assertNull(stream1.findFirst().orElse(null));
        assertEquals("No", stream2.findFirst().orElse(null));
    }
}
