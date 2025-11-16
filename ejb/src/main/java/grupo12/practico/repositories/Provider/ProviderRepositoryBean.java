package grupo12.practico.repositories.Provider;

import jakarta.ejb.Local;
import jakarta.ejb.Remote;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.ejb.EJB;

import grupo12.practico.models.Provider;
import grupo12.practico.repositories.NodosPerifericosConfig;
import java.util.List;

@Stateless
@Local(ProviderRepositoryLocal.class)
@Remote(ProviderRepositoryRemote.class)
public class ProviderRepositoryBean implements ProviderRepositoryRemote {

    @PersistenceContext(unitName = "practicoPersistenceUnit")
    private EntityManager em;

    @EJB
    private NodosPerifericosConfig config;

    @Override
    public Provider createProvider(Provider provider) {
        em.persist(provider);
        return provider;
    }

    @Override
    public Provider findProviderById(String id) {
        return em.find(Provider.class, id);
    }

    @Override
    public Provider findProviderByName(String providerName) {
        try {
            return em.createQuery("SELECT p FROM Provider p WHERE p.providerName = :providerName", Provider.class)
                    .setParameter("providerName", providerName)
                    .getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<Provider> findAllProviders() {
        return em.createQuery("SELECT p FROM Provider p ORDER BY p.createdAt DESC", Provider.class)
                .getResultList();
    }
}