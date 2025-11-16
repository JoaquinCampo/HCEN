package grupo12.practico.services.HealthUser;

import grupo12.practico.dtos.HealthUser.AddHealthUserDTO;
import grupo12.practico.dtos.HealthUser.HealthUserDTO;
import grupo12.practico.models.Gender;
import grupo12.practico.dtos.PaginationDTO;
import grupo12.practico.dtos.ClinicalDocument.ClinicalDocumentDTO;
import grupo12.practico.dtos.ClinicalHistory.ClinicalHistoryAccessLogResponseDTO;
import grupo12.practico.dtos.ClinicalHistory.ClinicalHistoryRequestDTO;
import grupo12.practico.dtos.ClinicalHistory.ClinicalHistoryResponseDTO;
import grupo12.practico.dtos.ClinicalHistory.HealthUserAccessHistoryResponseDTO;
import grupo12.practico.models.HealthUser;
import grupo12.practico.repositories.HealthUser.HealthUserRepositoryLocal;
import grupo12.practico.services.AccessPolicy.AccessPolicyServiceLocal;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("HealthUserServiceBean Tests")
class HealthUserServiceBeanTest {

    @Mock
    private HealthUserRepositoryLocal healthUserRepository;

    @Mock
    private AccessPolicyServiceLocal accessPolicyService;

    @InjectMocks
    private HealthUserServiceBean healthUserService;

    private HealthUser testHealthUser;
    private AddHealthUserDTO testAddHealthUserDTO;
    private HealthUserDTO testHealthUserDTO;

    @BeforeEach
    void setUp() {
        // Setup test data
        testHealthUser = new HealthUser();
        testHealthUser.setId("test-id-123");
        testHealthUser.setCi("54053584");
        testHealthUser.setFirstName("John");
        testHealthUser.setLastName("Doe");
        testHealthUser.setGender(Gender.MALE);
        testHealthUser.setEmail("john.doe@example.com");
        testHealthUser.setPhone("+598540535849");
        testHealthUser.setAddress("123 Main St");
        testHealthUser.setDateOfBirth(LocalDate.of(1990, 1, 1));
        testHealthUser.setClinicNames(new HashSet<>(Arrays.asList("Clinic A", "Clinic B")));
        testHealthUser.setCreatedAt(LocalDate.now());
        testHealthUser.setUpdatedAt(LocalDate.now());

        testAddHealthUserDTO = new AddHealthUserDTO();
        testAddHealthUserDTO.setCi("54053584");
        testAddHealthUserDTO.setFirstName("John");
        testAddHealthUserDTO.setLastName("Doe");
        testAddHealthUserDTO.setGender(Gender.MALE);
        testAddHealthUserDTO.setEmail("john.doe@example.com");
        testAddHealthUserDTO.setPhone("+598540535849");
        testAddHealthUserDTO.setAddress("123 Main St");
        testAddHealthUserDTO.setDateOfBirth(LocalDate.of(1990, 1, 1));
        testAddHealthUserDTO.setClinicNames(new HashSet<>(Arrays.asList("Clinic A", "Clinic B")));

        testHealthUserDTO = new HealthUserDTO();
        testHealthUserDTO.setId(testHealthUser.getId());
        testHealthUserDTO.setCi(testHealthUser.getCi());
        testHealthUserDTO.setFirstName(testHealthUser.getFirstName());
        testHealthUserDTO.setLastName(testHealthUser.getLastName());
        testHealthUserDTO.setGender(testHealthUser.getGender());
        testHealthUserDTO.setEmail(testHealthUser.getEmail());
        testHealthUserDTO.setPhone(testHealthUser.getPhone());
        testHealthUserDTO.setAddress(testHealthUser.getAddress());
    }

    @Test
    @DisplayName("findAll - Should return paginated health users with default parameters")
    void testFindAll_WithDefaultParameters() {
        // Arrange
        List<HealthUser> users = Arrays.asList(testHealthUser);
        when(healthUserRepository.findAllHealthUsers(null, null, null, 0, 20)).thenReturn(users);
        when(healthUserRepository.countHealthUsers(null, null, null)).thenReturn(1L);

        // Act
        PaginationDTO<HealthUserDTO> result = healthUserService.findAllHealthUsers(null, null, null, null, null);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getItems().size());
        assertEquals(0, result.getPageIndex());
        assertEquals(20, result.getPageSize());
        assertEquals(1L, result.getTotal());
        assertEquals(1L, result.getTotalPages());
        assertFalse(result.getHasNextPage());
        assertFalse(result.getHasPreviousPage());

