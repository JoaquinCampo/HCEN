package grupo12.practico.services.Clinic;

import grupo12.practico.dtos.Clinic.AddClinicDTO;
import grupo12.practico.dtos.Clinic.ClinicAdminDTO;
import grupo12.practico.dtos.Clinic.ClinicDTO;
import grupo12.practico.repositories.Clinic.ClinicRepositoryLocal;
import grupo12.practico.repositories.HealthUser.HealthUserRepositoryLocal;
import jakarta.ejb.EJB;
import jakarta.ejb.Local;
import jakarta.ejb.Remote;
import jakarta.ejb.Stateless;
import jakarta.validation.ValidationException;

import java.util.List;

@Stateless
@Local(ClinicServiceLocal.class)
@Remote(ClinicServiceRemote.class)
public class ClinicServiceBean implements ClinicServiceRemote {

    @EJB
    private HealthUserRepositoryLocal healthUserRepository;

    @EJB
    private ClinicRepositoryLocal clinicRepository;

    @Override
    public ClinicDTO create(AddClinicDTO addclinicDTO) {
        validateClinic(addclinicDTO);

        ClinicDTO createdClinic = clinicRepository.create(addclinicDTO);

        return createdClinic;
    }

    @Override
    public ClinicDTO findByName(String clinicName) {
        if (isBlank(clinicName)) {
            throw new ValidationException("Clinic name is required");
        }

        return clinicRepository.findByName(clinicName);
    }

    @Override
    public List<ClinicDTO> findAll() {
        return clinicRepository.findAll();
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

        ClinicAdminDTO admin = addClinicDTO.getClinicAdmin();
        if (admin == null) {
            throw new ValidationException("Clinic admin information is required");
        }
        if (isBlank(admin.getCi())) {
            throw new ValidationException("Clinic admin CI is required");
        }
        if (isBlank(admin.getFirstName())) {
            throw new ValidationException("Clinic admin first name is required");
        }
        if (isBlank(admin.getLastName())) {
            throw new ValidationException("Clinic admin last name is required");
        }
        if (isBlank(admin.getEmail())) {
            throw new ValidationException("Clinic admin email is required");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

}
