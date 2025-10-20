package grupo12.practico.rest;

import grupo12.practico.dtos.AccessRequest.AccessRequestDTO;
import grupo12.practico.dtos.AccessRequest.AddAccessRequestDTO;
import grupo12.practico.dtos.AccessRequest.GrantAccessDecisionDTO;
import grupo12.practico.dtos.AccessRequest.GrantAccessResultDTO;
import grupo12.practico.services.AccessRequest.AccessRequestServiceLocal;
import jakarta.ejb.EJB;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/access-requests")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AccessRequestResource {

    @EJB
    private AccessRequestServiceLocal accessRequestService;

    @POST
    public Response create(AddAccessRequestDTO dto) {
        AccessRequestDTO created = accessRequestService.create(dto);
        return Response.status(Response.Status.CREATED).entity(created).build();
    }

    @GET
    @Path("/{id}")
    public Response findById(@PathParam("id") String id) {
        AccessRequestDTO accessRequest = accessRequestService.findById(id);
        if (accessRequest == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\":\"Access request not found with id: " + id + "\"}")
                    .build();
        }
        return Response.ok(accessRequest).build();
    }

    @POST
    @Path("/{id}/grant-by-health-worker")
    public Response grantByHealthWorker(@PathParam("id") String accessRequestId, GrantAccessDecisionDTO dto) {
        GrantAccessResultDTO result = accessRequestService.grantAccessByHealthWorker(accessRequestId, dto);
        Response.Status status = result.isAccepted() ? Response.Status.CREATED : Response.Status.OK;
        return Response.status(status).entity(result).build();
    }

    @POST
    @Path("/{id}/grant-by-clinic")
    public Response grantByClinic(@PathParam("id") String accessRequestId, GrantAccessDecisionDTO dto) {
        GrantAccessResultDTO result = accessRequestService.grantAccessByClinic(accessRequestId, dto);
        Response.Status status = result.isAccepted() ? Response.Status.CREATED : Response.Status.OK;
        return Response.status(status).entity(result).build();
    }

    @POST
    @Path("/{id}/grant-by-specialty")
    public Response grantBySpecialty(@PathParam("id") String accessRequestId, GrantAccessDecisionDTO dto) {
        GrantAccessResultDTO result = accessRequestService.grantAccessBySpecialty(accessRequestId, dto);
        Response.Status status = result.isAccepted() ? Response.Status.CREATED : Response.Status.OK;
        return Response.status(status).entity(result).build();
    }
}
