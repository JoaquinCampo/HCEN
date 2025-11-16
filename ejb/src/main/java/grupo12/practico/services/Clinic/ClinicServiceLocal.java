package grupo12.practico.services.Clinic;

import jakarta.ejb.Local;

import grupo12.practico.dtos.Clinic.AddClinicDTO;
import grupo12.practico.dtos.Clinic.ClinicDTO;
import java.util.List;

@Local
public interface ClinicServiceLocal {
    ClinicDTO createClinic(AddClinicDTO addClinicDTO);

    ClinicDTO findClinicByName(String name);

    List<ClinicDTO> findAllClinics(String providerName);
}
