package grupo12.practico.services.HealthUser;

import grupo12.practico.dto.HealthUserDTO;
import grupo12.practico.models.HealthUser;
import grupo12.practico.repositories.HealthUser.HealthUserRepositoryLocal;
import jakarta.ejb.EJB;
import jakarta.ejb.Local;
import jakarta.ejb.Remote;
import jakarta.ejb.Stateless;
import jakarta.validation.ValidationException;

import java.util.List;

@Stateless
@Local(HealthUserServiceLocal.class)
@Remote(HealthUserServiceRemote.class)
public class HealthUserServiceBean implements HealthUserServiceRemote {

    @EJB
    private HealthUserRepositoryLocal userRepository;

    @Override
    public List<HealthUserDTO> findAll() {
        return userRepository.findAll();
    }

    @Override
    public HealthUserDTO findById(String id) {
        return userRepository.findById(id);
    }

    @Override
    public List<HealthUserDTO> findByName(String name) {
        return userRepository.findByName(name);
    }

    @Override
    public HealthUserDTO add(HealthUser user) {
        validateUser(user);

        return userRepository.add(user);
    }

    private void validateUser(HealthUser healthUser) {
        if (healthUser == null) {
            throw new ValidationException("User must not be null");
        }
        if (isBlank(healthUser.getFirstName()) || isBlank(healthUser.getLastName())) {
            throw new ValidationException("User first name and last name are required");
        }
        if (isBlank(healthUser.getDocument())) {
            throw new ValidationException("User document is required");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
