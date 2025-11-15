package grupo12.practico.repositories.HealthUser;

import grupo12.practico.dtos.ClinicalDocument.DocumentResponseDTO;
import grupo12.practico.dtos.ClinicalHistory.ClinicalHistoryAccessLogResponseDTO;
import grupo12.practico.models.HealthUser;
import grupo12.practico.models.Gender;
import grupo12.practico.repositories.NodoDocumentosConfig;
import grupo12.practico.services.HealthWorker.HealthWorkerServiceLocal;
import grupo12.practico.services.Clinic.ClinicServiceLocal;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("HealthUserRepositoryBean Tests")
class HealthUserRepositoryBeanTest {

    @Mock
    private EntityManager entityManager;

    @Mock
    private NodoDocumentosConfig config;

    @Mock
    private HealthWorkerServiceLocal healthWorkerService;

    @Mock
    private ClinicServiceLocal clinicService;

    @Mock
    private HttpClient httpClient;

    @Mock
    private TypedQuery<HealthUser> healthUserQuery;

    @Mock
    private TypedQuery<Long> longQuery;

    private HealthUserRepositoryBean repository;

    private HealthUser testHealthUser;

    @BeforeEach
    void setUp() throws Exception {
        // Setup test data
        testHealthUser = new HealthUser();
        testHealthUser.setId("test-id-123");
        testHealthUser.setCi("54053584");
        testHealthUser.setFirstName("John");
        testHealthUser.setLastName("Doe");
        testHealthUser.setGender(Gender.MALE);
        testHealthUser.setEmail("john.doe@example.com");
        testHealthUser.setPhone("+598123456789");
        testHealthUser.setAddress("123 Main St");
        testHealthUser.setDateOfBirth(LocalDate.of(1990, 1, 1));
        testHealthUser.setClinicNames(new HashSet<>(Arrays.asList("Clinic A", "Clinic B")));
        testHealthUser.setCreatedAt(LocalDate.now());
        testHealthUser.setUpdatedAt(LocalDate.now());

        // Inject the mocked HttpClient using reflection since it's created in
        // constructor
        java.lang.reflect.Field httpClientField = HealthUserRepositoryBean.class.getDeclaredField("httpClient");
        httpClientField.setAccessible(true);

        // Create repository instance and inject mocks
        repository = new HealthUserRepositoryBean();
        httpClientField.set(repository, httpClient);

        // Inject other dependencies using reflection
        java.lang.reflect.Field emField = HealthUserRepositoryBean.class.getDeclaredField("em");
        emField.setAccessible(true);
        emField.set(repository, entityManager);

        java.lang.reflect.Field configField = HealthUserRepositoryBean.class.getDeclaredField("config");
        configField.setAccessible(true);
        configField.set(repository, config);

        java.lang.reflect.Field healthWorkerServiceField = HealthUserRepositoryBean.class
                .getDeclaredField("healthWorkerService");
        healthWorkerServiceField.setAccessible(true);
        healthWorkerServiceField.set(repository, healthWorkerService);

        java.lang.reflect.Field clinicServiceField = HealthUserRepositoryBean.class.getDeclaredField("clinicService");
        clinicServiceField.setAccessible(true);
        clinicServiceField.set(repository, clinicService);
    }

    @Test
    @DisplayName("findAll - Should return all health users without filters")
    void testFindAll_NoFilters() {
        // Arrange
        List<HealthUser> users = Arrays.asList(testHealthUser);
        when(entityManager.createQuery(anyString(), eq(HealthUser.class))).thenReturn(healthUserQuery);
        when(healthUserQuery.getResultList()).thenReturn(users);

        // Act
        List<HealthUser> result = repository.findAll(null, null, null, null, null);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testHealthUser, result.get(0));

