package grupo12.practico.service.healthworker;

import grupo12.practico.model.HealthWorker;
import grupo12.practico.repository.healthworker.HealthWorkerRepositoryLocal;
import jakarta.ejb.EJB;
import jakarta.ejb.Local;
import jakarta.ejb.Remote;
import jakarta.ejb.Stateless;
import jakarta.validation.ValidationException;

import java.util.List;

@Stateless
@Local(HealthWorkerServiceLocal.class)
@Remote(HealthWorkerServiceRemote.class)
public class HealthWorkerServiceBean implements HealthWorkerServiceRemote {

    @EJB
    private HealthWorkerRepositoryLocal repository;

    @Override
    public HealthWorker addHealthWorker(HealthWorker healthWorker) {
        validateHealthWorker(healthWorker);
        return repository.add(healthWorker);
    }

    @Override
    public List<HealthWorker> getAllHealthWorkers() {
        return repository.findAll();
    }

    @Override
    public List<HealthWorker> findHealthWorkersByName(String name) {
        return repository.findByName(name);
    }

    @Override
    public List<HealthWorker> findHealthWorkersById(String id) {
        return repository.findById(id);
    }

    private void validateHealthWorker(HealthWorker hw) {
        if (hw == null) {
            throw new ValidationException("HealthWorker must not be null");
        }
        if (isBlank(hw.getFirstName()) || isBlank(hw.getLastName())) {
            throw new ValidationException("HealthWorker first name and last name are required");
        }
        if (isBlank(hw.getLicenseNumber())) {
            throw new ValidationException("License number is required");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
