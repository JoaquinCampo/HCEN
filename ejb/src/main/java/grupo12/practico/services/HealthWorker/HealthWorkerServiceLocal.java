package grupo12.practico.services.HealthWorker;

import java.util.List;

import grupo12.practico.dtos.HealthWorker.HealthWorkerDTO;

public interface HealthWorkerServiceLocal {
    List<HealthWorkerDTO> findAll();

    HealthWorkerDTO findById(String id);

    List<HealthWorkerDTO> findByName(String name);

    HealthWorkerDTO findByClinicAndCi(String clinicName, String healthWorkerCi);
}