        verify(entityManager).createQuery(
                "SELECT h FROM HealthUser h ORDER BY h.lastName ASC, h.firstName ASC, h.id ASC",
                HealthUser.class);
        verify(healthUserQuery).getResultList();
        verify(healthUserQuery, never()).setFirstResult(anyInt());
        verify(healthUserQuery, never()).setMaxResults(anyInt());
    }

    @Test
    @DisplayName("findAll - Should return paginated health users without filters")
    void testFindAll_NoFilters_WithPagination() {
        // Arrange
        List<HealthUser> users = Arrays.asList(testHealthUser);
        when(entityManager.createQuery(anyString(), eq(HealthUser.class))).thenReturn(healthUserQuery);
        when(healthUserQuery.getResultList()).thenReturn(users);

        // Act
        List<HealthUser> result = repository.findAll(null, null, null, 1, 10);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());

        verify(healthUserQuery).setFirstResult(10);
        verify(healthUserQuery).setMaxResults(10);
    }

    @Test
    @DisplayName("findAll - Should filter by CI")
    void testFindAll_FilterByCi() {
        // Arrange
        List<HealthUser> users = Arrays.asList(testHealthUser);
        when(entityManager.createQuery(anyString(), eq(HealthUser.class))).thenReturn(healthUserQuery);
        when(healthUserQuery.getResultList()).thenReturn(users);

        // Act
        List<HealthUser> result = repository.findAll(null, null, "54053584", 0, 20);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());

        ArgumentCaptor<String> jpqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(entityManager).createQuery(jpqlCaptor.capture(), eq(HealthUser.class));

        String jpql = jpqlCaptor.getValue();
        assertTrue(jpql.contains("LOWER(h.ci) LIKE :ci"));
        assertTrue(jpql.contains("ORDER BY h.lastName ASC, h.firstName ASC, h.id ASC"));

        verify(healthUserQuery).setParameter("ci", "%54053584%");
    }

    @Test
    @DisplayName("findAll - Should filter by clinic name")
    void testFindAll_FilterByClinic() {
        // Arrange
        List<HealthUser> users = Arrays.asList(testHealthUser);
        when(entityManager.createQuery(anyString(), eq(HealthUser.class))).thenReturn(healthUserQuery);
        when(healthUserQuery.getResultList()).thenReturn(users);

        // Act
        List<HealthUser> result = repository.findAll("Clinic A", null, null, 0, 20);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());

        ArgumentCaptor<String> jpqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(entityManager).createQuery(jpqlCaptor.capture(), eq(HealthUser.class));

        String jpql = jpqlCaptor.getValue();
        assertTrue(jpql.contains("JOIN h.clinicNames clinic"));
        assertTrue(jpql.contains("LOWER(clinic) LIKE :clinic"));

        verify(healthUserQuery).setParameter("clinic", "%clinic a%");
    }

    @Test
    @DisplayName("findAll - Should filter by name")
    void testFindAll_FilterByName() {
        // Arrange
        List<HealthUser> users = Arrays.asList(testHealthUser);
        when(entityManager.createQuery(anyString(), eq(HealthUser.class))).thenReturn(healthUserQuery);
        when(healthUserQuery.getResultList()).thenReturn(users);

        // Act
        List<HealthUser> result = repository.findAll(null, "John Doe", null, 0, 20);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());

        ArgumentCaptor<String> jpqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(entityManager).createQuery(jpqlCaptor.capture(), eq(HealthUser.class));

        String jpql = jpqlCaptor.getValue();
        assertTrue(jpql.contains(
                "LOWER(CONCAT(CONCAT(COALESCE(h.firstName, ''), ' '), COALESCE(h.lastName, ''))) LIKE :name"));

        verify(healthUserQuery).setParameter("name", "%john doe%");
    }

    @Test
    @DisplayName("findAll - Should filter by all parameters")
    void testFindAll_FilterByAllParameters() {
        // Arrange
        List<HealthUser> users = Arrays.asList(testHealthUser);
        when(entityManager.createQuery(anyString(), eq(HealthUser.class))).thenReturn(healthUserQuery);
        when(healthUserQuery.getResultList()).thenReturn(users);

        // Act
        List<HealthUser> result = repository.findAll("Clinic A", "John", "54053584", 0, 20);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());

        verify(healthUserQuery).setParameter("ci", "%54053584%");
        verify(healthUserQuery).setParameter("clinic", "%clinic a%");
        verify(healthUserQuery).setParameter("name", "%john%");
    }

    @Test
    @DisplayName("count - Should count all health users without filters")
    void testCount_NoFilters() {
        // Arrange
        when(entityManager.createQuery(anyString(), eq(Long.class))).thenReturn(longQuery);
        when(longQuery.getSingleResult()).thenReturn(25L);

        // Act
        long result = repository.count(null, null, null);

        // Assert
        assertEquals(25L, result);

        verify(entityManager).createQuery("SELECT COUNT(h) FROM HealthUser h", Long.class);
    }

    @Test
    @DisplayName("count - Should count with filters")
    void testCount_WithFilters() {
        // Arrange
        when(entityManager.createQuery(anyString(), eq(Long.class))).thenReturn(longQuery);
        when(longQuery.getSingleResult()).thenReturn(5L);

        // Act
        long result = repository.count("Clinic A", "John", "54053584");

        // Assert
        assertEquals(5L, result);

        verify(longQuery).setParameter("ci", "%54053584%");
        verify(longQuery).setParameter("clinic", "%clinic a%");
        verify(longQuery).setParameter("name", "%john%");
    }

    @Test
    @DisplayName("findByCi - Should return health user by CI")
    void testFindByCi_Success() {
        // Arrange
        when(entityManager.createQuery(anyString(), eq(HealthUser.class))).thenReturn(healthUserQuery);
        when(healthUserQuery.setParameter(anyString(), any())).thenReturn(healthUserQuery);
        when(healthUserQuery.setMaxResults(anyInt())).thenReturn(healthUserQuery);
        when(healthUserQuery.getResultStream()).thenReturn(Arrays.asList(testHealthUser).stream());

        // Act
        HealthUser result = repository.findByCi("54053584");

        // Assert
        assertNotNull(result);
        assertEquals(testHealthUser, result);

        verify(healthUserQuery).setParameter("ci", "54053584");
        verify(healthUserQuery).setMaxResults(1);
    }

    @Test
    @DisplayName("findByCi - Should throw ValidationException for null CI")
    void testFindByCi_NullCi() {
        // Act & Assert
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> repository.findByCi(null));

        assertEquals("Health user CI must not be null or empty", exception.getMessage());
        verify(entityManager, never()).createQuery(anyString(), any());
    }

    @Test
    @DisplayName("findByCi - Should throw ValidationException for empty CI")
    void testFindByCi_EmptyCi() {
        // Act & Assert
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> repository.findByCi(""));

        assertEquals("Health user CI must not be null or empty", exception.getMessage());
    }

    @Test
    @DisplayName("findByCi - Should return null when no user found")
    void testFindByCi_NotFound() {
        // Arrange - Mock the entire query chain to return empty results
        when(entityManager.createQuery(anyString(), eq(HealthUser.class))).thenReturn(healthUserQuery);
        // Make sure the query methods return the query itself for chaining
        when(healthUserQuery.setParameter(anyString(), any())).thenReturn(healthUserQuery);
        when(healthUserQuery.setMaxResults(anyInt())).thenReturn(healthUserQuery);
        // Return an empty stream - when findFirst().orElse(null) is called, it returns
        // null
        when(healthUserQuery.getResultStream()).thenReturn(Stream.empty());

        // Act
        HealthUser result = repository.findByCi("99999999");

        // Assert
        assertNull(result, "Should return null when no health user is found");

        // Verify the query was executed correctly
        verify(entityManager).createQuery("SELECT h FROM HealthUser h WHERE h.ci = :ci", HealthUser.class);
        verify(healthUserQuery).setParameter("ci", "99999999");
        verify(healthUserQuery).setMaxResults(1);
        verify(healthUserQuery).getResultStream();
    }

    @Test
    @DisplayName("findById - Should return health user by ID")
    void testFindById_Success() {
        // Arrange
        when(entityManager.find(HealthUser.class, "test-id-123")).thenReturn(testHealthUser);

        // Act
        HealthUser result = repository.findById("test-id-123");

        // Assert
        assertNotNull(result);
        assertEquals(testHealthUser, result);

        verify(entityManager).find(HealthUser.class, "test-id-123");
    }

    @Test
    @DisplayName("findById - Should throw ValidationException for null ID")
    void testFindById_NullId() {
        // Act & Assert
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> repository.findById(null));

        assertEquals("Health user ID must not be null or empty", exception.getMessage());
        verify(entityManager, never()).find(any(), any());
    }

    @Test
    @DisplayName("create - Should persist health user")
    void testCreate_Success() {
        // Arrange
        // No stubbing needed for persist

        // Act
        HealthUser result = repository.create(testHealthUser);

        // Assert
        assertNotNull(result);
        assertEquals(testHealthUser, result);

        verify(entityManager).persist(testHealthUser);
    }

    @Test
    @DisplayName("create - Should throw ValidationException for null health user")
    void testCreate_NullHealthUser() {
        // Act & Assert
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> repository.create(null));

        assertEquals("HealthUser must not be null", exception.getMessage());
        verify(entityManager, never()).persist(any());
    }

    @Test
    @DisplayName("linkClinicToHealthUser - Should link clinic when not already linked")
    void testLinkClinicToHealthUser_NewClinic() {
        // Arrange
        String healthUserCi = "54053584";
        String clinicName = "New Clinic";

        when(entityManager.createQuery(anyString(), eq(HealthUser.class))).thenReturn(healthUserQuery);
        when(healthUserQuery.setParameter(anyString(), any())).thenReturn(healthUserQuery);
        when(healthUserQuery.setMaxResults(anyInt())).thenReturn(healthUserQuery);
        when(healthUserQuery.getResultStream()).thenReturn(Arrays.asList(testHealthUser).stream());
        when(entityManager.merge(testHealthUser)).thenReturn(testHealthUser);

        // Act
        HealthUser result = repository.linkClinicToHealthUser(healthUserCi, clinicName);

        // Assert
        assertNotNull(result);
        assertTrue(result.getClinicNames().contains("New Clinic"));

        verify(entityManager).merge(testHealthUser);
    }

    @Test
    @DisplayName("linkClinicToHealthUser - Should not link clinic when already linked")
    void testLinkClinicToHealthUser_AlreadyLinked() {
        // Arrange
        String healthUserCi = "54053584";
        String clinicName = "Clinic A"; // Already in testHealthUser's clinicNames

        when(entityManager.createQuery(anyString(), eq(HealthUser.class))).thenReturn(healthUserQuery);
        when(healthUserQuery.setParameter(anyString(), any())).thenReturn(healthUserQuery);
        when(healthUserQuery.setMaxResults(anyInt())).thenReturn(healthUserQuery);
        when(healthUserQuery.getResultStream()).thenReturn(Arrays.asList(testHealthUser).stream());

        // Act
        HealthUser result = repository.linkClinicToHealthUser(healthUserCi, clinicName);

        // Assert
        assertNotNull(result);
        assertTrue(result.getClinicNames().contains("Clinic A"));

        verify(entityManager, never()).merge(any());
    }

    @Test
    @DisplayName("fetchClinicalHistory - Should successfully fetch clinical history")
    void testFetchClinicalHistory_Success() throws Exception {
        // Arrange
        String healthUserCi = "54053584";
        String healthWorkerCi = "87654321";
        String clinicName = "Clinic A";
        String providerName = "Provider X";

        String mockResponse = """
                [
                    {
                        "doc_id": "doc-1",
                        "created_by": "87654321",
                        "clinic_name": "Clinic A",
                        "s3_url": "https://s3.amazonaws.com/bucket/doc.pdf",
                        "created_at": "2023-01-01T10:00:00Z"
                    }
                ]
                """;

        when(config.getDocumentsApiBaseUrl()).thenReturn("http://api.example.com/");
        when(config.getDocumentsApiKey()).thenReturn("test-api-key");

        @SuppressWarnings("unchecked")
        HttpResponse<String> mockHttpResponse = mock(HttpResponse.class);
        when(mockHttpResponse.statusCode()).thenReturn(200);
        when(mockHttpResponse.body()).thenReturn(mockResponse);

        doReturn(mockHttpResponse).when(httpClient).send(any(HttpRequest.class), any());

        // Act
        List<DocumentResponseDTO> result = repository.fetchClinicalHistory(healthUserCi, healthWorkerCi, clinicName,
                providerName);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("doc-1", result.get(0).getId());
        assertEquals("https://s3.amazonaws.com/bucket/doc.pdf", result.get(0).getS3Url());

        verify(httpClient).send(any(HttpRequest.class), any());
    }

    @Test
    @DisplayName("fetchClinicalHistory - Should throw ValidationException for null parameters")
    void testFetchClinicalHistory_NullParameters() {
        // Act & Assert
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> repository.fetchClinicalHistory(null, "87654321", "Clinic A", "Provider X"));

        assertEquals("Health user CI is required", exception.getMessage());
    }

    @Test
    @DisplayName("fetchClinicalHistory - Should throw IllegalStateException for HTTP error")
    void testFetchClinicalHistory_HttpError() throws Exception {
        // Arrange
        String healthUserCi = "54053584";
        String healthWorkerCi = "87654321";
        String clinicName = "Clinic A";
        String providerName = "Provider_X";

        when(config.getDocumentsApiBaseUrl()).thenReturn("http://api.example.com/");
        when(config.getDocumentsApiKey()).thenReturn("test-api-key");

        @SuppressWarnings("unchecked")
        HttpResponse<String> mockHttpResponse = mock(HttpResponse.class);
        when(mockHttpResponse.statusCode()).thenReturn(500);
        when(mockHttpResponse.body()).thenReturn("Internal Server Error");

        doReturn(mockHttpResponse).when(httpClient).send(any(HttpRequest.class), any());

        // Act & Assert
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> repository.fetchClinicalHistory(healthUserCi, healthWorkerCi, clinicName, providerName));

        assertTrue(exception.getMessage().contains("HTTP 500"));
    }

    @Test
    @DisplayName("fetchHealthUserAccessHistory - Should successfully fetch access history")
    void testFetchHealthUserAccessHistory_Success() throws Exception {
        // Arrange
        String healthUserCi = "54053584";

        String mockResponse = """
                [
                    {
                        "id": 1,
                        "health_user_ci": "54053584",
                        "health_worker_ci": "87654321",
                        "clinic_name": "Clinic A",
                        "requested_at": "2023-01-01T10:00:00Z",
                        "viewed": true,
                        "decision_reason": "Approved"
                    }
                ]
                """;

        when(config.getDocumentsApiBaseUrl()).thenReturn("http://api.example.com/");
        when(config.getDocumentsApiKey()).thenReturn("test-api-key");

        @SuppressWarnings("unchecked")
        HttpResponse<String> mockHttpResponse = mock(HttpResponse.class);
        when(mockHttpResponse.statusCode()).thenReturn(200);
        when(mockHttpResponse.body()).thenReturn(mockResponse);

        doReturn(mockHttpResponse).when(httpClient).send(any(HttpRequest.class), any());

        // Act
        List<ClinicalHistoryAccessLogResponseDTO> result = repository.fetchHealthUserAccessHistory(healthUserCi);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals("54053584", result.get(0).getHealthUserCi());
        assertEquals("87654321", result.get(0).getHealthWorkerCi());
        assertEquals("Clinic A", result.get(0).getClinicName());
        assertTrue(result.get(0).getViewed());
        assertEquals("Approved", result.get(0).getDecisionReason());

        verify(httpClient).send(any(HttpRequest.class), any());
    }

    @Test
    @DisplayName("fetchHealthUserAccessHistory - Should throw ValidationException for null CI")
    void testFetchHealthUserAccessHistory_NullCi() {
        // Act & Assert
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> repository.fetchHealthUserAccessHistory(null));

        assertEquals("Health user CI is required", exception.getMessage());
    }

    @Test
    @DisplayName("fetchHealthUserAccessHistory - Should throw IllegalStateException for HTTP error")
    void testFetchHealthUserAccessHistory_HttpError() throws Exception {
        // Arrange
        String healthUserCi = "54053584";

        when(config.getDocumentsApiBaseUrl()).thenReturn("http://api.example.com/");
        when(config.getDocumentsApiKey()).thenReturn("test-api-key");

        @SuppressWarnings("unchecked")
        HttpResponse<String> mockHttpResponse = mock(HttpResponse.class);
        when(mockHttpResponse.statusCode()).thenReturn(500);
        when(mockHttpResponse.body()).thenReturn("Internal Server Error");

        doReturn(mockHttpResponse).when(httpClient).send(any(HttpRequest.class), any());

        // Act & Assert
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> repository.fetchHealthUserAccessHistory(healthUserCi));

        assertTrue(exception.getMessage().contains("Failed to fetch health user access history: HTTP 500"));
    }
}
