package grupo12.practico.repositories.Provider;

import jakarta.ejb.Local;
import grupo12.practico.models.Provider;
import grupo12.practico.dtos.Clinic.ClinicDTO;
import java.util.List;

@Local
public interface ProviderRepositoryLocal {
    Provider create(Provider provider);

    Provider findById(String id);

    Provider findByName(String providerName);

    List<Provider> findAll();

    List<ClinicDTO> fetchClinicsByProvider(String providerName);
}