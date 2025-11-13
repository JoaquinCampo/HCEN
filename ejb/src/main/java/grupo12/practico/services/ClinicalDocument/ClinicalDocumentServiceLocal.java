package grupo12.practico.services.ClinicalDocument;

import grupo12.practico.dtos.ClinicalDocument.CreateClinicalDocumentDTO;
import grupo12.practico.dtos.ClinicalDocument.DocumentResponseDTO;
import grupo12.practico.dtos.ClinicalDocument.PresignedUrlRequestDTO;
import grupo12.practico.dtos.ClinicalDocument.PresignedUrlResponseDTO;
import grupo12.practico.dtos.ClinicalHistory.ChatRequestDTO;
import grupo12.practico.dtos.ClinicalHistory.ChatResponseDTO;
import grupo12.practico.dtos.ClinicalHistory.ClinicalHistoryAccessLogResponseDTO;
import jakarta.ejb.Local;
import java.util.List;

@Local
public interface ClinicalDocumentServiceLocal {

    /**
     * Request a presigned URL for uploading a clinical document
     * 
     * @param request containing fileName, contentType, and clinicName
     * @return PresignedUrlResponseDTO with uploadUrl, s3Url, objectKey, and
     *         expiresInSeconds
     */
    PresignedUrlResponseDTO getPresignedUploadUrl(PresignedUrlRequestDTO request);

    /**
     * Create a clinical document record after the file has been uploaded
     * 
     * @param dto containing createdBy, healthUserCi, clinicName, and s3Url
     * @return DocumentResponseDTO with the created document details
     */
    DocumentResponseDTO createClinicalDocument(CreateClinicalDocumentDTO dto);

    /**
     * Fetch access history for a health worker
     * 
     * @param healthWorkerCi CI of the health worker
     * @param healthUserCi   Optional CI of the patient to filter by
     * @return List of ClinicalHistoryAccessLogResponseDTO
     */
    List<ClinicalHistoryAccessLogResponseDTO> fetchHealthWorkerAccessHistory(String healthWorkerCi,
            String healthUserCi);

    /**
     * Process a chat query against patient documents using RAG
     * 
     * @param request ChatRequestDTO containing query and patient CI
     * @return ChatResponseDTO with answer and sources
     */
    ChatResponseDTO chat(ChatRequestDTO request);
}
