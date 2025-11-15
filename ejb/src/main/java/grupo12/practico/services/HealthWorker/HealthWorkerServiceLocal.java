package grupo12.practico.services.HealthWorker;

import grupo12.practico.dtos.HealthWorker.HealthWorkerDTO;

public interface HealthWorkerServiceLocal {
    HealthWorkerDTO findByClinicAndCi(String clinicName, String healthWorkerCi);
}
