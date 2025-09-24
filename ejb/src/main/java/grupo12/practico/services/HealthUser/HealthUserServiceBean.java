package grupo12.practico.services.HealthUser;

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
    public List<HealthUser> findAll() {
        return userRepository.findAll();
    }

    @Override
    public HealthUser findById(String id) {
        return userRepository.findById(id);
    }

    @Override
    public List<HealthUser> findByName(String name) {
        return userRepository.findByName(name);
    }

    @Override
    public HealthUser add(HealthUser user) {
        validateUser(user);
        return userRepository.add(user);
    }

    @Override
    public List<HealthUser> getAllUsers() {
        return findAll();
    }

    @Override
    public List<HealthUser> findUsersByName(String name) {
        return findByName(name);
    }

    @Override
    public HealthUser addUser(HealthUser healthUser) {
        return add(healthUser);
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
        if (healthUser.getDocumentType() == null) {
            throw new ValidationException("User document type is required");
        }
        // Password is optional until authentication system is implemented
        // if (isBlank(healthUser.getPasswordHash())) {
        // throw new ValidationException("User password is required");
        // }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
