package grupo12.practico.repository.user;

import grupo12.practico.model.User;
import jakarta.ejb.Local;

import java.util.List;

@Local
public interface UserRepositoryLocal {
    User add(User user);
    List<User> findAll();
}


