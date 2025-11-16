package grupo12.practico.repositories.ClinicalDocument;

import jakarta.ejb.Local;

import grupo12.practico.dtos.ClinicalDocument.AddClinicalDocumentDTO;
import grupo12.practico.dtos.ClinicalDocument.PresignedUrlRequestDTO;
import grupo12.practico.dtos.ClinicalDocument.PresignedUrlResponseDTO;
import grupo12.practico.dtos.ClinicalHistory.ChatRequestDTO;
import grupo12.practico.dtos.ClinicalHistory.ChatResponseDTO;

@Local
public interface ClinicalDocumentRepositoryLocal {
    PresignedUrlResponseDTO getPresignedUploadUrl(PresignedUrlRequestDTO request);

    String createClinicalDocument(AddClinicalDocumentDTO dto);

    ChatResponseDTO chat(ChatRequestDTO request);
}