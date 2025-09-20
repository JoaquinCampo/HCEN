package grupo12.practico.repositories.HealthProvider;

import jakarta.ejb.Local;
import jakarta.ejb.Remote;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import grupo12.practico.models.Clinic;

@Singleton
@Startup
@Local(HealthProviderRepositoryLocal.class)
@Remote(HealthProviderRepositoryRemote.class)
public class HealthProviderRepositoryBean implements HealthProviderRepositoryRemote {

    private final Map<String, Clinic> idToHealthProvider = new HashMap<>();

    @Override
    public Clinic add(Clinic healthProvider) {
        if (healthProvider == null || healthProvider.getId() == null)
            return healthProvider;
        idToHealthProvider.put(healthProvider.getId(), healthProvider);
        return healthProvider;
    }

    @Override
    public List<Clinic> findAll() {
        return new ArrayList<>(idToHealthProvider.values());
    }

    @Override
    public Clinic findById(String id) {
        if (id == null || id.trim().isEmpty()) {
            return null;
        }
        return idToHealthProvider.get(id);
    }

    @Override
    public List<Clinic> findByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return findAll();
        }
        String normalized = name.trim().toLowerCase(Locale.ROOT);
        return idToHealthProvider.values().stream()
                .filter(hp -> (hp.getName() != null && hp.getName().toLowerCase(Locale.ROOT).contains(normalized)))
                .collect(Collectors.toList());
    }

}
