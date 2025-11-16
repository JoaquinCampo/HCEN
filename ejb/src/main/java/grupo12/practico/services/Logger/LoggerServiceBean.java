package grupo12.practico.services.Logger;

import grupo12.practico.models.AccessRequestLog;
import grupo12.practico.models.ClinicalHistoryLog;
import grupo12.practico.models.DocumentLog;
import grupo12.practico.models.HealthUserLog;
import grupo12.practico.repositories.Logger.LoggerRepositoryLocal;
import grupo12.practico.dtos.PaginationDTO;
import jakarta.ejb.EJB;
import jakarta.ejb.Local;
import jakarta.ejb.Stateless;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Stateless
@Local(LoggerServiceLocal.class)
public class LoggerServiceBean implements LoggerServiceLocal {

    private static final Logger LOGGER = Logger.getLogger(LoggerServiceBean.class.getName());

    @EJB
    private LoggerRepositoryLocal loggerRepository;

    // HealthUser Logs
    @Override
    public void logHealthUserCreated(String healthUserCi, String clinicName) {
        try {
            HealthUserLog log = new HealthUserLog();
            log.setHealthUserCi(healthUserCi);
            log.setAction("CREATED");
            log.setClinicName(clinicName);
            loggerRepository.createHealthUserLog(log);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to log health user creation", e);
        }
    }

    @Override
    public void logHealthUserClinicLinked(String healthUserCi, String clinicName) {
        try {
            HealthUserLog log = new HealthUserLog();
            log.setHealthUserCi(healthUserCi);
            log.setAction("CLINIC_LINKED");
            log.setClinicName(clinicName);
            loggerRepository.createHealthUserLog(log);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to log health user clinic link", e);
        }
    }

    @Override
    public PaginationDTO<HealthUserLog> getHealthUserLogs(String healthUserCi, Integer pageIndex, Integer pageSize) {
        int safePageIndex = pageIndex != null && pageIndex >= 0 ? pageIndex : 0;
        int safePageSize = pageSize != null && pageSize > 0 ? pageSize : 20;

        List<HealthUserLog> logs = loggerRepository.findHealthUserLogs(healthUserCi, safePageIndex, safePageSize);
        long total = loggerRepository.countHealthUserLogs(healthUserCi);

        PaginationDTO<HealthUserLog> paginationDTO = new PaginationDTO<>();
        paginationDTO.setItems(logs);
        paginationDTO.setPageIndex(safePageIndex);
        paginationDTO.setPageSize(safePageSize);
        paginationDTO.setTotal(total);
        paginationDTO.setTotalPages((long) Math.ceil((double) total / safePageSize));
        paginationDTO.setHasNextPage(safePageIndex < paginationDTO.getTotalPages() - 1);
        paginationDTO.setHasPreviousPage(safePageIndex > 0);

        return paginationDTO;
    }

    // AccessRequest Logs
    @Override
    public void logAccessRequestCreated(String accessRequestId, String healthUserCi,
                                       String healthWorkerCi, String clinicName,
                                       List<String> specialtyNames) {
        try {
            AccessRequestLog log = new AccessRequestLog();
            log.setAccessRequestId(accessRequestId);
            log.setHealthUserCi(healthUserCi);
            log.setHealthWorkerCi(healthWorkerCi);
            log.setClinicName(clinicName);
            log.setSpecialtyNames(specialtyNames);
            log.setAction("REQUESTED");
            loggerRepository.createAccessRequestLog(log);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to log access request creation", e);
        }
    }

    @Override
    public void logAccessRequestAcceptedByClinic(String accessRequestId, String healthUserCi,
                                                 String healthWorkerCi, String clinicName,
                                                 List<String> specialtyNames) {
        try {
            AccessRequestLog log = new AccessRequestLog();
            log.setAccessRequestId(accessRequestId);
            log.setHealthUserCi(healthUserCi);
            log.setHealthWorkerCi(healthWorkerCi);
            log.setClinicName(clinicName);
            log.setSpecialtyNames(specialtyNames);
            log.setAction("ACCEPTED_BY_CLINIC");
            loggerRepository.createAccessRequestLog(log);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to log access request acceptance by clinic", e);
        }
    }

