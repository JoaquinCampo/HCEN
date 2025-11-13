package grupo12.practico.rest;

import grupo12.practico.dtos.ClinicalDocument.ChatRequestDTO;
import grupo12.practico.dtos.ClinicalDocument.ChatResponseDTO;
import grupo12.practico.dtos.ClinicalDocument.ClinicalHistoryAccessLogResponseDTO;
import grupo12.practico.dtos.ClinicalDocument.CreateClinicalDocumentDTO;
import grupo12.practico.dtos.ClinicalDocument.DocumentResponseDTO;
import grupo12.practico.dtos.ClinicalDocument.PresignedUrlRequestDTO;
import grupo12.practico.dtos.ClinicalDocument.PresignedUrlResponseDTO;
import grupo12.practico.services.ClinicalDocument.ClinicalDocumentServiceLocal;
import jakarta.ejb.EJB;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

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

    /**
     * Fetch clinical history for a patient
     * GET /api/clinical-documents/clinical-history/{health_user_ci}
     * 
     * Query parameters:
     * - health_worker_ci: CI of the worker requesting the history
     * - clinic_name: Clinic requesting the history
     * 
     * Response: List of DocumentResponseDTO
     */
    @GET
    @Path("/clinical-history/{health_user_ci}")
    public Response fetchClinicalHistory(
            @PathParam("health_user_ci") String healthUserCi,
            @QueryParam("health_worker_ci") String healthWorkerCi,
            @QueryParam("clinic_name") String clinicName) {
        try {
            List<DocumentResponseDTO> response = clinicalDocumentService.fetchClinicalHistory(
                    healthUserCi, healthWorkerCi, clinicName);
            return Response.ok(response).build();
        } catch (Exception ex) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + ex.getMessage() + "\"}")
                    .build();
        }
    }

    /**
     * Fetch access history for a health worker
     * GET
     * /api/clinical-documents/clinical-history/health-workers/{health_worker_ci}/access-history
     * 
     * Query parameters:
     * - health_user_ci (optional): Filter by patient CI
     * 
     * Response: List of ClinicalHistoryAccessLogResponseDTO
     */
    @GET
    @Path("/clinical-history/health-workers/{health_worker_ci}/access-history")
    public Response fetchHealthWorkerAccessHistory(
            @PathParam("health_worker_ci") String healthWorkerCi,
            @QueryParam("health_user_ci") String healthUserCi) {
        try {
            List<ClinicalHistoryAccessLogResponseDTO> response = clinicalDocumentService
                    .fetchHealthWorkerAccessHistory(healthWorkerCi, healthUserCi);
            return Response.ok(response).build();
        } catch (Exception ex) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + ex.getMessage() + "\"}")
                    .build();
        }
    }

    /**
     * Process a chat query against patient documents using RAG
     * POST /api/clinical-documents/chat
     * 
     * Request body:
     * {
     * "query": "What medications is the patient taking?",
     * "health_user_ci": "12345678",
     * "conversation_history": [{"role": "user", "content": "..."}],
     * "document_id": "uuid" (optional)
     * }
     * 
     * Response: ChatResponseDTO with answer and sources
     */
    @POST
    @Path("/chat")
    public Response chat(ChatRequestDTO request) {
        try {
            ChatResponseDTO response = clinicalDocumentService.chat(request);
            return Response.ok(response).build();
        } catch (Exception ex) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + ex.getMessage() + "\"}")
                    .build();
        }
    }
}
