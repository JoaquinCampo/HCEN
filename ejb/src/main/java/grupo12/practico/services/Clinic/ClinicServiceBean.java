package grupo12.practico.services.Clinic;

import grupo12.practico.models.Clinic;
import grupo12.practico.dtos.Clinic.AddClinicDTO;
import grupo12.practico.dtos.Clinic.ClinicDTO;
import grupo12.practico.dtos.Clinic.ClinicAdminInfoDTO;
import grupo12.practico.repositories.Clinic.ClinicRepositoryLocal;
import jakarta.ejb.EJB;
import jakarta.ejb.Local;
import jakarta.ejb.Remote;
import jakarta.ejb.Stateless;
import jakarta.validation.ValidationException;

import java.util.List;
import java.util.stream.Collectors;

@Stateless
@Local(ClinicServiceLocal.class)
@Remote(ClinicServiceRemote.class)
public class ClinicServiceBean implements ClinicServiceRemote {

    @EJB
    private ClinicRepositoryLocal repository;

    @EJB
    private ClinicRegistrationNotifierLocal registrationNotifier;

    @Override
    public ClinicDTO addClinic(AddClinicDTO addclinicDTO) {
        validateClinic(addclinicDTO);
        Clinic clinic = new Clinic();
        clinic.setName(addclinicDTO.getName());
        clinic.setEmail(addclinicDTO.getEmail());
        clinic.setPhone(addclinicDTO.getPhone());
        clinic.setAddress(addclinicDTO.getAddress());
        Clinic persisted = repository.add(clinic);
        registrationNotifier.notifyClinicCreated(persisted, addclinicDTO.getClinicAdmin());
        return persisted.toDto();
    }

    @Override
    public List<ClinicDTO> findAll() {
        return repository.findAll().stream()
                .map(Clinic::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public ClinicDTO findById(String id) {
        return repository.findById(id).toDto();
    }

    @Override
    public List<ClinicDTO> findByName(String name) {
        return repository.findByName(name).stream()
                .map(Clinic::toDto)
                .collect(Collectors.toList());
    }

    private void validateClinic(AddClinicDTO addClinicDTO) {
        if (addClinicDTO == null) {
            throw new ValidationException("Clinic must not be null");
        }
        if (isBlank(addClinicDTO.getName())) {
            throw new ValidationException("Clinic name is required");
        }
        if (isBlank(addClinicDTO.getEmail())) {
            throw new ValidationException("Clinic email is required");
        }
        if (isBlank(addClinicDTO.getPhone())) {
            throw new ValidationException("Clinic phone is required");
        }
        if (isBlank(addClinicDTO.getAddress())) {
            throw new ValidationException("Address is required");
        }

        ClinicAdminInfoDTO admin = addClinicDTO.getClinicAdmin();
        if (admin == null) {
            throw new ValidationException("Clinic admin information is required");
        }
        if (isBlank(admin.getName())) {
            throw new ValidationException("Clinic admin name is required");
        }
        if (isBlank(admin.getEmail())) {
            throw new ValidationException("Clinic admin email is required");
        }

    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

}
