package grupo12.practico.integration.pdi;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("PdiServiceException Tests")
class PdiServiceExceptionTest {

    @Test
    @DisplayName("Constructor with message - Should create exception with message only")
    void constructorWithMessage_ShouldCreateExceptionWithMessageOnly() {
        PdiServiceException exception = new PdiServiceException("Test error message");

        assertEquals("Test error message", exception.getMessage());
        assertNull(exception.getErrorCode());
        assertNull(exception.getCause());
    }

    @Test
    @DisplayName("Constructor with message and error code - Should create exception with message and code")
    void constructorWithMessageAndErrorCode_ShouldCreateExceptionWithMessageAndCode() {
        PdiServiceException exception = new PdiServiceException("Test error message", 10001);

        assertEquals("Test error message", exception.getMessage());
        assertEquals(10001, exception.getErrorCode());
        assertNull(exception.getCause());
    }

    @Test
    @DisplayName("Constructor with message and cause - Should create exception with message and cause")
    void constructorWithMessageAndCause_ShouldCreateExceptionWithMessageAndCause() {
        Throwable cause = new RuntimeException("Root cause");
        PdiServiceException exception = new PdiServiceException("Test error message", cause);

        assertEquals("Test error message", exception.getMessage());
        assertNull(exception.getErrorCode());
        assertSame(cause, exception.getCause());
    }

    @Test
    @DisplayName("Constructor with message, error code, and cause - Should create exception with all parameters")
    void constructorWithAllParameters_ShouldCreateExceptionWithAllParameters() {
        Throwable cause = new RuntimeException("Root cause");
        PdiServiceException exception = new PdiServiceException("Test error message", 10002, cause);

        assertEquals("Test error message", exception.getMessage());
        assertEquals(10002, exception.getErrorCode());
        assertSame(cause, exception.getCause());
    }

    @Test
    @DisplayName("getErrorCode - Should return null when created with message only constructor")
    void getErrorCode_ShouldReturnNullWhenCreatedWithMessageOnlyConstructor() {
        PdiServiceException exception = new PdiServiceException("Error");

        assertNull(exception.getErrorCode());
    }

    @Test
    @DisplayName("getErrorCode - Should return null when created with message and cause constructor")
    void getErrorCode_ShouldReturnNullWhenCreatedWithMessageAndCauseConstructor() {
        PdiServiceException exception = new PdiServiceException("Error", new RuntimeException());

        assertNull(exception.getErrorCode());
    }

    @Test
    @DisplayName("getErrorCode - Should return code when created with message and code constructor")
    void getErrorCode_ShouldReturnCodeWhenCreatedWithMessageAndCodeConstructor() {
        PdiServiceException exception = new PdiServiceException("Error", 500);

        assertEquals(500, exception.getErrorCode());
    }

    @Test
    @DisplayName("getErrorCode - Should handle zero error code")
    void getErrorCode_ShouldHandleZeroErrorCode() {
        PdiServiceException exception = new PdiServiceException("Error", 0);

        assertEquals(0, exception.getErrorCode());
    }

    @Test
    @DisplayName("getErrorCode - Should handle negative error code")
    void getErrorCode_ShouldHandleNegativeErrorCode() {
        PdiServiceException exception = new PdiServiceException("Error", -1);

        assertEquals(-1, exception.getErrorCode());
    }

    @Test
    @DisplayName("Constructor - Should handle null message")
    void constructor_ShouldHandleNullMessage() {
        PdiServiceException exception = new PdiServiceException(null);

        assertNull(exception.getMessage());
        assertNull(exception.getErrorCode());
    }

    @Test
    @DisplayName("Constructor - Should handle empty message")
    void constructor_ShouldHandleEmptyMessage() {
        PdiServiceException exception = new PdiServiceException("");

        assertEquals("", exception.getMessage());
    }

    @Test
    @DisplayName("Constructor - Should handle null cause")
    void constructor_ShouldHandleNullCause() {
        PdiServiceException exception = new PdiServiceException("Error", (Throwable) null);

        assertEquals("Error", exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    @DisplayName("Exception - Should be instance of Exception")
    void exception_ShouldBeInstanceOfException() {
        PdiServiceException exception = new PdiServiceException("Error");

        assertTrue(exception instanceof Exception);
    }

    @Test
    @DisplayName("Exception - Should be throwable")
    void exception_ShouldBeThrowable() {
        assertThrows(PdiServiceException.class, () -> {
            throw new PdiServiceException("Test exception");
        });
    }

    @Test
    @DisplayName("Exception - Should preserve stack trace")
    void exception_ShouldPreserveStackTrace() {
        PdiServiceException exception = new PdiServiceException("Error");

        assertNotNull(exception.getStackTrace());
        assertTrue(exception.getStackTrace().length > 0);
    }

    @Test
    @DisplayName("Exception - Should preserve cause stack trace")
    void exception_ShouldPreserveCauseStackTrace() {
        RuntimeException cause = new RuntimeException("Root cause");
        PdiServiceException exception = new PdiServiceException("Error", cause);

        assertNotNull(exception.getCause().getStackTrace());
        assertTrue(exception.getCause().getStackTrace().length > 0);
    }

    @Test
    @DisplayName("Constructor - Should handle common PDI error codes")
    void constructor_ShouldHandleCommonPdiErrorCodes() {
        // Invalid CI format
        PdiServiceException invalidCi = new PdiServiceException("Formato de CI inválido", 10001);
        assertEquals(10001, invalidCi.getErrorCode());

        // Person not found
        PdiServiceException notFound = new PdiServiceException("Persona inexistente", 500);
        assertEquals(500, notFound.getErrorCode());
    }

    @Test
    @DisplayName("Constructor with cause - Should chain exceptions properly")
    void constructorWithCause_ShouldChainExceptionsProperly() {
        IllegalArgumentException rootCause = new IllegalArgumentException("Invalid argument");
        RuntimeException middleCause = new RuntimeException("Middle error", rootCause);
        PdiServiceException exception = new PdiServiceException("PDI error", middleCause);

        assertEquals(middleCause, exception.getCause());
        assertEquals(rootCause, exception.getCause().getCause());
    }

    @Test
    @DisplayName("Constructor with error code and cause - Should store both")
    void constructorWithErrorCodeAndCause_ShouldStoreBoth() {
        IOException cause = new IOException("Network error");
        PdiServiceException exception = new PdiServiceException("Connection failed", 503, cause);

        assertEquals("Connection failed", exception.getMessage());
        assertEquals(503, exception.getErrorCode());
        assertEquals(cause, exception.getCause());
        assertEquals("Network error", exception.getCause().getMessage());
    }

    @Test
    @DisplayName("getErrorCode - Should handle Integer max value")
    void getErrorCode_ShouldHandleIntegerMaxValue() {
        PdiServiceException exception = new PdiServiceException("Error", Integer.MAX_VALUE);

        assertEquals(Integer.MAX_VALUE, exception.getErrorCode());
    }

    @Test
    @DisplayName("getErrorCode - Should handle Integer min value")
    void getErrorCode_ShouldHandleIntegerMinValue() {
        PdiServiceException exception = new PdiServiceException("Error", Integer.MIN_VALUE);

        assertEquals(Integer.MIN_VALUE, exception.getErrorCode());
    }

    // Inner class for test - simulating IOException for cause testing
    private static class IOException extends Exception {
        IOException(String message) {
            super(message);
        }
    }
}
