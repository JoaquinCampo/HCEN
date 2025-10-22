package grupo12.practico.repositories.HealthUser;

import jakarta.ejb.Local;
import jakarta.ejb.Remote;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

import java.util.List;

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
    public List<HealthUser> findPage(int offset, int limit) {
        if (offset < 0) {
            offset = 0;
        }
        if (limit <= 0) {
            return List.of();
        }

        TypedQuery<HealthUser> query = em.createQuery("SELECT h FROM HealthUser h ORDER BY h.createdAt DESC",
                HealthUser.class);
        query.setFirstResult(offset);
        query.setMaxResults(limit);
        return query.getResultList();
    }

    @Override
    public long count() {
        return em.createQuery("SELECT COUNT(h) FROM HealthUser h", Long.class).getSingleResult();
    }
}
