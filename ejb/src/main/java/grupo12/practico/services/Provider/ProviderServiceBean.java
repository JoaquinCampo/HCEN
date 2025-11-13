package grupo12.practico.services.Provider;

import grupo12.practico.dtos.Clinic.ClinicDTO;
import grupo12.practico.dtos.Provider.AddProviderDTO;
import grupo12.practico.dtos.Provider.ProviderDTO;
import grupo12.practico.models.Provider;
import grupo12.practico.repositories.Provider.ProviderRepositoryLocal;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.validation.ValidationException;
import java.util.List;
import java.util.stream.Collectors;

@Stateless
public class ProviderServiceBean implements ProviderServiceLocal {

    @EJB
    private ProviderRepositoryLocal providerRepository;

    @Override
    public ProviderDTO create(AddProviderDTO addProviderDTO) {
        validateAddProviderDTO(addProviderDTO);

        Provider provider = new Provider();
        provider.setName(addProviderDTO.getProviderName());

        Provider createdProvider = providerRepository.create(provider);
        return createdProvider.toDTO();
    }

    @Override
    public List<ProviderDTO> findAll() {
        List<Provider> providers = providerRepository.findAll();
        return providers.stream()
                .map(Provider::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ProviderDTO findByName(String providerName) {
        if (providerName == null || providerName.trim().isEmpty()) {
            return null;
        }

        Provider provider = providerRepository.findByName(providerName.trim());
        return provider != null ? provider.toDTO() : null;
    }

    @Override
    public List<ClinicDTO> fetchClinicsByProvider(String providerName) {
        return providerRepository.fetchClinicsByProvider(providerName);
    }

    private void validateAddProviderDTO(AddProviderDTO addProviderDTO) {
        if (addProviderDTO == null) {
            throw new ValidationException("Provider data must not be null");
        }
        if (addProviderDTO.getProviderName() == null || addProviderDTO.getProviderName().trim().isEmpty()) {
            throw new ValidationException("Provider name is required");
        }
    }
}