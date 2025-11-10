package grupo12.practico.rest;

import grupo12.practico.dtos.ClinicalDocument.CreateClinicalDocumentDTO;
import grupo12.practico.dtos.ClinicalDocument.DocumentResponseDTO;
import grupo12.practico.dtos.ClinicalDocument.PresignedUrlRequestDTO;
import grupo12.practico.dtos.ClinicalDocument.PresignedUrlResponseDTO;
import grupo12.practico.services.ClinicalDocument.ClinicalDocumentServiceLocal;
import jakarta.ejb.EJB;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/clinical-documents")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ClinicalDocumentResource {

    @EJB
    private ClinicalDocumentServiceLocal clinicalDocumentService;

    /**
     * Request a presigned URL for uploading a clinical document file
     * POST /api/clinical-documents/upload-url
     * 
     * Request body:
     * {
     * "fileName": "document.pdf",
     * "contentType": "application/pdf",
     * "clinicName": "Clinic Name"
     * }
     * 
     * Response:
     * {
     * "uploadUrl": "https://...",
     * "key": "file-key"
     * }
     */
    @POST
    @Path("/upload-url")
    public Response getPresignedUploadUrl(PresignedUrlRequestDTO request) {
        try {
            PresignedUrlResponseDTO response = clinicalDocumentService.getPresignedUploadUrl(request);
            return Response.ok(response).build();
        } catch (Exception ex) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + ex.getMessage() + "\"}")
                    .build();
        }
    }

    /**
     * Create a clinical document record after file upload
     * POST /api/clinical-documents
     * 
     * Request body:
     * {
     * "createdBy": "admin-user",
     * "healthUserCi": "12345678",
     * "clinicName": "Clinic Name",
     * "s3Url": "https://..."
     * }
     * 
     * Response: DocumentResponseDTO
     */
    @POST
    public Response createClinicalDocument(CreateClinicalDocumentDTO dto) {
        try {
            DocumentResponseDTO response = clinicalDocumentService.createClinicalDocument(dto);
            return Response.status(Response.Status.CREATED)
                    .entity(response)
                    .build();
        } catch (Exception ex) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + ex.getMessage() + "\"}")
                    .build();
        }
    }
}
