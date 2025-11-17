package grupo12.practico.messaging.AccessPolicy.Clinic;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import grupo12.practico.dtos.AccessPolicy.AddClinicAccessPolicyDTO;
import jakarta.validation.ValidationException;

@DisplayName("ClinicAccessPolicyMessageMapper Tests")
class ClinicAccessPolicyMessageMapperTest {

    @Test
    @DisplayName("toMessage - Should convert DTO to pipe-separated message")
    void toMessage_ShouldConvertDTOToPipeSeparatedMessage() {
        // Given
        AddClinicAccessPolicyDTO dto = new AddClinicAccessPolicyDTO();
        dto.setHealthUserCi("12345678");
        dto.setClinicName("Hospital Central");
        dto.setAccessRequestId("REQ-001");

        // When
        String message = ClinicAccessPolicyMessageMapper.toMessage(dto);

        // Then
        assertEquals("12345678|Hospital Central|REQ-001", message);
    }

    @Test
    @DisplayName("toMessage - Should handle null accessRequestId")
    void toMessage_ShouldHandleNullAccessRequestId() {
        // Given
        AddClinicAccessPolicyDTO dto = new AddClinicAccessPolicyDTO();
        dto.setHealthUserCi("12345678");
        dto.setClinicName("Hospital Central");
        dto.setAccessRequestId(null);

        // When
        String message = ClinicAccessPolicyMessageMapper.toMessage(dto);

        // Then
        assertEquals("12345678|Hospital Central|", message);
    }

    @Test
    @DisplayName("toMessage - Should throw exception for null DTO")
    void toMessage_ShouldThrowExceptionForNullDTO() {
        // When & Then
        assertThrows(NullPointerException.class, () -> {
            ClinicAccessPolicyMessageMapper.toMessage(null);
        });
    }

