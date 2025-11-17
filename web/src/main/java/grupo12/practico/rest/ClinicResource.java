package grupo12.practico.rest;

import grupo12.practico.dtos.Clinic.AddClinicDTO;
import grupo12.practico.dtos.Clinic.ClinicDTO;
import grupo12.practico.dtos.HealthWorker.HealthWorkerDTO;
import grupo12.practico.services.Clinic.ClinicServiceLocal;
import grupo12.practico.services.HealthWorker.HealthWorkerServiceLocal;
import jakarta.ejb.EJB;
import jakarta.validation.ValidationException;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/clinics")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ClinicResource {

    @EJB
    private ClinicServiceLocal clinicService;

    @EJB
    private HealthWorkerServiceLocal healthWorkerService;

    @GET
    public Response findAllClinics(@QueryParam("providerName") String providerName) {
        List<ClinicDTO> clinics = clinicService.findAllClinics(providerName);
        return Response.ok(clinics).build();
    }

    @GET
    @Path("/{clinicName}")
    public Response findClinicByName(@PathParam("clinicName") String clinicName) {
        ClinicDTO clinic = clinicService.findClinicByName(clinicName);
        if (clinic == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(errorJson("Clinic not found with name: " + clinicName))
                    .build();
        }
        return Response.ok(clinic).build();
    }

    @POST
    public Response createClinic(AddClinicDTO addClinicDTO) {
        ClinicDTO created = clinicService.createClinic(addClinicDTO);
        return Response.status(Response.Status.CREATED).entity(created).build();
    }

    @GET
    @Path("/{clinicName}/health-workers")
    public Response findHealthWorkersByClinic(@PathParam("clinicName") String clinicName) {
        try {
            List<HealthWorkerDTO> healthWorkers = healthWorkerService.findByClinic(clinicName);
            if (healthWorkers == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(errorJson("Clinic not found with name: " + clinicName))
                        .build();
            }
            return Response.ok(healthWorkers).build();
        } catch (ValidationException ex) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(errorJson(ex.getMessage()))
                    .build();
        }
    }

    private String errorJson(String message) {
        String safeMessage = message == null ? "" : message.replace("\"", "\\\"");
        return "{\"error\":\"" + safeMessage + "\"}";
    }
}
