package grupo12.practico.messaging.AccessPolicy.HealthWorker;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import grupo12.practico.dtos.AccessPolicy.AddHealthWorkerAccessPolicyDTO;
import jakarta.validation.ValidationException;

@DisplayName("HealthWorkerAccessPolicyMessageMapper Tests")
class HealthWorkerAccessPolicyMessageMapperTest {

    @Test
    @DisplayName("toMessage - Should convert DTO to pipe-separated message")
    void toMessage_ShouldConvertDTOToPipeSeparatedMessage() {
        // Given
        AddHealthWorkerAccessPolicyDTO dto = new AddHealthWorkerAccessPolicyDTO();
        dto.setHealthUserCi("12345678");
        dto.setHealthWorkerCi("87654321");
        dto.setClinicName("Hospital Central");
        dto.setAccessRequestId("REQ-001");

        // When
        String message = HealthWorkerAccessPolicyMessageMapper.toMessage(dto);

        // Then
        assertEquals("12345678|87654321|Hospital Central|REQ-001", message);
    }

    @Test
    @DisplayName("toMessage - Should handle null accessRequestId")
    void toMessage_ShouldHandleNullAccessRequestId() {
        // Given
        AddHealthWorkerAccessPolicyDTO dto = new AddHealthWorkerAccessPolicyDTO();
        dto.setHealthUserCi("12345678");
        dto.setHealthWorkerCi("87654321");
        dto.setClinicName("Hospital Central");
        dto.setAccessRequestId(null);

        // When
        String message = HealthWorkerAccessPolicyMessageMapper.toMessage(dto);

        // Then
        assertEquals("12345678|87654321|Hospital Central|", message);
    }

    @Test
    @DisplayName("toMessage - Should throw exception for null DTO")
    void toMessage_ShouldThrowExceptionForNullDTO() {
        // When & Then
        assertThrows(NullPointerException.class, () -> {
            HealthWorkerAccessPolicyMessageMapper.toMessage(null);
        });
    }

