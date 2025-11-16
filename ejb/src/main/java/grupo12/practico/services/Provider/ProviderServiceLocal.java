package grupo12.practico.services.Provider;

import grupo12.practico.dtos.Provider.AddProviderDTO;
import grupo12.practico.dtos.Provider.ProviderDTO;
import jakarta.ejb.Local;
import java.util.List;

@Local
public interface ProviderServiceLocal {

    ProviderDTO createProvider(AddProviderDTO addProviderDTO);

    List<ProviderDTO> findAllProviders();

    ProviderDTO findProviderByName(String providerName);
}