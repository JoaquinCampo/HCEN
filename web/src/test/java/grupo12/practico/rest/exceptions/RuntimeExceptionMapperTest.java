package grupo12.practico.rest.exceptions;

import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("RuntimeExceptionMapper Tests")
class RuntimeExceptionMapperTest {

    private RuntimeExceptionMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new RuntimeExceptionMapper();
    }

    @Test
    @DisplayName("Should map RuntimeException to 500 internal server error response")
    void testToResponse() {
        // Arrange
        RuntimeException exception = new RuntimeException("Test error message");

        // Act
        Response response = mapper.toResponse(exception);

        // Assert
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
        assertNotNull(response.getEntity());
        assertTrue(response.getEntity() instanceof ErrorResponse);

        ErrorResponse errorResponse = (ErrorResponse) response.getEntity();
        assertEquals("INTERNAL_ERROR", errorResponse.getErrorCode());
        assertTrue(errorResponse.getMessage().contains("Test error message"));
        assertEquals(500, errorResponse.getStatus());
    }

    @Test
    @DisplayName("Should handle null pointer exception")
    void testToResponse_NullPointerException() {
        // Arrange
        NullPointerException exception = new NullPointerException("Null value encountered");

        // Act
        Response response = mapper.toResponse(exception);

        // Assert
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
        assertNotNull(response.getEntity());

        ErrorResponse errorResponse = (ErrorResponse) response.getEntity();
        assertEquals("INTERNAL_ERROR", errorResponse.getErrorCode());
        assertTrue(errorResponse.getMessage().contains("Null value encountered"));
    }

    @Test
    @DisplayName("Should handle exception with null message")
    void testToResponse_NullMessage() {
        // Arrange
        RuntimeException exception = new RuntimeException((String) null);

        // Act
        Response response = mapper.toResponse(exception);

        // Assert
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
        assertNotNull(response.getEntity());

        ErrorResponse errorResponse = (ErrorResponse) response.getEntity();
        assertEquals("INTERNAL_ERROR", errorResponse.getErrorCode());
        assertTrue(errorResponse.getMessage().contains("null"));
    }
}
