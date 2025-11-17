package grupo12.practico.rest.exceptions;

import jakarta.validation.ValidationException;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ValidationExceptionMapper Tests")
class ValidationExceptionMapperTest {

    private ValidationExceptionMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ValidationExceptionMapper();
    }

    @Test
    @DisplayName("Should map ValidationException to 400 bad request response")
    void testToResponse() {
        // Arrange
        ValidationException exception = new ValidationException("Invalid input data");

        // Act
        Response response = mapper.toResponse(exception);

        // Assert
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertNotNull(response.getEntity());
        assertTrue(response.getEntity() instanceof ErrorResponse);

        ErrorResponse errorResponse = (ErrorResponse) response.getEntity();
        assertEquals("VALIDATION_ERROR", errorResponse.getErrorCode());
        assertEquals("Invalid input data", errorResponse.getMessage());
        assertEquals(400, errorResponse.getStatus());
    }

    @Test
    @DisplayName("Should handle validation exception with detailed message")
    void testToResponse_DetailedMessage() {
        // Arrange
        ValidationException exception = new ValidationException("Field 'email' must be a valid email address");

        // Act
        Response response = mapper.toResponse(exception);

        // Assert
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());

        ErrorResponse errorResponse = (ErrorResponse) response.getEntity();
        assertEquals("VALIDATION_ERROR", errorResponse.getErrorCode());
        assertEquals("Field 'email' must be a valid email address", errorResponse.getMessage());
    }

    @Test
    @DisplayName("Should handle validation exception with null message")
    void testToResponse_NullMessage() {
        // Arrange
        ValidationException exception = new ValidationException((String) null);

        // Act
        Response response = mapper.toResponse(exception);

        // Assert
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());

        ErrorResponse errorResponse = (ErrorResponse) response.getEntity();
        assertEquals("VALIDATION_ERROR", errorResponse.getErrorCode());
        assertNull(errorResponse.getMessage());
    }
}
