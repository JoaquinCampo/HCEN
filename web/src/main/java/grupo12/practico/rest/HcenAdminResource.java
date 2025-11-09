package grupo12.practico.rest;

import grupo12.practico.dtos.HcenAdmin.AddHcenAdminDTO;
import grupo12.practico.dtos.HcenAdmin.HcenAdminDTO;
import grupo12.practico.services.HcenAdmin.HcenAdminServiceLocal;
import jakarta.ejb.EJB;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/hcen-admins")
@Produces(MediaType.APPLICATION_JSON)
public class HcenAdminResource {

    @EJB
    private HcenAdminServiceLocal hcenAdminService;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response create(AddHcenAdminDTO addHcenAdminDTO) {
        try {
            HcenAdminDTO createdAdmin = hcenAdminService.create(addHcenAdminDTO);
            return Response.status(Response.Status.CREATED).entity(createdAdmin).build();
        } catch (Exception ex) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"" + ex.getMessage() + "\"}")
                    .build();
        }
    }

    @GET
    @Path("/{ci}")
    public Response findByCi(@PathParam("ci") String ci) {
        HcenAdminDTO admin = hcenAdminService.findByCi(ci);
        if (admin == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\":\"HCEN admin not found with CI: " + ci + "\"}")
                    .build();
        }
        return Response.ok(admin).build();
    }
}
