package grupo12.practico.rest;

import grupo12.practico.dtos.NotificationToken.NotificationTokenDTO;
import grupo12.practico.rest.dto.NotificationUnsubscribeRequest;
import grupo12.practico.services.NotificationToken.NotificationTokenServiceLocal;
import jakarta.ejb.EJB;
import jakarta.validation.ValidationException;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/notification-tokens")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class NotificationTokenResource {

    @EJB
    private NotificationTokenServiceLocal notificationTokenService;

    @POST
    public Response register(NotificationTokenDTO dto) {
        try {
            NotificationTokenDTO saved = notificationTokenService.add(dto);
            return Response.status(Response.Status.CREATED).entity(saved).build();
        } catch (ValidationException ex) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"" + ex.getMessage() + "\"}")
                    .build();
        }
    }

    @DELETE
    @Path("/{userCi}/{token}")
    public Response unregister(@PathParam("userCi") String userCi, @PathParam("token") String token) {
        NotificationTokenDTO dto = new NotificationTokenDTO();
        dto.setUserCi(userCi);
        dto.setToken(token);
        notificationTokenService.delete(dto);
        return Response.noContent().build();
    }

    @POST
    @Path("/unsubscribe")
    public Response unsubscribe(NotificationUnsubscribeRequest request) {
        if (request == null || request.getUserCi() == null || request.getUserCi().trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"userCi is required\"}")
                    .build();
        }
        try {
            notificationTokenService.unsubscribe(request.getUserCi());
            return Response.noContent().build();
        } catch (ValidationException ex) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"" + ex.getMessage() + "\"}")
                    .build();
        }
    }

    @POST
    @Path("/subscribe")
    public Response subscribe(NotificationUnsubscribeRequest request) {
        if (request == null || request.getUserCi() == null || request.getUserCi().trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"userId is required\"}")
                    .build();
        }
        try {
            notificationTokenService.subscribe(request.getUserCi());
            return Response.noContent().build();
        } catch (ValidationException ex) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"" + ex.getMessage() + "\"}")
                    .build();
        }
    }
}
