package grupo12.practico.repositories.HealthUser;

import jakarta.ejb.Local;

import java.util.List;

import grupo12.practico.models.HealthUser;

@Local
public interface HealthUserRepositoryLocal {
    List<HealthUser> findAll(String clinicName, String name, String ci, Integer pageIndex, Integer pageSize);

    HealthUser create(HealthUser healthUser);

    HealthUser linkClinicToHealthUser(String healthUserId, String clinicName);

    HealthUser findByCi(String healthUserCi);

    HealthUser findById(String healthUserId);
}
