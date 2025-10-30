package grupo12.practico.repositories.HealthUser;

import jakarta.ejb.Local;

import java.util.List;

import grupo12.practico.models.HealthUser;

@Local
public interface HealthUserRepositoryLocal {
    List<HealthUser> findAll(String clinicName, String name, String ci, Integer pageIndex, Integer pageSize);

    HealthUser add(HealthUser healthUser);

    HealthUser addClinicToHealthUser(String healthUserId, String clinicName);

    HealthUser findHealthUserByCi(String healthUserCi);
}
