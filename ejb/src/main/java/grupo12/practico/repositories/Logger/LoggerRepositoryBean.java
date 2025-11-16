package grupo12.practico.repositories.Logger;

import grupo12.practico.models.AccessRequestLog;
import grupo12.practico.models.ClinicalHistoryLog;
import grupo12.practico.models.DocumentLog;
import grupo12.practico.models.HealthUserLog;
import jakarta.ejb.Local;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.util.List;

@Stateless
@Local(LoggerRepositoryLocal.class)
public class LoggerRepositoryBean implements LoggerRepositoryLocal {

    @PersistenceContext(unitName = "practicoPersistenceUnit")
    private EntityManager em;

    // HealthUser Logs
    @Override
    public HealthUserLog createHealthUserLog(HealthUserLog log) {
        em.persist(log);
        em.flush();
        return log;
    }

    @Override
    public List<HealthUserLog> findHealthUserLogs(String healthUserCi, Integer pageIndex, Integer pageSize) {
        StringBuilder jpql = new StringBuilder("SELECT l FROM HealthUserLog l WHERE 1=1");
        
        if (healthUserCi != null && !healthUserCi.isBlank()) {
            jpql.append(" AND l.healthUserCi = :healthUserCi");
        }
        
        jpql.append(" ORDER BY l.timestamp DESC");
        
        TypedQuery<HealthUserLog> query = em.createQuery(jpql.toString(), HealthUserLog.class);
        
        if (healthUserCi != null && !healthUserCi.isBlank()) {
            query.setParameter("healthUserCi", healthUserCi);
        }
        
        if (pageIndex != null && pageSize != null) {
            query.setFirstResult(pageIndex * pageSize);
            query.setMaxResults(pageSize);
        }
        
        return query.getResultList();
    }

    @Override
    public long countHealthUserLogs(String healthUserCi) {
        StringBuilder jpql = new StringBuilder("SELECT COUNT(l) FROM HealthUserLog l WHERE 1=1");
        
        if (healthUserCi != null && !healthUserCi.isBlank()) {
            jpql.append(" AND l.healthUserCi = :healthUserCi");
        }
        
        TypedQuery<Long> query = em.createQuery(jpql.toString(), Long.class);
        
        if (healthUserCi != null && !healthUserCi.isBlank()) {
            query.setParameter("healthUserCi", healthUserCi);
        }
        
        return query.getSingleResult();
    }

    // AccessRequest Logs
    @Override
    public AccessRequestLog createAccessRequestLog(AccessRequestLog log) {
        em.persist(log);
        em.flush();
        return log;
    }

    @Override
    public List<AccessRequestLog> findAccessRequestLogs(String healthUserCi, String healthWorkerCi, String clinicName, Integer pageIndex, Integer pageSize) {
        StringBuilder jpql = new StringBuilder("SELECT l FROM AccessRequestLog l WHERE 1=1");
        
        if (healthUserCi != null && !healthUserCi.isBlank()) {
            jpql.append(" AND l.healthUserCi = :healthUserCi");
        }
        if (healthWorkerCi != null && !healthWorkerCi.isBlank()) {
            jpql.append(" AND l.healthWorkerCi = :healthWorkerCi");
        }
        if (clinicName != null && !clinicName.isBlank()) {
            jpql.append(" AND l.clinicName = :clinicName");
        }
        
        jpql.append(" ORDER BY l.timestamp DESC");
        
        TypedQuery<AccessRequestLog> query = em.createQuery(jpql.toString(), AccessRequestLog.class);
        
        if (healthUserCi != null && !healthUserCi.isBlank()) {
            query.setParameter("healthUserCi", healthUserCi);
        }
        if (healthWorkerCi != null && !healthWorkerCi.isBlank()) {
            query.setParameter("healthWorkerCi", healthWorkerCi);
        }
        if (clinicName != null && !clinicName.isBlank()) {
            query.setParameter("clinicName", clinicName);
        }
        
        if (pageIndex != null && pageSize != null) {
            query.setFirstResult(pageIndex * pageSize);
            query.setMaxResults(pageSize);
        }
        
        return query.getResultList();
    }

    @Override
    public long countAccessRequestLogs(String healthUserCi, String healthWorkerCi, String clinicName) {
        StringBuilder jpql = new StringBuilder("SELECT COUNT(l) FROM AccessRequestLog l WHERE 1=1");
        
        if (healthUserCi != null && !healthUserCi.isBlank()) {
            jpql.append(" AND l.healthUserCi = :healthUserCi");
        }
        if (healthWorkerCi != null && !healthWorkerCi.isBlank()) {
            jpql.append(" AND l.healthWorkerCi = :healthWorkerCi");
        }
        if (clinicName != null && !clinicName.isBlank()) {
            jpql.append(" AND l.clinicName = :clinicName");
        }
        
        TypedQuery<Long> query = em.createQuery(jpql.toString(), Long.class);
        
        if (healthUserCi != null && !healthUserCi.isBlank()) {
            query.setParameter("healthUserCi", healthUserCi);
        }
        if (healthWorkerCi != null && !healthWorkerCi.isBlank()) {
            query.setParameter("healthWorkerCi", healthWorkerCi);
        }
        if (clinicName != null && !clinicName.isBlank()) {
            query.setParameter("clinicName", clinicName);
        }
        
        return query.getSingleResult();
    }

