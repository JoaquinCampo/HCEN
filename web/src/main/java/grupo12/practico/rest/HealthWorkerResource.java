package grupo12.practico.rest;

import grupo12.practico.dtos.HealthWorker.HealthWorkerDTO;
import grupo12.practico.services.HealthWorker.HealthWorkerServiceLocal;
import jakarta.ejb.EJB;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/health-workers")
@Produces(MediaType.APPLICATION_JSON)
public class HealthWorkerResource {

    @EJB
    private HealthWorkerServiceLocal healthWorkerService;

    @GET
    public Response findByClinicAndCi(
            @QueryParam("clinicName") String clinicName,
            @QueryParam("healthWorkerCi") String healthWorkerCi) {
        HealthWorkerDTO worker = healthWorkerService.findByClinicAndCi(clinicName, healthWorkerCi);
        if (worker == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\":\"Health worker not found\"}")
                    .build();
        }
        return Response.ok(worker).build();
    }
}
