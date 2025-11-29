package grupo12.practico.messaging.AccessRequest;

import grupo12.practico.dtos.AccessRequest.AddAccessRequestDTO;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("AccessRequestMessageMapper Tests")
class AccessRequestMessageMapperTest {

    @Test
    @DisplayName("toMessage - Should convert DTO to pipe-separated message")
    void toMessage_ShouldConvertDTOToPipeSeparatedMessage() {
        AddAccessRequestDTO dto = new AddAccessRequestDTO();
        dto.setHealthUserCi("12345678");
        dto.setHealthWorkerCi("87654321");
        dto.setClinicName("Test Clinic");
        dto.setSpecialtyNames(Arrays.asList("Cardiology", "Internal Medicine"));

        String message = AccessRequestMessageMapper.toMessage(dto);

        assertNotNull(message);
        assertEquals("12345678|87654321|Test Clinic|Cardiology,Internal Medicine", message);
    }

    @Test
    @DisplayName("toMessage - Should handle empty specialty names")
    void toMessage_ShouldHandleEmptySpecialtyNames() {
        AddAccessRequestDTO dto = new AddAccessRequestDTO();
        dto.setHealthUserCi("12345678");
        dto.setHealthWorkerCi("87654321");
        dto.setClinicName("Test Clinic");
        dto.setSpecialtyNames(Collections.emptyList());

        String message = AccessRequestMessageMapper.toMessage(dto);

        assertNotNull(message);
        assertEquals("12345678|87654321|Test Clinic|", message);
    }

    @Test
    @DisplayName("toMessage - Should handle null specialty names")
    void toMessage_ShouldHandleNullSpecialtyNames() {
        AddAccessRequestDTO dto = new AddAccessRequestDTO();
        dto.setHealthUserCi("12345678");
        dto.setHealthWorkerCi("87654321");
        dto.setClinicName("Test Clinic");
        dto.setSpecialtyNames(null);

        String message = AccessRequestMessageMapper.toMessage(dto);

        assertNotNull(message);
        assertEquals("12345678|87654321|Test Clinic|", message);
    }

    @Test
    @DisplayName("toMessage - Should handle single specialty name")
    void toMessage_ShouldHandleSingleSpecialtyName() {
        AddAccessRequestDTO dto = new AddAccessRequestDTO();
        dto.setHealthUserCi("12345678");
        dto.setHealthWorkerCi("87654321");
        dto.setClinicName("Test Clinic");
        dto.setSpecialtyNames(Collections.singletonList("Cardiology"));

        String message = AccessRequestMessageMapper.toMessage(dto);

        assertNotNull(message);
        assertEquals("12345678|87654321|Test Clinic|Cardiology", message);
    }

    @Test
    @DisplayName("toMessage - Should throw exception for null DTO")
    void toMessage_ShouldThrowExceptionForNullDTO() {
        assertThrows(NullPointerException.class, () -> {
            AccessRequestMessageMapper.toMessage(null);
        });
    }

