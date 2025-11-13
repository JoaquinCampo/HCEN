package grupo12.practico.repositories.HealthUser;

import jakarta.ejb.Local;

import java.util.List;

import grupo12.practico.dtos.ClinicalDocument.DocumentResponseDTO;
import grupo12.practico.dtos.ClinicalHistory.ClinicalHistoryAccessLogResponseDTO;
import grupo12.practico.models.HealthUser;

@Local
public interface HealthUserRepositoryLocal {
    List<HealthUser> findAll(String clinicName, String name, String ci, Integer pageIndex, Integer pageSize);

    long count(String clinicName, String name, String ci);

    HealthUser create(HealthUser healthUser);

    HealthUser linkClinicToHealthUser(String healthUserId, String clinicName);

    HealthUser findByCi(String healthUserCi);

    HealthUser findById(String healthUserId);

    List<DocumentResponseDTO> fetchClinicalHistory(String healthUserCi, String healthWorkerCi, String clinicName, String providerName);

    List<ClinicalHistoryAccessLogResponseDTO> fetchHealthUserAccessHistory(String healthUserCi);
}
