package grupo12.practico.services.Clinic;

import jakarta.ejb.Local;

import grupo12.practico.dtos.Clinic.AddClinicDTO;
import grupo12.practico.dtos.Clinic.ClinicDTO;
import java.util.List;

@Local
public interface ClinicServiceLocal {
    ClinicDTO create(AddClinicDTO addClinicDTO);

    ClinicDTO findByName(String name);

    List<ClinicDTO> findAll();
}