        verify(healthUserRepository).findAllHealthUsers(null, null, null, 0, 20);
        verify(healthUserRepository).countHealthUsers(null, null, null);
    }

    @Test
    @DisplayName("findAll - Should return paginated health users with custom parameters")
    void testFindAll_WithCustomParameters() {
        // Arrange
        String clinicName = "Clinic A";
        String name = "John";
        String ci = "54053584";
        List<HealthUser> users = Arrays.asList(testHealthUser);
        when(healthUserRepository.findAllHealthUsers(clinicName, name, ci, 1, 10)).thenReturn(users);
        when(healthUserRepository.countHealthUsers(clinicName, name, ci)).thenReturn(25L);

        // Act
        PaginationDTO<HealthUserDTO> result = healthUserService.findAllHealthUsers(clinicName, name, ci, 1, 10);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getItems().size());
        assertEquals(1, result.getPageIndex());
        assertEquals(10, result.getPageSize());
        assertEquals(25L, result.getTotal());
        assertEquals(3L, result.getTotalPages());
        assertTrue(result.getHasNextPage());
        assertTrue(result.getHasPreviousPage());

        verify(healthUserRepository).findAllHealthUsers(clinicName, name, ci, 1, 10);
        verify(healthUserRepository).countHealthUsers(clinicName, name, ci);
    }

    @Test
    @DisplayName("findAll - Should handle negative page index gracefully")
    void testFindAll_WithNegativePageIndex() {
        // Arrange
        when(healthUserRepository.findAllHealthUsers(null, null, null, 0, 20)).thenReturn(Collections.emptyList());
        when(healthUserRepository.countHealthUsers(null, null, null)).thenReturn(0L);

        // Act
        PaginationDTO<HealthUserDTO> result = healthUserService.findAllHealthUsers(null, null, null, -5, null);

        // Assert
        assertNotNull(result);
        assertEquals(0, result.getPageIndex());
        verify(healthUserRepository).findAllHealthUsers(null, null, null, 0, 20);
    }

    @Test
    @DisplayName("findAll - Should handle invalid page size gracefully")
    void testFindAll_WithInvalidPageSize() {
        // Arrange
        when(healthUserRepository.findAllHealthUsers(null, null, null, 0, 20)).thenReturn(Collections.emptyList());
        when(healthUserRepository.countHealthUsers(null, null, null)).thenReturn(0L);

        // Act
        PaginationDTO<HealthUserDTO> result = healthUserService.findAllHealthUsers(null, null, null, null, 0);

        // Assert
        assertNotNull(result);
        assertEquals(20, result.getPageSize());
        verify(healthUserRepository).findAllHealthUsers(null, null, null, 0, 20);
    }

    @Test
    @DisplayName("create - Should successfully create a health user")
    void testCreate_Success() {
        // Arrange
        when(healthUserRepository.createHealthUser(any(HealthUser.class))).thenReturn(testHealthUser);

        // Act
        HealthUserDTO result = healthUserService.createHealthUser(testAddHealthUserDTO);

        // Assert
        assertNotNull(result);
        assertEquals(testHealthUser.getCi(), result.getCi());
        assertEquals(testHealthUser.getFirstName(), result.getFirstName());
        assertEquals(testHealthUser.getLastName(), result.getLastName());
        assertEquals(testHealthUser.getEmail(), result.getEmail());

        verify(healthUserRepository).createHealthUser(argThat(user -> user.getCi().equals("54053584") &&
                user.getFirstName().equals("John") &&
                user.getLastName().equals("Doe")));
    }

    @Test
    @DisplayName("create - Should throw ValidationException when DTO is null")
    void testCreate_NullDTO() {
        // Act & Assert
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> healthUserService.createHealthUser(null));

        assertEquals("User data must not be null", exception.getMessage());
        verify(healthUserRepository, never()).createHealthUser(any());
    }

    @Test
    @DisplayName("create - Should throw ValidationException when first name is blank")
    void testCreate_BlankFirstName() {
        // Arrange
        testAddHealthUserDTO.setFirstName("");

        // Act & Assert
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> healthUserService.createHealthUser(testAddHealthUserDTO));

        assertEquals("User first name and last name are required", exception.getMessage());
        verify(healthUserRepository, never()).createHealthUser(any());
    }

    @Test
    @DisplayName("create - Should throw ValidationException when last name is blank")
    void testCreate_BlankLastName() {
        // Arrange
        testAddHealthUserDTO.setLastName("   ");

        // Act & Assert
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> healthUserService.createHealthUser(testAddHealthUserDTO));

        assertEquals("User first name and last name are required", exception.getMessage());
        verify(healthUserRepository, never()).createHealthUser(any());
    }

    @Test
    @DisplayName("create - Should throw ValidationException when CI is blank")
    void testCreate_BlankCi() {
        // Arrange
        testAddHealthUserDTO.setCi(null);

        // Act & Assert
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> healthUserService.createHealthUser(testAddHealthUserDTO));

        assertEquals("User document is required", exception.getMessage());
        verify(healthUserRepository, never()).createHealthUser(any());
    }

    @Test
    @DisplayName("findByCi - Should return health user by CI")
    void testFindByCi_Success() {
        // Arrange
        String ci = "54053584";
        when(healthUserRepository.findHealthUserByCi(ci)).thenReturn(testHealthUser);

        // Act
        HealthUserDTO result = healthUserService.findHealthUserByCi(ci);

        // Assert
        assertNotNull(result);
        assertEquals(testHealthUser.getCi(), result.getCi());
        assertEquals(testHealthUser.getFirstName(), result.getFirstName());

        verify(healthUserRepository).findHealthUserByCi(ci);
    }

    @Test
    @DisplayName("findById - Should return health user by ID")
    void testFindById_Success() {
        // Arrange
        String id = "test-id-123";
        when(healthUserRepository.findHealthUserById(id)).thenReturn(testHealthUser);

        // Act
        HealthUserDTO result = healthUserService.findHealthUserById(id);

        // Assert
        assertNotNull(result);
        assertEquals(testHealthUser.getId(), result.getId());
        assertEquals(testHealthUser.getCi(), result.getCi());

        verify(healthUserRepository).findHealthUserById(id);
    }

    @Test
    @DisplayName("linkClinicToHealthUser - Should successfully link clinic to health user")
    void testLinkClinicToHealthUser_Success() {
        // Arrange
        String healthUserId = "test-id-123";
        String clinicName = "Clinic C";
        when(healthUserRepository.linkClinicToHealthUser(healthUserId, clinicName)).thenReturn(testHealthUser);

        // Act
        HealthUserDTO result = healthUserService.linkClinicToHealthUser(healthUserId, clinicName);

        // Assert
        assertNotNull(result);
        assertEquals(testHealthUser.getId(), result.getId());

        verify(healthUserRepository).linkClinicToHealthUser(healthUserId, clinicName);
    }

    @Test
    @DisplayName("fetchClinicalHistory - Should return clinical history when clinic has access")
    void testFetchClinicalHistory_WithClinicAccess() {
        // Arrange
        String healthUserCi = "54053584";
        String healthWorkerCi = "19301176";
        String clinicName = "Clinic A";

        List<ClinicalDocumentDTO> documents = new ArrayList<>();
        ClinicalDocumentDTO doc = new ClinicalDocumentDTO();
        doc.setId("doc-1");
        documents.add(doc);

        when(accessPolicyService.hasClinicAccess(healthUserCi, clinicName)).thenReturn(true);
        when(healthUserRepository.findHealthUserByCi(healthUserCi)).thenReturn(testHealthUser);
        when(healthUserRepository.findHealthUserClinicalHistory(healthUserCi))
                .thenReturn(documents);

        ClinicalHistoryRequestDTO request = new ClinicalHistoryRequestDTO();
        request.setHealthUserCi(healthUserCi);
        request.setHealthWorkerCi(healthWorkerCi);
        request.setClinicName(clinicName);

        // Act
        ClinicalHistoryResponseDTO result = healthUserService.findHealthUserClinicalHistory(request);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getHealthUser());
        assertEquals(testHealthUser.getCi(), result.getHealthUser().getCi());
        assertEquals(1, result.getDocuments().size());

        verify(accessPolicyService).hasClinicAccess(healthUserCi, clinicName);
        verify(healthUserRepository).findHealthUserClinicalHistory(healthUserCi);
    }

    @Test
    @DisplayName("fetchClinicalHistory - Should return clinical history when health worker has access")
    void testFetchClinicalHistory_WithHealthWorkerAccess() {
        // Arranges
        String healthUserCi = "54053584";
        String healthWorkerCi = "19301176";
        String clinicName = "Clinic A";

        List<ClinicalDocumentDTO> documents = new ArrayList<>();

        when(accessPolicyService.hasClinicAccess(healthUserCi, clinicName)).thenReturn(false);
        when(accessPolicyService.hasHealthWorkerAccess(healthUserCi, healthWorkerCi)).thenReturn(true);
        when(healthUserRepository.findHealthUserByCi(healthUserCi)).thenReturn(testHealthUser);
        when(healthUserRepository.findHealthUserClinicalHistory(healthUserCi))
                .thenReturn(documents);

        ClinicalHistoryRequestDTO request = new ClinicalHistoryRequestDTO();
        request.setHealthUserCi(healthUserCi);
        request.setHealthWorkerCi(healthWorkerCi);
        request.setClinicName(clinicName);

        // Act
        ClinicalHistoryResponseDTO result = healthUserService.findHealthUserClinicalHistory(
                request);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getHealthUser());
        assertEquals(0, result.getDocuments().size());

        verify(accessPolicyService).hasClinicAccess(healthUserCi, clinicName);
        verify(accessPolicyService).hasHealthWorkerAccess(healthUserCi, healthWorkerCi);
        verify(healthUserRepository).findHealthUserClinicalHistory(healthUserCi);
    }

    @Test
    @DisplayName("fetchClinicalHistory - Should throw ValidationException when access is denied")
    void testFetchClinicalHistory_AccessDenied() {
        // Arrange
        String healthUserCi = "54053584";
        String healthWorkerCi = "19301176";
        String clinicName = "Clinic A";

        when(accessPolicyService.hasClinicAccess(healthUserCi, clinicName)).thenReturn(false);
        when(accessPolicyService.hasHealthWorkerAccess(healthUserCi, healthWorkerCi)).thenReturn(false);

        ClinicalHistoryRequestDTO request = new ClinicalHistoryRequestDTO();
        request.setHealthUserCi(healthUserCi);
        request.setHealthWorkerCi(healthWorkerCi);
        request.setClinicName(clinicName);

        // Act & Assert
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> healthUserService.findHealthUserClinicalHistory(request));

        assertTrue(exception.getMessage().contains("Access denied"));
        assertTrue(exception.getMessage().contains(healthUserCi));

        verify(accessPolicyService).hasClinicAccess(healthUserCi, clinicName);
        verify(accessPolicyService).hasHealthWorkerAccess(healthUserCi, healthWorkerCi);
        verify(healthUserRepository, never()).findHealthUserClinicalHistory(anyString());
    }

    @Test
    @DisplayName("fetchHealthUserAccessHistory - Should return access history for health user")
    void testFetchHealthUserAccessHistory_Success() {
        // Arrange
        String healthUserCi = "54053584";
        List<ClinicalHistoryAccessLogResponseDTO> accessLogs = new ArrayList<>();

        ClinicalHistoryAccessLogResponseDTO log = new ClinicalHistoryAccessLogResponseDTO();
        log.setHealthWorkerCi("19301176");
        accessLogs.add(log);

        when(healthUserRepository.findHealthUserByCi(healthUserCi)).thenReturn(testHealthUser);
        when(healthUserRepository.findHealthUserAccessHistory(healthUserCi)).thenReturn(accessLogs);

        // Act
        HealthUserAccessHistoryResponseDTO result = healthUserService.fetchHealthUserAccessHistory(healthUserCi);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getHealthUser());
        assertEquals(testHealthUser.getCi(), result.getHealthUser().getCi());
        assertEquals(1, result.getAccessHistory().size());

        verify(healthUserRepository).findHealthUserByCi(healthUserCi);
        verify(healthUserRepository).findHealthUserAccessHistory(healthUserCi);
    }

    @Test
    @DisplayName("fetchHealthUserAccessHistory - Should return empty access history")
    void testFetchHealthUserAccessHistory_EmptyHistory() {
        // Arrange
        String healthUserCi = "54053584";
        List<ClinicalHistoryAccessLogResponseDTO> accessLogs = new ArrayList<>();

        when(healthUserRepository.findHealthUserByCi(healthUserCi)).thenReturn(testHealthUser);
        when(healthUserRepository.findHealthUserAccessHistory(healthUserCi)).thenReturn(accessLogs);

        // Act
        HealthUserAccessHistoryResponseDTO result = healthUserService.fetchHealthUserAccessHistory(healthUserCi);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getHealthUser());
        assertEquals(0, result.getAccessHistory().size());

        verify(healthUserRepository).findHealthUserByCi(healthUserCi);
        verify(healthUserRepository).findHealthUserAccessHistory(healthUserCi);
    }
}
