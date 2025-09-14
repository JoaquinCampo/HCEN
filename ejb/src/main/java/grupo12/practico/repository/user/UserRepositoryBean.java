package grupo12.practico.repository.user;

import grupo12.practico.model.User;
import jakarta.ejb.Local;
import jakarta.ejb.Remote;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import java.util.List;
import java.util.ArrayList;

@Singleton
@Startup
@Local(UserRepositoryLocal.class)
@Remote(UserRepositoryRemote.class)
public class UserRepositoryBean implements UserRepositoryRemote {

    private final List<User> users = new ArrayList<>();
 

    @Override
    public User add(User user) {
        users.add(user);
        return user;
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users);
    }
}


