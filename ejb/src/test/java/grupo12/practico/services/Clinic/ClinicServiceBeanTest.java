package grupo12.practico.services.Clinic;

import grupo12.practico.dtos.Clinic.AddClinicDTO;
import grupo12.practico.dtos.Clinic.ClinicAdminDTO;
import grupo12.practico.dtos.Clinic.ClinicDTO;
import grupo12.practico.dtos.HealthWorker.HealthWorkerDTO;
import grupo12.practico.repositories.Clinic.ClinicRepositoryLocal;
import grupo12.practico.repositories.HealthUser.HealthUserRepositoryLocal;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ClinicServiceBean Tests")
class ClinicServiceBeanTest {

    @Mock
    private ClinicRepositoryLocal clinicRepository;

    @Mock
    private HealthUserRepositoryLocal healthUserRepository;

    private ClinicServiceBean service;

    private AddClinicDTO addClinicDTO;
    private ClinicAdminDTO clinicAdminDTO;
    private ClinicDTO clinicDTO;

    @BeforeEach
    void setUp() throws Exception {
        service = new ClinicServiceBean();
        Field clinicRepoField = ClinicServiceBean.class.getDeclaredField("clinicRepository");
        clinicRepoField.setAccessible(true);
        clinicRepoField.set(service, clinicRepository);

        Field healthUserRepoField = ClinicServiceBean.class.getDeclaredField("healthUserRepository");
        healthUserRepoField.setAccessible(true);
        healthUserRepoField.set(service, healthUserRepository);

        LocalDate now = LocalDate.of(2023, 6, 15);

        clinicAdminDTO = new ClinicAdminDTO();
        clinicAdminDTO.setCi("11111111");
        clinicAdminDTO.setFirstName("Admin");
        clinicAdminDTO.setLastName("User");
        clinicAdminDTO.setEmail("admin@clinic.com");
        clinicAdminDTO.setPhone("123456789");
        clinicAdminDTO.setAddress("123 Admin St");
        clinicAdminDTO.setDateOfBirth(LocalDate.of(1980, 1, 1));

        addClinicDTO = new AddClinicDTO();
        addClinicDTO.setName("Test Clinic");
        addClinicDTO.setEmail("clinic@test.com");
        addClinicDTO.setPhone("987654321");
        addClinicDTO.setAddress("456 Clinic Ave");
        addClinicDTO.setProviderName("Test Provider");
        addClinicDTO.setClinicAdmin(clinicAdminDTO);

        clinicDTO = new ClinicDTO();
        clinicDTO.setId("clinic-123");
        clinicDTO.setName("Test Clinic");
        clinicDTO.setEmail("clinic@test.com");
        clinicDTO.setPhone("987654321");
        clinicDTO.setAddress("456 Clinic Ave");
        clinicDTO.setCreatedAt(now);
        clinicDTO.setUpdatedAt(now);

        HealthWorkerDTO hw1 = new HealthWorkerDTO();
        hw1.setCi("22222222");
        hw1.setFirstName("Dr.");
        hw1.setLastName("Smith");
        hw1.setEmail("dr.smith@clinic.com");

        HealthWorkerDTO hw2 = new HealthWorkerDTO();
        hw2.setCi("33333333");
        hw2.setFirstName("Dr.");
        hw2.setLastName("Johnson");
        hw2.setEmail("dr.johnson@clinic.com");

    }

    // createClinic Tests
    @Test
    @DisplayName("createClinic - Should create clinic successfully")
    void createClinic_ShouldCreateClinicSuccessfully() {
        when(clinicRepository.createClinic(addClinicDTO)).thenReturn(clinicDTO);

        ClinicDTO result = service.createClinic(addClinicDTO);

        assertNotNull(result);
        assertEquals("clinic-123", result.getId());
        assertEquals("Test Clinic", result.getName());
        assertEquals("clinic@test.com", result.getEmail());
        assertEquals("987654321", result.getPhone());
        assertEquals("456 Clinic Ave", result.getAddress());

        verify(clinicRepository).createClinic(addClinicDTO);
    }

