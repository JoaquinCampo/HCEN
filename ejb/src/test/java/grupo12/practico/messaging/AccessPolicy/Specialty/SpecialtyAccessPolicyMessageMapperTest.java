package grupo12.practico.messaging.AccessPolicy.Specialty;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import grupo12.practico.dtos.AccessPolicy.AddSpecialtyAccessPolicyDTO;
import jakarta.validation.ValidationException;

@DisplayName("SpecialtyAccessPolicyMessageMapper Tests")
class SpecialtyAccessPolicyMessageMapperTest {

    @Test
    @DisplayName("toMessage - Should convert DTO to pipe-separated message")
    void toMessage_ShouldConvertDTOToPipeSeparatedMessage() {
        // Given
        AddSpecialtyAccessPolicyDTO dto = new AddSpecialtyAccessPolicyDTO();
        dto.setHealthUserCi("12345678");
        dto.setSpecialtyName("Cardiology");
        dto.setAccessRequestId("REQ-001");

        // When
        String message = SpecialtyAccessPolicyMessageMapper.toMessage(dto);

        // Then
        assertEquals("12345678|Cardiology|REQ-001", message);
    }

    @Test
    @DisplayName("toMessage - Should handle null accessRequestId")
    void toMessage_ShouldHandleNullAccessRequestId() {
        // Given
        AddSpecialtyAccessPolicyDTO dto = new AddSpecialtyAccessPolicyDTO();
        dto.setHealthUserCi("12345678");
        dto.setSpecialtyName("Cardiology");
        dto.setAccessRequestId(null);

        // When
        String message = SpecialtyAccessPolicyMessageMapper.toMessage(dto);

        // Then
        assertEquals("12345678|Cardiology|", message);
    }

    @Test
    @DisplayName("toMessage - Should throw exception for null DTO")
    void toMessage_ShouldThrowExceptionForNullDTO() {
        // When & Then
        assertThrows(NullPointerException.class, () -> {
            SpecialtyAccessPolicyMessageMapper.toMessage(null);
        });
    }

