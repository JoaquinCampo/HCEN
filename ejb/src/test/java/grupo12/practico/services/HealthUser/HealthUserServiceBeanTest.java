package grupo12.practico.services.HealthUser;

import grupo12.practico.dtos.HealthUser.AddHealthUserDTO;
import grupo12.practico.dtos.HealthUser.HealthUserDTO;
import grupo12.practico.models.Gender;
import grupo12.practico.dtos.PaginationDTO;
import grupo12.practico.dtos.ClinicalDocument.ClinicalDocumentDTO;
import grupo12.practico.dtos.ClinicalHistory.ClinicalHistoryRequestDTO;
import grupo12.practico.dtos.ClinicalHistory.ClinicalHistoryResponseDTO;
import grupo12.practico.models.HealthUser;
import grupo12.practico.repositories.HealthUser.HealthUserRepositoryLocal;
import grupo12.practico.services.AccessPolicy.AccessPolicyServiceLocal;
import grupo12.practico.services.Clinic.ClinicServiceLocal;
import grupo12.practico.services.Logger.LoggerServiceLocal;
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

    @Mock
    private ClinicServiceLocal clinicService;

    @Mock
    private LoggerServiceLocal loggerService;

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
    @DisplayName("findAll - Should filter by clinic name")
    void testFindAll_FilterByClinicName() {
        // Arrange - Users: John Doe (Clinic A,B), Jane Smith (Clinic B,C), Bob Johnson
        // (Clinic A)
        List<HealthUser> filteredUsers = Arrays.asList(
                createHealthUser("user1", "John", "Doe", "12345678", Set.of("Clinic A", "Clinic B")),
                createHealthUser("user3", "Bob", "Johnson", "11223344", Set.of("Clinic A")));
        when(healthUserRepository.findAllHealthUsers("Clinic A", null, null, 0, 20)).thenReturn(filteredUsers);
        when(healthUserRepository.countHealthUsers("Clinic A", null, null)).thenReturn(2L);

        // Act
        PaginationDTO<HealthUserDTO> result = healthUserService.findAllHealthUsers("Clinic A", null, null, null, null);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getItems().size());
        assertEquals(2L, result.getTotal());
        assertTrue(result.getItems().stream().anyMatch(u -> u.getCi().equals("12345678")));
        assertTrue(result.getItems().stream().anyMatch(u -> u.getCi().equals("11223344")));

        verify(healthUserRepository).findAllHealthUsers("Clinic A", null, null, 0, 20);
        verify(healthUserRepository).countHealthUsers("Clinic A", null, null);
    }

    @Test
    @DisplayName("findAll - Should filter by partial clinic name match")
    void testFindAll_FilterByPartialClinicName() {
        // Arrange - Users: John Doe (Central Clinic, East Clinic), Jane Smith (West
        // Clinic)
        List<HealthUser> filteredUsers = Arrays.asList(
                createHealthUser("user1", "John", "Doe", "12345678", Set.of("Central Clinic", "East Clinic")));
        when(healthUserRepository.findAllHealthUsers("Central", null, null, 0, 20)).thenReturn(filteredUsers);
        when(healthUserRepository.countHealthUsers("Central", null, null)).thenReturn(1L);

        // Act
        PaginationDTO<HealthUserDTO> result = healthUserService.findAllHealthUsers("Central", null, null, null, null);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getItems().size());
        assertEquals("12345678", result.getItems().get(0).getCi());

        verify(healthUserRepository).findAllHealthUsers("Central", null, null, 0, 20);
    }

    @Test
    @DisplayName("findAll - Should filter by name (firstName + lastName)")
    void testFindAll_FilterByName() {
        // Arrange - Users: John Doe, Jane Smith, John Johnson - filtering by "John"
        List<HealthUser> filteredUsers = Arrays.asList(
                createHealthUser("user1", "John", "Doe", "12345678", Set.of("Clinic A")),
                createHealthUser("user3", "John", "Johnson", "11223344", Set.of("Clinic A")));
        when(healthUserRepository.findAllHealthUsers(null, "John", null, 0, 20)).thenReturn(filteredUsers);
        when(healthUserRepository.countHealthUsers(null, "John", null)).thenReturn(2L);

        // Act
        PaginationDTO<HealthUserDTO> result = healthUserService.findAllHealthUsers(null, "John", null, null, null);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getItems().size());
        assertTrue(result.getItems().stream()
                .anyMatch(u -> u.getFirstName().equals("John") && u.getLastName().equals("Doe")));
        assertTrue(result.getItems().stream()
                .anyMatch(u -> u.getFirstName().equals("John") && u.getLastName().equals("Johnson")));

        verify(healthUserRepository).findAllHealthUsers(null, "John", null, 0, 20);
    }

    @Test
    @DisplayName("findAll - Should filter by partial name match")
    void testFindAll_FilterByPartialName() {
        // Arrange - Users: Michael Johnson, Michelle Smith - filtering by "Mich"
        List<HealthUser> filteredUsers = Arrays.asList(
                createHealthUser("user1", "Michael", "Johnson", "12345678", Set.of("Clinic A")),
                createHealthUser("user2", "Michelle", "Smith", "87654321", Set.of("Clinic A")));
        when(healthUserRepository.findAllHealthUsers(null, "Mich", null, 0, 20)).thenReturn(filteredUsers);
        when(healthUserRepository.countHealthUsers(null, "Mich", null)).thenReturn(2L);

        // Act
        PaginationDTO<HealthUserDTO> result = healthUserService.findAllHealthUsers(null, "Mich", null, null, null);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getItems().size());
        assertTrue(result.getItems().stream().anyMatch(u -> u.getFirstName().equals("Michael")));
        assertTrue(result.getItems().stream().anyMatch(u -> u.getFirstName().equals("Michelle")));

        verify(healthUserRepository).findAllHealthUsers(null, "Mich", null, 0, 20);
    }

    @Test
    @DisplayName("findAll - Should filter by CI")
    void testFindAll_FilterByCi() {
        // Arrange - Users: John Doe (12345678), Jane Smith (87654321), Bob Johnson
        // (12345679) - filtering by "123456"
        List<HealthUser> filteredUsers = Arrays.asList(
                createHealthUser("user1", "John", "Doe", "12345678", Set.of("Clinic A")),
                createHealthUser("user3", "Bob", "Johnson", "12345679", Set.of("Clinic A")));
        when(healthUserRepository.findAllHealthUsers(null, null, "123456", 0, 20)).thenReturn(filteredUsers);
        when(healthUserRepository.countHealthUsers(null, null, "123456")).thenReturn(2L);

        // Act
        PaginationDTO<HealthUserDTO> result = healthUserService.findAllHealthUsers(null, null, "123456", null, null);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getItems().size());
        assertTrue(result.getItems().stream().anyMatch(u -> u.getCi().equals("12345678")));
        assertTrue(result.getItems().stream().anyMatch(u -> u.getCi().equals("12345679")));

        verify(healthUserRepository).findAllHealthUsers(null, null, "123456", 0, 20);
    }

    @Test
    @DisplayName("findAll - Should filter by multiple criteria (clinic and name)")
    void testFindAll_FilterByMultipleCriteria() {
        // Arrange - Users: John Doe (Clinic A,B), Jane Smith (Clinic A), John Johnson
        // (Clinic B) - filtering by Clinic A and "John"
        List<HealthUser> filteredUsers = Arrays.asList(
                createHealthUser("user1", "John", "Doe", "12345678", Set.of("Clinic A", "Clinic B")));
        when(healthUserRepository.findAllHealthUsers("Clinic A", "John Doe", null, 0, 20)).thenReturn(filteredUsers);
        when(healthUserRepository.countHealthUsers("Clinic A", "John Doe", null)).thenReturn(1L);

        // Act
        PaginationDTO<HealthUserDTO> result = healthUserService.findAllHealthUsers("Clinic A", "John Doe", null, null,
                null);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getItems().size());
        assertEquals("John", result.getItems().get(0).getFirstName());
        assertEquals("Doe", result.getItems().get(0).getLastName());

        verify(healthUserRepository).findAllHealthUsers("Clinic A", "John Doe", null, 0, 20);
    }

    @Test
    @DisplayName("findAll - Should filter by multiple criteria (clinic, name, and CI)")
    void testFindAll_FilterByAllCriteria() {
        // Arrange - Users: John Doe (Clinic A, 12345678), Jane Smith (Clinic A,
        // 87654321) - filtering by all criteria
        List<HealthUser> filteredUsers = Arrays.asList(
                createHealthUser("user1", "John", "Doe", "12345678", Set.of("Clinic A")));
        when(healthUserRepository.findAllHealthUsers("Clinic A", "John", "12345678", 0, 20)).thenReturn(filteredUsers);
        when(healthUserRepository.countHealthUsers("Clinic A", "John", "12345678")).thenReturn(1L);

        // Act
        PaginationDTO<HealthUserDTO> result = healthUserService.findAllHealthUsers("Clinic A", "John", "12345678", null,
                null);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getItems().size());
        assertEquals("John", result.getItems().get(0).getFirstName());
        assertEquals("Doe", result.getItems().get(0).getLastName());
        assertEquals("12345678", result.getItems().get(0).getCi());

        verify(healthUserRepository).findAllHealthUsers("Clinic A", "John", "12345678", 0, 20);
    }

    @Test
    @DisplayName("findAll - Should return empty result when no users match filters")
    void testFindAll_NoMatches() {
        // Arrange
        when(healthUserRepository.findAllHealthUsers("NonExistent Clinic", null, null, 0, 20))
                .thenReturn(Collections.emptyList());
        when(healthUserRepository.countHealthUsers("NonExistent Clinic", null, null)).thenReturn(0L);

        // Act
        PaginationDTO<HealthUserDTO> result = healthUserService.findAllHealthUsers("NonExistent Clinic", null, null,
                null, null);

        // Assert
        assertNotNull(result);
        assertEquals(0, result.getItems().size());
        assertEquals(0L, result.getTotal());

        verify(healthUserRepository).findAllHealthUsers("NonExistent Clinic", null, null, 0, 20);
    }

    @Test
    @DisplayName("findAll - Should handle case insensitive filtering")
    void testFindAll_CaseInsensitiveFiltering() {
        // Arrange - Users: John Doe (clinic a), Jane Smith (CLINIC A) - filtering by
        // "clinic A"
        List<HealthUser> filteredUsers = Arrays.asList(
                createHealthUser("user1", "John", "Doe", "12345678", Set.of("clinic a")),
                createHealthUser("user2", "Jane", "Smith", "87654321", Set.of("CLINIC A")));
        when(healthUserRepository.findAllHealthUsers("clinic A", null, null, 0, 20)).thenReturn(filteredUsers);
        when(healthUserRepository.countHealthUsers("clinic A", null, null)).thenReturn(2L);

        // Act
        PaginationDTO<HealthUserDTO> result = healthUserService.findAllHealthUsers("clinic A", null, null, null, null);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getItems().size());

        verify(healthUserRepository).findAllHealthUsers("clinic A", null, null, 0, 20);
    }

    private HealthUser createHealthUser(String id, String firstName, String lastName, String ci,
            Set<String> clinicNames) {
        HealthUser user = new HealthUser();
        user.setId(id);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setCi(ci);
        user.setGender(Gender.MALE);
        user.setEmail(firstName.toLowerCase() + "@example.com");
        user.setPhone("+598123456789");
        user.setAddress("123 Test St");
        user.setDateOfBirth(LocalDate.of(1990, 1, 1));
        user.setClinicNames(clinicNames);
        user.setCreatedAt(LocalDate.now());
        user.setUpdatedAt(LocalDate.now());
        return user;
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
        verify(loggerService).logHealthUserCreated(testHealthUser.getCi(), "Clinic A");
        verify(loggerService).logHealthUserCreated(testHealthUser.getCi(), "Clinic B");
    }

    @Test
    @DisplayName("create - Should throw ValidationException when DTO is null")
    void testCreate_NullDTO() {
        // Act & Assert
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> healthUserService.createHealthUser(null));

        assertEquals("Health user data must not be null", exception.getMessage());
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

        assertEquals("Health user first name is required", exception.getMessage());
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

        assertEquals("Health user last name is required", exception.getMessage());
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

        assertEquals("Health user CI is required", exception.getMessage());
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
        verify(loggerService).logHealthUserClinicLinked(testHealthUser.getCi(), clinicName);
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
    @DisplayName("fetchClinicalHistory - Should return access denied response when access is denied")
    void testFetchClinicalHistory_AccessDenied() {
        // Arrange
        String healthUserCi = "54053584";
        String healthWorkerCi = "19301176";
        String clinicName = "Clinic A";

        when(accessPolicyService.hasClinicAccess(healthUserCi, clinicName)).thenReturn(false);
        when(accessPolicyService.hasHealthWorkerAccess(healthUserCi, healthWorkerCi)).thenReturn(false);
        when(accessPolicyService.hasSpecialtyAccess(healthUserCi, null)).thenReturn(false);

        // Mock the repository to return a HealthUser
        HealthUser healthUser = new HealthUser();
        healthUser.setCi(healthUserCi);
        healthUser.setFirstName("Test");
        healthUser.setLastName("User");
        when(healthUserRepository.findHealthUserByCi(healthUserCi)).thenReturn(healthUser);

        ClinicalHistoryRequestDTO request = new ClinicalHistoryRequestDTO();
        request.setHealthUserCi(healthUserCi);
        request.setHealthWorkerCi(healthWorkerCi);
        request.setClinicName(clinicName);

        // Act
        ClinicalHistoryResponseDTO result = healthUserService.findHealthUserClinicalHistory(request);

        // Assert
        assertNotNull(result);
        assertFalse(result.getHasAccess());
        assertTrue(result.getAccessMessage().contains("Access denied"));
        assertTrue(result.getAccessMessage().contains(healthUserCi));
        assertNotNull(result.getDocuments());
        assertTrue(result.getDocuments().isEmpty());

        verify(accessPolicyService).hasClinicAccess(healthUserCi, clinicName);
        verify(accessPolicyService).hasHealthWorkerAccess(healthUserCi, healthWorkerCi);
        verify(accessPolicyService).hasSpecialtyAccess(healthUserCi, null);
        verify(healthUserRepository, never()).findHealthUserClinicalHistory(anyString());
    }
}
