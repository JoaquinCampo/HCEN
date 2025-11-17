package grupo12.practico.messaging.HealthUser;

import grupo12.practico.dtos.HealthUser.AddHealthUserDTO;
import grupo12.practico.models.Gender;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("HealthUserRegistrationMessageMapper Tests")
class HealthUserRegistrationMessageMapperTest {

    @Test
    @DisplayName("toMessage - Should convert valid DTO to message")
    void toMessage_ShouldConvertValidDtoToMessage() {
        AddHealthUserDTO dto = createValidAddHealthUserDTO();

        String result = HealthUserRegistrationMessageMapper.toMessage(dto);

        // Check the main structure (clinic names order may vary due to HashSet)
        assertTrue(
                result.startsWith("12345678|John|Doe|MALE|john.doe@example.com|+598123456789|123 Main St|1990-01-01|"));
        String clinicsPart = result.substring(result.lastIndexOf('|') + 1);
        assertTrue(clinicsPart.contains("Clinic A"));
        assertTrue(clinicsPart.contains("Clinic B"));
        assertTrue(clinicsPart.contains(","));
    }

    @Test
    @DisplayName("toMessage - Should handle null optional fields")
    void toMessage_ShouldHandleNullOptionalFields() {
        AddHealthUserDTO dto = new AddHealthUserDTO();
        dto.setCi("12345678");
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setGender(Gender.MALE);
        dto.setDateOfBirth(LocalDate.of(1990, 1, 1));
        // Optional fields left null
        dto.setClinicNames(new HashSet<>(Arrays.asList("Clinic A")));

        String result = HealthUserRegistrationMessageMapper.toMessage(dto);

        String expected = "12345678|John|Doe|MALE||||1990-01-01|Clinic A";
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("toMessage - Should handle empty clinic names set")
    void toMessage_ShouldHandleEmptyClinicNamesSet() {
        AddHealthUserDTO dto = createValidAddHealthUserDTO();
        dto.setClinicNames(new HashSet<>()); // Empty set

        String result = HealthUserRegistrationMessageMapper.toMessage(dto);

        String expected = "12345678|John|Doe|MALE|john.doe@example.com|+598123456789|123 Main St|1990-01-01|";
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("toMessage - Should throw NullPointerException for null DTO")
    void toMessage_ShouldThrowNullPointerExceptionForNullDto() {
        NullPointerException exception = assertThrows(NullPointerException.class,
                () -> HealthUserRegistrationMessageMapper.toMessage(null));

        assertEquals("health user dto must not be null", exception.getMessage());
    }

    @Test
    @DisplayName("toMessage - Should throw ValidationException for null required fields")
    void toMessage_ShouldThrowValidationExceptionForNullRequiredFields() {
        AddHealthUserDTO dto = new AddHealthUserDTO();
        // Leave required fields null

        ValidationException exception = assertThrows(ValidationException.class,
                () -> HealthUserRegistrationMessageMapper.toMessage(dto));

        assertEquals("Field ci is required", exception.getMessage());
    }

    @Test
    @DisplayName("toMessage - Should throw ValidationException for pipe in field")
    void toMessage_ShouldThrowValidationExceptionForPipeInField() {
        AddHealthUserDTO dto = createValidAddHealthUserDTO();
        dto.setFirstName("John|With|Pipe");

        ValidationException exception = assertThrows(ValidationException.class,
                () -> HealthUserRegistrationMessageMapper.toMessage(dto));

        assertEquals("Field firstName must not contain '|'", exception.getMessage());
    }

    @Test
    @DisplayName("fromMessage - Should convert valid message to DTO")
    void fromMessage_ShouldConvertValidMessageToDto() {
        String message = "12345678|John|Doe|MALE|john.doe@example.com|+598123456789|123 Main St|1990-01-01|Clinic A,Clinic B";

        AddHealthUserDTO result = HealthUserRegistrationMessageMapper.fromMessage(message);

        assertEquals("12345678", result.getCi());
        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getLastName());
        assertEquals(Gender.MALE, result.getGender());
        assertEquals("john.doe@example.com", result.getEmail());
        assertEquals("+598123456789", result.getPhone());
        assertEquals("123 Main St", result.getAddress());
        assertEquals(LocalDate.of(1990, 1, 1), result.getDateOfBirth());
        assertEquals(Set.of("Clinic A", "Clinic B"), result.getClinicNames());
    }

    @Test
    @DisplayName("fromMessage - Should handle empty optional fields")
    void fromMessage_ShouldHandleEmptyOptionalFields() {
        String message = "12345678|John|Doe|MALE||||1990-01-01|Clinic A";

        AddHealthUserDTO result = HealthUserRegistrationMessageMapper.fromMessage(message);

        assertEquals("12345678", result.getCi());
        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getLastName());
        assertEquals(Gender.MALE, result.getGender());
        assertNull(result.getEmail());
        assertNull(result.getPhone());
        assertNull(result.getAddress());
        assertEquals(LocalDate.of(1990, 1, 1), result.getDateOfBirth());
        assertEquals(Set.of("Clinic A"), result.getClinicNames());
    }

