package grupo12.practico.rest;

import grupo12.practico.dtos.Clinic.AddClinicDTO;
import grupo12.practico.dtos.Clinic.ClinicDTO;
import grupo12.practico.messaging.Clinic.ClinicRegistrationProducerLocal;
import grupo12.practico.services.Clinic.ClinicServiceLocal;
import jakarta.ejb.EJB;
import jakarta.ws.rs.*;
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
    private ClinicRegistrationProducerLocal registrationProducer;

    @GET
    public List<ClinicDTO> findAll() {
        return clinicService.findAll();
    }

    @GET
    @Path("/{id}")
    public Response findById(@PathParam("id") String id) {
        ClinicDTO clinic = clinicService.findById(id);
        if (clinic == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\":\"Clinic not found with id: " + id + "\"}")
                    .build();
        }
        return Response.ok(clinic).build();
    }

    @GET
    @Path("/search")
    public List<ClinicDTO> findByName(@QueryParam("name") String name) {
        return clinicService.findByName(name);
    }

    @POST
    public Response add(AddClinicDTO addClinicDTO) {
        try {
            registrationProducer.enqueue(addClinicDTO);
            return Response.accepted()
                    .entity("{\"message\":\"Clinic registration request accepted; the clinic will be created shortly\"}")
                    .build();
        } catch (Exception ex) {
            String message = ex.getMessage() != null ? ex.getMessage()
                    : "Failed to enqueue clinic registration request";
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"" + message + "\"}")
                    .build();
        }
    }
}