package grupo12.practico.rest;

import grupo12.practico.dtos.ClinicalHistory.ChatRequestDTO;
import grupo12.practico.dtos.ClinicalHistory.ClinicalHistoryRequestDTO;
import grupo12.practico.dtos.ClinicalHistory.ClinicalHistoryResponseDTO;
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
    @Path("/health-users/{healthUserCi}/access-history")
    public Response findClinicalHistoryAccessHistory(
            @PathParam("healthUserCi") String healthUserCi,
            @QueryParam("pageIndex") Integer pageIndex,
            @QueryParam("pageSize") Integer pageSize) {
        return Response.ok(healthUserService.findHealthUserAccessHistory(healthUserCi, pageIndex, pageSize)).build();
    }

    @GET
    @Path("/{healthUserCi}")
    public Response findClinicalHistory(
            @PathParam("healthUserCi") String healthUserCi,
            @QueryParam("healthWorkerCi") String healthWorkerCi,
            @QueryParam("clinicName") String clinicName,
            @QueryParam("specialtyNames") List<String> specialtyNames) {
        ClinicalHistoryRequestDTO request = new ClinicalHistoryRequestDTO();
        request.setHealthUserCi(healthUserCi);
        request.setHealthWorkerCi(healthWorkerCi);
        request.setClinicName(clinicName);
        request.setSpecialtyNames(specialtyNames);

        ClinicalHistoryResponseDTO response = healthUserService.findHealthUserClinicalHistory(request);
        return Response.ok(response).build();
    }

    @POST
    @Path("/chat")
    public Response chat(ChatRequestDTO request) {
        return Response.ok(clinicalDocumentService.chat(request)).build();
    }

    @GET
    @Path("/health-users/{healthUserCi}/access-history")
    public Response getClinicalHistoryAccessHistory(
            @PathParam("healthUserCi") String healthUserCi,
            @QueryParam("pageIndex") @DefaultValue("0") int pageIndex,
            @QueryParam("pageSize") @DefaultValue("50") int pageSize) {
        return Response.ok(
                healthUserService.findHealthUserAccessHistory(healthUserCi, pageIndex, pageSize))
                .build();
    }
}
