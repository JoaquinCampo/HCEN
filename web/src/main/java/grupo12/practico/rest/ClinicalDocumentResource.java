package grupo12.practico.rest;

import grupo12.practico.dtos.ClinicalDocument.AddClinicalDocumentDTO;
import grupo12.practico.dtos.ClinicalDocument.PresignedUrlRequestDTO;
import grupo12.practico.dtos.ClinicalHistory.ChatRequestDTO;
import grupo12.practico.messaging.ClinicalDocument.CreateDocumentProducerLocal;
import grupo12.practico.services.ClinicalDocument.ClinicalDocumentServiceLocal;
import jakarta.ejb.EJB;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/clinical-documents")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ClinicalDocumentResource {

    @EJB
    private ClinicalDocumentServiceLocal clinicalDocumentService;

    @EJB
    private CreateDocumentProducerLocal createDocumentProducer;

    @POST
    @Path("/upload-url")
    public Response getPresignedUploadUrl(PresignedUrlRequestDTO request) {
        return Response.ok(clinicalDocumentService.getPresignedUploadUrl(request)).build();
    }

    @POST
    public Response createClinicalDocument(AddClinicalDocumentDTO dto) {
        createDocumentProducer.enqueue(dto);
        return Response.accepted()
                .entity("{\"message\":\"Clinical document creation request queued successfully\"}")
                .build();
    }

    @POST
    @Path("/chat")
    public Response chat(ChatRequestDTO request) {
        return Response.ok(clinicalDocumentService.chat(request)).build();
    }
}
