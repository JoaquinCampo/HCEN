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
}