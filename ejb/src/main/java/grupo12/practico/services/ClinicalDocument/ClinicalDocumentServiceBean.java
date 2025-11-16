package grupo12.practico.services.ClinicalDocument;

import grupo12.practico.dtos.ClinicalDocument.AddClinicalDocumentDTO;
import grupo12.practico.dtos.ClinicalDocument.PresignedUrlRequestDTO;
import grupo12.practico.dtos.ClinicalDocument.PresignedUrlResponseDTO;
import grupo12.practico.dtos.ClinicalHistory.ChatRequestDTO;
import grupo12.practico.dtos.ClinicalHistory.ChatResponseDTO;
import grupo12.practico.repositories.ClinicalDocument.ClinicalDocumentRepositoryLocal;
import grupo12.practico.services.AccessPolicy.AccessPolicyServiceLocal;
import grupo12.practico.services.Logger.LoggerServiceLocal;
import jakarta.ejb.Stateless;
import jakarta.validation.ValidationException;
import jakarta.ejb.EJB;
import jakarta.ejb.Local;
import jakarta.ejb.Remote;
import java.util.logging.Logger;

@Stateless
@Local(ClinicalDocumentServiceLocal.class)
@Remote(ClinicalDocumentServiceRemote.class)
public class ClinicalDocumentServiceBean implements ClinicalDocumentServiceLocal {

    private static final Logger LOGGER = Logger.getLogger(ClinicalDocumentServiceBean.class.getName());

    @EJB
    private AccessPolicyServiceLocal accessPolicyService;

    @EJB
    private ClinicalDocumentRepositoryLocal clinicalDocumentRepository;

    @EJB
    private LoggerServiceLocal loggerService;

    @Override
    public PresignedUrlResponseDTO getPresignedUploadUrl(PresignedUrlRequestDTO request) {
        validatePresignedUrlRequest(request);

        boolean hasClinicAccess = accessPolicyService.hasClinicAccess(
                request.getHealthUserCi(),
                request.getClinicName());

        boolean hasHealthWorkerAccess = accessPolicyService.hasHealthWorkerAccess(
                request.getHealthUserCi(),
                request.getHealthWorkerCi());

        boolean hasSpecialtyAccess = accessPolicyService.hasSpecialtyAccess(
                request.getHealthUserCi(),
                request.getSpecialtyNames());

        if (!hasClinicAccess && !hasHealthWorkerAccess && !hasSpecialtyAccess) {
            throw new ValidationException(
                    "Health worker does not have access to upload documents for the specified health user.");
        }

        return clinicalDocumentRepository.getPresignedUploadUrl(request);
    }

    @Override
    public String createClinicalDocument(AddClinicalDocumentDTO dto) {
        validateCreateClinicalDocumentRequest(dto);

        String documentId = clinicalDocumentRepository.createClinicalDocument(dto);

        // Log document creation
        try {
            loggerService.logDocumentCreated(
                documentId,
                dto.getHealthUserCi(),
                dto.getHealthWorkerCi(),
                dto.getClinicName()
            );
        } catch (Exception e) {
            LOGGER.warning("Failed to log document creation: " + e.getMessage());
        }

        return documentId;
    }

    private void validatePresignedUrlRequest(PresignedUrlRequestDTO request) {
        if (request == null) {
            throw new ValidationException("Presigned URL request must not be null");
        }
        if (request.getFileName() == null || request.getFileName().trim().isEmpty()) {
            throw new ValidationException("File name is required");
        }
        if (request.getContentType() == null || request.getContentType().trim().isEmpty()) {
            throw new ValidationException("Content type is required");
        }
        if (request.getClinicName() == null || request.getClinicName().trim().isEmpty()) {
            throw new ValidationException("Clinic name is required");
        }
    }

    private void validateCreateClinicalDocumentRequest(AddClinicalDocumentDTO dto) {
        if (dto == null) {
            throw new ValidationException("Clinical document creation request must not be null");
        }
        if (dto.getTitle() == null || dto.getTitle().isBlank()) {
            throw new ValidationException("Title is required");
        }
        if (dto.getHealthWorkerCi() == null || dto.getHealthWorkerCi().isBlank()) {
            throw new ValidationException("Created by (health worker CI) is required");
        }
        if (dto.getHealthUserCi() == null || dto.getHealthUserCi().isBlank()) {
            throw new ValidationException("Health user CI is required");
        }
        if (dto.getClinicName() == null || dto.getClinicName().isBlank()) {
            throw new ValidationException("Clinic name is required");
        }
        if (dto.getProviderName() == null || dto.getProviderName().isBlank()) {
            throw new ValidationException("Provider name is required");
        }
    }

    @Override
    public ChatResponseDTO chat(ChatRequestDTO request) {
        if (request == null) {
            throw new ValidationException("Chat request must not be null");
        }
        if (request.getQuery() == null || request.getQuery().trim().isEmpty()) {
            throw new ValidationException("Query is required");
        }
        if (request.getHealthUserCi() == null || request.getHealthUserCi().trim().isEmpty()) {
            throw new ValidationException("Health user CI is required");
        }

        return clinicalDocumentRepository.chat(request);
    }

}