    @Test
    @DisplayName("toMessage - Should throw exception for null healthUserCi")
    void toMessage_ShouldThrowExceptionForNullHealthUserCi() {
        // Given
        AddHealthWorkerAccessPolicyDTO dto = new AddHealthWorkerAccessPolicyDTO();
        dto.setHealthUserCi(null);
        dto.setHealthWorkerCi("87654321");
        dto.setClinicName("Hospital Central");

        // When & Then
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            HealthWorkerAccessPolicyMessageMapper.toMessage(dto);
        });
        assertEquals("Field healthUserCi is required", exception.getMessage());
    }

    @Test
    @DisplayName("toMessage - Should throw exception for null healthWorkerCi")
    void toMessage_ShouldThrowExceptionForNullHealthWorkerCi() {
        // Given
        AddHealthWorkerAccessPolicyDTO dto = new AddHealthWorkerAccessPolicyDTO();
        dto.setHealthUserCi("12345678");
        dto.setHealthWorkerCi(null);
        dto.setClinicName("Hospital Central");

        // When & Then
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            HealthWorkerAccessPolicyMessageMapper.toMessage(dto);
        });
        assertEquals("Field healthWorkerCi is required", exception.getMessage());
    }

    @Test
    @DisplayName("toMessage - Should throw exception for null clinicName")
    void toMessage_ShouldThrowExceptionForNullClinicName() {
        // Given
        AddHealthWorkerAccessPolicyDTO dto = new AddHealthWorkerAccessPolicyDTO();
        dto.setHealthUserCi("12345678");
        dto.setHealthWorkerCi("87654321");
        dto.setClinicName(null);

        // When & Then
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            HealthWorkerAccessPolicyMessageMapper.toMessage(dto);
        });
        assertEquals("Field clinicName is required", exception.getMessage());
    }

    @Test
    @DisplayName("toMessage - Should throw exception when healthUserCi contains pipe")
    void toMessage_ShouldThrowExceptionWhenHealthUserCiContainsPipe() {
        // Given
        AddHealthWorkerAccessPolicyDTO dto = new AddHealthWorkerAccessPolicyDTO();
        dto.setHealthUserCi("12345|678");
        dto.setHealthWorkerCi("87654321");
        dto.setClinicName("Hospital Central");

        // When & Then
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            HealthWorkerAccessPolicyMessageMapper.toMessage(dto);
        });
        assertTrue(exception.getMessage().contains("healthUserCi must not contain '|'"));
    }

    @Test
    @DisplayName("toMessage - Should throw exception when healthWorkerCi contains pipe")
    void toMessage_ShouldThrowExceptionWhenHealthWorkerCiContainsPipe() {
        // Given
        AddHealthWorkerAccessPolicyDTO dto = new AddHealthWorkerAccessPolicyDTO();
        dto.setHealthUserCi("12345678");
        dto.setHealthWorkerCi("87654|321");
        dto.setClinicName("Hospital Central");

        // When & Then
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            HealthWorkerAccessPolicyMessageMapper.toMessage(dto);
        });
        assertTrue(exception.getMessage().contains("healthWorkerCi must not contain '|'"));
    }

    @Test
    @DisplayName("toMessage - Should throw exception when clinicName contains pipe")
    void toMessage_ShouldThrowExceptionWhenClinicNameContainsPipe() {
        // Given
        AddHealthWorkerAccessPolicyDTO dto = new AddHealthWorkerAccessPolicyDTO();
        dto.setHealthUserCi("12345678");
        dto.setHealthWorkerCi("87654321");
        dto.setClinicName("Hospital|Central");

        // When & Then
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            HealthWorkerAccessPolicyMessageMapper.toMessage(dto);
        });
        assertTrue(exception.getMessage().contains("clinicName must not contain '|'"));
    }

    @Test
    @DisplayName("toMessage - Should throw exception when accessRequestId contains pipe")
    void toMessage_ShouldThrowExceptionWhenAccessRequestIdContainsPipe() {
        // Given
        AddHealthWorkerAccessPolicyDTO dto = new AddHealthWorkerAccessPolicyDTO();
        dto.setHealthUserCi("12345678");
        dto.setHealthWorkerCi("87654321");
        dto.setClinicName("Hospital Central");
        dto.setAccessRequestId("REQ|001");

        // When & Then
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            HealthWorkerAccessPolicyMessageMapper.toMessage(dto);
        });
        assertTrue(exception.getMessage().contains("accessRequestId must not contain '|'"));
    }

    @Test
    @DisplayName("fromMessage - Should convert pipe-separated message to DTO")
    void fromMessage_ShouldConvertPipeSeparatedMessageToDTO() {
        // Given
        String message = "12345678|87654321|Hospital Central|REQ-001";

        // When
        AddHealthWorkerAccessPolicyDTO dto = HealthWorkerAccessPolicyMessageMapper.fromMessage(message);

        // Then
        assertNotNull(dto);
        assertEquals("12345678", dto.getHealthUserCi());
        assertEquals("87654321", dto.getHealthWorkerCi());
        assertEquals("Hospital Central", dto.getClinicName());
        assertEquals("REQ-001", dto.getAccessRequestId());
    }

    @Test
    @DisplayName("fromMessage - Should handle empty accessRequestId")
    void fromMessage_ShouldHandleEmptyAccessRequestId() {
        // Given
        String message = "12345678|87654321|Hospital Central|";

        // When
        AddHealthWorkerAccessPolicyDTO dto = HealthWorkerAccessPolicyMessageMapper.fromMessage(message);

        // Then
        assertNotNull(dto);
        assertEquals("12345678", dto.getHealthUserCi());
        assertEquals("87654321", dto.getHealthWorkerCi());
        assertEquals("Hospital Central", dto.getClinicName());
        assertNull(dto.getAccessRequestId());
    }

    @Test
    @DisplayName("fromMessage - Should throw exception for null message")
    void fromMessage_ShouldThrowExceptionForNullMessage() {
        // When & Then
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            HealthWorkerAccessPolicyMessageMapper.fromMessage(null);
        });
        assertEquals("Message payload must not be empty", exception.getMessage());
    }

    @Test
    @DisplayName("fromMessage - Should throw exception for empty message")
    void fromMessage_ShouldThrowExceptionForEmptyMessage() {
        // When & Then
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            HealthWorkerAccessPolicyMessageMapper.fromMessage("");
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
            HealthWorkerAccessPolicyMessageMapper.fromMessage(message);
        });
        assertEquals("Message payload must not be empty", exception.getMessage());
    }

    @Test
    @DisplayName("fromMessage - Should throw exception for too few fields")
    void fromMessage_ShouldThrowExceptionForTooFewFields() {
        // Given
        String message = "12345678|87654321|Hospital Central";

        // When & Then
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            HealthWorkerAccessPolicyMessageMapper.fromMessage(message);
        });
        assertTrue(exception.getMessage().contains("Unexpected field count"));
    }

    @Test
    @DisplayName("fromMessage - Should throw exception for too many fields")
    void fromMessage_ShouldThrowExceptionForTooManyFields() {
        // Given
        String message = "12345678|87654321|Hospital Central|REQ-001|extra";

        // When & Then
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            HealthWorkerAccessPolicyMessageMapper.fromMessage(message);
        });
        assertTrue(exception.getMessage().contains("Unexpected field count"));
    }

    @Test
    @DisplayName("fromMessage - Should throw exception for empty healthUserCi")
    void fromMessage_ShouldThrowExceptionForEmptyHealthUserCi() {
        // Given
        String message = "|87654321|Hospital Central|REQ-001";

        // When & Then
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            HealthWorkerAccessPolicyMessageMapper.fromMessage(message);
        });
        assertTrue(exception.getMessage().contains("Field healthUserCi is required"));
    }

    @Test
    @DisplayName("fromMessage - Should throw exception for empty healthWorkerCi")
    void fromMessage_ShouldThrowExceptionForEmptyHealthWorkerCi() {
        // Given
        String message = "12345678||Hospital Central|REQ-001";

        // When & Then
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            HealthWorkerAccessPolicyMessageMapper.fromMessage(message);
        });
        assertTrue(exception.getMessage().contains("Field healthWorkerCi is required"));
    }

    @Test
    @DisplayName("fromMessage - Should throw exception for empty clinicName")
    void fromMessage_ShouldThrowExceptionForEmptyClinicName() {
        // Given
        String message = "12345678|87654321||REQ-001";

        // When & Then
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            HealthWorkerAccessPolicyMessageMapper.fromMessage(message);
        });
        assertTrue(exception.getMessage().contains("Field clinicName is required"));
    }

    @Test
    @DisplayName("fromMessage - Should handle whitespace in fields")
    void fromMessage_ShouldHandleWhitespaceInFields() {
        // Given
        String message = " 12345678 | 87654321 | Hospital Central | REQ-001 ";

        // When
        AddHealthWorkerAccessPolicyDTO dto = HealthWorkerAccessPolicyMessageMapper.fromMessage(message);

        // Then
        assertNotNull(dto);
        assertEquals("12345678", dto.getHealthUserCi());
        assertEquals("87654321", dto.getHealthWorkerCi());
        assertEquals("Hospital Central", dto.getClinicName());
        assertEquals(" REQ-001 ", dto.getAccessRequestId());
    }
}