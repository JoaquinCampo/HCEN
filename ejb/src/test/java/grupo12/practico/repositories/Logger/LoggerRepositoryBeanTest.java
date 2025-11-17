package grupo12.practico.repositories.Logger;

import grupo12.practico.models.AccessRequestLog;
import grupo12.practico.models.ClinicalHistoryLog;
import grupo12.practico.models.DocumentLog;
import grupo12.practico.models.HealthUserLog;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("LoggerRepositoryBean Tests")
class LoggerRepositoryBeanTest {

    @Mock
    private EntityManager entityManager;

    @Mock
    private TypedQuery<HealthUserLog> healthUserLogQuery;

    @Mock
    private TypedQuery<AccessRequestLog> accessRequestLogQuery;

    @Mock
    private TypedQuery<ClinicalHistoryLog> clinicalHistoryLogQuery;

    @Mock
    private TypedQuery<DocumentLog> documentLogQuery;

    @Mock
    private TypedQuery<Long> countQuery;

    @Mock
    private TypedQuery<Object[]> analyticsQuery;

    private LoggerRepositoryBean repository;

    private HealthUserLog healthUserLog;
    private AccessRequestLog accessRequestLog;
    private ClinicalHistoryLog clinicalHistoryLog;
    private DocumentLog documentLog;

    @BeforeEach
    void setUp() throws Exception {
        repository = new LoggerRepositoryBean();
        Field emField = LoggerRepositoryBean.class.getDeclaredField("em");
        emField.setAccessible(true);
        emField.set(repository, entityManager);

        LocalDateTime now = LocalDateTime.of(2023, 6, 15, 10, 30, 0);

        healthUserLog = new HealthUserLog();
        healthUserLog.setId("health-user-log-id");
        healthUserLog.setHealthUserCi("55555555");
        healthUserLog.setAction("CREATED");
        healthUserLog.setClinicName("Clinic One");
        healthUserLog.setTimestamp(now);

        accessRequestLog = new AccessRequestLog();
        accessRequestLog.setId("access-request-log-id");
        accessRequestLog.setAccessRequestId("request-123");
        accessRequestLog.setHealthUserCi("55555555");
        accessRequestLog.setHealthWorkerCi("33333333");
        accessRequestLog.setClinicName("Clinic One");
        accessRequestLog.setSpecialtyNames(List.of("Cardiology", "Neurology"));
        accessRequestLog.setAction("REQUESTED");
        accessRequestLog.setTimestamp(now);

        clinicalHistoryLog = new ClinicalHistoryLog();
        clinicalHistoryLog.setId("clinical-history-log-id");
        clinicalHistoryLog.setHealthUserCi("55555555");
        clinicalHistoryLog.setAccessorCi("33333333");
        clinicalHistoryLog.setAccessorType("HEALTH_WORKER");
        clinicalHistoryLog.setClinicName("Clinic One");
        clinicalHistoryLog.setSpecialtyNames(List.of("Cardiology"));
        clinicalHistoryLog.setAccessType("BY_CLINIC");
        clinicalHistoryLog.setTimestamp(now);

        documentLog = new DocumentLog();
        documentLog.setId("document-log-id");
        documentLog.setDocumentId("doc-123");
        documentLog.setHealthUserCi("55555555");
        documentLog.setHealthWorkerCi("33333333");
        documentLog.setClinicName("Clinic One");
        documentLog.setAction("CREATED");
        documentLog.setTimestamp(now);
    }

    // HealthUserLog Tests
    @Test
    @DisplayName("createHealthUserLog - Should persist and flush entity")
    void createHealthUserLog_ShouldPersistAndFlush() {
        HealthUserLog result = repository.createHealthUserLog(healthUserLog);

        assertEquals(healthUserLog, result);
        verify(entityManager).persist(healthUserLog);
        verify(entityManager).flush();
    }

    @Test
    @DisplayName("findHealthUserLogs - Should return logs with healthUserCi filter")
    void findHealthUserLogs_ShouldReturnFilteredLogs() {
        when(entityManager.createQuery(anyString(), eq(HealthUserLog.class))).thenReturn(healthUserLogQuery);
        when(healthUserLogQuery.setParameter(anyString(), any())).thenReturn(healthUserLogQuery);
        when(healthUserLogQuery.getResultList()).thenReturn(List.of(healthUserLog));

        List<HealthUserLog> result = repository.findHealthUserLogs("55555555", 0, 10);

        assertEquals(1, result.size());
        assertEquals(healthUserLog, result.get(0));
        verify(healthUserLogQuery).setParameter("healthUserCi", "55555555");
        verify(healthUserLogQuery).setFirstResult(0);
        verify(healthUserLogQuery).setMaxResults(10);
    }

