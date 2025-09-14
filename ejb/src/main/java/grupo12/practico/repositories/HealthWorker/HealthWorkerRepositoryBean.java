package grupo12.practico.repositories.HealthWorker;

import jakarta.ejb.Local;
import jakarta.ejb.Remote;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import grupo12.practico.models.HealthWorker;

import java.util.HashMap;
import java.util.Map;

@Singleton
@Startup
@Local(HealthWorkerRepositoryLocal.class)
@Remote(HealthWorkerRepositoryRemote.class)
public class HealthWorkerRepositoryBean implements HealthWorkerRepositoryRemote {

    private final Map<String, HealthWorker> idToHealthWorker = new HashMap<>();

    @Override
    public HealthWorker add(HealthWorker healthWorker) {
        if (healthWorker == null || healthWorker.getId() == null)
            return healthWorker;
        idToHealthWorker.put(healthWorker.getId(), healthWorker);
        return healthWorker;
    }

    @Override
    public List<HealthWorker> findAll() {
        return new ArrayList<>(idToHealthWorker.values());
    }

    @Override
    public List<HealthWorker> findByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return findAll();
        }
        String normalized = name.trim().toLowerCase(Locale.ROOT);
        return idToHealthWorker.values().stream()
                .filter(hw -> (hw.getFirstName() != null
                        && hw.getFirstName().toLowerCase(Locale.ROOT).contains(normalized)) ||
                        (hw.getLastName() != null && hw.getLastName().toLowerCase(Locale.ROOT).contains(normalized)))
                .collect(Collectors.toList());
    }

    @Override
    public HealthWorker findById(String id) {
        if (id == null || id.trim().isEmpty()) {
            return null;
        }
        return idToHealthWorker.get(id);
    }
}
