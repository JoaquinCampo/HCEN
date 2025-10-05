package grupo12.practico.services.HealthWorker;

import jakarta.ejb.Local;

import java.util.List;

import grupo12.practico.dtos.HealthWorker.AddHealthWorkerDTO;
import grupo12.practico.dtos.HealthWorker.HealthWorkerDTO;

@Local
public interface HealthWorkerServiceLocal {
    HealthWorkerDTO add(AddHealthWorkerDTO healthWorkerDTO);

    List<HealthWorkerDTO> findAll();

    List<HealthWorkerDTO> findByName(String name);

    HealthWorkerDTO findById(String id);
}
