package grupo12.practico.services.Specialty;

import jakarta.ejb.Local;

import java.util.List;

import grupo12.practico.dtos.Specialty.AddSpecialtyDTO;
import grupo12.practico.dtos.SpecialtyDTO;

@Local
public interface SpecialtyServiceLocal {
    List<SpecialtyDTO> findAll();

    SpecialtyDTO findById(String id);

    List<SpecialtyDTO> findByName(String name);

    SpecialtyDTO add(AddSpecialtyDTO addSpecialtyDTO);
}
