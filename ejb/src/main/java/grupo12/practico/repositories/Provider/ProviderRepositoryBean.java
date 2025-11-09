package grupo12.practico.repositories.Provider;

import jakarta.ejb.Local;
import jakarta.ejb.Remote;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

import grupo12.practico.models.Provider;
import java.util.List;

@Stateless
@Local(ProviderRepositoryLocal.class)
@Remote(ProviderRepositoryRemote.class)
public class ProviderRepositoryBean implements ProviderRepositoryRemote {

    @PersistenceContext(unitName = "practicoPersistenceUnit")
    private EntityManager em;

    @Override
    public Provider create(Provider provider) {
        em.persist(provider);
        return provider;
    }

    @Override
    public Provider findById(String id) {
        if (id == null || id.trim().isEmpty()) {
            return null;
        }

        try {
            return em.find(Provider.class, id);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Provider findByName(String providerName) {
        if (providerName == null || providerName.trim().isEmpty()) {
            return null;
        }

        TypedQuery<Provider> query = em.createQuery(
                "SELECT p FROM Provider p WHERE p.providerName = :name", Provider.class);
        query.setParameter("name", providerName.trim());
        List<Provider> results = query.getResultList();
        return results.isEmpty() ? null : results.get(0);
    }

    @Override
    public List<Provider> findAll() {
        TypedQuery<Provider> query = em.createQuery(
                "SELECT p FROM Provider p ORDER BY p.createdAt DESC", Provider.class);
        return query.getResultList();
    }
}