    @Test
    @DisplayName("toMessage - Should throw exception for null healthUserCi")
    void toMessage_ShouldThrowExceptionForNullHealthUserCi() {
        // Given
        AddSpecialtyAccessPolicyDTO dto = new AddSpecialtyAccessPolicyDTO();
        dto.setHealthUserCi(null);
        dto.setSpecialtyName("Cardiology");

        // When & Then
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            SpecialtyAccessPolicyMessageMapper.toMessage(dto);
        });
        assertEquals("Field healthUserCi is required", exception.getMessage());
    }

    @Test
    @DisplayName("toMessage - Should throw exception for null specialtyName")
    void toMessage_ShouldThrowExceptionForNullSpecialtyName() {
        // Given
        AddSpecialtyAccessPolicyDTO dto = new AddSpecialtyAccessPolicyDTO();
        dto.setHealthUserCi("12345678");
        dto.setSpecialtyName(null);

        // When & Then
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            SpecialtyAccessPolicyMessageMapper.toMessage(dto);
        });
        assertEquals("Field specialtyName is required", exception.getMessage());
    }

    @Test
    @DisplayName("toMessage - Should throw exception when healthUserCi contains pipe")
    void toMessage_ShouldThrowExceptionWhenHealthUserCiContainsPipe() {
        // Given
        AddSpecialtyAccessPolicyDTO dto = new AddSpecialtyAccessPolicyDTO();
        dto.setHealthUserCi("12345|678");
        dto.setSpecialtyName("Cardiology");

        // When & Then
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            SpecialtyAccessPolicyMessageMapper.toMessage(dto);
        });
        assertTrue(exception.getMessage().contains("healthUserCi must not contain '|'"));
    }

    @Test
    @DisplayName("toMessage - Should throw exception when specialtyName contains pipe")
    void toMessage_ShouldThrowExceptionWhenSpecialtyNameContainsPipe() {
        // Given
        AddSpecialtyAccessPolicyDTO dto = new AddSpecialtyAccessPolicyDTO();
        dto.setHealthUserCi("12345678");
        dto.setSpecialtyName("Cardio|logy");

        // When & Then
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            SpecialtyAccessPolicyMessageMapper.toMessage(dto);
        });
        assertTrue(exception.getMessage().contains("specialtyName must not contain '|'"));
    }

    @Test
    @DisplayName("toMessage - Should throw exception when accessRequestId contains pipe")
    void toMessage_ShouldThrowExceptionWhenAccessRequestIdContainsPipe() {
        // Given
        AddSpecialtyAccessPolicyDTO dto = new AddSpecialtyAccessPolicyDTO();
        dto.setHealthUserCi("12345678");
        dto.setSpecialtyName("Cardiology");
        dto.setAccessRequestId("REQ|001");

        // When & Then
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            SpecialtyAccessPolicyMessageMapper.toMessage(dto);
        });
        assertTrue(exception.getMessage().contains("accessRequestId must not contain '|'"));
    }

    @Test
    @DisplayName("fromMessage - Should convert pipe-separated message to DTO")
    void fromMessage_ShouldConvertPipeSeparatedMessageToDTO() {
        // Given
        String message = "12345678|Cardiology|REQ-001";

        // When
        AddSpecialtyAccessPolicyDTO dto = SpecialtyAccessPolicyMessageMapper.fromMessage(message);

        // Then
        assertNotNull(dto);
        assertEquals("12345678", dto.getHealthUserCi());
        assertEquals("Cardiology", dto.getSpecialtyName());
        assertEquals("REQ-001", dto.getAccessRequestId());
    }

    @Test
    @DisplayName("fromMessage - Should handle empty accessRequestId")
    void fromMessage_ShouldHandleEmptyAccessRequestId() {
        // Given
        String message = "12345678|Cardiology|";

        // When
        AddSpecialtyAccessPolicyDTO dto = SpecialtyAccessPolicyMessageMapper.fromMessage(message);

        // Then
        assertNotNull(dto);
        assertEquals("12345678", dto.getHealthUserCi());
        assertEquals("Cardiology", dto.getSpecialtyName());
        assertNull(dto.getAccessRequestId());
    }

    @Test
    @DisplayName("fromMessage - Should throw exception for null message")
    void fromMessage_ShouldThrowExceptionForNullMessage() {
        // When & Then
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            SpecialtyAccessPolicyMessageMapper.fromMessage(null);
        });
        assertEquals("Message payload must not be empty", exception.getMessage());
    }

    @Test
    @DisplayName("fromMessage - Should throw exception for empty message")
    void fromMessage_ShouldThrowExceptionForEmptyMessage() {
        // When & Then
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            SpecialtyAccessPolicyMessageMapper.fromMessage("");
        });
        assertEquals("Message payload must not be empty", exception.getMessage());
    }

    @Test
    @DisplayName("fromMessage - Should throw exception for blank message")
    void fromMessage_ShouldThrowExceptionForBlankMessage() {
        // Given
        String message = "   ";

        // When & Then
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            SpecialtyAccessPolicyMessageMapper.fromMessage(message);
        });
        assertEquals("Message payload must not be empty", exception.getMessage());
    }

    @Test
    @DisplayName("fromMessage - Should throw exception for too few fields")
    void fromMessage_ShouldThrowExceptionForTooFewFields() {
        // Given
        String message = "12345678|Cardiology";

        // When & Then
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            SpecialtyAccessPolicyMessageMapper.fromMessage(message);
        });
        assertTrue(exception.getMessage().contains("Unexpected field count"));
    }

    @Test
    @DisplayName("fromMessage - Should throw exception for too many fields")
    void fromMessage_ShouldThrowExceptionForTooManyFields() {
        // Given
        String message = "12345678|Cardiology|REQ-001|extra";

        // When & Then
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            SpecialtyAccessPolicyMessageMapper.fromMessage(message);
        });
        assertTrue(exception.getMessage().contains("Unexpected field count"));
    }

    @Test
    @DisplayName("fromMessage - Should throw exception for empty healthUserCi")
    void fromMessage_ShouldThrowExceptionForEmptyHealthUserCi() {
        // Given
        String message = "|Cardiology|REQ-001";

        // When & Then
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            SpecialtyAccessPolicyMessageMapper.fromMessage(message);
        });
        assertTrue(exception.getMessage().contains("Field healthUserCi is required"));
    }

    @Test
    @DisplayName("fromMessage - Should throw exception for empty specialtyName")
    void fromMessage_ShouldThrowExceptionForEmptySpecialtyName() {
        // Given
        String message = "12345678||REQ-001";

        // When & Then
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            SpecialtyAccessPolicyMessageMapper.fromMessage(message);
        });
        assertTrue(exception.getMessage().contains("Field specialtyName is required"));
    }

    @Test
    @DisplayName("fromMessage - Should handle whitespace in fields")
    void fromMessage_ShouldHandleWhitespaceInFields() {
        // Given
        String message = " 12345678 | Cardiology | REQ-001 ";

        // When
        AddSpecialtyAccessPolicyDTO dto = SpecialtyAccessPolicyMessageMapper.fromMessage(message);

        // Then
        assertNotNull(dto);
        assertEquals("12345678", dto.getHealthUserCi());
        assertEquals("Cardiology", dto.getSpecialtyName());
        assertEquals(" REQ-001 ", dto.getAccessRequestId());
    }
}