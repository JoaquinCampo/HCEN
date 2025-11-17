package grupo12.practico.repositories.AccessRequest;

import grupo12.practico.models.AccessRequest;
import grupo12.practico.models.HealthUser;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AccessRequestRepositoryBean Tests")
class AccessRequestRepositoryBeanTest {

    @Mock
    private EntityManager entityManager;

    @Mock
    private TypedQuery<AccessRequest> typedQuery;

    private AccessRequestRepositoryBean repository;

    private AccessRequest accessRequest;
    private HealthUser healthUser;

    @BeforeEach
    void setUp() throws Exception {
        repository = new AccessRequestRepositoryBean();

        // Use reflection to inject mocked EntityManager
        var emField = AccessRequestRepositoryBean.class.getDeclaredField("em");
        emField.setAccessible(true);
        emField.set(repository, entityManager);

        // Setup test data
        healthUser = new HealthUser();
        healthUser.setId("health-user-id");
        healthUser.setCi("12345678");

        accessRequest = new AccessRequest();
        accessRequest.setId("access-request-id");
        accessRequest.setHealthUser(healthUser);
        accessRequest.setHealthWorkerCi("87654321");
        accessRequest.setClinicName("Test Clinic");
        accessRequest.setSpecialtyNames(Arrays.asList("Cardiology", "Neurology"));
        accessRequest.setCreatedAt(LocalDate.of(2023, 1, 1));
    }

    @Test
    @DisplayName("createAccessRequest - Should persist and return access request")
    void createAccessRequest_ShouldPersistAndReturnAccessRequest() {
        doNothing().when(entityManager).persist(accessRequest);

        AccessRequest result = repository.createAccessRequest(accessRequest);

        assertEquals(accessRequest, result);
        verify(entityManager).persist(accessRequest);
    }

