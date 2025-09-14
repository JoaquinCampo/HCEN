package grupo12.practico.repository.healthworker;

import grupo12.practico.model.HealthWorker;
import jakarta.ejb.Local;
import jakarta.ejb.Remote;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.HashSet;
import java.util.Set;

@Singleton
@Startup
@Local(HealthWorkerRepositoryLocal.class)
@Remote(HealthWorkerRepositoryRemote.class)
public class HealthWorkerRepositoryBean implements HealthWorkerRepositoryRemote {

    private final Set<HealthWorker> healthWorkers = new HashSet<>();

    @Override
    public HealthWorker add(HealthWorker healthWorker) {
        healthWorkers.add(healthWorker);
        return healthWorker;
    }

    @Override
    public List<HealthWorker> findAll() {
        return new ArrayList<>(healthWorkers);
    }

    @Override
    public List<HealthWorker> findByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return findAll();
        }
        String normalized = name.trim().toLowerCase(Locale.ROOT);
        return healthWorkers.stream()
                .filter(hw ->
                        (hw.getFirstName() != null && hw.getFirstName().toLowerCase(Locale.ROOT).contains(normalized)) ||
                        (hw.getLastName() != null && hw.getLastName().toLowerCase(Locale.ROOT).contains(normalized))
                )
                .collect(Collectors.toList());
    }

    @Override
    public List<HealthWorker> findById(String id) {
        if (id == null || id.trim().isEmpty()) {
            return findAll();
        }
        return healthWorkers.stream()
                .filter(hw -> hw.getId() != null && hw.getId().equals(id))
                .collect(Collectors.toList());
    }
}


