package grupo12.practico.rest;

import grupo12.practico.dtos.ClinicalDocument.AddClinicalDocumentDTO;
import grupo12.practico.dtos.ClinicalDocument.ClinicalDocumentDTO;
import grupo12.practico.services.ClinicalDocument.ClinicalDocumentServiceLocal;
import jakarta.ejb.EJB;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.net.URI;
import java.util.List;

@Path("/clinical-documents")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ClinicalDocumentResource {

    @EJB
    private ClinicalDocumentServiceLocal clinicalDocumentService;

    @GET
    public List<ClinicalDocumentDTO> findAll() {
        return clinicalDocumentService.findAll();
    }

    @GET
    @Path("/{id}")
    public Response findById(@PathParam("id") String id) {
        ClinicalDocumentDTO document = clinicalDocumentService.findById(id);
        if (document == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\":\"ClinicalDocument not found with id: " + id + "\"}")
                    .build();
        }
        return Response.ok(document).build();
    }

    @GET
    @Path("/search")
    public List<ClinicalDocumentDTO> findByTitle(@QueryParam("title") String title) {
        return clinicalDocumentService.findByTitle(title);
    }

    @POST
    public Response add(AddClinicalDocumentDTO addClinicalDocumentDTO) {
        ClinicalDocumentDTO created = clinicalDocumentService.add(addClinicalDocumentDTO);
        URI location = URI.create("/clinical-documents/" + created.getId());
        return Response.created(location).entity(created).build();
    }
}