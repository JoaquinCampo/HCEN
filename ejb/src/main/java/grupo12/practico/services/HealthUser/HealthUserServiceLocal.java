package grupo12.practico.services.HealthUser;

import jakarta.ejb.Local;

import java.util.List;

import grupo12.practico.dtos.HealthUser.AddHealthUserDTO;
import grupo12.practico.dtos.HealthUser.HealthUserDTO;

@Local
public interface HealthUserServiceLocal {
    List<HealthUserDTO> findAll();

    HealthUserDTO findById(String id);

    List<HealthUserDTO> findByName(String name);

    HealthUserDTO add(AddHealthUserDTO addHealthUserDTO);
}
