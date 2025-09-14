package grupo12.practico.repository.healthprovider;

import grupo12.practico.model.HealthProvider;
import jakarta.ejb.Local;
import jakarta.ejb.Remote;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

@Singleton
@Startup
@Local(HealthProviderRepositoryLocal.class)
@Remote(HealthProviderRepositoryRemote.class)
public class HealthProviderRepositoryBean implements HealthProviderRepositoryRemote {

    private final List<HealthProvider> healthProviders = new ArrayList<>();

    @Override
    public HealthProvider add(HealthProvider healthProvider) {
        healthProviders.add(healthProvider);
        return healthProvider;
    }

    @Override
    public List<HealthProvider> findAll() {
        return new ArrayList<>(healthProviders);
    }

    @Override
    public HealthProvider findById(String id) {
        if (id == null || id.trim().isEmpty()) {
            return null;
        }
        Optional<HealthProvider> result = healthProviders.stream()
                .filter(hp -> id.equals(hp.getId()))
                .findFirst();
        return result.orElse(null);
    }

    @Override
    public List<HealthProvider> findByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return findAll();
        }
        String normalized = name.trim().toLowerCase(Locale.ROOT);
        return healthProviders.stream()
                .filter(hp -> (hp.getName() != null && hp.getName().toLowerCase(Locale.ROOT).contains(normalized)))
                .collect(Collectors.toList());
    }

    @Override
    public List<HealthProvider> findActive() {
        return healthProviders.stream()
                .filter(HealthProvider::isActive)
                .collect(Collectors.toList());
    }
}
