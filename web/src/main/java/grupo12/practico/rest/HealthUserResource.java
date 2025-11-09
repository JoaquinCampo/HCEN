package grupo12.practico.rest;

import grupo12.practico.dtos.PaginationDTO;
import grupo12.practico.dtos.HealthUser.AddHealthUserDTO;
import grupo12.practico.dtos.HealthUser.ClinicalHistoryDTO;
import grupo12.practico.dtos.HealthUser.HealthUserDTO;
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

    @GET
    public Response findAll(
            @QueryParam("clinicName") String clinicName,
            @QueryParam("name") String name,
            @QueryParam("ci") String ci,
            @QueryParam("pageIndex") Integer pageIndex,
            @QueryParam("pageSize") Integer pageSize) {
        PaginationDTO<HealthUserDTO> paginationResult = healthUserService.findAll(clinicName, name, ci, pageIndex, pageSize);
        return Response.ok(paginationResult).build();
    }

    @GET
    @Path("/{ci}")
    public Response findByCi(@PathParam("ci") String ci) {
        HealthUserDTO user = healthUserService.findByCi(ci);
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
        try {
            HealthUserDTO createdUser = healthUserService.create(addHealthUserDTO);
            return Response.status(Response.Status.CREATED).entity(createdUser).build();
        } catch (Exception ex) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"" + ex.getMessage() + "\"}")
                    .build();
        }
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

    @GET
    @Path("/{healthUserCi}/clinical-history")
    public Response findClinicalHistory(
            @PathParam("healthUserCi") String healthUserCi,
            @QueryParam("clinicName") String clinicName,
            @QueryParam("healthWorkerCi") String healthWorkerCi) {
        ClinicalHistoryDTO clinicalHistory = healthUserService.findClinicalHistory(healthUserCi, clinicName,
                healthWorkerCi);
        return Response.ok(clinicalHistory).build();
    }
}
