package grupo12.practico.repositories.Clinic;

import jakarta.ejb.Local;
import jakarta.ejb.Remote;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

import java.util.List;

import grupo12.practico.models.Clinic;

@Stateless
@Local(ClinicRepositoryLocal.class)
@Remote(ClinicRepositoryRemote.class)
public class ClinicRepositoryBean implements ClinicRepositoryRemote {

    @PersistenceContext(unitName = "practicoPersistenceUnit")
    private EntityManager em;

    @Override
    public Clinic add(Clinic clinic) {
        if (clinic == null) {
            return null;
        }
        em.persist(clinic);
        return clinic;
    }

    @Override
    public List<Clinic> findAll() {
        TypedQuery<Clinic> query = em.createQuery("SELECT c FROM Clinic c", Clinic.class);
        return query.getResultList();
    }

    @Override
    public Clinic findById(String id) {
        if (id == null || id.trim().isEmpty()) {
            return null;
        }
        return em.find(Clinic.class, id);
    }

    @Override
    public List<Clinic> findByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return findAll();
        }
        TypedQuery<Clinic> query = em.createQuery(
                "SELECT c FROM Clinic c WHERE LOWER(c.name) LIKE LOWER(:name)",
                Clinic.class);
        query.setParameter("name", "%" + name.trim() + "%");
        return query.getResultList();
    }

}
