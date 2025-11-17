package grupo12.practico.rest.exceptions;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ErrorResponse Tests")
class ErrorResponseTest {

    @Test
    @DisplayName("Should create ErrorResponse with default constructor")
    void testDefaultConstructor() {
        // Act
        ErrorResponse error = new ErrorResponse();

        // Assert
        assertNotNull(error);
        assertNull(error.getErrorCode());
        assertNull(error.getMessage());
        assertEquals(0, error.getStatus());
    }

    @Test
    @DisplayName("Should create ErrorResponse with parameterized constructor")
    void testParameterizedConstructor() {
        // Act
        ErrorResponse error = new ErrorResponse("TEST_ERROR", "Test message", 400);

        // Assert
        assertEquals("TEST_ERROR", error.getErrorCode());
        assertEquals("Test message", error.getMessage());
        assertEquals(400, error.getStatus());
    }

    @Test
    @DisplayName("Should set and get errorCode")
    void testSetGetErrorCode() {
        // Arrange
        ErrorResponse error = new ErrorResponse();

        // Act
        error.setErrorCode("VALIDATION_ERROR");

        // Assert
        assertEquals("VALIDATION_ERROR", error.getErrorCode());
    }

    @Test
    @DisplayName("Should set and get message")
    void testSetGetMessage() {
        // Arrange
        ErrorResponse error = new ErrorResponse();

        // Act
        error.setMessage("Invalid field value");

        // Assert
        assertEquals("Invalid field value", error.getMessage());
    }

    @Test
    @DisplayName("Should set and get status")
    void testSetGetStatus() {
        // Arrange
        ErrorResponse error = new ErrorResponse();

        // Act
        error.setStatus(500);

        // Assert
        assertEquals(500, error.getStatus());
    }

    @Test
    @DisplayName("Should handle null values")
    void testNullValues() {
        // Arrange
        ErrorResponse error = new ErrorResponse(null, null, 0);

        // Assert
        assertNull(error.getErrorCode());
        assertNull(error.getMessage());
        assertEquals(0, error.getStatus());
    }
}
