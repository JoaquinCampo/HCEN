package grupo12.practico.repositories.HealthUser;

import jakarta.ejb.Local;

import java.util.List;

import grupo12.practico.models.HealthUser;

@Local
public interface HealthUserRepositoryLocal {
    List<HealthUser> findAll();

    HealthUser findById(String id);

    List<HealthUser> findByName(String name);

    HealthUser add(HealthUser healthUser);

    HealthUser findByDocument(String document);

    List<HealthUser> findPage(int offset, int limit);

    long count();
}
