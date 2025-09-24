package grupo12.practico.services.HealthUser;

import jakarta.ejb.Local;

import java.util.List;

import grupo12.practico.models.HealthUser;

@Local
public interface HealthUserServiceLocal {
    List<HealthUser> findAll();

    HealthUser findById(String id);

    List<HealthUser> findByName(String name);

    HealthUser add(HealthUser healthUser);

    // Alias methods for backwards compatibility
    default List<HealthUser> getAllUsers() {
        return findAll();
    }

    default List<HealthUser> findUsersByName(String name) {
        return findByName(name);
    }

    default HealthUser addUser(HealthUser healthUser) {
        return add(healthUser);
    }
}
