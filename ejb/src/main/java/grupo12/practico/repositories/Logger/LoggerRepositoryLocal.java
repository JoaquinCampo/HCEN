package grupo12.practico.repositories.Logger;

import grupo12.practico.models.AccessRequestLog;
import grupo12.practico.models.ClinicalHistoryLog;
import grupo12.practico.models.DocumentLog;
import grupo12.practico.models.HealthUserLog;
import jakarta.ejb.Local;
import java.util.List;

@Local
public interface LoggerRepositoryLocal {
    // HealthUser Logs
    HealthUserLog createHealthUserLog(HealthUserLog log);
    List<HealthUserLog> findHealthUserLogs(String healthUserCi, Integer pageIndex, Integer pageSize);
    long countHealthUserLogs(String healthUserCi);

    // AccessRequest Logs
    AccessRequestLog createAccessRequestLog(AccessRequestLog log);
    List<AccessRequestLog> findAccessRequestLogs(String healthUserCi, String healthWorkerCi, String clinicName, Integer pageIndex, Integer pageSize);
    long countAccessRequestLogs(String healthUserCi, String healthWorkerCi, String clinicName);

    // ClinicalHistory Logs
    ClinicalHistoryLog createClinicalHistoryLog(ClinicalHistoryLog log);
    List<ClinicalHistoryLog> findClinicalHistoryLogs(String healthUserCi, String accessorCi, Integer pageIndex, Integer pageSize);
    long countClinicalHistoryLogs(String healthUserCi, String accessorCi);

    // Document Logs
    DocumentLog createDocumentLog(DocumentLog log);
    List<DocumentLog> findDocumentLogs(String healthUserCi, String healthWorkerCi, String clinicName, Integer pageIndex, Integer pageSize);
    long countDocumentLogs(String healthUserCi, String healthWorkerCi, String clinicName);

    // Analytics Methods
    java.util.Map<String, Long> countHealthUserLogsByAction(java.time.LocalDateTime startDate, java.time.LocalDateTime endDate);
    java.util.Map<String, Long> countAccessRequestLogsByAction(java.time.LocalDateTime startDate, java.time.LocalDateTime endDate);
    java.util.Map<String, Long> countClinicalHistoryLogsByAccessType(java.time.LocalDateTime startDate, java.time.LocalDateTime endDate);
    long countDocumentLogsByDateRange(java.time.LocalDateTime startDate, java.time.LocalDateTime endDate);
    java.util.Map<String, Long> countAllLogsByDate(java.time.LocalDateTime startDate, java.time.LocalDateTime endDate);
}

