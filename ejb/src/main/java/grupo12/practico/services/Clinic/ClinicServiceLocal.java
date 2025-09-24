package grupo12.practico.services.Clinic;

import jakarta.ejb.Local;

import java.util.List;

import grupo12.practico.models.Clinic;

@Local
public interface ClinicServiceLocal {
    Clinic addClinic(Clinic clinic);

    List<Clinic> findAll();

    Clinic findById(String id);

    List<Clinic> findByName(String name);
}
