package grupo12.practico.services.HealthUser;

import jakarta.ejb.Local;

import grupo12.practico.dtos.PaginationDTO;
import grupo12.practico.dtos.ClinicalHistory.ClinicalHistoryRequestDTO;
import grupo12.practico.dtos.ClinicalHistory.ClinicalHistoryResponseDTO;
import grupo12.practico.dtos.ClinicalHistory.HealthUserAccessHistoryResponseDTO;
import grupo12.practico.dtos.HealthUser.AddHealthUserDTO;
import grupo12.practico.dtos.HealthUser.HealthUserDTO;

@Local
public interface HealthUserServiceLocal {
    PaginationDTO<HealthUserDTO> findAllHealthUsers(String clinicName, String name, String ci, Integer pageIndex,
            Integer pageSize);

    HealthUserDTO createHealthUser(AddHealthUserDTO addHealthUserDTO);

    HealthUserDTO findHealthUserById(String healthUserId);

    HealthUserDTO findHealthUserByCi(String healthUserCi);

    HealthUserDTO linkClinicToHealthUser(String healthUserId, String clinicName);

    ClinicalHistoryResponseDTO findHealthUserClinicalHistory(ClinicalHistoryRequestDTO request);

    HealthUserAccessHistoryResponseDTO findHealthUserAccessHistory(String healthUserCi, Integer pageIndex,
            Integer pageSize);
}
