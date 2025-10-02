package grupo12.practico.repositories.ClinicalHistory;

import jakarta.ejb.Local;
import jakarta.ejb.Remote;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

import java.util.List;

import grupo12.practico.models.ClinicalHistory;

@Stateless
@Local(ClinicalHistoryRepositoryLocal.class)
@Remote(ClinicalHistoryRepositoryRemote.class)
public class ClinicalHistoryRepositoryBean implements ClinicalHistoryRepositoryRemote {

    @PersistenceContext(unitName = "practicoPersistenceUnit")
    private EntityManager em;

    @Override
    public List<ClinicalHistory> findAll() {
        TypedQuery<ClinicalHistory> query = em.createQuery("SELECT c FROM ClinicalHistory c", ClinicalHistory.class);
        return query.getResultList();
    }

    @Override
    public ClinicalHistory findById(String id) {
        if (id == null || id.trim().isEmpty()) {
            return null;
        }
        return em.find(ClinicalHistory.class, id);
    }

    @Override
    public ClinicalHistory add(ClinicalHistory clinicalHistory) {
        if (clinicalHistory == null) {
            return null;
        }
        em.persist(clinicalHistory);
        return clinicalHistory;
    }

}
