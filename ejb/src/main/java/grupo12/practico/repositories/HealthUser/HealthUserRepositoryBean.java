package grupo12.practico.repositories.HealthUser;

import jakarta.ejb.Local;
import jakarta.ejb.Remote;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import grupo12.practico.models.HealthUser;

import jakarta.validation.ValidationException;

@Singleton
@Startup
@Local(HealthUserRepositoryLocal.class)
@Remote(HealthUserRepositoryRemote.class)
public class HealthUserRepositoryBean implements HealthUserRepositoryRemote {

    private final Map<String, HealthUser> healthUserMap = new HashMap<>();

    @Override
    public List<HealthUser> findAll() {
        return new ArrayList<>(healthUserMap.values());
    }

    @Override
    public HealthUser findById(String id) {
        if (id == null || id.trim().isEmpty())
            return null;
        return healthUserMap.get(id);
    }

    @Override
    public List<HealthUser> findByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return findAll();
        }

        String normalized = name.trim().toLowerCase(Locale.ROOT);
        return healthUserMap.values().stream()
                .filter(u -> (u.getFirstName() != null
                        && u.getFirstName().toLowerCase(Locale.ROOT).contains(normalized)) ||
                        (u.getLastName() != null && u.getLastName().toLowerCase(Locale.ROOT).contains(normalized)))
                .collect(Collectors.toList());
    }

    @Override
    public HealthUser add(HealthUser healthUser) {
        if (healthUser == null || healthUser.getId() == null)
            throw new ValidationException("HealthUser must not be null");

        healthUserMap.put(healthUser.getId(), healthUser);
        return healthUser;
    }
}
