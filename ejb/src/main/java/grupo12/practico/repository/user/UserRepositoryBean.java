package grupo12.practico.repository.user;

import grupo12.practico.model.User;
import jakarta.ejb.Local;
import jakarta.ejb.Remote;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import java.util.List;
import java.util.ArrayList;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.Set;
import java.util.HashSet;

@Singleton
@Startup
@Local(UserRepositoryLocal.class)
@Remote(UserRepositoryRemote.class)
public class UserRepositoryBean implements UserRepositoryRemote {

    private final Set<User> users = new HashSet<>();

    @Override
    public User add(User user) {
        users.add(user);
        return user;
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users);
    }

    @Override
    public List<User> findByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return findAll();
        }
        String normalized = name.trim().toLowerCase(Locale.ROOT);
        return users.stream()
                .filter(u -> (u.getFirstName() != null
                        && u.getFirstName().toLowerCase(Locale.ROOT).contains(normalized)) ||
                        (u.getLastName() != null && u.getLastName().toLowerCase(Locale.ROOT).contains(normalized)))
                .collect(Collectors.toList());
    }
}
