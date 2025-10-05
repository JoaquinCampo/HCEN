package grupo12.practico.repositories.HealthWorker;

import jakarta.ejb.Local;
import jakarta.ejb.Remote;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

import java.util.List;

import grupo12.practico.models.HealthWorker;

@Stateless
@Local(HealthWorkerRepositoryLocal.class)
@Remote(HealthWorkerRepositoryRemote.class)
public class HealthWorkerRepositoryBean implements HealthWorkerRepositoryRemote {

    @PersistenceContext(unitName = "practicoPersistenceUnit")
    private EntityManager em;

    @Override
    public HealthWorker add(HealthWorker healthWorker) {
        if (healthWorker == null) {
            return null;
        }
        em.persist(healthWorker);
        return healthWorker;
    }

    @Override
    public List<HealthWorker> findAll() {
        TypedQuery<HealthWorker> query = em.createQuery("SELECT h FROM HealthWorker h", HealthWorker.class);
        return query.getResultList();
    }

    @Override
    public List<HealthWorker> findByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return findAll();
        }
        TypedQuery<HealthWorker> query = em.createQuery(
                "SELECT h FROM HealthWorker h WHERE LOWER(h.firstName) LIKE LOWER(:name) OR LOWER(h.lastName) LIKE LOWER(:name)",
                HealthWorker.class);
        query.setParameter("name", "%" + name.trim() + "%");
        return query.getResultList();
    }

    @Override
    public HealthWorker findById(String id) {
        if (id == null || id.trim().isEmpty()) {
            return null;
        }
        return em.find(HealthWorker.class, id);
    }
}
