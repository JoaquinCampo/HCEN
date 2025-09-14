package grupo12.practico.repository.healthprovider;

import grupo12.practico.model.HealthProvider;
import jakarta.ejb.Local;

import java.util.List;

@Local
public interface HealthProviderRepositoryLocal {
    HealthProvider add(HealthProvider healthProvider);

    List<HealthProvider> findAll();

    HealthProvider findById(String id);

    List<HealthProvider> findByName(String name);
}
