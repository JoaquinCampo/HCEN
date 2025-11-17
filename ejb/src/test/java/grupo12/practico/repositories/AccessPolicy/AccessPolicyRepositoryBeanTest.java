package grupo12.practico.repositories.AccessPolicy;

import grupo12.practico.models.ClinicAccessPolicy;
import grupo12.practico.models.HealthUser;
import grupo12.practico.models.HealthWorkerAccessPolicy;
import grupo12.practico.models.SpecialtyAccessPolicy;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AccessPolicyRepositoryBean Tests")
class AccessPolicyRepositoryBeanTest {

    @Mock
    private EntityManager entityManager;

    @Mock
    private TypedQuery<ClinicAccessPolicy> clinicPoliciesQuery;

    @Mock
    private TypedQuery<HealthWorkerAccessPolicy> healthWorkerPoliciesQuery;

    @Mock
    private TypedQuery<SpecialtyAccessPolicy> specialtyPoliciesQuery;

    @Mock
    private TypedQuery<Long> countQuery;

    private AccessPolicyRepositoryBean repository;

    private HealthUser healthUser;
    private ClinicAccessPolicy clinicPolicy;
    private HealthWorkerAccessPolicy healthWorkerPolicy;
    private SpecialtyAccessPolicy specialtyPolicy;

    @BeforeEach
    void setUp() throws Exception {
        repository = new AccessPolicyRepositoryBean();
        Field emField = AccessPolicyRepositoryBean.class.getDeclaredField("em");
        emField.setAccessible(true);
        emField.set(repository, entityManager);

        healthUser = new HealthUser();
        healthUser.setId("health-user-id-123");
        healthUser.setCi("55555555");
        healthUser.setCreatedAt(LocalDate.of(2023, 1, 1));
        healthUser.setUpdatedAt(LocalDate.of(2023, 1, 2));

        clinicPolicy = new ClinicAccessPolicy();
        clinicPolicy.setId("clinic-policy-id");
        clinicPolicy.setHealthUser(healthUser);
        clinicPolicy.setClinicName("Clinic One");
        clinicPolicy.setCreatedAt(LocalDate.of(2023, 2, 1));

        healthWorkerPolicy = new HealthWorkerAccessPolicy();
        healthWorkerPolicy.setId("hw-policy-id");
        healthWorkerPolicy.setHealthUser(healthUser);
        healthWorkerPolicy.setHealthWorkerCi("33333333");
        healthWorkerPolicy.setClinicName("Clinic One");
        healthWorkerPolicy.setCreatedAt(LocalDate.of(2023, 2, 2));

        specialtyPolicy = new SpecialtyAccessPolicy();
        specialtyPolicy.setId("specialty-policy-id");
        specialtyPolicy.setHealthUser(healthUser);
        specialtyPolicy.setSpecialtyName("Cardiology");
        specialtyPolicy.setCreatedAt(LocalDate.of(2023, 2, 3));
    }

    @Test
    @DisplayName("createClinicAccessPolicy - Should persist and return entity")
    void createClinicAccessPolicy_ShouldPersistEntity() {
        ClinicAccessPolicy result = repository.createClinicAccessPolicy(clinicPolicy);

        assertEquals(clinicPolicy, result);
        verify(entityManager).persist(clinicPolicy);
    }

    @Test
    @DisplayName("createHealthWorkerAccessPolicy - Should persist and return entity")
    void createHealthWorkerAccessPolicy_ShouldPersistEntity() {
        HealthWorkerAccessPolicy result = repository.createHealthWorkerAccessPolicy(healthWorkerPolicy);

        assertEquals(healthWorkerPolicy, result);
        verify(entityManager).persist(healthWorkerPolicy);
    }

    @Test
    @DisplayName("createSpecialtyAccessPolicy - Should persist and return entity")
    void createSpecialtyAccessPolicy_ShouldPersistEntity() {
        SpecialtyAccessPolicy result = repository.createSpecialtyAccessPolicy(specialtyPolicy);

        assertEquals(specialtyPolicy, result);
        verify(entityManager).persist(specialtyPolicy);
    }

    @Test
    @DisplayName("findAllClinicAccessPolicies - Should return policies for health user")
    void findAllClinicAccessPolicies_ShouldReturnPolicies() {
        when(entityManager.createQuery(
                "SELECT c FROM ClinicAccessPolicy c WHERE c.healthUser.id = :healthUserId",
                ClinicAccessPolicy.class)).thenReturn(clinicPoliciesQuery);
        when(clinicPoliciesQuery.setParameter("healthUserId", healthUser.getId())).thenReturn(clinicPoliciesQuery);
        when(clinicPoliciesQuery.getResultList()).thenReturn(List.of(clinicPolicy));

        List<ClinicAccessPolicy> result = repository.findAllClinicAccessPolicies(healthUser.getId());

        assertEquals(1, result.size());
        assertEquals(clinicPolicy, result.get(0));
        verify(clinicPoliciesQuery).setParameter("healthUserId", healthUser.getId());
        verify(clinicPoliciesQuery).getResultList();
    }

