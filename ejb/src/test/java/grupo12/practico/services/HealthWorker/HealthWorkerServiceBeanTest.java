package grupo12.practico.services.HealthWorker;

import grupo12.practico.dtos.HealthWorker.HealthWorkerDTO;
import grupo12.practico.repositories.HealthWorker.HealthWorkerRepositoryLocal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import jakarta.validation.ValidationException;
import java.util.List;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("HealthWorkerServiceBean Tests")
class HealthWorkerServiceBeanTest {

    @Mock
    private HealthWorkerRepositoryLocal healthWorkerRepository;

    @InjectMocks
    private HealthWorkerServiceBean healthWorkerService;

    private HealthWorkerDTO testHealthWorkerDTO;

    @BeforeEach
    void setUp() {
        // Setup test data
        testHealthWorkerDTO = new HealthWorkerDTO();
        testHealthWorkerDTO.setCi("87654321");
        testHealthWorkerDTO.setFirstName("Jane");
        testHealthWorkerDTO.setLastName("Smith");
        testHealthWorkerDTO.setEmail("jane.smith@example.com");
    }

    @Test
    @DisplayName("findByClinicAndCi - Should return health worker from repository")
    void testFindByClinicAndCi_Success() {
        // Arrange
        String clinicName = "Clinic A";
        String healthWorkerCi = "87654321";

        when(healthWorkerRepository.findByClinicAndCi(clinicName, healthWorkerCi)).thenReturn(testHealthWorkerDTO);

        // Act
        HealthWorkerDTO result = healthWorkerService.findByClinicAndCi(clinicName, healthWorkerCi);

        // Assert
        assertNotNull(result);
        assertEquals(testHealthWorkerDTO, result);

        verify(healthWorkerRepository).findByClinicAndCi(clinicName, healthWorkerCi);
    }

    @Test
    @DisplayName("findByClinicAndCi - Should return null when repository returns null")
    void testFindByClinicAndCi_NotFound() {
        // Arrange
        String clinicName = "Clinic A";
        String healthWorkerCi = "99999999";

        when(healthWorkerRepository.findByClinicAndCi(clinicName, healthWorkerCi)).thenReturn(null);

        // Act
        HealthWorkerDTO result = healthWorkerService.findByClinicAndCi(clinicName, healthWorkerCi);

        // Assert
        assertNull(result);

        verify(healthWorkerRepository).findByClinicAndCi(clinicName, healthWorkerCi);
    }

    @Test
    @DisplayName("findByClinic - Should return null when repository returns null")
    void testFindByClinic_NotFound() {
        // Arrange
        String clinicName = "Clinic A";

        when(healthWorkerRepository.findByClinic(clinicName)).thenReturn(null);

        // Act
        List<HealthWorkerDTO> result = healthWorkerService.findByClinic(clinicName);

        // Assert
        assertNull(result);

        verify(healthWorkerRepository).findByClinic(clinicName);
    }

    @Test
    @DisplayName("findByClinic - Should return health workers list when found")
    void testFindByClinic_Success() {
        // Arrange
        String clinicName = "Clinic A";
        List<HealthWorkerDTO> expectedWorkers = Arrays.asList(testHealthWorkerDTO);

        when(healthWorkerRepository.findByClinic(clinicName)).thenReturn(expectedWorkers);

        // Act
        List<HealthWorkerDTO> result = healthWorkerService.findByClinic(clinicName);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testHealthWorkerDTO, result.get(0));

        verify(healthWorkerRepository).findByClinic(clinicName);
    }

    @Test
    @DisplayName("findByClinic - Should throw ValidationException when clinic name is null")
    void testFindByClinic_NullClinicName() {
        // Act & Assert
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> healthWorkerService.findByClinic(null));

        assertEquals("Clinic name is required", exception.getMessage());
        verifyNoInteractions(healthWorkerRepository);
    }

    @Test
    @DisplayName("findByClinic - Should throw ValidationException when clinic name is empty")
    void testFindByClinic_EmptyClinicName() {
        // Act & Assert
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> healthWorkerService.findByClinic(""));

        assertEquals("Clinic name is required", exception.getMessage());
        verifyNoInteractions(healthWorkerRepository);
    }

    @Test
    @DisplayName("findByClinic - Should throw ValidationException when clinic name is blank")
    void testFindByClinic_BlankClinicName() {
        // Act & Assert
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> healthWorkerService.findByClinic("   "));

        assertEquals("Clinic name is required", exception.getMessage());
        verifyNoInteractions(healthWorkerRepository);
    }

    @Test
    @DisplayName("findByClinicAndCi - Should handle null clinic name")
    void testFindByClinicAndCi_NullClinicName() {
        // Arrange
        String healthWorkerCi = "87654321";

        when(healthWorkerRepository.findByClinicAndCi(null, healthWorkerCi)).thenReturn(null);

        // Act
        HealthWorkerDTO result = healthWorkerService.findByClinicAndCi(null, healthWorkerCi);

        // Assert
        assertNull(result);
        verify(healthWorkerRepository).findByClinicAndCi(null, healthWorkerCi);
    }

    @Test
    @DisplayName("findByClinicAndCi - Should handle null health worker CI")
    void testFindByClinicAndCi_NullHealthWorkerCi() {
        // Arrange
        String clinicName = "Clinic A";

        when(healthWorkerRepository.findByClinicAndCi(clinicName, null)).thenReturn(null);

        // Act
        HealthWorkerDTO result = healthWorkerService.findByClinicAndCi(clinicName, null);

        // Assert
        assertNull(result);
        verify(healthWorkerRepository).findByClinicAndCi(clinicName, null);
    }

    @Test
    @DisplayName("findByClinicAndCi - Should handle both null parameters")
    void testFindByClinicAndCi_BothNullParameters() {
        // Arrange
        when(healthWorkerRepository.findByClinicAndCi(null, null)).thenReturn(null);

        // Act
        HealthWorkerDTO result = healthWorkerService.findByClinicAndCi(null, null);

        // Assert
        assertNull(result);
        verify(healthWorkerRepository).findByClinicAndCi(null, null);
    }

    @Test
    @DisplayName("findByClinic - Should return empty list when repository returns empty list")
    void testFindByClinic_EmptyList() {
        // Arrange
        String clinicName = "Clinic A";
        List<HealthWorkerDTO> emptyList = Arrays.asList();

        when(healthWorkerRepository.findByClinic(clinicName)).thenReturn(emptyList);

        // Act
        List<HealthWorkerDTO> result = healthWorkerService.findByClinic(clinicName);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(healthWorkerRepository).findByClinic(clinicName);
    }

    @Test
    @DisplayName("findByClinic - Should return multiple health workers")
    void testFindByClinic_MultipleWorkers() {
        // Arrange
        String clinicName = "Clinic A";
        HealthWorkerDTO worker2 = new HealthWorkerDTO();
        worker2.setCi("12345678");
        worker2.setFirstName("John");
        worker2.setLastName("Doe");
        worker2.setEmail("john.doe@example.com");

        List<HealthWorkerDTO> workers = Arrays.asList(testHealthWorkerDTO, worker2);

        when(healthWorkerRepository.findByClinic(clinicName)).thenReturn(workers);

        // Act
        List<HealthWorkerDTO> result = healthWorkerService.findByClinic(clinicName);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(testHealthWorkerDTO, result.get(0));
        assertEquals(worker2, result.get(1));

        verify(healthWorkerRepository).findByClinic(clinicName);
    }

}