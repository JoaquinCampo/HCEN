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

import grupo12.practico.rest.dto.HealthUserPageResponse;

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
    public Response findAll(
            @DefaultValue("1") @QueryParam("pageIndex") int page,
            @DefaultValue("20") @QueryParam("pageSize") int size,
            @QueryParam("ci") String documentFragment,
            @QueryParam("clinic") String clinicName) {
        if (size <= 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"Parameter 'size' must be greater than 0\"}")
                    .build();
        }
        if (page <= 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"Parameter 'page' must be greater than 0\"}")
                    .build();
        }

        int normalizedSize = Math.min(size, 100);
        String normalizedDocumentFragment = documentFragment != null && !documentFragment.trim().isEmpty()
                ? documentFragment.trim()
                : null;
        String normalizedClinicName = clinicName != null && !clinicName.trim().isEmpty()
                ? clinicName.trim()
                : null;

        long totalItems = healthUserService.count(normalizedDocumentFragment, normalizedClinicName);
        long totalPages = normalizedSize == 0 ? 0 : (totalItems + normalizedSize - 1) / normalizedSize;
        int zeroBasedPage = page - 1;

        List<HealthUserDTO> items = List.of();
        if (totalItems > 0 && zeroBasedPage < totalPages) {
            items = healthUserService.findPage(zeroBasedPage, normalizedSize, normalizedDocumentFragment,
                    normalizedClinicName);
        }

        HealthUserPageResponse response = new HealthUserPageResponse();
        response.setItems(items);
        response.setPage(page);
        response.setSize(normalizedSize);
        response.setTotalItems(totalItems);
        response.setTotalPages(totalPages);
        return Response.ok(response).build();
    }

    @GET
    @Path("/{id:[0-9a-fA-F-]{36}}")
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
    @Path("/{id:[0-9a-fA-F-]{36}}/demographics")
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

    @GET
    @Path("/{ci:[0-9]+}")
    public Response findByDocument(@PathParam("ci") String document) {
        HealthUserDTO user = healthUserService.findByDocument(document);
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\":\"HealthUser not found with ci: " + document + "\"}")
                    .build();
        }

        return Response.ok(user).build();
    }
}
