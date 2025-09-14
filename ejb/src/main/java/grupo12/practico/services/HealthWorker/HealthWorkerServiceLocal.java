package grupo12.practico.services.HealthWorker;

import jakarta.ejb.Local;

import java.util.List;

import grupo12.practico.models.HealthWorker;

@Local
public interface HealthWorkerServiceLocal {
    HealthWorker addHealthWorker(HealthWorker healthWorker);

    List<HealthWorker> getAllHealthWorkers();

    List<HealthWorker> findHealthWorkersByName(String name);

    HealthWorker findById(String id);
}
