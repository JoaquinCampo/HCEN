package grupo12.practico.services.HealthWorker;

import jakarta.ejb.Local;

import java.util.List;

import grupo12.practico.dtos.HealthWorker.AddHealthWorkerDTO;
import grupo12.practico.dtos.HealthWorker.HealthWorkerDTO;

@Local
public interface HealthWorkerServiceLocal {
    HealthWorkerDTO addHealthWorker(AddHealthWorkerDTO healthWorkerDTO);

    List<HealthWorkerDTO> getAllHealthWorkers();

    List<HealthWorkerDTO> findHealthWorkersByName(String name);

    HealthWorkerDTO findById(String id);
}
