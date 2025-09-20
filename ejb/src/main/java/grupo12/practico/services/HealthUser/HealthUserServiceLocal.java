package grupo12.practico.services.HealthUser;

import jakarta.ejb.Local;

import java.util.List;

import grupo12.practico.dto.HealthUserDTO;
import grupo12.practico.models.HealthUser;

@Local
public interface HealthUserServiceLocal {
    List<HealthUserDTO> findAll();

    HealthUserDTO findById(String id);

    List<HealthUserDTO> findByName(String name);

    HealthUserDTO add(HealthUser healthUser);
}
