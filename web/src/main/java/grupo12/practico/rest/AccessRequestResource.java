package grupo12.practico.rest;

import grupo12.practico.dtos.AccessRequest.AccessRequestDTO;
import grupo12.practico.dtos.AccessRequest.AddAccessRequestDTO;
import grupo12.practico.messaging.AccessRequest.AccessRequestProducerLocal;
import grupo12.practico.services.AccessRequest.AccessRequestServiceLocal;
import jakarta.ejb.EJB;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/access-requests")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AccessRequestResource {

    @EJB
    private AccessRequestServiceLocal accessRequestService;

    @EJB
    private AccessRequestProducerLocal accessRequestProducer;

    @POST
    public Response createAccessRequest(AddAccessRequestDTO dto) {
        accessRequestProducer.enqueue(dto);
        return Response.accepted()
                .entity("{\"message\":\"Access request creation queued successfully\"}")
                .build();
    }

    @GET
    public Response findAllAccessRequests(
            @QueryParam("healthUserCi") String healthUserCi,
            @QueryParam("healthWorkerCi") String healthWorkerCi,
            @QueryParam("clinicName") String clinicName) {
        List<AccessRequestDTO> requests = accessRequestService.findAllAccessRequests(healthUserCi, healthWorkerCi, clinicName);
        return Response.ok(requests).build();
    }

    @DELETE
    @Path("/{accessRequestId}")
    public Response deleteAccessRequest(@PathParam("accessRequestId") String accessRequestId) {
        accessRequestService.deleteAccessRequest(accessRequestId);
        return Response.ok()
                .entity("{\"message\":\"Access request deleted successfully\"}")
                .build();
    }
}