    @Override
    public void logAccessRequestAcceptedByHealthWorker(String accessRequestId, String healthUserCi,
                                                       String healthWorkerCi, String clinicName,
                                                       List<String> specialtyNames) {
        try {
            AccessRequestLog log = new AccessRequestLog();
            log.setAccessRequestId(accessRequestId);
            log.setHealthUserCi(healthUserCi);
            log.setHealthWorkerCi(healthWorkerCi);
            log.setClinicName(clinicName);
            log.setSpecialtyNames(specialtyNames);
            log.setAction("ACCEPTED_BY_HEALTH_WORKER");
            loggerRepository.createAccessRequestLog(log);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to log access request acceptance by health worker", e);
        }
    }

    @Override
    public void logAccessRequestAcceptedBySpecialty(String accessRequestId, String healthUserCi,
                                                    String healthWorkerCi, String clinicName,
                                                    List<String> specialtyNames) {
        try {
            AccessRequestLog log = new AccessRequestLog();
            log.setAccessRequestId(accessRequestId);
            log.setHealthUserCi(healthUserCi);
            log.setHealthWorkerCi(healthWorkerCi);
            log.setClinicName(clinicName);
            log.setSpecialtyNames(specialtyNames);
            log.setAction("ACCEPTED_BY_SPECIALTY");
            loggerRepository.createAccessRequestLog(log);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to log access request acceptance by specialty", e);
        }
    }

    @Override
    public void logAccessRequestDenied(String accessRequestId, String healthUserCi,
                                      String healthWorkerCi, String clinicName,
                                      List<String> specialtyNames) {
        try {
            AccessRequestLog log = new AccessRequestLog();
            log.setAccessRequestId(accessRequestId);
            log.setHealthUserCi(healthUserCi);
            log.setHealthWorkerCi(healthWorkerCi);
            log.setClinicName(clinicName);
            log.setSpecialtyNames(specialtyNames);
            log.setAction("DENIED");
            loggerRepository.createAccessRequestLog(log);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to log access request denial", e);
        }
    }

    @Override
    public PaginationDTO<AccessRequestLog> getAccessRequestLogs(String healthUserCi, String healthWorkerCi,
                                                                String clinicName, Integer pageIndex, Integer pageSize) {
        int safePageIndex = pageIndex != null && pageIndex >= 0 ? pageIndex : 0;
        int safePageSize = pageSize != null && pageSize > 0 ? pageSize : 20;

        List<AccessRequestLog> logs = loggerRepository.findAccessRequestLogs(healthUserCi, healthWorkerCi, clinicName, safePageIndex, safePageSize);
        long total = loggerRepository.countAccessRequestLogs(healthUserCi, healthWorkerCi, clinicName);

        PaginationDTO<AccessRequestLog> paginationDTO = new PaginationDTO<>();
        paginationDTO.setItems(logs);
        paginationDTO.setPageIndex(safePageIndex);
        paginationDTO.setPageSize(safePageSize);
        paginationDTO.setTotal(total);
        paginationDTO.setTotalPages((long) Math.ceil((double) total / safePageSize));
        paginationDTO.setHasNextPage(safePageIndex < paginationDTO.getTotalPages() - 1);
        paginationDTO.setHasPreviousPage(safePageIndex > 0);

        return paginationDTO;
    }

    // ClinicalHistory Logs
    @Override
    public void logClinicalHistoryAccessBySelf(String healthUserCi) {
        try {
            ClinicalHistoryLog log = new ClinicalHistoryLog();
            log.setHealthUserCi(healthUserCi);
            log.setAccessorCi(healthUserCi);
            log.setAccessorType("HEALTH_USER");
            log.setAccessType("SELF_ACCESS");
            loggerRepository.createClinicalHistoryLog(log);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to log clinical history self access", e);
        }
    }

    @Override
    public void logClinicalHistoryAccessByHealthWorker(String healthUserCi, String healthWorkerCi,
                                                       String clinicName, List<String> specialtyNames,
                                                       String accessType) {
        try {
            ClinicalHistoryLog log = new ClinicalHistoryLog();
            log.setHealthUserCi(healthUserCi);
            log.setAccessorCi(healthWorkerCi);
            log.setAccessorType("HEALTH_WORKER");
            log.setClinicName(clinicName);
            log.setSpecialtyNames(specialtyNames);
            log.setAccessType(accessType);
            loggerRepository.createClinicalHistoryLog(log);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to log clinical history access by health worker", e);
        }
    }

