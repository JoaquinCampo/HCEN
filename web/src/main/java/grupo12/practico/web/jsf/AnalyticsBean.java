package grupo12.practico.web.jsf;

import grupo12.practico.services.Logger.LoggerServiceLocal;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;
import java.time.LocalDateTime;
import java.util.Map;

@Named("analytics")
@RequestScoped
public class AnalyticsBean {

    @EJB
    private LoggerServiceLocal loggerService;

    private Map<String, Long> healthUserLogsByAction;
    private Map<String, Long> accessRequestLogsByAction;
    private Map<String, Long> clinicalHistoryLogsByAccessType;
    private long documentLogsCount;
    private Map<String, Long> logActivityByType;
    
    private int daysRange = 30; // Default to last 30 days

    @PostConstruct
    public void init() {
        loadAnalytics();
    }

    public void loadAnalytics() {
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.minusDays(daysRange);

        healthUserLogsByAction = loggerService.getHealthUserLogsByAction(startDate, endDate);
        accessRequestLogsByAction = loggerService.getAccessRequestLogsByAction(startDate, endDate);
        clinicalHistoryLogsByAccessType = loggerService.getClinicalHistoryLogsByAccessType(startDate, endDate);
        documentLogsCount = loggerService.getDocumentLogsCount(startDate, endDate);
        logActivityByType = loggerService.getLogActivityByDate(startDate, endDate);
    }

    public void setDaysRange(int days) {
        this.daysRange = days;
        loadAnalytics();
    }

    // Getters
    public Map<String, Long> getHealthUserLogsByAction() {
        return healthUserLogsByAction;
    }

    public Map<String, Long> getAccessRequestLogsByAction() {
        return accessRequestLogsByAction;
    }

    public Map<String, Long> getClinicalHistoryLogsByAccessType() {
        return clinicalHistoryLogsByAccessType;
    }

    public long getDocumentLogsCount() {
        return documentLogsCount;
    }

    public Map<String, Long> getLogActivityByType() {
        return logActivityByType;
    }

    public int getDaysRange() {
        return daysRange;
    }

    public long getTotalLogs() {
        if (logActivityByType == null) {
            return 0;
        }
        return logActivityByType.values().stream().mapToLong(Long::longValue).sum();
    }

    // Helper methods for UI
    public String getHealthUserActionLabel(String action) {
        return switch (action) {
            case "CREATED" -> "Usuarios Creados";
            case "CLINIC_LINKED" -> "Clínicas Vinculadas";
            default -> action;
        };
    }

    public String getAccessRequestActionLabel(String action) {
        return switch (action) {
            case "REQUESTED" -> "Solicitadas";
            case "ACCEPTED_BY_CLINIC" -> "Aceptadas por Clínica";
            case "ACCEPTED_BY_HEALTH_WORKER" -> "Aceptadas por Trabajador";
            case "ACCEPTED_BY_SPECIALTY" -> "Aceptadas por Especialidad";
            case "DENIED" -> "Denegadas";
            default -> action;
        };
    }

    public String getClinicalHistoryAccessTypeLabel(String accessType) {
        return switch (accessType) {
            case "SELF_ACCESS" -> "Acceso Propio";
            case "BY_CLINIC" -> "Por Clínica";
            case "BY_HEALTH_WORKER" -> "Por Trabajador";
            case "BY_SPECIALTY" -> "Por Especialidad";
            default -> accessType;
        };
    }

    public String getLogTypeLabel(String logType) {
        return switch (logType) {
            case "HealthUser" -> "Usuarios de Salud";
            case "AccessRequest" -> "Solicitudes de Acceso";
            case "ClinicalHistory" -> "Historias Clínicas";
            case "Document" -> "Documentos";
            default -> logType;
        };
    }
}

