package grupo12.practico.rest;

import grupo12.practico.dtos.Authorization.AuthorizationCheckRequestDTO;
import grupo12.practico.dtos.Authorization.AuthorizationDecisionDTO;
import grupo12.practico.services.Authorization.AuthorizationServiceLocal;
import jakarta.ejb.EJB;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/authorization")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthorizationResource {

    @EJB
    private AuthorizationServiceLocal authorizationService;

    @POST
    @Path("/check")
    public Response checkAccess(AuthorizationCheckRequestDTO request) {
        AuthorizationDecisionDTO decision = authorizationService.checkAccess(request);
        Response.Status status = decision.isAllowed() ? Response.Status.OK : Response.Status.FORBIDDEN;
        return Response.status(status).entity(decision).build();
    }
}