    // ClinicalHistory Logs
    @Override
    public ClinicalHistoryLog createClinicalHistoryLog(ClinicalHistoryLog log) {
        em.persist(log);
        em.flush();
        return log;
    }

    @Override
    public List<ClinicalHistoryLog> findClinicalHistoryLogs(String healthUserCi, String accessorCi, Integer pageIndex, Integer pageSize) {
        StringBuilder jpql = new StringBuilder("SELECT l FROM ClinicalHistoryLog l WHERE 1=1");
        
        if (healthUserCi != null && !healthUserCi.isBlank()) {
            jpql.append(" AND l.healthUserCi = :healthUserCi");
        }
        if (accessorCi != null && !accessorCi.isBlank()) {
            jpql.append(" AND l.accessorCi = :accessorCi");
        }
        
        jpql.append(" ORDER BY l.timestamp DESC");
        
        TypedQuery<ClinicalHistoryLog> query = em.createQuery(jpql.toString(), ClinicalHistoryLog.class);
        
        if (healthUserCi != null && !healthUserCi.isBlank()) {
            query.setParameter("healthUserCi", healthUserCi);
        }
        if (accessorCi != null && !accessorCi.isBlank()) {
            query.setParameter("accessorCi", accessorCi);
        }
        
        if (pageIndex != null && pageSize != null) {
            query.setFirstResult(pageIndex * pageSize);
            query.setMaxResults(pageSize);
        }
        
        return query.getResultList();
    }

    @Override
    public long countClinicalHistoryLogs(String healthUserCi, String accessorCi) {
        StringBuilder jpql = new StringBuilder("SELECT COUNT(l) FROM ClinicalHistoryLog l WHERE 1=1");
        
        if (healthUserCi != null && !healthUserCi.isBlank()) {
            jpql.append(" AND l.healthUserCi = :healthUserCi");
        }
        if (accessorCi != null && !accessorCi.isBlank()) {
            jpql.append(" AND l.accessorCi = :accessorCi");
        }
        
        TypedQuery<Long> query = em.createQuery(jpql.toString(), Long.class);
        
        if (healthUserCi != null && !healthUserCi.isBlank()) {
            query.setParameter("healthUserCi", healthUserCi);
        }
        if (accessorCi != null && !accessorCi.isBlank()) {
            query.setParameter("accessorCi", accessorCi);
        }
        
        return query.getSingleResult();
    }

    // Document Logs
    @Override
    public DocumentLog createDocumentLog(DocumentLog log) {
        em.persist(log);
        em.flush();
        return log;
    }

    @Override
    public List<DocumentLog> findDocumentLogs(String healthUserCi, String healthWorkerCi, String clinicName, Integer pageIndex, Integer pageSize) {
        StringBuilder jpql = new StringBuilder("SELECT l FROM DocumentLog l WHERE 1=1");
        
        if (healthUserCi != null && !healthUserCi.isBlank()) {
            jpql.append(" AND l.healthUserCi = :healthUserCi");
        }
        if (healthWorkerCi != null && !healthWorkerCi.isBlank()) {
            jpql.append(" AND l.healthWorkerCi = :healthWorkerCi");
        }
        if (clinicName != null && !clinicName.isBlank()) {
            jpql.append(" AND l.clinicName = :clinicName");
        }
        
        jpql.append(" ORDER BY l.timestamp DESC");
        
        TypedQuery<DocumentLog> query = em.createQuery(jpql.toString(), DocumentLog.class);
        
        if (healthUserCi != null && !healthUserCi.isBlank()) {
            query.setParameter("healthUserCi", healthUserCi);
        }
        if (healthWorkerCi != null && !healthWorkerCi.isBlank()) {
            query.setParameter("healthWorkerCi", healthWorkerCi);
        }
        if (clinicName != null && !clinicName.isBlank()) {
            query.setParameter("clinicName", clinicName);
        }
        
        if (pageIndex != null && pageSize != null) {
            query.setFirstResult(pageIndex * pageSize);
            query.setMaxResults(pageSize);
        }
        
        return query.getResultList();
    }

    @Override
    public long countDocumentLogs(String healthUserCi, String healthWorkerCi, String clinicName) {
        StringBuilder jpql = new StringBuilder("SELECT COUNT(l) FROM DocumentLog l WHERE 1=1");
        
        if (healthUserCi != null && !healthUserCi.isBlank()) {
            jpql.append(" AND l.healthUserCi = :healthUserCi");
        }
        if (healthWorkerCi != null && !healthWorkerCi.isBlank()) {
            jpql.append(" AND l.healthWorkerCi = :healthWorkerCi");
        }
        if (clinicName != null && !clinicName.isBlank()) {
            jpql.append(" AND l.clinicName = :clinicName");
        }
        
        TypedQuery<Long> query = em.createQuery(jpql.toString(), Long.class);
        
        if (healthUserCi != null && !healthUserCi.isBlank()) {
            query.setParameter("healthUserCi", healthUserCi);
        }
        if (healthWorkerCi != null && !healthWorkerCi.isBlank()) {
            query.setParameter("healthWorkerCi", healthWorkerCi);
        }
        if (clinicName != null && !clinicName.isBlank()) {
            query.setParameter("clinicName", clinicName);
        }
        
        return query.getSingleResult();
    }

