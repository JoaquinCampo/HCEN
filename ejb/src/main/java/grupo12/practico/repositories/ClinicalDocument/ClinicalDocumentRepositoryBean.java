package grupo12.practico.repositories.ClinicalDocument;

import jakarta.ejb.Local;
import jakarta.ejb.Remote;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

import java.util.List;

import grupo12.practico.models.ClinicalDocument;

@Stateless
@Local(ClinicalDocumentRepositoryLocal.class)
@Remote(ClinicalDocumentRepositoryRemote.class)
public class ClinicalDocumentRepositoryBean implements ClinicalDocumentRepositoryRemote {

    @PersistenceContext(unitName = "practicoPersistenceUnit")
    private EntityManager em;

    @Override
    public ClinicalDocument add(ClinicalDocument doc) {
        if (doc == null) {
            return null;
        }
        em.persist(doc);
        return doc;
    }

    @Override
    public List<ClinicalDocument> findAll() {
        TypedQuery<ClinicalDocument> query = em.createQuery("SELECT c FROM ClinicalDocument c", ClinicalDocument.class);
        return query.getResultList();
    }

    @Override
    public ClinicalDocument findById(String id) {
        if (id == null || id.trim().isEmpty()) {
            return null;
        }
        return em.find(ClinicalDocument.class, id);
    }

    @Override
    public List<ClinicalDocument> findByTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            return findAll();
        }
        TypedQuery<ClinicalDocument> query = em.createQuery(
                "SELECT c FROM ClinicalDocument c WHERE LOWER(c.title) LIKE LOWER(:title)",
                ClinicalDocument.class);
        query.setParameter("title", "%" + title.trim() + "%");
        return query.getResultList();
    }
}
