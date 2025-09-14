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

import grupo12.practico.models.HealthProvider;

@Singleton
@Startup
@Local(HealthProviderRepositoryLocal.class)
@Remote(HealthProviderRepositoryRemote.class)
public class HealthProviderRepositoryBean implements HealthProviderRepositoryRemote {

    private final Map<String, HealthProvider> idToHealthProvider = new HashMap<>();

    @Override
    public HealthProvider add(HealthProvider healthProvider) {
        if (healthProvider == null || healthProvider.getId() == null)
            return healthProvider;
        idToHealthProvider.put(healthProvider.getId(), healthProvider);
        return healthProvider;
    }

    @Override
    public List<HealthProvider> findAll() {
        return new ArrayList<>(idToHealthProvider.values());
    }

    @Override
    public HealthProvider findById(String id) {
        if (id == null || id.trim().isEmpty()) {
            return null;
        }
        return idToHealthProvider.get(id);
    }

    @Override
    public List<HealthProvider> findByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return findAll();
        }
        String normalized = name.trim().toLowerCase(Locale.ROOT);
        return idToHealthProvider.values().stream()
                .filter(hp -> (hp.getName() != null && hp.getName().toLowerCase(Locale.ROOT).contains(normalized)))
                .collect(Collectors.toList());
    }

}
