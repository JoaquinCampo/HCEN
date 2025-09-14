package grupo12.practico.service.healthprovider;

import grupo12.practico.model.HealthProvider;
import grupo12.practico.repository.healthprovider.HealthProviderRepositoryLocal;
import jakarta.ejb.EJB;
import jakarta.ejb.Local;
import jakarta.ejb.Remote;
import jakarta.ejb.Stateless;
import jakarta.validation.ValidationException;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Stateless
@Local(HealthProviderServiceLocal.class)
@Remote(HealthProviderServiceRemote.class)
public class HealthProviderServiceBean implements HealthProviderServiceRemote {

    @EJB
    private HealthProviderRepositoryLocal repository;

    @Override
    public HealthProvider addHealthProvider(HealthProvider healthProvider) {
        validateHealthProvider(healthProvider);
        return repository.add(healthProvider);
    }

    @Override
    public List<HealthProvider> getAllHealthProviders() {
        return repository.findAll();
    }

    @Override
    public HealthProvider getHealthProviderById(String id) {
        return repository.findById(id);
    }

    @Override
    public List<HealthProvider> searchHealthProvidersByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return getAllHealthProviders();
        }
        String normalized = name.trim().toLowerCase(Locale.ROOT);
        return getAllHealthProviders().stream()
                .filter(hp -> (hp.getName() != null && hp.getName().toLowerCase(Locale.ROOT).contains(normalized)))
                .collect(Collectors.toList());
    }

    @Override
    public List<HealthProvider> getActiveHealthProviders() {
        return repository.findActive();
    }

    private void validateHealthProvider(HealthProvider hp) {
        if (hp == null) {
            throw new ValidationException("HealthProvider must not be null");
        }
        if (isBlank(hp.getName())) {
            throw new ValidationException("HealthProvider name is required");
        }
        if (isBlank(hp.getAddress())) {
            throw new ValidationException("Address is required");
        }
        if (hp.getRegistrationDate() != null && hp.getRegistrationDate().isAfter(LocalDate.now())) {
            throw new ValidationException("Registration date cannot be in the future");
        }

    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

}