    @Test
    @DisplayName("findAllClinicAccessPolicies - Should return empty list when none exist")
    void findAllClinicAccessPolicies_ShouldReturnEmptyList() {
        when(entityManager.createQuery(anyString(), eq(ClinicAccessPolicy.class))).thenReturn(clinicPoliciesQuery);
        when(clinicPoliciesQuery.setParameter(anyString(), any())).thenReturn(clinicPoliciesQuery);
        when(clinicPoliciesQuery.getResultList()).thenReturn(List.of());

        List<ClinicAccessPolicy> result = repository.findAllClinicAccessPolicies("missing-user");

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("findAllHealthWorkerAccessPolicies - Should return policies for health user")
    void findAllHealthWorkerAccessPolicies_ShouldReturnPolicies() {
        when(entityManager.createQuery(
                "SELECT h FROM HealthWorkerAccessPolicy h WHERE h.healthUser.id = :healthUserId",
                HealthWorkerAccessPolicy.class)).thenReturn(healthWorkerPoliciesQuery);
        when(healthWorkerPoliciesQuery.setParameter("healthUserId", healthUser.getId()))
                .thenReturn(healthWorkerPoliciesQuery);
        when(healthWorkerPoliciesQuery.getResultList()).thenReturn(List.of(healthWorkerPolicy));

        List<HealthWorkerAccessPolicy> result = repository.findAllHealthWorkerAccessPolicies(healthUser.getId());

        assertEquals(1, result.size());
        assertEquals(healthWorkerPolicy, result.get(0));
    }

    @Test
    @DisplayName("findAllSpecialtyAccessPolicies - Should return policies for health user")
    void findAllSpecialtyAccessPolicies_ShouldReturnPolicies() {
        when(entityManager.createQuery(
                "SELECT s FROM SpecialtyAccessPolicy s WHERE s.healthUser.id = :healthUserId",
                SpecialtyAccessPolicy.class)).thenReturn(specialtyPoliciesQuery);
        when(specialtyPoliciesQuery.setParameter("healthUserId", healthUser.getId()))
                .thenReturn(specialtyPoliciesQuery);
        when(specialtyPoliciesQuery.getResultList()).thenReturn(List.of(specialtyPolicy));

        List<SpecialtyAccessPolicy> result = repository.findAllSpecialtyAccessPolicies(healthUser.getId());

        assertEquals(1, result.size());
        assertEquals(specialtyPolicy, result.get(0));
    }

    @Test
    @DisplayName("deleteClinicAccessPolicy - Should remove entity when present")
    void deleteClinicAccessPolicy_ShouldRemoveWhenExists() {
        when(entityManager.find(ClinicAccessPolicy.class, clinicPolicy.getId())).thenReturn(clinicPolicy);

        repository.deleteClinicAccessPolicy(clinicPolicy.getId());

        verify(entityManager).remove(clinicPolicy);
    }

    @Test
    @DisplayName("deleteClinicAccessPolicy - Should ignore missing entity")
    void deleteClinicAccessPolicy_ShouldNotRemoveWhenMissing() {
        when(entityManager.find(ClinicAccessPolicy.class, "missing"))
                .thenReturn(null);

        repository.deleteClinicAccessPolicy("missing");

        verify(entityManager, never()).remove(any(ClinicAccessPolicy.class));
    }

    @Test
    @DisplayName("deleteHealthWorkerAccessPolicy - Should remove entity when present")
    void deleteHealthWorkerAccessPolicy_ShouldRemoveWhenExists() {
        when(entityManager.find(HealthWorkerAccessPolicy.class, healthWorkerPolicy.getId()))
                .thenReturn(healthWorkerPolicy);

        repository.deleteHealthWorkerAccessPolicy(healthWorkerPolicy.getId());

        verify(entityManager).remove(healthWorkerPolicy);
    }

    @Test
    @DisplayName("deleteHealthWorkerAccessPolicy - Should ignore missing entity")
    void deleteHealthWorkerAccessPolicy_ShouldNotRemoveWhenMissing() {
        when(entityManager.find(HealthWorkerAccessPolicy.class, "missing"))
                .thenReturn(null);

        repository.deleteHealthWorkerAccessPolicy("missing");

        verify(entityManager, never()).remove(any(HealthWorkerAccessPolicy.class));
    }

    @Test
    @DisplayName("deleteSpecialtyAccessPolicy - Should remove entity when present")
    void deleteSpecialtyAccessPolicy_ShouldRemoveWhenExists() {
        when(entityManager.find(SpecialtyAccessPolicy.class, specialtyPolicy.getId()))
                .thenReturn(specialtyPolicy);

        repository.deleteSpecialtyAccessPolicy(specialtyPolicy.getId());

        verify(entityManager).remove(specialtyPolicy);
    }

    @Test
    @DisplayName("deleteSpecialtyAccessPolicy - Should ignore missing entity")
    void deleteSpecialtyAccessPolicy_ShouldNotRemoveWhenMissing() {
        when(entityManager.find(SpecialtyAccessPolicy.class, "missing"))
                .thenReturn(null);

        repository.deleteSpecialtyAccessPolicy("missing");

        verify(entityManager, never()).remove(any(SpecialtyAccessPolicy.class));
    }

    @Test
    @DisplayName("hasClinicAccess - Should return true when count > 0")
    void hasClinicAccess_ShouldReturnTrueWhenCountPositive() {
        when(entityManager.createQuery(
                "SELECT COUNT(c) FROM ClinicAccessPolicy c WHERE c.healthUser.id = :healthUserId AND c.clinicName = :clinicName",
                Long.class)).thenReturn(countQuery);
        when(countQuery.setParameter(anyString(), any())).thenReturn(countQuery);
        when(countQuery.getSingleResult()).thenReturn(1L);

        boolean result = repository.hasClinicAccess(healthUser.getId(), "Clinic One");

        assertTrue(result);
    }

    @Test
    @DisplayName("hasClinicAccess - Should return false when count == 0")
    void hasClinicAccess_ShouldReturnFalseWhenNoRecords() {
        when(entityManager.createQuery(anyString(), eq(Long.class))).thenReturn(countQuery);
        when(countQuery.setParameter(anyString(), any())).thenReturn(countQuery);
        when(countQuery.getSingleResult()).thenReturn(0L);

        boolean result = repository.hasClinicAccess("missing", "Clinic One");

        assertFalse(result);
    }

    @Test
    @DisplayName("hasHealthWorkerAccess - Should return true when count > 0")
    void hasHealthWorkerAccess_ShouldReturnTrueWhenCountPositive() {
        when(entityManager.createQuery(
                "SELECT COUNT(h) FROM HealthWorkerAccessPolicy h WHERE h.healthUser.id = :healthUserId AND h.healthWorkerCi = :healthWorkerCi",
                Long.class)).thenReturn(countQuery);
        when(countQuery.setParameter(anyString(), any())).thenReturn(countQuery);
        when(countQuery.getSingleResult()).thenReturn(2L);

        boolean result = repository.hasHealthWorkerAccess(healthUser.getId(), "33333333");

        assertTrue(result);
    }

    @Test
    @DisplayName("hasHealthWorkerAccess - Should return false when count == 0")
    void hasHealthWorkerAccess_ShouldReturnFalseWhenNoRecords() {
        when(entityManager.createQuery(anyString(), eq(Long.class))).thenReturn(countQuery);
        when(countQuery.setParameter(anyString(), any())).thenReturn(countQuery);
        when(countQuery.getSingleResult()).thenReturn(0L);

        boolean result = repository.hasHealthWorkerAccess("missing", "nope");

        assertFalse(result);
    }

    @Test
    @DisplayName("hasSpecialtyAccess - Should return true when count > 0")
    void hasSpecialtyAccess_ShouldReturnTrueWhenCountPositive() {
        when(entityManager.createQuery(
                "SELECT COUNT(s) FROM SpecialtyAccessPolicy s WHERE s.healthUser.id = :healthUserId AND s.specialtyName IN :specialtyNames",
                Long.class)).thenReturn(countQuery);
        when(countQuery.setParameter(anyString(), any())).thenReturn(countQuery);
        when(countQuery.getSingleResult()).thenReturn(1L);

        boolean result = repository.hasSpecialtyAccess(healthUser.getId(), List.of("Cardiology"));

        assertTrue(result);
    }

    @Test
    @DisplayName("hasSpecialtyAccess - Should return false when count == 0")
    void hasSpecialtyAccess_ShouldReturnFalseWhenNoRecords() {
        when(entityManager.createQuery(anyString(), eq(Long.class))).thenReturn(countQuery);
        when(countQuery.setParameter(anyString(), any())).thenReturn(countQuery);
        when(countQuery.getSingleResult()).thenReturn(0L);

        boolean result = repository.hasSpecialtyAccess("missing", List.of("Dermatology"));

        assertFalse(result);
    }
}
