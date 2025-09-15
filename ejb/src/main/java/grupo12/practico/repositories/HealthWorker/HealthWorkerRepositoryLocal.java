package grupo12.practico.repositories.HealthWorker;

import jakarta.ejb.Local;

import java.util.List;

import grupo12.practico.models.HealthWorker;

@Local
public interface HealthWorkerRepositoryLocal {
    HealthWorker add(HealthWorker healthWorker);

    List<HealthWorker> findAll();

    List<HealthWorker> findByName(String name);

    HealthWorker findById(String id);
}
