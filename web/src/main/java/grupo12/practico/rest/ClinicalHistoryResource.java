package grupo12.practico.rest;

import grupo12.practico.dtos.ClinicalHistory.ChatRequestDTO;
import grupo12.practico.dtos.ClinicalHistory.ClinicalHistoryRequestDTO;
import grupo12.practico.messaging.ClinicalDocument.Chat.ChatProducerLocal;
import grupo12.practico.messaging.ClinicalHistory.ClinicalHistoryProducerLocal;
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

    @EJB
    private ClinicalHistoryProducerLocal clinicalHistoryProducer;

    @EJB
    private ChatProducerLocal chatProducer;

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

        clinicalHistoryProducer.enqueue(request);
        return Response.accepted()
                .entity("{\"message\":\"Clinical history request queued successfully\"}")
                .build();
    }

    @POST
    @Path("/chat")
    public Response chat(ChatRequestDTO request) {
        chatProducer.enqueue(request);
        return Response.accepted()
                .entity("{\"message\":\"Chat request queued successfully\"}")
                .build();
    }
}
