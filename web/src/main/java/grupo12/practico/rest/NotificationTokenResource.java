package grupo12.practico.rest;

import jakarta.ejb.EJB;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import grupo12.practico.services.NotificationToken.NotificationTokenServiceLocal;
import grupo12.practico.dtos.NotificationToken.NotificationTokenDTO;

@Path("/notification-tokens")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class NotificationTokenResource {

    @EJB
    private NotificationTokenServiceLocal notificationTokenService;

    @POST
    public Response register(NotificationTokenDTO dto) {
        NotificationTokenDTO saved = notificationTokenService.add(dto);
        return Response.status(Response.Status.CREATED).entity(saved).build();
    }

    @DELETE
    @Path("/{userId}/{token}")
    public Response unregister(@PathParam("userId") String userId, @PathParam("token") String token) {
        NotificationTokenDTO dto = new NotificationTokenDTO();
        dto.setUserId(userId);
        dto.setToken(token);
        notificationTokenService.delete(dto);
        return Response.noContent().build();
    }
}
