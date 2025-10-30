package grupo12.practico.repositories.Clinic;

import java.util.List;

import grupo12.practico.dtos.Clinic.AddClinicDTO;
import grupo12.practico.dtos.Clinic.ClinicDTO;
import jakarta.ejb.Local;

@Local
public interface ClinicRepositoryLocal {
    ClinicDTO create(AddClinicDTO clinicDTO);

    ClinicDTO findByName(String name);

    List<ClinicDTO> findAll();
}
