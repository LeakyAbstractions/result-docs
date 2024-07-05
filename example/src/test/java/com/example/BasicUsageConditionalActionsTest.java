package com.example;

import com.leakyabstractions.result.api.Result;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static com.leakyabstractions.result.core.Results.failure;
import static com.leakyabstractions.result.core.Results.success;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Basic Usage / Conditional Actions")
class BasicUsageConditionalActionsTest {

    @DisplayName("Result.ifSuccess")
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

    @DisplayName("Result.ifFailure")
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

    @DisplayName("Result.ifSuccessOrElse")
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
}