    @Test
    @DisplayName("toMessage - Should throw exception for null healthUserCi")
    void toMessage_ShouldThrowExceptionForNullHealthUserCi() {
        // Given
        AddClinicAccessPolicyDTO dto = new AddClinicAccessPolicyDTO();
        dto.setHealthUserCi(null);
        dto.setClinicName("Hospital Central");

        // When & Then
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            ClinicAccessPolicyMessageMapper.toMessage(dto);
        });
        assertEquals("Field healthUserCi is required", exception.getMessage());
    }

    @Test
    @DisplayName("toMessage - Should throw exception for null clinicName")
    void toMessage_ShouldThrowExceptionForNullClinicName() {
        // Given
        AddClinicAccessPolicyDTO dto = new AddClinicAccessPolicyDTO();
        dto.setHealthUserCi("12345678");
        dto.setClinicName(null);

        // When & Then
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            ClinicAccessPolicyMessageMapper.toMessage(dto);
        });
        assertEquals("Field clinicName is required", exception.getMessage());
    }

    @Test
    @DisplayName("toMessage - Should throw exception when healthUserCi contains pipe")
    void toMessage_ShouldThrowExceptionWhenHealthUserCiContainsPipe() {
        // Given
        AddClinicAccessPolicyDTO dto = new AddClinicAccessPolicyDTO();
        dto.setHealthUserCi("12345|678");
        dto.setClinicName("Hospital Central");

        // When & Then
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            ClinicAccessPolicyMessageMapper.toMessage(dto);
        });
        assertTrue(exception.getMessage().contains("healthUserCi must not contain '|'"));
    }

    @Test
    @DisplayName("toMessage - Should throw exception when clinicName contains pipe")
    void toMessage_ShouldThrowExceptionWhenClinicNameContainsPipe() {
        // Given
        AddClinicAccessPolicyDTO dto = new AddClinicAccessPolicyDTO();
        dto.setHealthUserCi("12345678");
        dto.setClinicName("Hospital|Central");

        // When & Then
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            ClinicAccessPolicyMessageMapper.toMessage(dto);
        });
        assertTrue(exception.getMessage().contains("clinicName must not contain '|'"));
    }

    @Test
    @DisplayName("toMessage - Should throw exception when accessRequestId contains pipe")
    void toMessage_ShouldThrowExceptionWhenAccessRequestIdContainsPipe() {
        // Given
        AddClinicAccessPolicyDTO dto = new AddClinicAccessPolicyDTO();
        dto.setHealthUserCi("12345678");
        dto.setClinicName("Hospital Central");
        dto.setAccessRequestId("REQ|001");

        // When & Then
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            ClinicAccessPolicyMessageMapper.toMessage(dto);
        });
        assertTrue(exception.getMessage().contains("accessRequestId must not contain '|'"));
    }

    @Test
    @DisplayName("fromMessage - Should convert pipe-separated message to DTO")
    void fromMessage_ShouldConvertPipeSeparatedMessageToDTO() {
        // Given
        String message = "12345678|Hospital Central|REQ-001";

        // When
        AddClinicAccessPolicyDTO dto = ClinicAccessPolicyMessageMapper.fromMessage(message);

        // Then
        assertNotNull(dto);
        assertEquals("12345678", dto.getHealthUserCi());
        assertEquals("Hospital Central", dto.getClinicName());
        assertEquals("REQ-001", dto.getAccessRequestId());
    }

    @Test
    @DisplayName("fromMessage - Should handle empty accessRequestId")
    void fromMessage_ShouldHandleEmptyAccessRequestId() {
        // Given
        String message = "12345678|Hospital Central|";

        // When
        AddClinicAccessPolicyDTO dto = ClinicAccessPolicyMessageMapper.fromMessage(message);

        // Then
        assertNotNull(dto);
        assertEquals("12345678", dto.getHealthUserCi());
        assertEquals("Hospital Central", dto.getClinicName());
        assertNull(dto.getAccessRequestId());
    }

    @Test
    @DisplayName("fromMessage - Should throw exception for null message")
    void fromMessage_ShouldThrowExceptionForNullMessage() {
        // When & Then
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            ClinicAccessPolicyMessageMapper.fromMessage(null);
        });
        assertEquals("Message payload must not be empty", exception.getMessage());
    }

    @Test
    @DisplayName("fromMessage - Should throw exception for empty message")
    void fromMessage_ShouldThrowExceptionForEmptyMessage() {
        // When & Then
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            ClinicAccessPolicyMessageMapper.fromMessage("");
        });
        assertEquals("Message payload must not be empty", exception.getMessage());
    }

    @Test
    @DisplayName("fromMessage - Should throw exception for blank message")
    void fromMessage_ShouldThrowExceptionForBlankMessage() {
        // When & Then
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            ClinicAccessPolicyMessageMapper.fromMessage("   ");
        });
        assertEquals("Message payload must not be empty", exception.getMessage());
    }

    @Test
    @DisplayName("fromMessage - Should throw exception for too few fields")
    void fromMessage_ShouldThrowExceptionForTooFewFields() {
        // Given
        String message = "12345678|Hospital Central";

        // When & Then
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            ClinicAccessPolicyMessageMapper.fromMessage(message);
        });
        assertTrue(exception.getMessage().contains("Unexpected field count"));
    }

    @Test
    @DisplayName("fromMessage - Should throw exception for too many fields")
    void fromMessage_ShouldThrowExceptionForTooManyFields() {
        // Given
        String message = "12345678|Hospital Central|REQ-001|extra";

        // When & Then
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            ClinicAccessPolicyMessageMapper.fromMessage(message);
        });
        assertTrue(exception.getMessage().contains("Unexpected field count"));
    }

    @Test
    @DisplayName("fromMessage - Should throw exception for empty healthUserCi")
    void fromMessage_ShouldThrowExceptionForEmptyHealthUserCi() {
        // Given
        String message = "|Hospital Central|REQ-001";

        // When & Then
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            ClinicAccessPolicyMessageMapper.fromMessage(message);
        });
        assertTrue(exception.getMessage().contains("Field healthUserCi is required"));
    }

    @Test
    @DisplayName("fromMessage - Should throw exception for empty clinicName")
    void fromMessage_ShouldThrowExceptionForEmptyClinicName() {
        // Given
        String message = "12345678||REQ-001";

        // When & Then
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            ClinicAccessPolicyMessageMapper.fromMessage(message);
        });
        assertTrue(exception.getMessage().contains("Field clinicName is required"));
    }

    @Test
    @DisplayName("fromMessage - Should handle whitespace in fields")
    void fromMessage_ShouldHandleWhitespaceInFields() {
        // Given
        String message = " 12345678 | Hospital Central | REQ-001 ";

        // When
        AddClinicAccessPolicyDTO dto = ClinicAccessPolicyMessageMapper.fromMessage(message);

        // Then
        assertNotNull(dto);
        assertEquals("12345678", dto.getHealthUserCi());
        assertEquals("Hospital Central", dto.getClinicName());
        assertEquals("REQ-001", dto.getAccessRequestId());
    }
}