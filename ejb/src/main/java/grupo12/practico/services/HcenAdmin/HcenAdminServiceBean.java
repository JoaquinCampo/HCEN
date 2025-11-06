package grupo12.practico.services.HcenAdmin;

import grupo12.practico.models.HcenAdmin;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

/**
 * Service for HcenAdmin operations
 */
@Stateless
public class HcenAdminServiceBean implements HcenAdminServiceLocal {

    @PersistenceContext(unitName = "practicoPersistenceUnit")
    private EntityManager em;

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
    public boolean isHcenAdmin(String ci) {
        return findByCi(ci) != null;
    }
}