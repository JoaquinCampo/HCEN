package grupo12.practico.rest;

import grupo12.practico.dtos.Clinic.ClinicDTO;
import grupo12.practico.services.Clinic.ClinicServiceLocal;
import jakarta.ejb.EJB;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/clinics")
@Produces(MediaType.APPLICATION_JSON)
public class ClinicResource {

    @EJB
    private ClinicServiceLocal clinicService;

    @GET
    public Response findAll() {
        List<ClinicDTO> clinics = clinicService.findAll();
        return Response.ok(clinics).build();
    }

    @GET
    @Path("/search")
    public Response findByName(@QueryParam("name") String name) {
        ClinicDTO clinic = clinicService.findByName(name);
        if (clinic == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\":\"Clinic not found with name: " + name + "\"}")
                    .build();
        }
        return Response.ok(clinic).build();
    }
}
