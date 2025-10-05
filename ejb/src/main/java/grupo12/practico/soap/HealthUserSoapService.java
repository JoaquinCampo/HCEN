package grupo12.practico.soap;

import grupo12.practico.dtos.HealthUser.AddHealthUserDTO;
import grupo12.practico.dtos.HealthUser.HealthUserDTO;
import grupo12.practico.services.HealthUser.HealthUserServiceLocal;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;

import java.util.List;

@Stateless
public class HealthUserSoapService {

    @EJB
    private HealthUserServiceLocal healthUserService;

    public List<HealthUserDTO> getAllHealthUsers() {
        return healthUserService.findAll();
    }

    public HealthUserDTO getHealthUserById(String id) {
        return healthUserService.findById(id);
    }

    public List<HealthUserDTO> searchHealthUsersByName(String name) {
        return healthUserService.findByName(name);
    }

    public HealthUserDTO createHealthUser(AddHealthUserDTO healthUserData) {
        return healthUserService.add(healthUserData);
    }
}