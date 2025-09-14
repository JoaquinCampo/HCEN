package grupo12.practico.service.healthprovider;

import grupo12.practico.model.HealthProvider;
import jakarta.ejb.Local;

import java.util.List;

@Local
public interface HealthProviderServiceLocal {
    HealthProvider addHealthProvider(HealthProvider healthProvider);

    List<HealthProvider> getAllHealthProviders();

    HealthProvider getHealthProviderById(String id);

    List<HealthProvider> searchHealthProvidersByName(String name);

    List<HealthProvider> getActiveHealthProviders();
}