    @Test
    @DisplayName("findHealthUserLogs - Should return logs without filters")
    void findHealthUserLogs_ShouldReturnAllLogs() {
        when(entityManager.createQuery(anyString(), eq(HealthUserLog.class))).thenReturn(healthUserLogQuery);
        when(healthUserLogQuery.getResultList()).thenReturn(List.of(healthUserLog));

        List<HealthUserLog> result = repository.findHealthUserLogs(null, null, null);

        assertEquals(1, result.size());
        verify(healthUserLogQuery, never()).setFirstResult(anyInt());
        verify(healthUserLogQuery, never()).setMaxResults(anyInt());
    }

    @Test
    @DisplayName("countHealthUserLogs - Should return count with filter")
    void countHealthUserLogs_ShouldReturnCount() {
        when(entityManager.createQuery(anyString(), eq(Long.class))).thenReturn(countQuery);
        when(countQuery.setParameter(anyString(), any())).thenReturn(countQuery);
        when(countQuery.getSingleResult()).thenReturn(5L);

        long result = repository.countHealthUserLogs("55555555");

        assertEquals(5L, result);
        verify(countQuery).setParameter("healthUserCi", "55555555");
    }

    @Test
    @DisplayName("countHealthUserLogs - Should return count without filter")
    void countHealthUserLogs_ShouldReturnCountWithoutFilter() {
        when(entityManager.createQuery(anyString(), eq(Long.class))).thenReturn(countQuery);
        when(countQuery.getSingleResult()).thenReturn(10L);

        long result = repository.countHealthUserLogs(null);

        assertEquals(10L, result);
    }

    // AccessRequestLog Tests
    @Test
    @DisplayName("createAccessRequestLog - Should persist and flush entity")
    void createAccessRequestLog_ShouldPersistAndFlush() {
        AccessRequestLog result = repository.createAccessRequestLog(accessRequestLog);

        assertEquals(accessRequestLog, result);
        verify(entityManager).persist(accessRequestLog);
        verify(entityManager).flush();
    }

    @Test
    @DisplayName("findAccessRequestLogs - Should return logs with multiple filters")
    void findAccessRequestLogs_ShouldReturnFilteredLogs() {
        when(entityManager.createQuery(anyString(), eq(AccessRequestLog.class))).thenReturn(accessRequestLogQuery);
        when(accessRequestLogQuery.setParameter(anyString(), any())).thenReturn(accessRequestLogQuery);
        when(accessRequestLogQuery.getResultList()).thenReturn(List.of(accessRequestLog));

        List<AccessRequestLog> result = repository.findAccessRequestLogs("55555555", "33333333", "Clinic One", 0, 10);

        assertEquals(1, result.size());
        assertEquals(accessRequestLog, result.get(0));
        verify(accessRequestLogQuery).setParameter("healthUserCi", "55555555");
        verify(accessRequestLogQuery).setParameter("healthWorkerCi", "33333333");
        verify(accessRequestLogQuery).setParameter("clinicName", "Clinic One");
        verify(accessRequestLogQuery).setFirstResult(0);
        verify(accessRequestLogQuery).setMaxResults(10);
    }

    @Test
    @DisplayName("findAccessRequestLogs - Should return logs with partial filters")
    void findAccessRequestLogs_ShouldReturnLogsWithPartialFilters() {
        when(entityManager.createQuery(anyString(), eq(AccessRequestLog.class))).thenReturn(accessRequestLogQuery);
        when(accessRequestLogQuery.setParameter(anyString(), any())).thenReturn(accessRequestLogQuery);
        when(accessRequestLogQuery.getResultList()).thenReturn(List.of(accessRequestLog));

        List<AccessRequestLog> result = repository.findAccessRequestLogs("55555555", null, null, null, null);

        assertEquals(1, result.size());
        verify(accessRequestLogQuery).setParameter("healthUserCi", "55555555");
        verify(accessRequestLogQuery, never()).setParameter(eq("healthWorkerCi"), any());
        verify(accessRequestLogQuery, never()).setParameter(eq("clinicName"), any());
    }

    @Test
    @DisplayName("countAccessRequestLogs - Should return count with filters")
    void countAccessRequestLogs_ShouldReturnCount() {
        when(entityManager.createQuery(anyString(), eq(Long.class))).thenReturn(countQuery);
        when(countQuery.setParameter(anyString(), any())).thenReturn(countQuery);
        when(countQuery.getSingleResult()).thenReturn(3L);

        long result = repository.countAccessRequestLogs("55555555", "33333333", "Clinic One");

        assertEquals(3L, result);
        verify(countQuery).setParameter("healthUserCi", "55555555");
        verify(countQuery).setParameter("healthWorkerCi", "33333333");
        verify(countQuery).setParameter("clinicName", "Clinic One");
    }

