package grupo12.practico.service.healthworker;

import grupo12.practico.model.HealthWorker;
import jakarta.ejb.Local;

import java.util.List;

@Local
public interface HealthWorkerServiceLocal {
    HealthWorker addHealthWorker(HealthWorker healthWorker);
    List<HealthWorker> getAllHealthWorkers();
    List<HealthWorker> findHealthWorkersByName(String name);
    List<HealthWorker> findHealthWorkersById(String id);
}


