package grupo12.practico.repositories.Clinic;

import jakarta.ejb.Local;
import jakarta.ejb.Remote;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import grupo12.practico.models.Clinic;

@Singleton
@Startup
@Local(ClinicRepositoryLocal.class)
@Remote(ClinicRepositoryRemote.class)
public class ClinicRepositoryBean implements ClinicRepositoryRemote {

    private final Map<String, Clinic> idToClinic = new HashMap<>();

    @Override
    public Clinic add(Clinic clinic) {
        if (clinic == null || clinic.getId() == null)
            return clinic;
        idToClinic.put(clinic.getId(), clinic);
        return clinic;
    }

    @Override
    public List<Clinic> findAll() {
        return new ArrayList<>(idToClinic.values());
    }

    @Override
    public Clinic findById(String id) {
        if (id == null || id.trim().isEmpty()) {
            return null;
        }
        return idToClinic.get(id);
    }

    @Override
    public List<Clinic> findByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return findAll();
        }
        String normalized = name.trim().toLowerCase(Locale.ROOT);
        return idToClinic.values().stream()
                .filter(clinic -> (clinic.getName() != null && clinic.getName().toLowerCase(Locale.ROOT).contains(normalized)))
                .collect(Collectors.toList());
    }

}