    // ClinicalHistoryLog Tests
    @Test
    @DisplayName("createClinicalHistoryLog - Should persist and flush entity")
    void createClinicalHistoryLog_ShouldPersistAndFlush() {
        ClinicalHistoryLog result = repository.createClinicalHistoryLog(clinicalHistoryLog);

        assertEquals(clinicalHistoryLog, result);
        verify(entityManager).persist(clinicalHistoryLog);
        verify(entityManager).flush();
    }

    @Test
    @DisplayName("findClinicalHistoryLogs - Should return logs with filters")
    void findClinicalHistoryLogs_ShouldReturnFilteredLogs() {
        when(entityManager.createQuery(anyString(), eq(ClinicalHistoryLog.class))).thenReturn(clinicalHistoryLogQuery);
        when(clinicalHistoryLogQuery.setParameter(anyString(), any())).thenReturn(clinicalHistoryLogQuery);
        when(clinicalHistoryLogQuery.getResultList()).thenReturn(List.of(clinicalHistoryLog));

        List<ClinicalHistoryLog> result = repository.findClinicalHistoryLogs("55555555", "33333333", 0, 10);

        assertEquals(1, result.size());
        assertEquals(clinicalHistoryLog, result.get(0));
        verify(clinicalHistoryLogQuery).setParameter("healthUserCi", "55555555");
        verify(clinicalHistoryLogQuery).setParameter("accessorCi", "33333333");
        verify(clinicalHistoryLogQuery).setFirstResult(0);
        verify(clinicalHistoryLogQuery).setMaxResults(10);
    }

    @Test
    @DisplayName("countClinicalHistoryLogs - Should return count with filters")
    void countClinicalHistoryLogs_ShouldReturnCount() {
        when(entityManager.createQuery(anyString(), eq(Long.class))).thenReturn(countQuery);
        when(countQuery.setParameter(anyString(), any())).thenReturn(countQuery);
        when(countQuery.getSingleResult()).thenReturn(7L);

        long result = repository.countClinicalHistoryLogs("55555555", "33333333");

        assertEquals(7L, result);
    }

    // DocumentLog Tests
    @Test
    @DisplayName("createDocumentLog - Should persist and flush entity")
    void createDocumentLog_ShouldPersistAndFlush() {
        DocumentLog result = repository.createDocumentLog(documentLog);

        assertEquals(documentLog, result);
        verify(entityManager).persist(documentLog);
        verify(entityManager).flush();
    }

    @Test
    @DisplayName("findDocumentLogs - Should return logs with filters")
    void findDocumentLogs_ShouldReturnFilteredLogs() {
        when(entityManager.createQuery(anyString(), eq(DocumentLog.class))).thenReturn(documentLogQuery);
        when(documentLogQuery.setParameter(anyString(), any())).thenReturn(documentLogQuery);
        when(documentLogQuery.getResultList()).thenReturn(List.of(documentLog));

        List<DocumentLog> result = repository.findDocumentLogs("55555555", "33333333", "Clinic One", 0, 10);

        assertEquals(1, result.size());
        assertEquals(documentLog, result.get(0));
        verify(documentLogQuery).setParameter("healthUserCi", "55555555");
        verify(documentLogQuery).setParameter("healthWorkerCi", "33333333");
        verify(documentLogQuery).setParameter("clinicName", "Clinic One");
        verify(documentLogQuery).setFirstResult(0);
        verify(documentLogQuery).setMaxResults(10);
    }

    @Test
    @DisplayName("countDocumentLogs - Should return count with filters")
    void countDocumentLogs_ShouldReturnCount() {
        when(entityManager.createQuery(anyString(), eq(Long.class))).thenReturn(countQuery);
        when(countQuery.setParameter(anyString(), any())).thenReturn(countQuery);
        when(countQuery.getSingleResult()).thenReturn(2L);

        long result = repository.countDocumentLogs("55555555", "33333333", "Clinic One");

        assertEquals(2L, result);
    }

    // Analytics Tests
    @Test
    @DisplayName("countHealthUserLogsByAction - Should return action counts")
    void countHealthUserLogsByAction_ShouldReturnActionCounts() {
        LocalDateTime startDate = LocalDateTime.of(2023, 6, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2023, 6, 30, 23, 59);

        when(entityManager.createQuery(anyString(), eq(Object[].class))).thenReturn(analyticsQuery);
        when(analyticsQuery.setParameter("startDate", startDate)).thenReturn(analyticsQuery);
        when(analyticsQuery.setParameter("endDate", endDate)).thenReturn(analyticsQuery);
        when(analyticsQuery.getResultList()).thenReturn(List.of(
                new Object[] { "CREATED", 10L },
                new Object[] { "CLINIC_LINKED", 5L }));

        Map<String, Long> result = repository.countHealthUserLogsByAction(startDate, endDate);

        assertEquals(2, result.size());
        assertEquals(10L, result.get("CREATED"));
        assertEquals(5L, result.get("CLINIC_LINKED"));
    }

