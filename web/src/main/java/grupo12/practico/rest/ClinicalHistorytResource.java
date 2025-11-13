package grupo12.practico.rest;

import grupo12.practico.dtos.ClinicalDocument.ChatRequestDTO;
import grupo12.practico.dtos.ClinicalDocument.ChatResponseDTO;
import grupo12.practico.dtos.ClinicalDocument.ClinicalHistoryAccessLogResponseDTO;
import grupo12.practico.dtos.ClinicalDocument.DocumentResponseDTO;
import grupo12.practico.services.ClinicalDocument.ClinicalDocumentServiceLocal;
import jakarta.ejb.EJB;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/clinical-history")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ClinicalHistorytResource {

    @EJB
    private ClinicalDocumentServiceLocal clinicalDocumentService;

    @GET
    @Path("/{healthUserCi}")
    public Response fetchClinicalHistory(
            @PathParam("healthUserCi") String healthUserCi,
            @QueryParam("healthWorkerCi") String healthWorkerCi,
            @QueryParam("clinicName") String clinicName) {
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

    @GET
    @Path("/health-workers/{healthWorkerCi}/access-history")
    public Response fetchHealthWorkerAccessHistory(
            @PathParam("healthWorkerCi") String healthWorkerCi,
            @QueryParam("healthUserCi") String healthUserCi) {
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
