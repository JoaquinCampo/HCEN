package grupo12.practico.rest;

import grupo12.practico.dtos.NotificationToken.NotificationSubscriptionDTO;
import grupo12.practico.dtos.NotificationToken.NotificationTokenDTO;
import grupo12.practico.dtos.NotificationToken.NotificationUnsubscribeRequestDTO;
import grupo12.practico.models.NotificationType;
import grupo12.practico.services.NotificationToken.NotificationTokenServiceLocal;
import jakarta.ejb.EJB;
import jakarta.validation.ValidationException;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
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
    public Response unsubscribe(NotificationUnsubscribeRequestDTO request) {
        if (request == null || request.getUserCi() == null || request.getUserCi().trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"userCi is required\"}")
                    .build();
        }
        if (request.getNotificationType() == null || request.getNotificationType().trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"notificationType is required\"}")
                    .build();
        }

        NotificationType type;
        try {
            type = NotificationType.valueOf(request.getNotificationType());
        } catch (IllegalArgumentException ex) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"Invalid notificationType. Valid values: ACCESS_REQUEST, CLINICAL_HISTORY_ACCESS\"}")
                    .build();
        }

        try {
            notificationTokenService.unsubscribe(request.getUserCi(), type);
            return Response.noContent().build();
        } catch (ValidationException ex) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"" + ex.getMessage() + "\"}")
                    .build();
        }
    }

    @POST
    @Path("/subscribe")
    public Response subscribe(NotificationUnsubscribeRequestDTO request) {
        if (request == null || request.getUserCi() == null || request.getUserCi().trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"userCi is required\"}")
                    .build();
        }
        if (request.getNotificationType() == null || request.getNotificationType().trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"notificationType is required\"}")
                    .build();
        }

        NotificationType type;
        try {
            type = NotificationType.valueOf(request.getNotificationType());
        } catch (IllegalArgumentException ex) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"Invalid notificationType. Valid values: ACCESS_REQUEST, CLINICAL_HISTORY_ACCESS\"}")
                    .build();
        }

        try {
            notificationTokenService.subscribe(request.getUserCi(), type);
            return Response.noContent().build();
        } catch (ValidationException ex) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"" + ex.getMessage() + "\"}")
                    .build();
        }
    }

    @GET
    @Path("/subscription-preferences/{userCi}")
    public Response getSubscriptionPreferences(@PathParam("userCi") String userCi) {
        if (userCi == null || userCi.trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"userCi is required\"}")
                    .build();
        }

        try {
            NotificationSubscriptionDTO preferences = notificationTokenService.getSubscriptionPreferences(userCi);
            return Response.ok(preferences).build();
        } catch (ValidationException ex) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"" + ex.getMessage() + "\"}")
                    .build();
        }
    }
}
