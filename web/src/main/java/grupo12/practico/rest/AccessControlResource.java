package grupo12.practico.rest;

import grupo12.practico.dtos.AccessControl.AccessCheckRequestDTO;
import grupo12.practico.dtos.AccessControl.AccessDecisionDTO;
import grupo12.practico.services.AccessControl.AccessControlServiceLocal;
import jakarta.ejb.EJB;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/access")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AccessControlResource {

    @EJB
    private AccessControlServiceLocal accessControlService;

    @POST
    @Path("/check")
    public Response checkAccess(AccessCheckRequestDTO request) {
        AccessDecisionDTO decision = accessControlService.checkAccess(request);
        Response.Status status = decision.isAllowed() ? Response.Status.OK : Response.Status.FORBIDDEN;
        return Response.status(status).entity(decision).build();
    }
}
