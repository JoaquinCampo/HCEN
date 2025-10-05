package grupo12.practico.soap;

import grupo12.practico.dtos.HealthWorker.AddHealthWorkerDTO;
import grupo12.practico.dtos.HealthWorker.HealthWorkerDTO;
import grupo12.practico.services.HealthWorker.HealthWorkerServiceLocal;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;

import java.util.List;

@Stateless
public class HealthWorkerSoapService {

    @EJB
    private HealthWorkerServiceLocal healthWorkerService;

    public List<HealthWorkerDTO> getAllHealthWorkers() {
        return healthWorkerService.getAllHealthWorkers();
    }

    public HealthWorkerDTO getHealthWorkerById(String id) {
        return healthWorkerService.findById(id);
    }

    public List<HealthWorkerDTO> searchHealthWorkersByName(String name) {
        return healthWorkerService.findHealthWorkersByName(name);
    }

    public HealthWorkerDTO createHealthWorker(AddHealthWorkerDTO healthWorkerData) {
        return healthWorkerService.addHealthWorker(healthWorkerData);
    }
}