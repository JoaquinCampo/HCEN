package grupo12.practico.services.ClinicalDocument;

import grupo12.practico.dtos.ClinicalDocument.AddClinicalDocumentDTO;
import grupo12.practico.dtos.ClinicalDocument.PresignedUrlRequestDTO;
import grupo12.practico.dtos.ClinicalDocument.PresignedUrlResponseDTO;
import grupo12.practico.dtos.ClinicalHistory.ChatRequestDTO;
import grupo12.practico.dtos.ClinicalHistory.ChatResponseDTO;
import jakarta.ejb.Local;

@Local
public interface ClinicalDocumentServiceLocal {

    PresignedUrlResponseDTO getPresignedUploadUrl(PresignedUrlRequestDTO request);

    String createClinicalDocument(AddClinicalDocumentDTO dto);

    ChatResponseDTO chat(ChatRequestDTO request);
}