    @Test
    @DisplayName("fromMessage - Should handle empty clinic names")
    void fromMessage_ShouldHandleEmptyClinicNames() {
        String message = "12345678|John|Doe|MALE|john.doe@example.com|+598123456789|123 Main St|1990-01-01|";

        AddHealthUserDTO result = HealthUserRegistrationMessageMapper.fromMessage(message);

        assertEquals(Set.of(), result.getClinicNames());
    }

    @Test
    @DisplayName("fromMessage - Should throw ValidationException for null message")
    void fromMessage_ShouldThrowValidationExceptionForNullMessage() {
        ValidationException exception = assertThrows(ValidationException.class,
                () -> HealthUserRegistrationMessageMapper.fromMessage(null));

        assertEquals("Message payload must not be empty", exception.getMessage());
    }

    @Test
    @DisplayName("fromMessage - Should throw ValidationException for empty message")
    void fromMessage_ShouldThrowValidationExceptionForEmptyMessage() {
        ValidationException exception = assertThrows(ValidationException.class,
                () -> HealthUserRegistrationMessageMapper.fromMessage(""));

        assertEquals("Message payload must not be empty", exception.getMessage());
    }

    @Test
    @DisplayName("fromMessage - Should throw ValidationException for wrong field count")
    void fromMessage_ShouldThrowValidationExceptionForWrongFieldCount() {
        String message = "12345678|John|Doe|MALE"; // Too few fields

        ValidationException exception = assertThrows(ValidationException.class,
                () -> HealthUserRegistrationMessageMapper.fromMessage(message));

        assertTrue(exception.getMessage().contains("Unexpected field count"));
    }

    @Test
    @DisplayName("fromMessage - Should throw ValidationException for invalid gender")
    void fromMessage_ShouldThrowValidationExceptionForInvalidGender() {
        String message = "12345678|John|Doe|INVALID_GENDER|john.doe@example.com|+598123456789|123 Main St|1990-01-01|Clinic A";

        ValidationException exception = assertThrows(ValidationException.class,
                () -> HealthUserRegistrationMessageMapper.fromMessage(message));

        assertEquals("Invalid gender: INVALID_GENDER", exception.getMessage());
    }

    @Test
    @DisplayName("fromMessage - Should throw ValidationException for invalid date")
    void fromMessage_ShouldThrowValidationExceptionForInvalidDate() {
        String message = "12345678|John|Doe|MALE|john.doe@example.com|+598123456789|123 Main St|invalid-date|Clinic A";

        ValidationException exception = assertThrows(ValidationException.class,
                () -> HealthUserRegistrationMessageMapper.fromMessage(message));

        assertTrue(exception.getMessage().contains("Invalid dateOfBirth format"));
    }

    @Test
    @DisplayName("fromMessage - Should throw ValidationException for empty required field")
    void fromMessage_ShouldThrowValidationExceptionForEmptyRequiredField() {
        String message = "|John|Doe|MALE|john.doe@example.com|+598123456789|123 Main St|1990-01-01|Clinic A";

        ValidationException exception = assertThrows(ValidationException.class,
                () -> HealthUserRegistrationMessageMapper.fromMessage(message));

        assertEquals("Field ci is required", exception.getMessage());
    }

    private AddHealthUserDTO createValidAddHealthUserDTO() {
        AddHealthUserDTO dto = new AddHealthUserDTO();
        dto.setCi("12345678");
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setGender(Gender.MALE);
        dto.setEmail("john.doe@example.com");
        dto.setPhone("+598123456789");
        dto.setAddress("123 Main St");
        dto.setDateOfBirth(LocalDate.of(1990, 1, 1));
        dto.setClinicNames(new HashSet<>(Arrays.asList("Clinic A", "Clinic B")));
        return dto;
    }
}