package grupo12.practico.services.HealthProvider;

import jakarta.ejb.Local;

import java.util.List;

import grupo12.practico.models.Clinic;

@Local
public interface HealthProviderServiceLocal {
    Clinic addHealthProvider(Clinic healthProvider);

    List<Clinic> findAll();

    Clinic findById(String id);

    List<Clinic> findByName(String name);
}
