package grupo12.practico.service.healthworker;

import grupo12.practico.model.HealthWorker;
import grupo12.practico.repository.healthworker.HealthWorkerRepositoryLocal;
import jakarta.ejb.EJB;
import jakarta.ejb.Local;
import jakarta.ejb.Remote;
import jakarta.ejb.Stateless;
import jakarta.validation.ValidationException;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

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
    public List<HealthWorker> searchHealthWorkersByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return getAllHealthWorkers();
        }
        String normalized = name.trim().toLowerCase(Locale.ROOT);
        return getAllHealthWorkers().stream()
                .filter(hw ->
                        (hw.getFirstName() != null && hw.getFirstName().toLowerCase(Locale.ROOT).contains(normalized)) ||
                        (hw.getLastName() != null && hw.getLastName().toLowerCase(Locale.ROOT).contains(normalized))
                )
                .collect(Collectors.toList());
    }

    @Override
    public List<HealthWorker> searchHealthWorkersById(String id) {
        if (id == null || id.trim().isEmpty()) {
            return getAllHealthWorkers();
        }
        return getAllHealthWorkers().stream()
                .filter(hw ->
                        (hw.getId() != null && hw.getId().equals(id))
                )
                .collect(Collectors.toList());
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


