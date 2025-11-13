package grupo12.practico.rest;

import grupo12.practico.dtos.ClinicalDocument.CreateClinicalDocumentDTO;
import grupo12.practico.dtos.ClinicalDocument.CreateClinicalDocumentResponseDTO;
import grupo12.practico.dtos.ClinicalDocument.PresignedUrlRequestDTO;
import grupo12.practico.dtos.ClinicalDocument.PresignedUrlResponseDTO;
import grupo12.practico.dtos.ClinicalHistory.ChatRequestDTO;
import grupo12.practico.dtos.ClinicalHistory.ChatResponseDTO;
import grupo12.practico.dtos.ClinicalHistory.ClinicalHistoryAccessLogResponseDTO;
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

    @POST
    public Response createClinicalDocument(CreateClinicalDocumentDTO dto) {
        try {
            String docId = clinicalDocumentService.createClinicalDocument(dto);
            CreateClinicalDocumentResponseDTO response = new CreateClinicalDocumentResponseDTO(docId);
            return Response.status(Response.Status.CREATED)
                    .entity(response)
                    .build();
        } catch (Exception ex) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + ex.getMessage() + "\"}")
                    .build();
        }
    }

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