    @Override
    public PaginationDTO<ClinicalHistoryLog> getClinicalHistoryLogs(String healthUserCi, String accessorCi,
                                                                    Integer pageIndex, Integer pageSize) {
        int safePageIndex = pageIndex != null && pageIndex >= 0 ? pageIndex : 0;
        int safePageSize = pageSize != null && pageSize > 0 ? pageSize : 20;

        List<ClinicalHistoryLog> logs = loggerRepository.findClinicalHistoryLogs(healthUserCi, accessorCi, safePageIndex, safePageSize);
        long total = loggerRepository.countClinicalHistoryLogs(healthUserCi, accessorCi);

        PaginationDTO<ClinicalHistoryLog> paginationDTO = new PaginationDTO<>();
        paginationDTO.setItems(logs);
        paginationDTO.setPageIndex(safePageIndex);
        paginationDTO.setPageSize(safePageSize);
        paginationDTO.setTotal(total);
        paginationDTO.setTotalPages((long) Math.ceil((double) total / safePageSize));
        paginationDTO.setHasNextPage(safePageIndex < paginationDTO.getTotalPages() - 1);
        paginationDTO.setHasPreviousPage(safePageIndex > 0);

        return paginationDTO;
    }

    // Document Logs
    @Override
    public void logDocumentCreated(String documentId, String healthUserCi,
                                  String healthWorkerCi, String clinicName) {
        try {
            DocumentLog log = new DocumentLog();
            log.setDocumentId(documentId);
            log.setHealthUserCi(healthUserCi);
            log.setHealthWorkerCi(healthWorkerCi);
            log.setClinicName(clinicName);
            log.setAction("CREATED");
            loggerRepository.createDocumentLog(log);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to log document creation", e);
        }
    }

    @Override
    public PaginationDTO<DocumentLog> getDocumentLogs(String healthUserCi, String healthWorkerCi,
                                                      String clinicName, Integer pageIndex, Integer pageSize) {
        int safePageIndex = pageIndex != null && pageIndex >= 0 ? pageIndex : 0;
        int safePageSize = pageSize != null && pageSize > 0 ? pageSize : 20;

        List<DocumentLog> logs = loggerRepository.findDocumentLogs(healthUserCi, healthWorkerCi, clinicName, safePageIndex, safePageSize);
        long total = loggerRepository.countDocumentLogs(healthUserCi, healthWorkerCi, clinicName);

        PaginationDTO<DocumentLog> paginationDTO = new PaginationDTO<>();
        paginationDTO.setItems(logs);
        paginationDTO.setPageIndex(safePageIndex);
        paginationDTO.setPageSize(safePageSize);
        paginationDTO.setTotal(total);
        paginationDTO.setTotalPages((long) Math.ceil((double) total / safePageSize));
        paginationDTO.setHasNextPage(safePageIndex < paginationDTO.getTotalPages() - 1);
        paginationDTO.setHasPreviousPage(safePageIndex > 0);

        return paginationDTO;
    }

    // Analytics Methods
    @Override
    public java.util.Map<String, Long> getHealthUserLogsByAction(java.time.LocalDateTime startDate, java.time.LocalDateTime endDate) {
        return loggerRepository.countHealthUserLogsByAction(startDate, endDate);
    }

    @Override
    public java.util.Map<String, Long> getAccessRequestLogsByAction(java.time.LocalDateTime startDate, java.time.LocalDateTime endDate) {
        return loggerRepository.countAccessRequestLogsByAction(startDate, endDate);
    }

    @Override
    public java.util.Map<String, Long> getClinicalHistoryLogsByAccessType(java.time.LocalDateTime startDate, java.time.LocalDateTime endDate) {
        return loggerRepository.countClinicalHistoryLogsByAccessType(startDate, endDate);
    }

    @Override
    public long getDocumentLogsCount(java.time.LocalDateTime startDate, java.time.LocalDateTime endDate) {
        return loggerRepository.countDocumentLogsByDateRange(startDate, endDate);
    }

    @Override
    public java.util.Map<String, Long> getLogActivityByDate(java.time.LocalDateTime startDate, java.time.LocalDateTime endDate) {
        return loggerRepository.countAllLogsByDate(startDate, endDate);
    }
}

