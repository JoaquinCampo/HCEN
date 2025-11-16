package grupo12.practico.repositories.HcenAdmin;

import jakarta.ejb.Local;
import jakarta.ejb.Remote;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.validation.ValidationException;

import grupo12.practico.models.HcenAdmin;
import java.util.List;

@Stateless
@Local(HcenAdminRepositoryLocal.class)
@Remote(HcenAdminRepositoryRemote.class)
public class HcenAdminRepositoryBean implements HcenAdminRepositoryRemote {

    @PersistenceContext(unitName = "practicoPersistenceUnit")
    private EntityManager em;

    @Override
    public HcenAdmin create(HcenAdmin hcenAdmin) {
        if (hcenAdmin == null) {
            throw new ValidationException("HcenAdmin must not be null");
        }
        em.persist(hcenAdmin);
        return hcenAdmin;
    }

    @Override
    public HcenAdmin findByCi(String ci) {
        if (ci == null || ci.trim().isEmpty()) {
            return null;
        }

        TypedQuery<HcenAdmin> query = em.createQuery(
                "SELECT h FROM HcenAdmin h WHERE h.ci = :ci", HcenAdmin.class);
        query.setParameter("ci", ci.trim());

        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public HcenAdmin findById(String id) {
        if (id == null || id.trim().isEmpty()) {
            return null;
        }

        try {
            return em.find(HcenAdmin.class, id);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<HcenAdmin> findAll() {
        TypedQuery<HcenAdmin> query = em.createQuery(
                "SELECT h FROM HcenAdmin h ORDER BY h.createdAt DESC", HcenAdmin.class);
        return query.getResultList();
    }
}