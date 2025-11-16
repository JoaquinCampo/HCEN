package grupo12.practico.services.Logger;

import grupo12.practico.models.AccessRequestLog;
import grupo12.practico.models.ClinicalHistoryLog;
import grupo12.practico.models.DocumentLog;
import grupo12.practico.models.HealthUserLog;
import grupo12.practico.dtos.PaginationDTO;
import jakarta.ejb.Local;

@Local
public interface LoggerServiceLocal {
    // HealthUser Logs
    void logHealthUserCreated(String healthUserCi, String clinicName);
    void logHealthUserClinicLinked(String healthUserCi, String clinicName);
    PaginationDTO<HealthUserLog> getHealthUserLogs(String healthUserCi, Integer pageIndex, Integer pageSize);

    // AccessRequest Logs
    void logAccessRequestCreated(String accessRequestId, String healthUserCi, 
                                 String healthWorkerCi, String clinicName, 
                                 java.util.List<String> specialtyNames);
    void logAccessRequestAcceptedByClinic(String accessRequestId, String healthUserCi, 
                                          String healthWorkerCi, String clinicName, 
                                          java.util.List<String> specialtyNames);
    void logAccessRequestAcceptedByHealthWorker(String accessRequestId, String healthUserCi, 
                                                String healthWorkerCi, String clinicName, 
                                                java.util.List<String> specialtyNames);
    void logAccessRequestAcceptedBySpecialty(String accessRequestId, String healthUserCi, 
                                             String healthWorkerCi, String clinicName, 
                                             java.util.List<String> specialtyNames);
    void logAccessRequestDenied(String accessRequestId, String healthUserCi, 
                                String healthWorkerCi, String clinicName, 
                                java.util.List<String> specialtyNames);
    PaginationDTO<AccessRequestLog> getAccessRequestLogs(String healthUserCi, String healthWorkerCi, 
                                                         String clinicName, Integer pageIndex, Integer pageSize);

    // ClinicalHistory Logs
    void logClinicalHistoryAccessBySelf(String healthUserCi);
    void logClinicalHistoryAccessByHealthWorker(String healthUserCi, String healthWorkerCi, 
                                                String clinicName, java.util.List<String> specialtyNames, 
                                                String accessType);
    PaginationDTO<ClinicalHistoryLog> getClinicalHistoryLogs(String healthUserCi, String accessorCi, 
                                                             Integer pageIndex, Integer pageSize);

    // Document Logs
    void logDocumentCreated(String documentId, String healthUserCi, 
                           String healthWorkerCi, String clinicName);
    PaginationDTO<DocumentLog> getDocumentLogs(String healthUserCi, String healthWorkerCi, 
                                               String clinicName, Integer pageIndex, Integer pageSize);

    // Analytics Methods
    java.util.Map<String, Long> getHealthUserLogsByAction(java.time.LocalDateTime startDate, java.time.LocalDateTime endDate);
    java.util.Map<String, Long> getAccessRequestLogsByAction(java.time.LocalDateTime startDate, java.time.LocalDateTime endDate);
    java.util.Map<String, Long> getClinicalHistoryLogsByAccessType(java.time.LocalDateTime startDate, java.time.LocalDateTime endDate);
    long getDocumentLogsCount(java.time.LocalDateTime startDate, java.time.LocalDateTime endDate);
    java.util.Map<String, Long> getLogActivityByDate(java.time.LocalDateTime startDate, java.time.LocalDateTime endDate);
}

