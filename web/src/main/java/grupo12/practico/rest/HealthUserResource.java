package grupo12.practico.rest;

import grupo12.practico.dtos.PaginationDTO;
import grupo12.practico.dtos.HealthUser.AddHealthUserDTO;
import grupo12.practico.dtos.HealthUser.HealthUserDTO;
import grupo12.practico.messaging.HealthUser.HealthUserRegistrationProducerLocal;
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

    @GET
    public Response findAllHealthUsers(
            @QueryParam("clinicName") String clinicName,
            @QueryParam("name") String name,
            @QueryParam("ci") String ci,
            @QueryParam("pageIndex") Integer pageIndex,
            @QueryParam("pageSize") Integer pageSize) {
        PaginationDTO<HealthUserDTO> paginationResult = healthUserService.findAllHealthUsers(clinicName, name, ci, pageIndex,
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