    // Analytics Methods
    @Override
    public java.util.Map<String, Long> countHealthUserLogsByAction(java.time.LocalDateTime startDate, java.time.LocalDateTime endDate) {
        String jpql = "SELECT l.action, COUNT(l) FROM HealthUserLog l WHERE l.timestamp BETWEEN :startDate AND :endDate GROUP BY l.action";
        TypedQuery<Object[]> query = em.createQuery(jpql, Object[].class);
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);
        
        java.util.Map<String, Long> result = new java.util.HashMap<>();
        for (Object[] row : query.getResultList()) {
            result.put((String) row[0], (Long) row[1]);
        }
        return result;
    }

    @Override
    public java.util.Map<String, Long> countAccessRequestLogsByAction(java.time.LocalDateTime startDate, java.time.LocalDateTime endDate) {
        String jpql = "SELECT l.action, COUNT(l) FROM AccessRequestLog l WHERE l.timestamp BETWEEN :startDate AND :endDate GROUP BY l.action";
        TypedQuery<Object[]> query = em.createQuery(jpql, Object[].class);
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);
        
        java.util.Map<String, Long> result = new java.util.HashMap<>();
        for (Object[] row : query.getResultList()) {
            result.put((String) row[0], (Long) row[1]);
        }
        return result;
    }

    @Override
    public java.util.Map<String, Long> countClinicalHistoryLogsByAccessType(java.time.LocalDateTime startDate, java.time.LocalDateTime endDate) {
        String jpql = "SELECT l.accessType, COUNT(l) FROM ClinicalHistoryLog l WHERE l.timestamp BETWEEN :startDate AND :endDate GROUP BY l.accessType";
        TypedQuery<Object[]> query = em.createQuery(jpql, Object[].class);
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);
        
        java.util.Map<String, Long> result = new java.util.HashMap<>();
        for (Object[] row : query.getResultList()) {
            result.put((String) row[0], (Long) row[1]);
        }
        return result;
    }

    @Override
    public long countDocumentLogsByDateRange(java.time.LocalDateTime startDate, java.time.LocalDateTime endDate) {
        String jpql = "SELECT COUNT(l) FROM DocumentLog l WHERE l.timestamp BETWEEN :startDate AND :endDate";
        TypedQuery<Long> query = em.createQuery(jpql, Long.class);
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);
        return query.getSingleResult();
    }

    @Override
    public java.util.Map<String, Long> countAllLogsByDate(java.time.LocalDateTime startDate, java.time.LocalDateTime endDate) {
        java.util.Map<String, Long> result = new java.util.LinkedHashMap<>();
        
        // Count each log type
        String healthUserJpql = "SELECT COUNT(l) FROM HealthUserLog l WHERE l.timestamp BETWEEN :startDate AND :endDate";
        TypedQuery<Long> healthUserQuery = em.createQuery(healthUserJpql, Long.class);
        healthUserQuery.setParameter("startDate", startDate);
        healthUserQuery.setParameter("endDate", endDate);
        result.put("HealthUser", healthUserQuery.getSingleResult());
        
        String accessRequestJpql = "SELECT COUNT(l) FROM AccessRequestLog l WHERE l.timestamp BETWEEN :startDate AND :endDate";
        TypedQuery<Long> accessRequestQuery = em.createQuery(accessRequestJpql, Long.class);
        accessRequestQuery.setParameter("startDate", startDate);
        accessRequestQuery.setParameter("endDate", endDate);
        result.put("AccessRequest", accessRequestQuery.getSingleResult());
        
        String clinicalHistoryJpql = "SELECT COUNT(l) FROM ClinicalHistoryLog l WHERE l.timestamp BETWEEN :startDate AND :endDate";
        TypedQuery<Long> clinicalHistoryQuery = em.createQuery(clinicalHistoryJpql, Long.class);
        clinicalHistoryQuery.setParameter("startDate", startDate);
        clinicalHistoryQuery.setParameter("endDate", endDate);
        result.put("ClinicalHistory", clinicalHistoryQuery.getSingleResult());
        
        String documentJpql = "SELECT COUNT(l) FROM DocumentLog l WHERE l.timestamp BETWEEN :startDate AND :endDate";
        TypedQuery<Long> documentQuery = em.createQuery(documentJpql, Long.class);
        documentQuery.setParameter("startDate", startDate);
        documentQuery.setParameter("endDate", endDate);
        result.put("Document", documentQuery.getSingleResult());
        
        return result;
    }
}

