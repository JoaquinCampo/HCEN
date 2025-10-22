package grupo12.practico.repositories.HealthUser;

import jakarta.ejb.Local;
import jakarta.ejb.Remote;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import grupo12.practico.models.HealthUser;

import jakarta.validation.ValidationException;

@Stateless
@Local(HealthUserRepositoryLocal.class)
@Remote(HealthUserRepositoryRemote.class)
public class HealthUserRepositoryBean implements HealthUserRepositoryRemote {

    @PersistenceContext(unitName = "practicoPersistenceUnit")
    private EntityManager em;

    @Override
    public List<HealthUser> findAll() {
        TypedQuery<HealthUser> query = em.createQuery("SELECT h FROM HealthUser h", HealthUser.class);
        return query.getResultList();
    }

    @Override
    public HealthUser findById(String id) {
        if (id == null || id.trim().isEmpty()) {
            return null;
        }
        return em.find(HealthUser.class, id);
    }

    @Override
    public List<HealthUser> findByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return findAll();
        }

        TypedQuery<HealthUser> query = em.createQuery(
                "SELECT h FROM HealthUser h WHERE LOWER(h.firstName) LIKE LOWER(:name) OR LOWER(h.lastName) LIKE LOWER(:name)",
                HealthUser.class);
        query.setParameter("name", "%" + name.trim() + "%");
        return query.getResultList();
    }

    @Override
    public HealthUser add(HealthUser healthUser) {
        if (healthUser == null) {
            throw new ValidationException("HealthUser must not be null");
        }
        em.persist(healthUser);
        return healthUser;
    }

    @Override
    public HealthUser findByDocument(String document) {
        if (document == null || document.trim().isEmpty()) {
            return null;
        }

        String normalizedDocument = document.trim();
        TypedQuery<HealthUser> query = em.createQuery(
                "SELECT h FROM HealthUser h WHERE h.document = :document",
                HealthUser.class);
        query.setParameter("document", normalizedDocument);
        query.setMaxResults(1);
        List<HealthUser> results = query.getResultList();
        return results.isEmpty() ? null : results.get(0);
    }

    @Override
    public List<HealthUser> findPage(String documentFragment, String clinicName, int offset, int limit) {
        if (offset < 0) {
            offset = 0;
        }
        if (limit <= 0) {
            return List.of();
        }

        QueryComponents components = buildQueryComponents(documentFragment, clinicName,
                "SELECT DISTINCT h FROM HealthUser h", " ORDER BY h.createdAt DESC");
        TypedQuery<HealthUser> query = em.createQuery(components.jpql(), HealthUser.class);
        components.parameters().forEach(query::setParameter);
        query.setFirstResult(offset);
        query.setMaxResults(limit);
        return query.getResultList();
    }

    @Override
    public long count(String documentFragment, String clinicName) {
        QueryComponents components = buildQueryComponents(documentFragment, clinicName,
                "SELECT COUNT(DISTINCT h.id) FROM HealthUser h", "");
        TypedQuery<Long> query = em.createQuery(components.jpql(), Long.class);
        components.parameters().forEach(query::setParameter);
        return query.getSingleResult();
    }

    private QueryComponents buildQueryComponents(String documentFragment, String clinicName, String baseSelect,
            String suffix) {
        StringBuilder jpql = new StringBuilder(baseSelect);
        Map<String, Object> parameters = new HashMap<>();
        boolean joinClinics = clinicName != null && !clinicName.trim().isEmpty();

        if (joinClinics) {
            jpql.append(" JOIN h.clinics c");
        }

        boolean whereAdded = false;
        if (documentFragment != null && !documentFragment.trim().isEmpty()) {
            jpql.append(whereAdded ? " AND " : " WHERE ");
            jpql.append("LOWER(h.document) LIKE :documentPattern");
            parameters.put("documentPattern", "%" + documentFragment.trim().toLowerCase() + "%");
            whereAdded = true;
        }

        if (joinClinics) {
            jpql.append(whereAdded ? " AND " : " WHERE ");
            jpql.append("LOWER(c.name) LIKE :clinicNamePattern");
            parameters.put("clinicNamePattern", "%" + clinicName.trim().toLowerCase() + "%");
        }

        jpql.append(suffix);
        return new QueryComponents(jpql.toString(), parameters);
    }

    private record QueryComponents(String jpql, Map<String, Object> parameters) {
    }
}
