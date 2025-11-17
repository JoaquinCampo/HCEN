package grupo12.practico.services.HealthWorker;

import grupo12.practico.dtos.HealthWorker.HealthWorkerDTO;
import java.util.List;

public interface HealthWorkerServiceLocal {
    HealthWorkerDTO findByClinicAndCi(String clinicName, String healthWorkerCi);

    List<HealthWorkerDTO> findByClinic(String clinicName);
}
