package grupo12.practico.service.user;

import grupo12.practico.model.User;
import grupo12.practico.repository.user.UserRepositoryLocal;
import jakarta.ejb.EJB;
import jakarta.ejb.Local;
import jakarta.ejb.Remote;
import jakarta.ejb.Stateless;
import jakarta.validation.ValidationException;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Stateless
@Local(UserServiceLocal.class)
@Remote(UserServiceRemote.class)
public class UserServiceBean implements UserServiceRemote {

    @EJB
    private UserRepositoryLocal userRepository;

    @Override
    public User addUser(User user) {
        validateUser(user);
        return userRepository.add(user);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public List<User> searchUsersByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return getAllUsers();
        }
        String normalized = name.trim().toLowerCase(Locale.ROOT);
        return getAllUsers().stream()
                .filter(u ->
                        (u.getFirstName() != null && u.getFirstName().toLowerCase(Locale.ROOT).contains(normalized)) ||
                        (u.getLastName() != null && u.getLastName().toLowerCase(Locale.ROOT).contains(normalized))
                )
                .collect(Collectors.toList());
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
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}


