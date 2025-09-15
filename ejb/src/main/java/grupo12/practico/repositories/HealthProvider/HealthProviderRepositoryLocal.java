package grupo12.practico.repositories.HealthProvider;

import jakarta.ejb.Local;

import java.util.List;

import grupo12.practico.models.HealthProvider;

@Local
public interface HealthProviderRepositoryLocal {
    HealthProvider add(HealthProvider healthProvider);

    List<HealthProvider> findAll();

    HealthProvider findById(String id);

    List<HealthProvider> findByName(String name);
}
