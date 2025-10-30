package grupo12.practico.services.HealthUser;

import jakarta.ejb.Local;

import java.util.List;

import grupo12.practico.dtos.HealthUser.AddHealthUserDTO;
import grupo12.practico.dtos.HealthUser.ClinicalHistoryDTO;
import grupo12.practico.dtos.HealthUser.HealthUserDTO;

@Local
public interface HealthUserServiceLocal {
    List<HealthUserDTO> findAll(String clinicName, String name, String ci, Integer pageIndex, Integer pageSize);

    HealthUserDTO create(AddHealthUserDTO addHealthUserDTO);

    HealthUserDTO findById(String healthUserId);

    HealthUserDTO findByCi(String healthUserCi);

    HealthUserDTO linkClinicToHealthUser(String healthUserId, String clinicName);

    ClinicalHistoryDTO findClinicalHistory(String healthUserId, String clinicName, String healthWorkerCi);
}