    @Test
    @DisplayName("toMessage - Should throw exception when healthUserCi contains pipe")
    void toMessage_ShouldThrowExceptionWhenHealthUserCiContainsPipe() {
        AddAccessRequestDTO dto = new AddAccessRequestDTO();
        dto.setHealthUserCi("123|45678");
        dto.setHealthWorkerCi("87654321");
        dto.setClinicName("Test Clinic");
        dto.setSpecialtyNames(Collections.emptyList());

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            AccessRequestMessageMapper.toMessage(dto);
        });
        assertTrue(exception.getMessage().contains("must not contain '|'"));
    }

    @Test
    @DisplayName("toMessage - Should throw exception when healthWorkerCi contains pipe")
    void toMessage_ShouldThrowExceptionWhenHealthWorkerCiContainsPipe() {
        AddAccessRequestDTO dto = new AddAccessRequestDTO();
        dto.setHealthUserCi("12345678");
        dto.setHealthWorkerCi("876|54321");
        dto.setClinicName("Test Clinic");
        dto.setSpecialtyNames(Collections.emptyList());

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            AccessRequestMessageMapper.toMessage(dto);
        });
        assertTrue(exception.getMessage().contains("must not contain '|'"));
    }

    @Test
    @DisplayName("toMessage - Should throw exception when clinicName contains pipe")
    void toMessage_ShouldThrowExceptionWhenClinicNameContainsPipe() {
        AddAccessRequestDTO dto = new AddAccessRequestDTO();
        dto.setHealthUserCi("12345678");
        dto.setHealthWorkerCi("87654321");
        dto.setClinicName("Test|Clinic");
        dto.setSpecialtyNames(Collections.emptyList());

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            AccessRequestMessageMapper.toMessage(dto);
        });
        assertTrue(exception.getMessage().contains("must not contain '|'"));
    }

    @Test
    @DisplayName("toMessage - Should throw exception when specialty name contains pipe")
    void toMessage_ShouldThrowExceptionWhenSpecialtyNameContainsPipe() {
        AddAccessRequestDTO dto = new AddAccessRequestDTO();
        dto.setHealthUserCi("12345678");
        dto.setHealthWorkerCi("87654321");
        dto.setClinicName("Test Clinic");
        dto.setSpecialtyNames(Arrays.asList("Card|iology"));

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            AccessRequestMessageMapper.toMessage(dto);
        });
        assertTrue(exception.getMessage().contains("must not contain '|'"));
    }

    @Test
    @DisplayName("toMessage - Should throw exception when specialty name contains comma")
    void toMessage_ShouldThrowExceptionWhenSpecialtyNameContainsComma() {
        AddAccessRequestDTO dto = new AddAccessRequestDTO();
        dto.setHealthUserCi("12345678");
        dto.setHealthWorkerCi("87654321");
        dto.setClinicName("Test Clinic");
        dto.setSpecialtyNames(Arrays.asList("Card,iology"));

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            AccessRequestMessageMapper.toMessage(dto);
        });
        assertTrue(exception.getMessage().contains("must not contain ','"));
    }

    @Test
    @DisplayName("toMessage - Should throw exception when healthUserCi is null")
    void toMessage_ShouldThrowExceptionWhenHealthUserCiIsNull() {
        AddAccessRequestDTO dto = new AddAccessRequestDTO();
        dto.setHealthUserCi(null);
        dto.setHealthWorkerCi("87654321");
        dto.setClinicName("Test Clinic");
        dto.setSpecialtyNames(Collections.emptyList());

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            AccessRequestMessageMapper.toMessage(dto);
        });
        assertTrue(exception.getMessage().contains("healthUserCi") && exception.getMessage().contains("required"));
    }

    @Test
    @DisplayName("toMessage - Should throw exception when healthUserCi is blank")
    void toMessage_ShouldThrowExceptionWhenHealthUserCiIsBlank() {
        AddAccessRequestDTO dto = new AddAccessRequestDTO();
        dto.setHealthUserCi("   ");
        dto.setHealthWorkerCi("87654321");
        dto.setClinicName("Test Clinic");
        dto.setSpecialtyNames(Collections.emptyList());

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            AccessRequestMessageMapper.toMessage(dto);
        });
        assertTrue(exception.getMessage().contains("healthUserCi") && exception.getMessage().contains("required"));
    }

    @Test
    @DisplayName("toMessage - Should throw exception when healthWorkerCi is null")
    void toMessage_ShouldThrowExceptionWhenHealthWorkerCiIsNull() {
        AddAccessRequestDTO dto = new AddAccessRequestDTO();
        dto.setHealthUserCi("12345678");
        dto.setHealthWorkerCi(null);
        dto.setClinicName("Test Clinic");
        dto.setSpecialtyNames(Collections.emptyList());

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            AccessRequestMessageMapper.toMessage(dto);
        });
        assertTrue(exception.getMessage().contains("healthWorkerCi") && exception.getMessage().contains("required"));
    }

    @Test
    @DisplayName("toMessage - Should throw exception when clinicName is null")
    void toMessage_ShouldThrowExceptionWhenClinicNameIsNull() {
        AddAccessRequestDTO dto = new AddAccessRequestDTO();
        dto.setHealthUserCi("12345678");
        dto.setHealthWorkerCi("87654321");
        dto.setClinicName(null);
        dto.setSpecialtyNames(Collections.emptyList());

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            AccessRequestMessageMapper.toMessage(dto);
        });
        assertTrue(exception.getMessage().contains("clinicName") && exception.getMessage().contains("required"));
    }

    @Test
    @DisplayName("toMessage - Should trim specialty names")
    void toMessage_ShouldTrimSpecialtyNames() {
        AddAccessRequestDTO dto = new AddAccessRequestDTO();
        dto.setHealthUserCi("12345678");
        dto.setHealthWorkerCi("87654321");
        dto.setClinicName("Test Clinic");
        dto.setSpecialtyNames(Arrays.asList("  Cardiology  ", "  Internal Medicine  "));

        String message = AccessRequestMessageMapper.toMessage(dto);

        assertNotNull(message);
        assertEquals("12345678|87654321|Test Clinic|Cardiology,Internal Medicine", message);
    }

    // fromMessage tests

    @Test
    @DisplayName("fromMessage - Should convert pipe-separated message to DTO")
    void fromMessage_ShouldConvertPipeSeparatedMessageToDTO() {
        String message = "12345678|87654321|Test Clinic|Cardiology,Internal Medicine";

        AddAccessRequestDTO dto = AccessRequestMessageMapper.fromMessage(message);

        assertNotNull(dto);
        assertEquals("12345678", dto.getHealthUserCi());
        assertEquals("87654321", dto.getHealthWorkerCi());
        assertEquals("Test Clinic", dto.getClinicName());
        assertEquals(Arrays.asList("Cardiology", "Internal Medicine"), dto.getSpecialtyNames());
    }

    @Test
    @DisplayName("fromMessage - Should handle empty specialty names")
    void fromMessage_ShouldHandleEmptySpecialtyNames() {
        String message = "12345678|87654321|Test Clinic|";

        AddAccessRequestDTO dto = AccessRequestMessageMapper.fromMessage(message);

        assertNotNull(dto);
        assertEquals("12345678", dto.getHealthUserCi());
        assertEquals("87654321", dto.getHealthWorkerCi());
        assertEquals("Test Clinic", dto.getClinicName());
        assertEquals(Collections.emptyList(), dto.getSpecialtyNames());
    }

    @Test
    @DisplayName("fromMessage - Should handle single specialty name")
    void fromMessage_ShouldHandleSingleSpecialtyName() {
        String message = "12345678|87654321|Test Clinic|Cardiology";

        AddAccessRequestDTO dto = AccessRequestMessageMapper.fromMessage(message);

        assertNotNull(dto);
        assertEquals(Collections.singletonList("Cardiology"), dto.getSpecialtyNames());
    }

    @Test
    @DisplayName("fromMessage - Should throw exception for null message")
    void fromMessage_ShouldThrowExceptionForNullMessage() {
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            AccessRequestMessageMapper.fromMessage(null);
        });
        assertEquals("Message payload must not be empty", exception.getMessage());
    }

    @Test
    @DisplayName("fromMessage - Should throw exception for empty message")
    void fromMessage_ShouldThrowExceptionForEmptyMessage() {
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            AccessRequestMessageMapper.fromMessage("");
        });
        assertEquals("Message payload must not be empty", exception.getMessage());
    }

    @Test
    @DisplayName("fromMessage - Should throw exception for blank message")
    void fromMessage_ShouldThrowExceptionForBlankMessage() {
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            AccessRequestMessageMapper.fromMessage("   ");
        });
        assertEquals("Message payload must not be empty", exception.getMessage());
    }

    @Test
    @DisplayName("fromMessage - Should throw exception for incorrect field count")
    void fromMessage_ShouldThrowExceptionForIncorrectFieldCount() {
        String message = "12345678|87654321|Test Clinic"; // Missing specialtyNames

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            AccessRequestMessageMapper.fromMessage(message);
        });
        assertTrue(exception.getMessage().contains("Unexpected field count"));
    }

    @Test
    @DisplayName("fromMessage - Should throw exception for too many fields")
    void fromMessage_ShouldThrowExceptionForTooManyFields() {
        String message = "12345678|87654321|Test Clinic|Cardiology|ExtraField";

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            AccessRequestMessageMapper.fromMessage(message);
        });
        assertTrue(exception.getMessage().contains("Unexpected field count"));
    }

    @Test
    @DisplayName("fromMessage - Should throw exception when healthUserCi is blank")
    void fromMessage_ShouldThrowExceptionWhenHealthUserCiIsBlank() {
        String message = "   |87654321|Test Clinic|Cardiology";

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            AccessRequestMessageMapper.fromMessage(message);
        });
        assertTrue(exception.getMessage().contains("healthUserCi") && exception.getMessage().contains("required"));
    }

    @Test
    @DisplayName("fromMessage - Should throw exception when healthWorkerCi is blank")
    void fromMessage_ShouldThrowExceptionWhenHealthWorkerCiIsBlank() {
        String message = "12345678||Test Clinic|Cardiology";

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            AccessRequestMessageMapper.fromMessage(message);
        });
        assertTrue(exception.getMessage().contains("healthWorkerCi") && exception.getMessage().contains("required"));
    }

    @Test
    @DisplayName("fromMessage - Should throw exception when clinicName is blank")
    void fromMessage_ShouldThrowExceptionWhenClinicNameIsBlank() {
        String message = "12345678|87654321|   |Cardiology";

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            AccessRequestMessageMapper.fromMessage(message);
        });
        assertTrue(exception.getMessage().contains("clinicName") && exception.getMessage().contains("required"));
    }

    @Test
    @DisplayName("fromMessage - Should trim specialty names")
    void fromMessage_ShouldTrimSpecialtyNames() {
        String message = "12345678|87654321|Test Clinic|  Cardiology  ,  Internal Medicine  ";

        AddAccessRequestDTO dto = AccessRequestMessageMapper.fromMessage(message);

        assertNotNull(dto);
        assertEquals(Arrays.asList("Cardiology", "Internal Medicine"), dto.getSpecialtyNames());
    }

    @Test
    @DisplayName("fromMessage - Should filter empty specialty names after split")
    void fromMessage_ShouldFilterEmptySpecialtyNamesAfterSplit() {
        String message = "12345678|87654321|Test Clinic|Cardiology,,Internal Medicine";

        AddAccessRequestDTO dto = AccessRequestMessageMapper.fromMessage(message);

        assertNotNull(dto);
        assertEquals(Arrays.asList("Cardiology", "Internal Medicine"), dto.getSpecialtyNames());
    }

    @Test
    @DisplayName("roundtrip - toMessage and fromMessage should be consistent")
    void roundtrip_ToMessageAndFromMessageShouldBeConsistent() {
        AddAccessRequestDTO original = new AddAccessRequestDTO();
        original.setHealthUserCi("12345678");
        original.setHealthWorkerCi("87654321");
        original.setClinicName("Test Clinic");
        original.setSpecialtyNames(Arrays.asList("Cardiology", "Internal Medicine"));

        String message = AccessRequestMessageMapper.toMessage(original);
        AddAccessRequestDTO restored = AccessRequestMessageMapper.fromMessage(message);

        assertEquals(original.getHealthUserCi(), restored.getHealthUserCi());
        assertEquals(original.getHealthWorkerCi(), restored.getHealthWorkerCi());
        assertEquals(original.getClinicName(), restored.getClinicName());
        assertEquals(original.getSpecialtyNames(), restored.getSpecialtyNames());
    }

    @Test
    @DisplayName("roundtrip - with empty specialty names")
    void roundtrip_WithEmptySpecialtyNames() {
        AddAccessRequestDTO original = new AddAccessRequestDTO();
        original.setHealthUserCi("12345678");
        original.setHealthWorkerCi("87654321");
        original.setClinicName("Test Clinic");
        original.setSpecialtyNames(Collections.emptyList());

        String message = AccessRequestMessageMapper.toMessage(original);
        AddAccessRequestDTO restored = AccessRequestMessageMapper.fromMessage(message);

        assertEquals(original.getHealthUserCi(), restored.getHealthUserCi());
        assertEquals(original.getHealthWorkerCi(), restored.getHealthWorkerCi());
        assertEquals(original.getClinicName(), restored.getClinicName());
        assertEquals(Collections.emptyList(), restored.getSpecialtyNames());
    }
}
