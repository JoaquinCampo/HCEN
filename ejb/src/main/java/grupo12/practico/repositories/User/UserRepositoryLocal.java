package grupo12.practico.repositories.User;

import jakarta.ejb.Local;

import java.util.List;

import grupo12.practico.models.User;

@Local
public interface UserRepositoryLocal {
    User add(User user);

    List<User> findAll();

    List<User> findByName(String name);

    User findById(String id);
}
