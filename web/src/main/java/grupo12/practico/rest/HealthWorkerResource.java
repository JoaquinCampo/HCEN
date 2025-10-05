package grupo12.practico.rest;

import grupo12.practico.dtos.HealthWorker.AddHealthWorkerDTO;
import grupo12.practico.dtos.HealthWorker.HealthWorkerDTO;
import grupo12.practico.services.HealthWorker.HealthWorkerServiceLocal;
import grupo12.practico.messaging.HealthWorker.HealthWorkerRegistrationProducerLocal;
import jakarta.ejb.EJB;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/health-workers")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class HealthWorkerResource {

    @EJB
    private HealthWorkerServiceLocal healthWorkerService;

    @EJB
    private HealthWorkerRegistrationProducerLocal healthWorkerRegistrationProducer;

    @GET
    public List<HealthWorkerDTO> findAll() {
        return healthWorkerService.findAll();
    }

    @GET
    @Path("/{id}")
    public Response findById(@PathParam("id") String id) {
        HealthWorkerDTO worker = healthWorkerService.findById(id);
        if (worker == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\":\"HealthWorker not found with id: " + id + "\"}")
                    .build();
        }
        return Response.ok(worker).build();
    }

    @GET
    @Path("/search")
    public List<HealthWorkerDTO> findByName(@QueryParam("name") String name) {
        return healthWorkerService.findByName(name);
    }

    @POST
    public Response add(AddHealthWorkerDTO addHealthWorkerDTO) {
        healthWorkerRegistrationProducer.enqueue(addHealthWorkerDTO);
        return Response.accepted()
                .entity("{\"message\":\"Health worker registration request queued successfully for document: "
                        + addHealthWorkerDTO.getDocument() + "\"}")
                .build();
    }
}
