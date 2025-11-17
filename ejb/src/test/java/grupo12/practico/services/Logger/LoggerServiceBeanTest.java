package grupo12.practico.services.Logger;

import grupo12.practico.dtos.PaginationDTO;
import grupo12.practico.models.AccessRequestLog;
import grupo12.practico.models.ClinicalHistoryLog;
import grupo12.practico.models.DocumentLog;
import grupo12.practico.models.HealthUserLog;
import grupo12.practico.repositories.Logger.LoggerRepositoryLocal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("LoggerServiceBean Tests")
class LoggerServiceBeanTest {

    @Mock
    private LoggerRepositoryLocal loggerRepository;

    @InjectMocks
    private LoggerServiceBean service;

    private HealthUserLog healthUserLog;
    private AccessRequestLog accessRequestLog;
    private ClinicalHistoryLog clinicalHistoryLog;
    private DocumentLog documentLog;

    @BeforeEach
    void setUp() {
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

    // HealthUser Log Tests
    @Test
    @DisplayName("logHealthUserCreated - Should create log successfully")
    void logHealthUserCreated_ShouldCreateLog() {
        service.logHealthUserCreated("55555555", "Clinic One");

        verify(loggerRepository).createHealthUserLog(argThat(log -> log.getHealthUserCi().equals("55555555") &&
                log.getAction().equals("CREATED") &&
                log.getClinicName().equals("Clinic One")));
    }

    @Test
    @DisplayName("logHealthUserCreated - Should handle exceptions gracefully")
    void logHealthUserCreated_ShouldHandleExceptions() {
        doThrow(new RuntimeException("Database error")).when(loggerRepository).createHealthUserLog(any());

        // Should not throw exception
        assertDoesNotThrow(() -> service.logHealthUserCreated("55555555", "Clinic One"));
    }

    @Test
    @DisplayName("logHealthUserClinicLinked - Should create log successfully")
    void logHealthUserClinicLinked_ShouldCreateLog() {
        service.logHealthUserClinicLinked("55555555", "Clinic One");

        verify(loggerRepository).createHealthUserLog(argThat(log -> log.getHealthUserCi().equals("55555555") &&
                log.getAction().equals("CLINIC_LINKED") &&
                log.getClinicName().equals("Clinic One")));
    }

    @Test
    @DisplayName("getHealthUserLogs - Should return paginated logs")
    void getHealthUserLogs_ShouldReturnPaginatedLogs() {
        List<HealthUserLog> logs = List.of(healthUserLog);
        when(loggerRepository.findHealthUserLogs("55555555", 0, 20)).thenReturn(logs);
        when(loggerRepository.countHealthUserLogs("55555555")).thenReturn(1L);

        PaginationDTO<HealthUserLog> result = service.getHealthUserLogs("55555555", 0, 20);

        assertEquals(1, result.getItems().size());
        assertEquals(healthUserLog, result.getItems().get(0));
        assertEquals(0, result.getPageIndex());
        assertEquals(20, result.getPageSize());
        assertEquals(1L, result.getTotal());
        assertEquals(1L, result.getTotalPages());
        assertFalse(result.getHasNextPage());
        assertFalse(result.getHasPreviousPage());
    }

    @Test
    @DisplayName("getHealthUserLogs - Should handle null parameters")
    void getHealthUserLogs_ShouldHandleNullParameters() {
        List<HealthUserLog> logs = List.of(healthUserLog);
        when(loggerRepository.findHealthUserLogs(null, 0, 20)).thenReturn(logs);
        when(loggerRepository.countHealthUserLogs(null)).thenReturn(1L);

        PaginationDTO<HealthUserLog> result = service.getHealthUserLogs(null, null, null);

        assertEquals(1, result.getItems().size());
        assertEquals(0, result.getPageIndex());
        assertEquals(20, result.getPageSize());
    }

    // AccessRequest Log Tests
    @Test
    @DisplayName("logAccessRequestCreated - Should create log successfully")
    void logAccessRequestCreated_ShouldCreateLog() {
        List<String> specialties = List.of("Cardiology", "Neurology");
        service.logAccessRequestCreated("request-123", "55555555", "33333333", "Clinic One", specialties);

        verify(loggerRepository).createAccessRequestLog(argThat(log -> log.getAccessRequestId().equals("request-123") &&
                log.getHealthUserCi().equals("55555555") &&
                log.getHealthWorkerCi().equals("33333333") &&
                log.getClinicName().equals("Clinic One") &&
                log.getSpecialtyNames().equals(specialties) &&
                log.getAction().equals("REQUESTED")));
    }

    @Test
    @DisplayName("logAccessRequestAcceptedByClinic - Should create log successfully")
    void logAccessRequestAcceptedByClinic_ShouldCreateLog() {
        List<String> specialties = List.of("Cardiology");
        service.logAccessRequestAcceptedByClinic("request-123", "55555555", "33333333", "Clinic One", specialties);

        verify(loggerRepository).createAccessRequestLog(argThat(log -> log.getAction().equals("ACCEPTED_BY_CLINIC")));
    }

    @Test
    @DisplayName("logAccessRequestAcceptedByHealthWorker - Should create log successfully")
    void logAccessRequestAcceptedByHealthWorker_ShouldCreateLog() {
        List<String> specialties = List.of("Cardiology");
        service.logAccessRequestAcceptedByHealthWorker("request-123", "55555555", "33333333", "Clinic One",
                specialties);

        verify(loggerRepository)
                .createAccessRequestLog(argThat(log -> log.getAction().equals("ACCEPTED_BY_HEALTH_WORKER")));
    }

    @Test
    @DisplayName("logAccessRequestAcceptedBySpecialty - Should create log successfully")
    void logAccessRequestAcceptedBySpecialty_ShouldCreateLog() {
        List<String> specialties = List.of("Cardiology");
        service.logAccessRequestAcceptedBySpecialty("request-123", "55555555", "33333333", "Clinic One", specialties);

        verify(loggerRepository)
                .createAccessRequestLog(argThat(log -> log.getAction().equals("ACCEPTED_BY_SPECIALTY")));
    }

    @Test
    @DisplayName("logAccessRequestDenied - Should create log successfully")
    void logAccessRequestDenied_ShouldCreateLog() {
        List<String> specialties = List.of("Cardiology");
        service.logAccessRequestDenied("request-123", "55555555", "33333333", "Clinic One", specialties);

        verify(loggerRepository).createAccessRequestLog(argThat(log -> log.getAction().equals("DENIED")));
    }

    @Test
    @DisplayName("getAccessRequestLogs - Should return paginated logs")
    void getAccessRequestLogs_ShouldReturnPaginatedLogs() {
        List<AccessRequestLog> logs = List.of(accessRequestLog);
        when(loggerRepository.findAccessRequestLogs("55555555", "33333333", "Clinic One", 0, 20)).thenReturn(logs);
        when(loggerRepository.countAccessRequestLogs("55555555", "33333333", "Clinic One")).thenReturn(1L);

        PaginationDTO<AccessRequestLog> result = service.getAccessRequestLogs("55555555", "33333333", "Clinic One", 0,
                20);

        assertEquals(1, result.getItems().size());
        assertEquals(accessRequestLog, result.getItems().get(0));
        assertEquals(0, result.getPageIndex());
        assertEquals(20, result.getPageSize());
        assertEquals(1L, result.getTotal());
    }

    // ClinicalHistory Log Tests
    @Test
    @DisplayName("logClinicalHistoryAccessBySelf - Should create log successfully")
    void logClinicalHistoryAccessBySelf_ShouldCreateLog() {
        service.logClinicalHistoryAccessBySelf("55555555");

        verify(loggerRepository).createClinicalHistoryLog(argThat(log -> log.getHealthUserCi().equals("55555555") &&
                log.getAccessorCi().equals("55555555") &&
                log.getAccessorType().equals("HEALTH_USER") &&
                log.getAccessType().equals("SELF_ACCESS")));
    }

    @Test
    @DisplayName("logClinicalHistoryAccessByHealthWorker - Should create log successfully")
    void logClinicalHistoryAccessByHealthWorker_ShouldCreateLog() {
        List<String> specialties = List.of("Cardiology");
        service.logClinicalHistoryAccessByHealthWorker("55555555", "33333333", "Clinic One", specialties, "BY_CLINIC");

        verify(loggerRepository).createClinicalHistoryLog(argThat(log -> log.getHealthUserCi().equals("55555555") &&
                log.getAccessorCi().equals("33333333") &&
                log.getAccessorType().equals("HEALTH_WORKER") &&
                log.getClinicName().equals("Clinic One") &&
                log.getSpecialtyNames().equals(specialties) &&
                log.getAccessType().equals("BY_CLINIC")));
    }

    @Test
    @DisplayName("getClinicalHistoryLogs - Should return paginated logs")
    void getClinicalHistoryLogs_ShouldReturnPaginatedLogs() {
        List<ClinicalHistoryLog> logs = List.of(clinicalHistoryLog);
        when(loggerRepository.findClinicalHistoryLogs("55555555", "33333333", 0, 20)).thenReturn(logs);
        when(loggerRepository.countClinicalHistoryLogs("55555555", "33333333")).thenReturn(1L);

        PaginationDTO<ClinicalHistoryLog> result = service.getClinicalHistoryLogs("55555555", "33333333", 0, 20);

        assertEquals(1, result.getItems().size());
        assertEquals(clinicalHistoryLog, result.getItems().get(0));
        assertEquals(0, result.getPageIndex());
        assertEquals(20, result.getPageSize());
        assertEquals(1L, result.getTotal());
    }

    // Document Log Tests
    @Test
    @DisplayName("logDocumentCreated - Should create log successfully")
    void logDocumentCreated_ShouldCreateLog() {
        service.logDocumentCreated("doc-123", "55555555", "33333333", "Clinic One");

        verify(loggerRepository).createDocumentLog(argThat(log -> log.getDocumentId().equals("doc-123") &&
                log.getHealthUserCi().equals("55555555") &&
                log.getHealthWorkerCi().equals("33333333") &&
                log.getClinicName().equals("Clinic One") &&
                log.getAction().equals("CREATED")));
    }

    @Test
    @DisplayName("getDocumentLogs - Should return paginated logs")
    void getDocumentLogs_ShouldReturnPaginatedLogs() {
        List<DocumentLog> logs = List.of(documentLog);
        when(loggerRepository.findDocumentLogs("55555555", "33333333", "Clinic One", 0, 20)).thenReturn(logs);
        when(loggerRepository.countDocumentLogs("55555555", "33333333", "Clinic One")).thenReturn(1L);

        PaginationDTO<DocumentLog> result = service.getDocumentLogs("55555555", "33333333", "Clinic One", 0, 20);

        assertEquals(1, result.getItems().size());
        assertEquals(documentLog, result.getItems().get(0));
        assertEquals(0, result.getPageIndex());
        assertEquals(20, result.getPageSize());
        assertEquals(1L, result.getTotal());
    }

    // Analytics Tests
    @Test
    @DisplayName("getHealthUserLogsByAction - Should return action counts")
    void getHealthUserLogsByAction_ShouldReturnActionCounts() {
        LocalDateTime startDate = LocalDateTime.of(2023, 6, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2023, 6, 30, 23, 59);
        Map<String, Long> expectedCounts = Map.of("CREATED", 10L, "CLINIC_LINKED", 5L);

        when(loggerRepository.countHealthUserLogsByAction(startDate, endDate)).thenReturn(expectedCounts);

        Map<String, Long> result = service.getHealthUserLogsByAction(startDate, endDate);

        assertEquals(expectedCounts, result);
    }

    @Test
    @DisplayName("getAccessRequestLogsByAction - Should return action counts")
    void getAccessRequestLogsByAction_ShouldReturnActionCounts() {
        LocalDateTime startDate = LocalDateTime.of(2023, 6, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2023, 6, 30, 23, 59);
        Map<String, Long> expectedCounts = Map.of("REQUESTED", 15L, "ACCEPTED_BY_CLINIC", 8L);

        when(loggerRepository.countAccessRequestLogsByAction(startDate, endDate)).thenReturn(expectedCounts);

        Map<String, Long> result = service.getAccessRequestLogsByAction(startDate, endDate);

        assertEquals(expectedCounts, result);
    }

    @Test
    @DisplayName("getClinicalHistoryLogsByAccessType - Should return access type counts")
    void getClinicalHistoryLogsByAccessType_ShouldReturnAccessTypeCounts() {
        LocalDateTime startDate = LocalDateTime.of(2023, 6, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2023, 6, 30, 23, 59);
        Map<String, Long> expectedCounts = Map.of("SELF_ACCESS", 12L, "BY_CLINIC", 6L);

        when(loggerRepository.countClinicalHistoryLogsByAccessType(startDate, endDate)).thenReturn(expectedCounts);

        Map<String, Long> result = service.getClinicalHistoryLogsByAccessType(startDate, endDate);

        assertEquals(expectedCounts, result);
    }

    @Test
    @DisplayName("getDocumentLogsCount - Should return count")
    void getDocumentLogsCount_ShouldReturnCount() {
        LocalDateTime startDate = LocalDateTime.of(2023, 6, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2023, 6, 30, 23, 59);

        when(loggerRepository.countDocumentLogsByDateRange(startDate, endDate)).thenReturn(20L);

        long result = service.getDocumentLogsCount(startDate, endDate);

        assertEquals(20L, result);
    }

    @Test
    @DisplayName("getLogActivityByDate - Should return activity counts for all log types")
    void getLogActivityByDate_ShouldReturnActivityCounts() {
        LocalDateTime startDate = LocalDateTime.of(2023, 6, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2023, 6, 30, 23, 59);
        Map<String, Long> expectedCounts = Map.of(
                "HealthUser", 5L,
                "AccessRequest", 3L,
                "ClinicalHistory", 7L,
                "Document", 2L);

        when(loggerRepository.countAllLogsByDate(startDate, endDate)).thenReturn(expectedCounts);

        Map<String, Long> result = service.getLogActivityByDate(startDate, endDate);

        assertEquals(expectedCounts, result);
    }
}