package grupo12.practico.services.HealthProvider;

import grupo12.practico.models.Clinic;
import grupo12.practico.repositories.HealthProvider.HealthProviderRepositoryLocal;
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
    public Clinic addHealthProvider(Clinic healthProvider) {
        validateHealthProvider(healthProvider);
        return repository.add(healthProvider);
    }

    @Override
    public List<Clinic> findAll() {
        return repository.findAll();
    }

    @Override
    public Clinic findById(String id) {
        return repository.findById(id);
    }

    @Override
    public List<Clinic> findByName(String name) {
        return repository.findByName(name);
    }

    private void validateHealthProvider(Clinic hp) {
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