    @Test
    @DisplayName("findAccessRequestById - Should return access request when found")
    void findAccessRequestById_ShouldReturnAccessRequestWhenFound() {
        List<AccessRequest> expectedResults = Arrays.asList(accessRequest);
        when(entityManager.createQuery(anyString(), eq(AccessRequest.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter("id", "access-request-id")).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(expectedResults);

        AccessRequest result = repository.findAccessRequestById("access-request-id");

        assertEquals(accessRequest, result);
        verify(entityManager).createQuery(
                "SELECT ar FROM AccessRequest ar LEFT JOIN FETCH ar.specialtyNames WHERE ar.id = :id",
                AccessRequest.class);
        verify(typedQuery).setParameter("id", "access-request-id");
        verify(typedQuery).getResultList();
    }

    @Test
    @DisplayName("findAccessRequestById - Should return null when not found")
    void findAccessRequestById_ShouldReturnNullWhenNotFound() {
        List<AccessRequest> expectedResults = Arrays.asList();
        when(entityManager.createQuery(anyString(), eq(AccessRequest.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter("id", "non-existent-id")).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(expectedResults);

        AccessRequest result = repository.findAccessRequestById("non-existent-id");

        assertNull(result);
        verify(entityManager).createQuery(
                "SELECT ar FROM AccessRequest ar LEFT JOIN FETCH ar.specialtyNames WHERE ar.id = :id",
                AccessRequest.class);
        verify(typedQuery).setParameter("id", "non-existent-id");
        verify(typedQuery).getResultList();
    }

    @Test
    @DisplayName("findAllAccessRequests - Should return all access requests when no filters")
    void findAllAccessRequests_ShouldReturnAllAccessRequestsWhenNoFilters() {
        List<AccessRequest> expectedRequests = Arrays.asList(accessRequest);
        when(entityManager.createQuery(anyString(), eq(AccessRequest.class))).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(expectedRequests);

        List<AccessRequest> result = repository.findAllAccessRequests(null, null, null);

        assertEquals(expectedRequests, result);
        verify(entityManager).createQuery(
                "SELECT DISTINCT ar FROM AccessRequest ar LEFT JOIN FETCH ar.specialtyNames WHERE 1=1",
                AccessRequest.class);
        verify(typedQuery).getResultList();
    }

    @Test
    @DisplayName("findAllAccessRequests - Should filter by health user id")
    void findAllAccessRequests_ShouldFilterByHealthUserId() {
        List<AccessRequest> expectedRequests = Arrays.asList(accessRequest);
        when(entityManager.createQuery(anyString(), eq(AccessRequest.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter("healthUserId", "health-user-id")).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(expectedRequests);

        List<AccessRequest> result = repository.findAllAccessRequests("health-user-id", null, null);

        assertEquals(expectedRequests, result);
        verify(entityManager).createQuery(
                "SELECT DISTINCT ar FROM AccessRequest ar LEFT JOIN FETCH ar.specialtyNames WHERE 1=1 AND ar.healthUser.id = :healthUserId",
                AccessRequest.class);
        verify(typedQuery).setParameter("healthUserId", "health-user-id");
    }

    @Test
    @DisplayName("findAllAccessRequests - Should filter by health worker ci")
    void findAllAccessRequests_ShouldFilterByHealthWorkerCi() {
        List<AccessRequest> expectedRequests = Arrays.asList(accessRequest);
        when(entityManager.createQuery(anyString(), eq(AccessRequest.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter("healthWorkerCi", "87654321")).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(expectedRequests);

        List<AccessRequest> result = repository.findAllAccessRequests(null, "87654321", null);

        assertEquals(expectedRequests, result);
        verify(entityManager).createQuery(
                "SELECT DISTINCT ar FROM AccessRequest ar LEFT JOIN FETCH ar.specialtyNames WHERE 1=1 AND ar.healthWorkerCi = :healthWorkerCi",
                AccessRequest.class);
        verify(typedQuery).setParameter("healthWorkerCi", "87654321");
    }

    @Test
    @DisplayName("findAllAccessRequests - Should filter by clinic name")
    void findAllAccessRequests_ShouldFilterByClinicName() {
        List<AccessRequest> expectedRequests = Arrays.asList(accessRequest);
        when(entityManager.createQuery(anyString(), eq(AccessRequest.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter("clinicName", "Test Clinic")).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(expectedRequests);

        List<AccessRequest> result = repository.findAllAccessRequests(null, null, "Test Clinic");

        assertEquals(expectedRequests, result);
        verify(entityManager).createQuery(
                "SELECT DISTINCT ar FROM AccessRequest ar LEFT JOIN FETCH ar.specialtyNames WHERE 1=1 AND ar.clinicName = :clinicName",
                AccessRequest.class);
        verify(typedQuery).setParameter("clinicName", "Test Clinic");
    }

    @Test
    @DisplayName("findAllAccessRequests - Should filter by all parameters")
    void findAllAccessRequests_ShouldFilterByAllParameters() {
        List<AccessRequest> expectedRequests = Arrays.asList(accessRequest);
        when(entityManager.createQuery(anyString(), eq(AccessRequest.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter("healthUserId", "health-user-id")).thenReturn(typedQuery);
        when(typedQuery.setParameter("healthWorkerCi", "87654321")).thenReturn(typedQuery);
        when(typedQuery.setParameter("clinicName", "Test Clinic")).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(expectedRequests);

        List<AccessRequest> result = repository.findAllAccessRequests("health-user-id", "87654321", "Test Clinic");

        assertEquals(expectedRequests, result);
        verify(entityManager).createQuery(
                "SELECT DISTINCT ar FROM AccessRequest ar LEFT JOIN FETCH ar.specialtyNames WHERE 1=1 AND ar.healthUser.id = :healthUserId AND ar.healthWorkerCi = :healthWorkerCi AND ar.clinicName = :clinicName",
                AccessRequest.class);
        verify(typedQuery).setParameter("healthUserId", "health-user-id");
        verify(typedQuery).setParameter("healthWorkerCi", "87654321");
        verify(typedQuery).setParameter("clinicName", "Test Clinic");
    }

    @Test
    @DisplayName("deleteAccessRequest - Should remove access request when found")
    void deleteAccessRequest_ShouldRemoveAccessRequestWhenFound() {
        when(entityManager.find(AccessRequest.class, "access-request-id")).thenReturn(accessRequest);

        repository.deleteAccessRequest("access-request-id");

        verify(entityManager).find(AccessRequest.class, "access-request-id");
        verify(entityManager).remove(accessRequest);
    }

    @Test
    @DisplayName("deleteAccessRequest - Should not throw when access request not found")
    void deleteAccessRequest_ShouldNotThrowWhenAccessRequestNotFound() {
        when(entityManager.find(AccessRequest.class, "non-existent-id")).thenReturn(null);

        assertDoesNotThrow(() -> repository.deleteAccessRequest("non-existent-id"));

        verify(entityManager).find(AccessRequest.class, "non-existent-id");
        verify(entityManager, never()).remove(any());
    }
}