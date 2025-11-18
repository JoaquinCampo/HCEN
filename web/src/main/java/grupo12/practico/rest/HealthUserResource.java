package grupo12.practico.rest;

import grupo12.practico.dtos.PaginationDTO;
import grupo12.practico.dtos.HealthUser.AddHealthUserDTO;
import grupo12.practico.dtos.HealthUser.HealthUserDTO;
import grupo12.practico.messaging.HealthUser.HealthUserRegistrationProducerLocal;
// AGE VERIFICATION FUNCTIONALITY COMMENTED OUT - Not working on deployed instance
// import grupo12.practico.services.AgeVerification.AgeVerificationException;
// import grupo12.practico.services.AgeVerification.AgeVerificationServiceLocal;
import grupo12.practico.services.HealthUser.HealthUserServiceLocal;
import jakarta.ejb.EJB;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/health-users")
@Produces(MediaType.APPLICATION_JSON)
public class HealthUserResource {

    @EJB
    private HealthUserServiceLocal healthUserService;

    @EJB
    private HealthUserRegistrationProducerLocal healthUserRegistrationProducer;

    // AGE VERIFICATION FUNCTIONALITY COMMENTED OUT - Not working on deployed
    // instance
    // @EJB
    // private AgeVerificationServiceLocal ageVerificationService;

    @GET
    public Response findAllHealthUsers(
            @QueryParam("clinicName") String clinicName,
            @QueryParam("name") String name,
            @QueryParam("ci") String ci,
            @QueryParam("pageIndex") Integer pageIndex,
            @QueryParam("pageSize") Integer pageSize) {
        PaginationDTO<HealthUserDTO> paginationResult = healthUserService.findAllHealthUsers(clinicName, name, ci,
                pageIndex,
                pageSize);
        return Response.ok(paginationResult).build();
    }

    @GET
    @Path("/{ci}")
    public Response findByCi(@PathParam("ci") String ci) {
        HealthUserDTO user = healthUserService.findHealthUserByCi(ci);
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\":\"Health user not found with CI: " + ci + "\"}")
                    .build();
        }
        return Response.ok(user).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response create(AddHealthUserDTO addHealthUserDTO) {
        // Validar que la CI existe en PDI y que el usuario es mayor de edad
        if (addHealthUserDTO == null || addHealthUserDTO.getCi() == null || addHealthUserDTO.getCi().isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"CI is required\"}")
                    .build();
        }

        // AGE VERIFICATION FUNCTIONALITY COMMENTED OUT - Not working on deployed
        // instance
        // TODO: Re-enable when PDI service is available
        /*
         * try {
         * boolean esMayorDeEdad =
         * ageVerificationService.verificarMayorDeEdad(addHealthUserDTO.getCi());
         * if (!esMayorDeEdad) {
         * return Response.status(Response.Status.BAD_REQUEST)
         * .entity("{\"error\":\"El usuario debe ser mayor de edad\"}")
         * .build();
         * }
         * } catch (AgeVerificationException e) {
         * return Response.status(Response.Status.BAD_REQUEST)
         * .entity("{\"error\":\"" + e.getMessage() + "\"}")
         * .build();
         * }
         */

        // Si la validación pasa, encolar el mensaje
        healthUserRegistrationProducer.enqueue(addHealthUserDTO);
        return Response.accepted()
                .entity("{\"message\":\"Health user registration request queued successfully\"}")
                .build();
    }

    @POST
    @Path("/{healthUserId}/link-clinic")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response linkClinicToHealthUser(
            @PathParam("healthUserId") String healthUserId,
            @QueryParam("clinicName") String clinicName) {
        HealthUserDTO user = healthUserService.linkClinicToHealthUser(healthUserId, clinicName);
        return Response.ok(user).build();
    }

}
