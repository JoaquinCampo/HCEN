package grupo12.practico.repositories.HcenAdmin;

import jakarta.ejb.Local;
import jakarta.ejb.Remote;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import grupo12.practico.models.HcenAdmin;
import java.util.List;

@Stateless
@Local(HcenAdminRepositoryLocal.class)
@Remote(HcenAdminRepositoryRemote.class)
public class HcenAdminRepositoryBean implements HcenAdminRepositoryRemote {

    @PersistenceContext(unitName = "practicoPersistenceUnit")
    private EntityManager em;

    @Override
    public HcenAdmin createHcenAdmin(HcenAdmin hcenAdmin) {
        em.persist(hcenAdmin);
        return hcenAdmin;
    }

    @Override
    public HcenAdmin findHcenAdminByCi(String ci) {
        try {
            return em.createQuery("SELECT h FROM HcenAdmin h WHERE h.ci = :ci", HcenAdmin.class)
                    .setParameter("ci", ci)
                    .getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public HcenAdmin findHcenAdminById(String id) {
        return em.find(HcenAdmin.class, id);
    }

    @Override
    public List<HcenAdmin> findAllHcenAdmins() {
        return em.createQuery("SELECT h FROM HcenAdmin h ORDER BY h.createdAt DESC", HcenAdmin.class)
                .getResultList();
    }
}