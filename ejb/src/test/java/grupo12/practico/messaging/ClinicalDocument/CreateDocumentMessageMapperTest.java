package grupo12.practico.messaging.ClinicalDocument;

import grupo12.practico.dtos.ClinicalDocument.AddClinicalDocumentDTO;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CreateDocumentMessageMapper Tests")
class CreateDocumentMessageMapperTest {

    @Test
    @DisplayName("toMessage - Should convert DTO to JSON message")
    void toMessage_ShouldConvertDTOToJSONMessage() {
        AddClinicalDocumentDTO dto = new AddClinicalDocumentDTO();
        dto.setTitle("Test Document");
        dto.setDescription("Test Description");
        dto.setContent("Test Content");
        dto.setContentType("application/pdf");
        dto.setContentUrl("http://example.com/doc.pdf");
        dto.setHealthWorkerCi("12345678");
        dto.setHealthUserCi("87654321");
        dto.setClinicName("Test Clinic");
        dto.setProviderName("Test Provider");
        dto.setSpecialtyNames(Arrays.asList("Cardiology", "Internal Medicine"));

        String message = CreateDocumentMessageMapper.toMessage(dto);

        assertNotNull(message);
        assertTrue(message.contains("\"title\":\"Test Document\""));
        assertTrue(message.contains("\"description\":\"Test Description\""));
        assertTrue(message.contains("\"content\":\"Test Content\""));
        assertTrue(message.contains("\"contentType\":\"application/pdf\""));
        assertTrue(message.contains("\"contentUrl\":\"http://example.com/doc.pdf\""));
        assertTrue(message.contains("\"healthWorkerCi\":\"12345678\""));
        assertTrue(message.contains("\"healthUserCi\":\"87654321\""));
        assertTrue(message.contains("\"clinicName\":\"Test Clinic\""));
        assertTrue(message.contains("\"providerName\":\"Test Provider\""));
        assertTrue(message.contains("\"specialtyNames\":[\"Cardiology\",\"Internal Medicine\"]"));
    }

    @Test
    @DisplayName("toMessage - Should handle null values")
    void toMessage_ShouldHandleNullValues() {
        AddClinicalDocumentDTO dto = new AddClinicalDocumentDTO();
        dto.setHealthWorkerCi("12345678");
        dto.setHealthUserCi("87654321");
        // Leave other fields null

        String message = CreateDocumentMessageMapper.toMessage(dto);

        assertNotNull(message);
        assertTrue(message.contains("\"title\":\"\""));
        assertTrue(message.contains("\"description\":\"\""));
        assertTrue(message.contains("\"content\":\"\""));
        assertTrue(message.contains("\"contentType\":\"\""));
        assertTrue(message.contains("\"contentUrl\":\"\""));
        assertTrue(message.contains("\"clinicName\":\"\""));
        assertTrue(message.contains("\"providerName\":\"\""));
        assertTrue(message.contains("\"specialtyNames\":[]"));
    }

    @Test
    @DisplayName("toMessage - Should handle null specialty names")
    void toMessage_ShouldHandleNullSpecialtyNames() {
        AddClinicalDocumentDTO dto = new AddClinicalDocumentDTO();
        dto.setHealthWorkerCi("12345678");
        dto.setHealthUserCi("87654321");
        dto.setSpecialtyNames(null);

        String message = CreateDocumentMessageMapper.toMessage(dto);

        assertNotNull(message);
        assertTrue(message.contains("\"specialtyNames\":[]"));
    }

    @Test
    @DisplayName("toMessage - Should throw exception for null DTO")
    void toMessage_ShouldThrowExceptionForNullDTO() {
        assertThrows(NullPointerException.class, () -> {
            CreateDocumentMessageMapper.toMessage(null);
        });
    }

    @Test
    @DisplayName("fromMessage - Should convert JSON message to DTO")
    void fromMessage_ShouldConvertJSONMessageToDTO() {
        String jsonMessage = """
                {
                    "title": "Test Document",
                    "description": "Test Description",
                    "content": "Test Content",
                    "contentType": "application/pdf",
                    "contentUrl": "http://example.com/doc.pdf",
                    "healthWorkerCi": "12345678",
                    "healthUserCi": "87654321",
                    "clinicName": "Test Clinic",
                    "providerName": "Test Provider",
                    "specialtyNames": ["Cardiology", "Internal Medicine"]
                }
                """;

        AddClinicalDocumentDTO dto = CreateDocumentMessageMapper.fromMessage(jsonMessage);

        assertNotNull(dto);
        assertEquals("Test Document", dto.getTitle());
        assertEquals("Test Description", dto.getDescription());
        assertEquals("Test Content", dto.getContent());
        assertEquals("application/pdf", dto.getContentType());
        assertEquals("http://example.com/doc.pdf", dto.getContentUrl());
        assertEquals("12345678", dto.getHealthWorkerCi());
        assertEquals("87654321", dto.getHealthUserCi());
        assertEquals("Test Clinic", dto.getClinicName());
        assertEquals("Test Provider", dto.getProviderName());
        assertEquals(Arrays.asList("Cardiology", "Internal Medicine"), dto.getSpecialtyNames());
    }

