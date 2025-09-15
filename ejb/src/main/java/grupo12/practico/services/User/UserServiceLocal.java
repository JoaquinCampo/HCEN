package grupo12.practico.services.User;

import jakarta.ejb.Local;

import java.util.List;

import grupo12.practico.models.User;

@Local
public interface UserServiceLocal {
    User addUser(User user);

    List<User> getAllUsers();

    List<User> findUsersByName(String name);

    List<User> findAll();

    User findById(String id);
}
