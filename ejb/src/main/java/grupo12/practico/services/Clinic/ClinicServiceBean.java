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
public class ClinicServiceBean implements ClinicServiceLocal {

    @EJB
    private HealthUserRepositoryLocal healthUserRepository;

    @EJB
    private ClinicRepositoryLocal clinicRepository;

    @Override
    public ClinicDTO createClinic(AddClinicDTO addclinicDTO) {
        validateClinic(addclinicDTO);

        ClinicDTO createdClinic = clinicRepository.createClinic(addclinicDTO);

        return createdClinic;
    }

    @Override
    public ClinicDTO findClinicByName(String clinicName) {
        if (clinicName == null || clinicName.isBlank()) {
            throw new ValidationException("Clinic name is required");
        }

        return clinicRepository.findClinicByName(clinicName);
    }

    @Override
    public List<ClinicDTO> findAllClinics(String providerName) {
        return clinicRepository.findAllClinics(providerName);
    }

    private void validateClinic(AddClinicDTO addClinicDTO) {
        if (addClinicDTO == null) {
            throw new ValidationException("Clinic must not be null");
        }

        if (addClinicDTO.getName() == null || addClinicDTO.getName().isBlank()) {
            throw new ValidationException("Clinic name is required");
        }

        if (addClinicDTO.getEmail() == null || addClinicDTO.getEmail().isBlank()) {
            throw new ValidationException("Clinic email is required");
        }

        if (addClinicDTO.getPhone() == null || addClinicDTO.getPhone().isBlank()) {
            throw new ValidationException("Clinic phone is required");
        }

        if (addClinicDTO.getAddress() == null || addClinicDTO.getAddress().isBlank()) {
            throw new ValidationException("Address is required");
        }

        ClinicAdminDTO admin = addClinicDTO.getClinicAdmin();

        if (admin == null) {
            throw new ValidationException("Clinic admin information is required");
        }
        if (admin.getCi() == null || admin.getCi().isBlank()) {
            throw new ValidationException("Clinic admin CI is required");
        }
        if (admin.getFirstName() == null || admin.getFirstName().isBlank()) {
            throw new ValidationException("Clinic admin first name is required");
        }
        if (admin.getLastName() == null || admin.getLastName().isBlank()) {
            throw new ValidationException("Clinic admin last name is required");
        }
        if (admin.getEmail() == null || admin.getEmail().isBlank()) {
            throw new ValidationException("Clinic admin email is required");
        }
    }
}
