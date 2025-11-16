package grupo12.practico.repositories.HealthUser;

import jakarta.ejb.Local;

import java.util.List;

import grupo12.practico.dtos.ClinicalDocument.ClinicalDocumentDTO;
import grupo12.practico.dtos.ClinicalHistory.ClinicalHistoryAccessLogResponseDTO;
import grupo12.practico.models.HealthUser;

@Local
public interface HealthUserRepositoryLocal {
    List<HealthUser> findAllHealthUsers(String clinicName, String name, String ci, Integer pageIndex, Integer pageSize);

    long countHealthUsers(String clinicName, String name, String ci);

    HealthUser createHealthUser(HealthUser healthUser);

    HealthUser linkClinicToHealthUser(String healthUserId, String clinicName);

    HealthUser findHealthUserByCi(String healthUserCi);

    HealthUser findHealthUserById(String healthUserId);

    List<ClinicalDocumentDTO> findHealthUserClinicalHistory(String healthUserCi);

    List<ClinicalHistoryAccessLogResponseDTO> findHealthUserAccessHistory(String healthUserCi);
}
