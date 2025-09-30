package grupo12.practico.services.Clinic;

import jakarta.ejb.Local;

import java.util.List;

import grupo12.practico.dtos.Clinic.AddClinicDTO;
import grupo12.practico.dtos.Clinic.ClinicDTO;

@Local
public interface ClinicServiceLocal {
    ClinicDTO addClinic(AddClinicDTO addClinicDTO);

    List<ClinicDTO> findAll();

    ClinicDTO findById(String id);

    List<ClinicDTO> findByName(String name);
}
