package grupo12.practico.services.Clinic;

import grupo12.practico.models.Clinic;
import grupo12.practico.repositories.Clinic.ClinicRepositoryLocal;
import jakarta.ejb.EJB;
import jakarta.ejb.Local;
import jakarta.ejb.Remote;
import jakarta.ejb.Stateless;
import jakarta.validation.ValidationException;

import java.time.LocalDate;
import java.util.List;

@Stateless
@Local(ClinicServiceLocal.class)
@Remote(ClinicServiceRemote.class)
public class ClinicServiceBean implements ClinicServiceRemote {

    @EJB
    private ClinicRepositoryLocal repository;

    @Override
    public Clinic addClinic(Clinic clinic) {
        validateClinic(clinic);
        return repository.add(clinic);
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

    private void validateClinic(Clinic clinic) {
        if (clinic == null) {
            throw new ValidationException("Clinic must not be null");
        }
        if (isBlank(clinic.getName())) {
            throw new ValidationException("Clinic name is required");
        }
        if (isBlank(clinic.getAddress())) {
            throw new ValidationException("Address is required");
        }
        if (clinic.getRegistrationDate() != null && clinic.getRegistrationDate().isAfter(LocalDate.now())) {
            throw new ValidationException("Registration date cannot be in the future");
        }

    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

}
