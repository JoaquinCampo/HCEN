package grupo12.practico.rest;

import grupo12.practico.dtos.AccessRequest.AccessRequestDTO;
import grupo12.practico.dtos.AccessRequest.AddAccessRequestDTO;
import grupo12.practico.dtos.AccessRequest.GrantAccessDTO;
import grupo12.practico.services.AccessRequest.AccessRequestServiceLocal;
import grupo12.practico.services.HealthUser.HealthUserServiceLocal;
import grupo12.practico.dtos.HealthUser.HealthUserDTO;
import jakarta.ejb.EJB;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
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
    private HealthUserServiceLocal healthUserService;

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

    @GET
    @Path("/health-user/{healthUserId}")
    public List<AccessRequestDTO> findAllByHealthUserId(@PathParam("healthUserId") String healthUserId) {
        return accessRequestService.findAllByHealthUserId(healthUserId);
    }

    @GET
    @Path("/health-user/name/{name}")
    public List<AccessRequestDTO> findAllByHealthUserName(@PathParam("name") String name) {
        List<HealthUserDTO> healthUsers = healthUserService.findByName(name);
        String healthUserId = healthUsers.get(0).getId();
        return accessRequestService.findAllByHealthUserId(healthUserId);
    }

    @POST
    @Path("/grant-by-health-worker")
    public Response grantByHealthWorker(GrantAccessDTO dto) {
        GrantAccessResultDTO result = accessRequestService.grantAccessByHealthWorker(dto.getAccessRequestId(), dto);
        Response.Status status = result.isAccepted() ? Response.Status.CREATED : Response.Status.OK;
        return Response.status(status).entity(result).build();
    }

    @POST
    @Path("/grant-by-clinic")
    public Response grantByClinic(GrantAccessDTO dto) {
        GrantAccessResultDTO result = accessRequestService.grantAccessByClinic(dto.getAccessRequestId(), dto);
        Response.Status status = result.isAccepted() ? Response.Status.CREATED : Response.Status.OK;
        return Response.status(status).entity(result).build();
    }

    @POST
    @Path("/grant-by-specialty")
    public Response grantBySpecialty(GrantAccessDTO dto) {
        GrantAccessResultDTO result = accessRequestService.grantAccessBySpecialty(dto.getAccessRequestId(), dto);
        Response.Status status = result.isAccepted() ? Response.Status.CREATED : Response.Status.OK;
        return Response.status(status).entity(result).build();
    }

    @POST
    @Path("/deny")
    public Response deny(GrantAccessDTO dto) {
        GrantAccessResultDTO result = accessRequestService.denyAccess(dto.getAccessRequestId(), dto);
        Response.Status status = result.isAccepted() ? Response.Status.CREATED : Response.Status.OK;
        return Response.status(status).entity(result).build();
    }
}
