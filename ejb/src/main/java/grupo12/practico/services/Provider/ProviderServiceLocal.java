package grupo12.practico.services.Provider;

import grupo12.practico.dtos.Clinic.ClinicDTO;
import grupo12.practico.dtos.Provider.AddProviderDTO;
import grupo12.practico.dtos.Provider.ProviderDTO;
import jakarta.ejb.Local;
import java.util.List;

@Local
public interface ProviderServiceLocal {

    ProviderDTO create(AddProviderDTO addProviderDTO);

    List<ProviderDTO> findAll();

    ProviderDTO findByName(String providerName);

    /**
     * Fetch all clinics associated with a provider from the external API
     * 
     * @param providerName The name of the provider
     * @return List of clinics, empty list if none found or on error
     */
    List<ClinicDTO> fetchClinicsByProvider(String providerName);
}