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
    public List<HealthProvider> findAll() {
        return repository.findAll();
    }

    @Override
    public HealthProvider findById(String id) {
        return repository.findById(id);
    }

    @Override
    public List<HealthProvider> findByName(String name) {
        return repository.findByName(name);
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
