package grupo12.practico.service.user;

import grupo12.practico.model.User;
import jakarta.ejb.Local;

import java.util.List;

@Local
public interface UserServiceLocal {
    User addUser(User user);

    List<User> getAllUsers();

    List<User> searchUsersByName(String name);
}
