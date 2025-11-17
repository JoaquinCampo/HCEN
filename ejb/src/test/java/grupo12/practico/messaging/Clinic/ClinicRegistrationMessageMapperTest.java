package grupo12.practico.messaging.Clinic;

import grupo12.practico.dtos.Clinic.AddClinicDTO;
import grupo12.practico.dtos.Clinic.ClinicAdminDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ClinicRegistrationMessageMapper Tests")
class ClinicRegistrationMessageMapperTest {

    @Test
    @DisplayName("toMessage - Should convert AddClinicDTO to message string")
    void toMessage_ShouldConvertAddClinicDTOToMessageString() {
        ClinicAdminDTO admin = new ClinicAdminDTO();
        admin.setCi("12345678");
        admin.setFirstName("Admin");
        admin.setLastName("User");
        admin.setEmail("admin@clinic.com");
        admin.setPhone("123456789");
        admin.setAddress("123 Admin St");
        admin.setDateOfBirth(LocalDate.of(1980, 1, 1));

        AddClinicDTO dto = new AddClinicDTO();
        dto.setName("Test Clinic");
        dto.setEmail("clinic@test.com");
        dto.setPhone("987654321");
        dto.setAddress("456 Clinic Ave");
        dto.setProviderName("Test Provider");
        dto.setClinicAdmin(admin);

        String result = ClinicRegistrationMessageMapper.toMessage(dto);

        String expected = "Test Clinic|clinic@test.com|987654321|456 Clinic Ave|12345678|Admin|User|admin@clinic.com|123456789|123 Admin St|1980-01-01|Test Provider";
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("toMessage - Should throw ValidationException when clinic admin is null")
    void toMessage_ShouldThrowValidationExceptionWhenClinicAdminIsNull() {
        AddClinicDTO dto = new AddClinicDTO();
        dto.setName("Test Clinic");
        dto.setEmail("clinic@test.com");
        dto.setPhone("987654321");
        dto.setAddress("456 Clinic Ave");
        dto.setProviderName("Test Provider");
        dto.setClinicAdmin(null);

        jakarta.validation.ValidationException exception = assertThrows(
                jakarta.validation.ValidationException.class,
                () -> ClinicRegistrationMessageMapper.toMessage(dto));

        assertTrue(exception.getMessage().contains("clinicAdmin.ci is required"));
    }

    @Test
    @DisplayName("toMessage - Should handle null optional fields in clinic admin")
    void toMessage_ShouldHandleNullOptionalFieldsInClinicAdmin() {
        ClinicAdminDTO admin = new ClinicAdminDTO();
        admin.setCi("12345678");
        admin.setFirstName("Admin");
        admin.setLastName("User");
        admin.setEmail("admin@clinic.com");
        admin.setPhone(null);
        admin.setAddress(null);
        admin.setDateOfBirth(null);

        AddClinicDTO dto = new AddClinicDTO();
        dto.setName("Test Clinic");
        dto.setEmail("clinic@test.com");
        dto.setPhone("987654321");
        dto.setAddress("456 Clinic Ave");
        dto.setProviderName(null);
        dto.setClinicAdmin(admin);

        String result = ClinicRegistrationMessageMapper.toMessage(dto);

        String expected = "Test Clinic|clinic@test.com|987654321|456 Clinic Ave|12345678|Admin|User|admin@clinic.com||||";
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("toMessage - Should throw NullPointerException when dto is null")
    void toMessage_ShouldThrowNullPointerExceptionWhenDtoIsNull() {
        NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> ClinicRegistrationMessageMapper.toMessage(null));

        assertEquals("clinic dto must not be null", exception.getMessage());
    }

    @Test
    @DisplayName("toMessage - Should throw ValidationException when required field contains pipe")
    void toMessage_ShouldThrowValidationExceptionWhenRequiredFieldContainsPipe() {
        ClinicAdminDTO admin = new ClinicAdminDTO();
        admin.setCi("12345678");
        admin.setFirstName("Admin");
        admin.setLastName("User");
        admin.setEmail("admin@clinic.com");

        AddClinicDTO dto = new AddClinicDTO();
        dto.setName("Test|Clinic");
        dto.setEmail("clinic@test.com");
        dto.setPhone("987654321");
        dto.setAddress("456 Clinic Ave");
        dto.setClinicAdmin(admin);

        jakarta.validation.ValidationException exception = assertThrows(
                jakarta.validation.ValidationException.class,
                () -> ClinicRegistrationMessageMapper.toMessage(dto));

        assertTrue(exception.getMessage().contains("must not contain '|'"));
    }

    @Test
    @DisplayName("toMessage - Should throw ValidationException when optional field contains pipe")
    void toMessage_ShouldThrowValidationExceptionWhenOptionalFieldContainsPipe() {
        ClinicAdminDTO admin = new ClinicAdminDTO();
        admin.setCi("12345678");
        admin.setFirstName("Admin");
        admin.setLastName("User");
        admin.setEmail("admin@clinic.com");
        admin.setPhone("123|456");

        AddClinicDTO dto = new AddClinicDTO();
        dto.setName("Test Clinic");
        dto.setEmail("clinic@test.com");
        dto.setPhone("987654321");
        dto.setAddress("456 Clinic Ave");
        dto.setClinicAdmin(admin);

        jakarta.validation.ValidationException exception = assertThrows(
                jakarta.validation.ValidationException.class,
                () -> ClinicRegistrationMessageMapper.toMessage(dto));

        assertTrue(exception.getMessage().contains("must not contain '|'"));
    }

    @Test
    @DisplayName("fromMessage - Should convert message string to AddClinicDTO")
    void fromMessage_ShouldConvertMessageStringToAddClinicDTO() {
        String message = "Test Clinic|clinic@test.com|987654321|456 Clinic Ave|12345678|Admin|User|admin@clinic.com|123456789|123 Admin St|1980-01-01|Test Provider";

        AddClinicDTO result = ClinicRegistrationMessageMapper.fromMessage(message);

        assertNotNull(result);
        assertEquals("Test Clinic", result.getName());
        assertEquals("clinic@test.com", result.getEmail());
        assertEquals("987654321", result.getPhone());
        assertEquals("456 Clinic Ave", result.getAddress());
        assertEquals("Test Provider", result.getProviderName());

        assertNotNull(result.getClinicAdmin());
        ClinicAdminDTO admin = result.getClinicAdmin();
        assertEquals("12345678", admin.getCi());
        assertEquals("Admin", admin.getFirstName());
        assertEquals("User", admin.getLastName());
        assertEquals("admin@clinic.com", admin.getEmail());
        assertEquals("123456789", admin.getPhone());
        assertEquals("123 Admin St", admin.getAddress());
        assertEquals(LocalDate.of(1980, 1, 1), admin.getDateOfBirth());
    }

    @Test
    @DisplayName("fromMessage - Should handle empty optional fields")
    void fromMessage_ShouldHandleEmptyOptionalFields() {
        String message = "Test Clinic|clinic@test.com|987654321|456 Clinic Ave|12345678|Admin|User|admin@clinic.com|||1980-01-01|";

        AddClinicDTO result = ClinicRegistrationMessageMapper.fromMessage(message);

        assertNotNull(result);
        assertEquals("Test Clinic", result.getName());
        assertNull(result.getProviderName());

        ClinicAdminDTO admin = result.getClinicAdmin();
        assertNull(admin.getPhone());
        assertNull(admin.getAddress());
    }

    @Test
    @DisplayName("fromMessage - Should throw ValidationException when message is null")
    void fromMessage_ShouldThrowValidationExceptionWhenMessageIsNull() {
        jakarta.validation.ValidationException exception = assertThrows(
                jakarta.validation.ValidationException.class,
                () -> ClinicRegistrationMessageMapper.fromMessage(null));

        assertEquals("Message payload must not be empty", exception.getMessage());
    }

    @Test
    @DisplayName("fromMessage - Should throw ValidationException when message is empty")
    void fromMessage_ShouldThrowValidationExceptionWhenMessageIsEmpty() {
        jakarta.validation.ValidationException exception = assertThrows(
                jakarta.validation.ValidationException.class,
                () -> ClinicRegistrationMessageMapper.fromMessage(""));

        assertEquals("Message payload must not be empty", exception.getMessage());
    }

    @Test
    @DisplayName("fromMessage - Should throw ValidationException when field count is wrong")
    void fromMessage_ShouldThrowValidationExceptionWhenFieldCountIsWrong() {
        String message = "Test Clinic|clinic@test.com|987654321"; // Too few fields

        jakarta.validation.ValidationException exception = assertThrows(
                jakarta.validation.ValidationException.class,
                () -> ClinicRegistrationMessageMapper.fromMessage(message));

        assertTrue(exception.getMessage().contains("Unexpected field count"));
    }

    @Test
    @DisplayName("fromMessage - Should throw ValidationException when required field is null")
    void fromMessage_ShouldThrowValidationExceptionWhenRequiredFieldIsNull() {
        String message = "|clinic@test.com|987654321|456 Clinic Ave|12345678|Admin|User|admin@clinic.com|123456789|123 Admin St|1980-01-01|Test Provider";

        jakarta.validation.ValidationException exception = assertThrows(
                jakarta.validation.ValidationException.class,
                () -> ClinicRegistrationMessageMapper.fromMessage(message));

        assertTrue(exception.getMessage().contains("is required"));
    }

    @Test
    @DisplayName("fromMessage - Should throw ValidationException when required field is empty")
    void fromMessage_ShouldThrowValidationExceptionWhenRequiredFieldIsEmpty() {
        String message = " |clinic@test.com|987654321|456 Clinic Ave|12345678|Admin|User|admin@clinic.com|123456789|123 Admin St|1980-01-01|Test Provider";

        jakarta.validation.ValidationException exception = assertThrows(
                jakarta.validation.ValidationException.class,
                () -> ClinicRegistrationMessageMapper.fromMessage(message));

        assertTrue(exception.getMessage().contains("is required"));
    }
}