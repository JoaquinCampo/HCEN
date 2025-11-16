package grupo12.practico.repositories.ClinicalDocument;

import jakarta.ejb.Local;

import grupo12.practico.dtos.ClinicalDocument.AddClinicalDocumentDTO;
import grupo12.practico.dtos.ClinicalDocument.PresignedUrlRequestDTO;
import grupo12.practico.dtos.ClinicalDocument.PresignedUrlResponseDTO;
import grupo12.practico.dtos.ClinicalHistory.ChatRequestDTO;
import grupo12.practico.dtos.ClinicalHistory.ChatResponseDTO;
import grupo12.practico.dtos.ClinicalHistory.ClinicalHistoryAccessLogResponseDTO;

import java.util.List;

@Local
public interface ClinicalDocumentRepositoryLocal {
    PresignedUrlResponseDTO getPresignedUploadUrl(PresignedUrlRequestDTO request);

    String createClinicalDocument(AddClinicalDocumentDTO dto);

    List<ClinicalHistoryAccessLogResponseDTO> fetchHealthWorkerAccessHistory(String healthWorkerCi, String healthUserCi);

    ChatResponseDTO chat(ChatRequestDTO request);
}