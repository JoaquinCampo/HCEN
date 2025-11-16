package grupo12.practico.services.Provider;

import grupo12.practico.dtos.Provider.AddProviderDTO;
import grupo12.practico.dtos.Provider.ProviderDTO;
import grupo12.practico.models.Provider;
import grupo12.practico.repositories.Provider.ProviderRepositoryLocal;
import jakarta.ejb.EJB;
import jakarta.ejb.Local;
import jakarta.ejb.Remote;
import jakarta.ejb.Stateless;
import jakarta.validation.ValidationException;
import java.util.List;
import java.util.stream.Collectors;

@Stateless
@Local(ProviderServiceLocal.class)
@Remote(ProviderServiceRemote.class)
public class ProviderServiceBean implements ProviderServiceLocal {

    @EJB
    private ProviderRepositoryLocal providerRepository;

    @Override
    public ProviderDTO createProvider(AddProviderDTO addProviderDTO) {
        validateAddProviderDTO(addProviderDTO);

        Provider provider = new Provider();
        provider.setName(addProviderDTO.getProviderName());

        Provider createdProvider = providerRepository.createProvider(provider);
        return createdProvider.toDTO();
    }

    @Override
    public List<ProviderDTO> findAllProviders() {
        List<Provider> providers = providerRepository.findAllProviders();
        return providers.stream()
                .map(Provider::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ProviderDTO findProviderByName(String providerName) {
        if (providerName == null || providerName.isBlank()) {
            return null;
        }

        Provider provider = providerRepository.findProviderByName(providerName);
        return provider != null ? provider.toDTO() : null;
    }

    private void validateAddProviderDTO(AddProviderDTO addProviderDTO) {
        if (addProviderDTO == null) {
            throw new ValidationException("Provider data must not be null");
        }
        if (addProviderDTO.getProviderName() == null || addProviderDTO.getProviderName().isBlank()) {
            throw new ValidationException("Provider name is required");
        }
    }
}