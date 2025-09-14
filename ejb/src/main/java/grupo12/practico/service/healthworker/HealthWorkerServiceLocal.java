package grupo12.practico.service.healthworker;

import grupo12.practico.model.HealthWorker;
import jakarta.ejb.Local;

import java.util.List;

@Local
public interface HealthWorkerServiceLocal {
    HealthWorker addHealthWorker(HealthWorker healthWorker);
    List<HealthWorker> getAllHealthWorkers();
    List<HealthWorker> searchHealthWorkersByName(String name);
    List<HealthWorker> searchHealthWorkersById(String id);
}