    @Test
    @DisplayName("createClinic - Should throw when clinic name is null")
    void createClinic_ShouldThrowWhenNameIsNull() {
        addClinicDTO.setName(null);

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> service.createClinic(addClinicDTO));

        assertEquals("Clinic name is required", exception.getMessage());
        verifyNoInteractions(clinicRepository);
    }

    @Test
    @DisplayName("createClinic - Should throw when clinic name is empty")
    void createClinic_ShouldThrowWhenNameIsEmpty() {
        addClinicDTO.setName("");

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> service.createClinic(addClinicDTO));

        assertEquals("Clinic name is required", exception.getMessage());
        verifyNoInteractions(clinicRepository);
    }

    @Test
    @DisplayName("createClinic - Should throw when clinic name is blank")
    void createClinic_ShouldThrowWhenNameIsBlank() {
        addClinicDTO.setName("   ");

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> service.createClinic(addClinicDTO));

        assertEquals("Clinic name is required", exception.getMessage());
        verifyNoInteractions(clinicRepository);
    }

    @Test
    @DisplayName("createClinic - Should throw when email is null")
    void createClinic_ShouldThrowWhenEmailIsNull() {
        addClinicDTO.setEmail(null);

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> service.createClinic(addClinicDTO));

        assertEquals("Clinic email is required", exception.getMessage());
        verifyNoInteractions(clinicRepository);
    }

    @Test
    @DisplayName("createClinic - Should throw when email is empty")
    void createClinic_ShouldThrowWhenEmailIsEmpty() {
        addClinicDTO.setEmail("");

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> service.createClinic(addClinicDTO));

        assertEquals("Clinic email is required", exception.getMessage());
        verifyNoInteractions(clinicRepository);
    }

    @Test
    @DisplayName("createClinic - Should throw when phone is null")
    void createClinic_ShouldThrowWhenPhoneIsNull() {
        addClinicDTO.setPhone(null);

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> service.createClinic(addClinicDTO));

        assertEquals("Clinic phone is required", exception.getMessage());
        verifyNoInteractions(clinicRepository);
    }

    @Test
    @DisplayName("createClinic - Should throw when phone is empty")
    void createClinic_ShouldThrowWhenPhoneIsEmpty() {
        addClinicDTO.setPhone("");

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> service.createClinic(addClinicDTO));

        assertEquals("Clinic phone is required", exception.getMessage());
        verifyNoInteractions(clinicRepository);
    }

    @Test
    @DisplayName("createClinic - Should throw when address is null")
    void createClinic_ShouldThrowWhenAddressIsNull() {
        addClinicDTO.setAddress(null);

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> service.createClinic(addClinicDTO));

        assertEquals("Address is required", exception.getMessage());
        verifyNoInteractions(clinicRepository);
    }

    @Test
    @DisplayName("createClinic - Should throw when address is empty")
    void createClinic_ShouldThrowWhenAddressIsEmpty() {
        addClinicDTO.setAddress("");

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> service.createClinic(addClinicDTO));

        assertEquals("Address is required", exception.getMessage());
        verifyNoInteractions(clinicRepository);
    }

    @Test
    @DisplayName("createClinic - Should throw when clinic admin is null")
    void createClinic_ShouldThrowWhenClinicAdminIsNull() {
        addClinicDTO.setClinicAdmin(null);

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> service.createClinic(addClinicDTO));

        assertEquals("Clinic admin information is required", exception.getMessage());
        verifyNoInteractions(clinicRepository);
    }

    @Test
    @DisplayName("createClinic - Should throw when clinic admin CI is null")
    void createClinic_ShouldThrowWhenClinicAdminCiIsNull() {
        clinicAdminDTO.setCi(null);
        addClinicDTO.setClinicAdmin(clinicAdminDTO);

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> service.createClinic(addClinicDTO));

        assertEquals("Clinic admin CI is required", exception.getMessage());
        verifyNoInteractions(clinicRepository);
    }

    @Test
    @DisplayName("createClinic - Should throw when clinic admin CI is empty")
    void createClinic_ShouldThrowWhenClinicAdminCiIsEmpty() {
        clinicAdminDTO.setCi("");
        addClinicDTO.setClinicAdmin(clinicAdminDTO);

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> service.createClinic(addClinicDTO));

        assertEquals("Clinic admin CI is required", exception.getMessage());
        verifyNoInteractions(clinicRepository);
    }

    @Test
    @DisplayName("createClinic - Should throw when clinic admin first name is null")
    void createClinic_ShouldThrowWhenClinicAdminFirstNameIsNull() {
        clinicAdminDTO.setFirstName(null);
        addClinicDTO.setClinicAdmin(clinicAdminDTO);

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> service.createClinic(addClinicDTO));

        assertEquals("Clinic admin first name is required", exception.getMessage());
        verifyNoInteractions(clinicRepository);
    }

    @Test
    @DisplayName("createClinic - Should throw when clinic admin first name is empty")
    void createClinic_ShouldThrowWhenClinicAdminFirstNameIsEmpty() {
        clinicAdminDTO.setFirstName("");
        addClinicDTO.setClinicAdmin(clinicAdminDTO);

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> service.createClinic(addClinicDTO));

        assertEquals("Clinic admin first name is required", exception.getMessage());
        verifyNoInteractions(clinicRepository);
    }

    @Test
    @DisplayName("createClinic - Should throw when clinic admin last name is null")
    void createClinic_ShouldThrowWhenClinicAdminLastNameIsNull() {
        clinicAdminDTO.setLastName(null);
        addClinicDTO.setClinicAdmin(clinicAdminDTO);

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> service.createClinic(addClinicDTO));

        assertEquals("Clinic admin last name is required", exception.getMessage());
        verifyNoInteractions(clinicRepository);
    }

    @Test
    @DisplayName("createClinic - Should throw when clinic admin last name is empty")
    void createClinic_ShouldThrowWhenClinicAdminLastNameIsEmpty() {
        clinicAdminDTO.setLastName("");
        addClinicDTO.setClinicAdmin(clinicAdminDTO);

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> service.createClinic(addClinicDTO));

        assertEquals("Clinic admin last name is required", exception.getMessage());
        verifyNoInteractions(clinicRepository);
    }

    @Test
    @DisplayName("createClinic - Should throw when clinic admin email is null")
    void createClinic_ShouldThrowWhenClinicAdminEmailIsNull() {
        clinicAdminDTO.setEmail(null);
        addClinicDTO.setClinicAdmin(clinicAdminDTO);

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> service.createClinic(addClinicDTO));

        assertEquals("Clinic admin email is required", exception.getMessage());
        verifyNoInteractions(clinicRepository);
    }

    @Test
    @DisplayName("createClinic - Should throw when clinic admin email is empty")
    void createClinic_ShouldThrowWhenClinicAdminEmailIsEmpty() {
        clinicAdminDTO.setEmail("");
        addClinicDTO.setClinicAdmin(clinicAdminDTO);

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> service.createClinic(addClinicDTO));

        assertEquals("Clinic admin email is required", exception.getMessage());
        verifyNoInteractions(clinicRepository);
    }

    @Test
    @DisplayName("createClinic - Should throw when repository throws exception")
    void createClinic_ShouldThrowWhenRepositoryThrowsException() {
        when(clinicRepository.createClinic(addClinicDTO))
                .thenThrow(new IllegalStateException("Repository error"));

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> service.createClinic(addClinicDTO));

        assertEquals("Repository error", exception.getMessage());
        verify(clinicRepository).createClinic(addClinicDTO);
    }

    // findClinicByName Tests
    @Test
    @DisplayName("findClinicByName - Should return clinic when found")
    void findClinicByName_ShouldReturnClinicWhenFound() {
        when(clinicRepository.findClinicByName("Test Clinic")).thenReturn(clinicDTO);

        ClinicDTO result = service.findClinicByName("Test Clinic");

        assertNotNull(result);
        assertEquals("clinic-123", result.getId());
        assertEquals("Test Clinic", result.getName());

        verify(clinicRepository).findClinicByName("Test Clinic");
    }

    @Test
    @DisplayName("findClinicByName - Should return null when clinic not found")
    void findClinicByName_ShouldReturnNullWhenNotFound() {
        when(clinicRepository.findClinicByName("Nonexistent Clinic")).thenReturn(null);

        ClinicDTO result = service.findClinicByName("Nonexistent Clinic");

        assertNull(result);

        verify(clinicRepository).findClinicByName("Nonexistent Clinic");
    }

    @Test
    @DisplayName("findClinicByName - Should throw when clinic name is null")
    void findClinicByName_ShouldThrowWhenNameIsNull() {
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> service.findClinicByName(null));

        assertEquals("Clinic name is required", exception.getMessage());
        verifyNoInteractions(clinicRepository);
    }

    @Test
    @DisplayName("findClinicByName - Should throw when clinic name is empty")
    void findClinicByName_ShouldThrowWhenNameIsEmpty() {
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> service.findClinicByName(""));

        assertEquals("Clinic name is required", exception.getMessage());
        verifyNoInteractions(clinicRepository);
    }

    @Test
    @DisplayName("findClinicByName - Should throw when repository throws exception")
    void findClinicByName_ShouldThrowWhenRepositoryThrowsException() {
        when(clinicRepository.findClinicByName("Test Clinic"))
                .thenThrow(new IllegalStateException("Repository error"));

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> service.findClinicByName("Test Clinic"));

        assertEquals("Repository error", exception.getMessage());
        verify(clinicRepository).findClinicByName("Test Clinic");
    }

    // findAllClinics Tests
    @Test
    @DisplayName("findAllClinics - Should return all clinics without provider filter")
    void findAllClinics_ShouldReturnAllClinics() {
        List<ClinicDTO> clinics = List.of(clinicDTO);
        when(clinicRepository.findAllClinics(null)).thenReturn(clinics);

        List<ClinicDTO> result = service.findAllClinics(null);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("clinic-123", result.get(0).getId());

        verify(clinicRepository).findAllClinics(null);
    }

    @Test
    @DisplayName("findAllClinics - Should return clinics filtered by provider")
    void findAllClinics_ShouldReturnClinicsFilteredByProvider() {
        List<ClinicDTO> clinics = List.of(clinicDTO);
        when(clinicRepository.findAllClinics("Test Provider")).thenReturn(clinics);

        List<ClinicDTO> result = service.findAllClinics("Test Provider");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("clinic-123", result.get(0).getId());

        verify(clinicRepository).findAllClinics("Test Provider");
    }

    @Test
    @DisplayName("findAllClinics - Should return empty list when no clinics found")
    void findAllClinics_ShouldReturnEmptyListWhenNoClinics() {
        when(clinicRepository.findAllClinics(null)).thenReturn(List.of());

        List<ClinicDTO> result = service.findAllClinics(null);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(clinicRepository).findAllClinics(null);
    }

    @Test
    @DisplayName("findAllClinics - Should throw when repository throws exception")
    void findAllClinics_ShouldThrowWhenRepositoryThrowsException() {
        when(clinicRepository.findAllClinics(null))
                .thenThrow(new IllegalStateException("Repository error"));

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> service.findAllClinics(null));

        assertEquals("Repository error", exception.getMessage());
        verify(clinicRepository).findAllClinics(null);
    }
}