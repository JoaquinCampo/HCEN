package grupo12.practico.repositories.Specialty;

import java.util.List;

import grupo12.practico.models.Specialty;
import jakarta.ejb.Local;

@Local
public interface SpecialtyRepositoryLocal {
    Specialty add(Specialty specialty);

    Specialty findById(String id);

    List<Specialty> findAll();

    List<Specialty> findByName(String name);
}