    @Test
    @DisplayName("fromMessage - Should handle empty specialty names array")
    void fromMessage_ShouldHandleEmptySpecialtyNamesArray() {
        String jsonMessage = """
                {
                    "title": "Test Document",
                    "healthWorkerCi": "12345678",
                    "healthUserCi": "87654321",
                    "specialtyNames": []
                }
                """;

        AddClinicalDocumentDTO dto = CreateDocumentMessageMapper.fromMessage(jsonMessage);

        assertNotNull(dto);
        assertEquals(Collections.emptyList(), dto.getSpecialtyNames());
    }

    @Test
    @DisplayName("fromMessage - Should handle missing specialty names")
    void fromMessage_ShouldHandleMissingSpecialtyNames() {
        String jsonMessage = """
                {
                    "title": "Test Document",
                    "healthWorkerCi": "12345678",
                    "healthUserCi": "87654321"
                }
                """;

        AddClinicalDocumentDTO dto = CreateDocumentMessageMapper.fromMessage(jsonMessage);

        assertNotNull(dto);
        assertEquals(Collections.emptyList(), dto.getSpecialtyNames());
    }

    @Test
    @DisplayName("fromMessage - Should throw exception for null message")
    void fromMessage_ShouldThrowExceptionForNullMessage() {
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            CreateDocumentMessageMapper.fromMessage(null);
        });
        assertEquals("Message payload must not be empty", exception.getMessage());
    }

    @Test
    @DisplayName("fromMessage - Should throw exception for empty message")
    void fromMessage_ShouldThrowExceptionForEmptyMessage() {
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            CreateDocumentMessageMapper.fromMessage("");
        });
        assertEquals("Message payload must not be empty", exception.getMessage());
    }

    @Test
    @DisplayName("fromMessage - Should throw exception for blank message")
    void fromMessage_ShouldThrowExceptionForBlankMessage() {
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            CreateDocumentMessageMapper.fromMessage("   ");
        });
        assertEquals("Message payload must not be empty", exception.getMessage());
    }

    @Test
    @DisplayName("fromMessage - Should throw exception for missing required field")
    void fromMessage_ShouldThrowExceptionForMissingRequiredField() {
        String jsonMessage = """
                {
                    "title": "Test Document",
                    "healthUserCi": "87654321"
                }
                """;

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            CreateDocumentMessageMapper.fromMessage(jsonMessage);
        });
        assertEquals("Invalid JSON payload: Field healthWorkerCi is required", exception.getMessage());
    }

    @Test
    @DisplayName("fromMessage - Should throw exception for empty required field")
    void fromMessage_ShouldThrowExceptionForEmptyRequiredField() {
        String jsonMessage = """
                {
                    "title": "Test Document",
                    "healthWorkerCi": "",
                    "healthUserCi": "87654321"
                }
                """;

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            CreateDocumentMessageMapper.fromMessage(jsonMessage);
        });
        assertEquals("Invalid JSON payload: Field healthWorkerCi is required", exception.getMessage());
    }

    @Test
    @DisplayName("fromMessage - Should throw exception for invalid JSON")
    void fromMessage_ShouldThrowExceptionForInvalidJSON() {
        String invalidJson = "{ invalid json }";

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            CreateDocumentMessageMapper.fromMessage(invalidJson);
        });
        assertTrue(exception.getMessage().startsWith("Invalid JSON payload:"));
    }

    @Test
    @DisplayName("fromMessage - Should trim whitespace from string fields")
    void fromMessage_ShouldTrimWhitespaceFromStringFields() {
        String jsonMessage = """
                {
                    "title": "  Test Document  ",
                    "description": "  Test Description  ",
                    "healthWorkerCi": "  12345678  ",
                    "healthUserCi": "  87654321  ",
                    "clinicName": "  Test Clinic  "
                }
                """;

        AddClinicalDocumentDTO dto = CreateDocumentMessageMapper.fromMessage(jsonMessage);

        assertNotNull(dto);
        assertEquals("Test Document", dto.getTitle());
        assertEquals("Test Description", dto.getDescription());
        assertEquals("12345678", dto.getHealthWorkerCi());
        assertEquals("87654321", dto.getHealthUserCi());
        assertEquals("Test Clinic", dto.getClinicName());
    }

    @Test
    @DisplayName("fromMessage - Should convert empty strings to null")
    void fromMessage_ShouldConvertEmptyStringsToNull() {
        String jsonMessage = """
                {
                    "title": "",
                    "description": "   ",
                    "healthWorkerCi": "12345678",
                    "healthUserCi": "87654321"
                }
                """;

        AddClinicalDocumentDTO dto = CreateDocumentMessageMapper.fromMessage(jsonMessage);

        assertNotNull(dto);
        assertNull(dto.getTitle());
        assertNull(dto.getDescription());
    }
}