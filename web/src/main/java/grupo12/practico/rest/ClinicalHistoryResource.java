package grupo12.practico.rest;

import grupo12.practico.dtos.ClinicalHistory.ChatRequestDTO;
import grupo12.practico.dtos.ClinicalHistory.ChatResponseDTO;
import grupo12.practico.dtos.ClinicalHistory.ClinicalHistoryAccessLogResponseDTO;
import grupo12.practico.dtos.ClinicalHistory.ClinicalHistoryResponseDTO;
import grupo12.practico.dtos.ClinicalHistory.HealthUserAccessHistoryResponseDTO;
import grupo12.practico.services.ClinicalDocument.ClinicalDocumentServiceLocal;
import grupo12.practico.services.HealthUser.HealthUserServiceLocal;
import jakarta.ejb.EJB;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/clinical-history")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ClinicalHistoryResource {

    @EJB
    private ClinicalDocumentServiceLocal clinicalDocumentService;

    @EJB
    private HealthUserServiceLocal healthUserService;

    @GET
    @Path("/{healthUserCi}")
    public Response fetchClinicalHistory(
            @PathParam("healthUserCi") String healthUserCi,
            @QueryParam("healthWorkerCi") String healthWorkerCi,
            @QueryParam("clinicName") String clinicName,
            @QueryParam("providerName") String providerName) {
        try {
            ClinicalHistoryResponseDTO response = healthUserService.fetchClinicalHistory(
                    healthUserCi, healthWorkerCi, clinicName, providerName);
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

    @GET
    @Path("/health-users/{healthUserCi}/access-history")
    public Response fetchHealthUserAccessHistory(@PathParam("healthUserCi") String healthUserCi) {
        try {
            HealthUserAccessHistoryResponseDTO response = healthUserService
                    .fetchHealthUserAccessHistory(healthUserCi);
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
