package grupo12.practico.services.User;

import grupo12.practico.models.HealthWorker;
import grupo12.practico.models.User;
import grupo12.practico.repositories.User.UserRepositoryLocal;
import jakarta.ejb.EJB;
import jakarta.ejb.Local;
import jakarta.ejb.Remote;
import jakarta.ejb.Stateless;
import jakarta.validation.ValidationException;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@Stateless
@Local(UserServiceLocal.class)
@Remote(UserServiceRemote.class)
public class UserServiceBean implements UserServiceRemote {

    @EJB
    private UserRepositoryLocal userRepository;

    @Override
    public User addUser(User user) {
        validateUser(user);
        for (HealthWorker hw : user.getHealthWorkers()) {
            if (hw != null) {
                hw.addPatient(user);
            }
        }
        return userRepository.add(user);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public List<User> findUsersByName(String name) {
        return userRepository.findByName(name);
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public User findById(String id) {
        return userRepository.findById(id);
    }

    private void validateUser(User user) {
        if (user == null) {
            throw new ValidationException("User must not be null");
        }
        if (isBlank(user.getFirstName()) || isBlank(user.getLastName())) {
            throw new ValidationException("First name and last name are required");
        }
        if (isBlank(user.getDni())) {
            throw new ValidationException("DNI is required");
        }
        LocalDate dob = user.getDateOfBirth();
        if (dob == null || Period.between(dob, LocalDate.now()).getYears() < 18) {
            throw new ValidationException("User must be at least 18 years old");
        }
        if (user.getHealthWorkers() == null || user.getHealthWorkers().isEmpty()) {
            throw new ValidationException("User must be associated with at least one HealthWorker");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
