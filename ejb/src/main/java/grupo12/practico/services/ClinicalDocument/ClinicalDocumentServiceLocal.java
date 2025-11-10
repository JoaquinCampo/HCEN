package grupo12.practico.services.ClinicalDocument;

import grupo12.practico.dtos.ClinicalDocument.CreateClinicalDocumentDTO;
import grupo12.practico.dtos.ClinicalDocument.DocumentResponseDTO;
import grupo12.practico.dtos.ClinicalDocument.PresignedUrlRequestDTO;
import grupo12.practico.dtos.ClinicalDocument.PresignedUrlResponseDTO;
import jakarta.ejb.Local;

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
}
