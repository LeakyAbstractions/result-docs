package com.example;

import com.leakyabstractions.result.api.Result;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static com.leakyabstractions.result.core.Results.failure;
import static com.leakyabstractions.result.core.Results.success;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Landing Page Example")
class LandingPageExampleTest {

    static final int EXPECTED_UPTIME = 123;
    static final String SUCCESS_MESSAGE = "Server refreshed";
    static final String FAILURE_MESSAGE = "Connection error";

    @DisplayName("Using Exceptions (success scenario)")
    @Test
    void testUsingExceptionsSuccess() {
        // Given
        Logger logger = new Logger();
        UsingExceptions example = new UsingExceptions(false, logger);
        // When
        int hours = example.getServerUptime();
        // Then
        assertEquals(EXPECTED_UPTIME, hours);
        assertEquals(1, logger.info().size());
        assertEquals(SUCCESS_MESSAGE, logger.info().getFirst());
        assertTrue(logger.errors().isEmpty());
    }

    @DisplayName("Using Exceptions (failure scenario)")
    @Test
    void testUsingExceptionsFailure() {
        // Given
        Logger logger = new Logger();
        UsingExceptions example = new UsingExceptions(true, logger);
        // When
        int hours = example.getServerUptime();
        // Then
        assertEquals(-1, hours);
        assertEquals(1, logger.errors().size());
        assertEquals(FAILURE_MESSAGE, logger.errors().getFirst());
        assertTrue(logger.info().isEmpty());
    }

    @DisplayName("Using Results (success scenario)")
    @Test
    void testUsingResultsSuccess() {
        // Given
        Logger logger = new Logger();
        UsingResults example = new UsingResults(false, logger);
        // When
        int hours = example.getServerUptime();
        // Then
        assertEquals(EXPECTED_UPTIME, hours);
        assertEquals(1, logger.info().size());
        assertEquals(SUCCESS_MESSAGE, logger.info().getFirst());
        assertTrue(logger.errors().isEmpty());
    }

    @DisplayName("Using Results (failure scenario)")
    @Test
    void testUsingResultsFailure() {
        // Given
        Logger logger = new Logger();
        UsingResults example = new UsingResults(true, logger);
        // When
        int hours = example.getServerUptime();
        // Then
        assertEquals(-1, hours);
        assertEquals(1, logger.errors().size());
        assertEquals(FAILURE_MESSAGE, logger.errors().getFirst());
        assertTrue(logger.info().isEmpty());
    }

    @DisplayName("Embracing Results (success scenario)")
    @Test
    void testEmbracingResultsSuccess() {
        // Given
        Logger logger = new Logger();
        EmbracingResults example = new EmbracingResults(false, logger);
        // When
        Result<Integer, String> hours = example.getServerUptime();
        // Then
        assertEquals(EXPECTED_UPTIME, hours.orElse(-1));
        assertEquals(1, logger.info().size());
        assertEquals(SUCCESS_MESSAGE, logger.info().getFirst());
        assertTrue(logger.errors().isEmpty());
    }

    @DisplayName("Embracing Results (failure scenario)")
    @Test
    void testEmbracingResultsFailure() {
        // Given
        Logger logger = new Logger();
        EmbracingResults example = new EmbracingResults(true, logger);
        // When
        Result<Integer, String> hours = example.getServerUptime();
        // Then
        assertEquals(FAILURE_MESSAGE, hours.getFailure().orElse(null));
        assertEquals(1, logger.errors().size());
        assertEquals(FAILURE_MESSAGE, logger.errors().getFirst());
        assertTrue(logger.info().isEmpty());
    }

    record Logger(List<String> info, List<String> errors) {

        Logger() {
            this(new ArrayList<>(), new ArrayList<>());
        }

        public void info(String message) {
            info.add(message);
        }

        public void error(String message) {
            errors.add(message);
        }
    }

    record Server(Logger logger) {

        public void refresh() {
            logger.info(SUCCESS_MESSAGE);
        }

        public int getUptime() {
            return EXPECTED_UPTIME;
        }
    }

    static class ConnectionException extends Exception {
        ConnectionException() {
            super(FAILURE_MESSAGE);
        }
    }

    record UsingExceptions(boolean simulateError, Logger logger) {

        Server connect() throws ConnectionException {
            if (simulateError) {
                throw new ConnectionException();
            }
            return new Server(logger);
        }

        public int getServerUptime() {
            int hours;
            try {
                Server server = connect();
                server.refresh();
                hours = server.getUptime();
            } catch (ConnectionException exception) {
                logger.error(exception.getMessage());
                hours = -1;
            }
            return hours;
        }
    }

    record UsingResults(boolean simulateError, Logger logger) {

        Result<Server, String> connect() {
            return simulateError ? failure(FAILURE_MESSAGE) : success(new Server(logger));
        }

        public int getServerUptime() {
            final Result<Server, String> result = connect();
            result.ifSuccess(Server::refresh);
            result.ifFailure(logger::error);
            return result.mapSuccess(Server::getUptime).orElse(-1);
        }
    }

    record EmbracingResults(boolean simulateError, Logger logger) {

        Result<Server, String> connect() {
            return simulateError ? failure(FAILURE_MESSAGE) : success(new Server(logger));
        }

        public Result<Integer, String> getServerUptime() {
            return connect().ifSuccessOrElse(Server::refresh, logger::error).mapSuccess(Server::getUptime);
        }
    }
}
