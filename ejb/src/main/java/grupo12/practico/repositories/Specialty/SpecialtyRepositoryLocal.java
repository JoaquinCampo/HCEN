package grupo12.practico.repositories.Specialty;

import java.util.List;

import grupo12.practico.models.Specialty;
import jakarta.ejb.Local;

@Local
public interface SpecialtyRepositoryLocal {
    Specialty add(Specialty specialty);

    List<Specialty> findAll();

    Specialty findById(String id);

    List<Specialty> findByName(String name);
}
