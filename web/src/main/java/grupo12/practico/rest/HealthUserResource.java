package grupo12.practico.rest;

import grupo12.practico.dtos.HealthUser.AddHealthUserDTO;
import grupo12.practico.dtos.HealthUser.HealthUserDTO;
import grupo12.practico.services.HealthUser.HealthUserServiceLocal;
import jakarta.ejb.EJB;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.net.URI;
import java.util.List;

@Path("/health-users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class HealthUserResource {

    @EJB
    private HealthUserServiceLocal healthUserService;

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
        HealthUserDTO created = healthUserService.add(addHealthUserDTO);
        URI location = URI.create("/api/health-users/" + created.getId());
        return Response.created(location).entity(created).build();
    }
}