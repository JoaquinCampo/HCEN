package grupo12.practico.services.ClinicalDocument;

import grupo12.practico.dtos.ClinicalDocument.AddClinicalDocumentDTO;
import grupo12.practico.dtos.ClinicalDocument.PresignedUrlRequestDTO;
import grupo12.practico.dtos.ClinicalDocument.PresignedUrlResponseDTO;
import grupo12.practico.dtos.ClinicalHistory.ChatRequestDTO;
import grupo12.practico.dtos.ClinicalHistory.ChatResponseDTO;
import grupo12.practico.dtos.ClinicalHistory.ClinicalHistoryAccessLogResponseDTO;
import jakarta.ejb.Local;
import java.util.List;

@Local
public interface ClinicalDocumentServiceLocal {

    PresignedUrlResponseDTO getPresignedUploadUrl(PresignedUrlRequestDTO request);

    String createClinicalDocument(AddClinicalDocumentDTO dto);

    List<ClinicalHistoryAccessLogResponseDTO> fetchHealthWorkerAccessHistory(String healthWorkerCi,
            String healthUserCi);

    ChatResponseDTO chat(ChatRequestDTO request);
}
