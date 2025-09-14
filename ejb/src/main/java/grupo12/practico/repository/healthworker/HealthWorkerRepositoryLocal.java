package grupo12.practico.repository.healthworker;

import grupo12.practico.model.HealthWorker;
import jakarta.ejb.Local;

import java.util.List;

@Local
public interface HealthWorkerRepositoryLocal {
    HealthWorker add(HealthWorker healthWorker);
    List<HealthWorker> findAll();
}


