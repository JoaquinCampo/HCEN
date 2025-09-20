package grupo12.practico.repositories.HealthProvider;

import jakarta.ejb.Local;

import java.util.List;

import grupo12.practico.models.Clinic;

@Local
public interface HealthProviderRepositoryLocal {
    Clinic add(Clinic healthProvider);

    List<Clinic> findAll();

    Clinic findById(String id);

    List<Clinic> findByName(String name);
}
