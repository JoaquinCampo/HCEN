package grupo12.practico.rest;

import grupo12.practico.dtos.HealthUser.AddHealthUserDTO;
import grupo12.practico.dtos.HealthUser.HealthUserDTO;
import grupo12.practico.services.HealthUser.HealthUserServiceLocal;
import grupo12.practico.services.ExternalDataServiceLocal;
import grupo12.practico.messaging.HealthUser.HealthUserRegistrationProducerLocal;
import jakarta.ejb.EJB;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.Map;

@Path("/health-users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class HealthUserResource {

    @EJB
    private HealthUserServiceLocal healthUserService;

    @EJB
    private HealthUserRegistrationProducerLocal healthUserRegistrationProducer;

    @EJB
    private ExternalDataServiceLocal externalDataService;

    @GET
    public List<HealthUserDTO> findAll() {
        return healthUserService.findAll();
    }

    @GET
    @Path("/{id}")
    public Response findById(@PathParam("id") String id) {
        HealthUserDTO user = healthUserService.findById(id);
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\":\"HealthUser not found with id: " + id + "\"}")
                    .build();
        }
        return Response.ok(user).build();
    }

    @GET
    @Path("/search")
    public List<HealthUserDTO> findByName(@QueryParam("name") String name) {
        return healthUserService.findByName(name);
    }

    @POST
    public Response add(AddHealthUserDTO addHealthUserDTO) {
        healthUserRegistrationProducer.enqueue(addHealthUserDTO);
        return Response.accepted()
                .entity("{\"message\":\"Health user registration request queued successfully for document: "
                        + addHealthUserDTO.getDocument() + "\"}")
                .build();
    }

    @GET
    @Path("/{id}/demographics")
    public Response getUserDemographics(@PathParam("id") String id) {
        try {
            HealthUserDTO user = healthUserService.findById(id);
            if (user == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"error\":\"HealthUser not found with id: " + id + "\"}")
                        .build();
            }

            Map<String, Object> demographics = externalDataService.getDemographicData(user.getFirstName());
            return Response.ok(demographics).build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"Error retrieving demographics: " + e.getMessage() + "\"}")
                    .build();
        }
    }
}
