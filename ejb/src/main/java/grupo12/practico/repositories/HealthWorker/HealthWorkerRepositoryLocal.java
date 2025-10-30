package grupo12.practico.repositories.HealthWorker;

import grupo12.practico.dtos.HealthWorker.HealthWorkerDTO;
import jakarta.ejb.Local;

@Local
public interface HealthWorkerRepositoryLocal {
    HealthWorkerDTO findByClinicAndCi(String clinicName, String healthWorkerCi);
}