    @Test
    @DisplayName("countAccessRequestLogsByAction - Should return action counts")
    void countAccessRequestLogsByAction_ShouldReturnActionCounts() {
        LocalDateTime startDate = LocalDateTime.of(2023, 6, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2023, 6, 30, 23, 59);

        when(entityManager.createQuery(anyString(), eq(Object[].class))).thenReturn(analyticsQuery);
        when(analyticsQuery.setParameter("startDate", startDate)).thenReturn(analyticsQuery);
        when(analyticsQuery.setParameter("endDate", endDate)).thenReturn(analyticsQuery);
        when(analyticsQuery.getResultList()).thenReturn(List.of(
                new Object[] { "REQUESTED", 15L },
                new Object[] { "ACCEPTED_BY_CLINIC", 8L }));

        Map<String, Long> result = repository.countAccessRequestLogsByAction(startDate, endDate);

        assertEquals(2, result.size());
        assertEquals(15L, result.get("REQUESTED"));
        assertEquals(8L, result.get("ACCEPTED_BY_CLINIC"));
    }

    @Test
    @DisplayName("countClinicalHistoryLogsByAccessType - Should return access type counts")
    void countClinicalHistoryLogsByAccessType_ShouldReturnAccessTypeCounts() {
        LocalDateTime startDate = LocalDateTime.of(2023, 6, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2023, 6, 30, 23, 59);

        when(entityManager.createQuery(anyString(), eq(Object[].class))).thenReturn(analyticsQuery);
        when(analyticsQuery.setParameter("startDate", startDate)).thenReturn(analyticsQuery);
        when(analyticsQuery.setParameter("endDate", endDate)).thenReturn(analyticsQuery);
        when(analyticsQuery.getResultList()).thenReturn(List.of(
                new Object[] { "SELF_ACCESS", 12L },
                new Object[] { "BY_CLINIC", 6L }));

        Map<String, Long> result = repository.countClinicalHistoryLogsByAccessType(startDate, endDate);

        assertEquals(2, result.size());
        assertEquals(12L, result.get("SELF_ACCESS"));
        assertEquals(6L, result.get("BY_CLINIC"));
    }

    @Test
    @DisplayName("countDocumentLogsByDateRange - Should return count")
    void countDocumentLogsByDateRange_ShouldReturnCount() {
        LocalDateTime startDate = LocalDateTime.of(2023, 6, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2023, 6, 30, 23, 59);

        when(entityManager.createQuery(anyString(), eq(Long.class))).thenReturn(countQuery);
        when(countQuery.setParameter("startDate", startDate)).thenReturn(countQuery);
        when(countQuery.setParameter("endDate", endDate)).thenReturn(countQuery);
        when(countQuery.getSingleResult()).thenReturn(20L);

        long result = repository.countDocumentLogsByDateRange(startDate, endDate);

        assertEquals(20L, result);
    }

    @Test
    @DisplayName("countAllLogsByDate - Should return counts for all log types")
    void countAllLogsByDate_ShouldReturnAllLogTypeCounts() {
        LocalDateTime startDate = LocalDateTime.of(2023, 6, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2023, 6, 30, 23, 59);

        when(entityManager.createQuery(contains("HealthUser"), eq(Long.class))).thenReturn(countQuery);
        when(entityManager.createQuery(contains("AccessRequest"), eq(Long.class))).thenReturn(countQuery);
        when(entityManager.createQuery(contains("ClinicalHistory"), eq(Long.class))).thenReturn(countQuery);
        when(entityManager.createQuery(contains("Document"), eq(Long.class))).thenReturn(countQuery);

        when(countQuery.setParameter("startDate", startDate)).thenReturn(countQuery);
        when(countQuery.setParameter("endDate", endDate)).thenReturn(countQuery);
        when(countQuery.getSingleResult()).thenReturn(5L, 3L, 7L, 2L);

        Map<String, Long> result = repository.countAllLogsByDate(startDate, endDate);

        assertEquals(4, result.size());
        assertEquals(5L, result.get("HealthUser"));
        assertEquals(3L, result.get("AccessRequest"));
        assertEquals(7L, result.get("ClinicalHistory"));
        assertEquals(2L, result.get("Document"));
    }
}