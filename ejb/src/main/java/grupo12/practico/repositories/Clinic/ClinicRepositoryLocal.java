package grupo12.practico.repositories.Clinic;

import java.util.List;

import grupo12.practico.dtos.Clinic.AddClinicDTO;
import grupo12.practico.dtos.Clinic.ClinicDTO;
import jakarta.ejb.Local;

@Local
public interface ClinicRepositoryLocal {
    ClinicDTO createClinic(AddClinicDTO clinicDTO);

    ClinicDTO findClinicByName(String name);

    List<ClinicDTO> findAllClinics(String providerName);
}
