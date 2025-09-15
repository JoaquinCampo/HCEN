package grupo12.practico.repositories.User;

import jakarta.ejb.Local;
import jakarta.ejb.Remote;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import java.util.List;
import java.util.ArrayList;
import java.util.Locale;
import java.util.stream.Collectors;

import grupo12.practico.models.User;

import java.util.Map;
import java.util.HashMap;

@Singleton
@Startup
@Local(UserRepositoryLocal.class)
@Remote(UserRepositoryRemote.class)
public class UserRepositoryBean implements UserRepositoryRemote {

    private final Map<String, User> idToUser = new HashMap<>();

    @Override
    public User add(User user) {
        if (user == null || user.getId() == null)
            return user;
        idToUser.put(user.getId(), user);
        return user;
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(idToUser.values());
    }

    @Override
    public List<User> findByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return findAll();
        }
        String normalized = name.trim().toLowerCase(Locale.ROOT);
        return idToUser.values().stream()
                .filter(u -> (u.getFirstName() != null
                        && u.getFirstName().toLowerCase(Locale.ROOT).contains(normalized)) ||
                        (u.getLastName() != null && u.getLastName().toLowerCase(Locale.ROOT).contains(normalized)))
                .collect(Collectors.toList());
    }

    @Override
    public User findById(String id) {
        if (id == null || id.trim().isEmpty())
            return null;
        return idToUser.get(id);
    }
}
