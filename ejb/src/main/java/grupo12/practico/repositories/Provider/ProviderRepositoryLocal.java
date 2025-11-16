package grupo12.practico.repositories.Provider;

import jakarta.ejb.Local;
import grupo12.practico.models.Provider;
import java.util.List;

@Local
public interface ProviderRepositoryLocal {
    Provider createProvider(Provider provider);

    Provider findProviderById(String id);

    Provider findProviderByName(String providerName);

    List<Provider> findAllProviders();
}