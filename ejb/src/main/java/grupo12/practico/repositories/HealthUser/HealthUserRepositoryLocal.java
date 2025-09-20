package grupo12.practico.repositories.HealthUser;

import jakarta.ejb.Local;

import java.util.List;

import grupo12.practico.models.HealthUser;
import grupo12.practico.dto.HealthUserDTO;

@Local
public interface HealthUserRepositoryLocal {
    List<HealthUserDTO> findAll();

    HealthUserDTO findById(String id);

    List<HealthUserDTO> findByName(String name);

    HealthUserDTO add(HealthUser healthUser);
}
