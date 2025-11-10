package grupo12.practico.rest;

import grupo12.practico.dtos.AccessPolicy.AddClinicAccessPolicyDTO;
import grupo12.practico.dtos.AccessPolicy.AddHealthWorkerAccessPolicyDTO;
import grupo12.practico.dtos.AccessPolicy.ClinicAccessPolicyDTO;
import grupo12.practico.dtos.AccessPolicy.HealthWorkerAccessPolicyDTO;
import grupo12.practico.messaging.AccessPolicy.ClinicAccessPolicyProducerLocal;
import grupo12.practico.messaging.AccessPolicy.HealthWorkerAccessPolicyProducerLocal;
import grupo12.practico.services.AccessPolicy.AccessPolicyServiceLocal;
import jakarta.ejb.EJB;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/access-policies")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AccessPolicyResource {

    @EJB
    private AccessPolicyServiceLocal accessPolicyService;

    @EJB
    private ClinicAccessPolicyProducerLocal clinicAccessPolicyProducer;

    @EJB
    private HealthWorkerAccessPolicyProducerLocal healthWorkerAccessPolicyProducer;

    @POST
    @Path("/clinic")
    public Response createClinicAccessPolicy(AddClinicAccessPolicyDTO dto) {
        clinicAccessPolicyProducer.enqueue(dto);
        return Response.accepted()
                .entity("{\"message\":\"Clinic access policy creation request queued successfully\"}")
                .build();
    }

    @POST
    @Path("/health-worker")
    public Response createHealthWorkerAccessPolicy(AddHealthWorkerAccessPolicyDTO dto) {
        healthWorkerAccessPolicyProducer.enqueue(dto);
        return Response.accepted()
                .entity("{\"message\":\"Health worker access policy creation request queued successfully\"}")
                .build();
    }

    @GET
    @Path("/clinic/health-user/{healthUserCi}")
    public Response findAllClinicAccessPolicies(@PathParam("healthUserCi") String healthUserCi) {
        List<ClinicAccessPolicyDTO> policies = accessPolicyService.findAllClinicAccessPolicies(healthUserCi);
        return Response.ok(policies).build();
    }

    @GET
    @Path("/health-worker/health-user/{healthUserCi}")
    public Response findAllHealthWorkerAccessPolicies(@PathParam("healthUserCi") String healthUserCi) {
        List<HealthWorkerAccessPolicyDTO> policies = accessPolicyService
                .findAllHealthWorkerAccessPolicies(healthUserCi);
        return Response.ok(policies).build();
    }

    @DELETE
    @Path("/clinic/{clinicAccessPolicyId}")
    public Response deleteClinicAccessPolicy(@PathParam("clinicAccessPolicyId") String clinicAccessPolicyId) {
        accessPolicyService.deleteClinicAccessPolicy(clinicAccessPolicyId);
        return Response.ok()
                .entity("{\"message\":\"Clinic access policy deleted successfully\"}")
                .build();
    }

    @DELETE
    @Path("/health-worker/{healthWorkerAccessPolicyId}")
    public Response deleteHealthWorkerAccessPolicy(
            @PathParam("healthWorkerAccessPolicyId") String healthWorkerAccessPolicyId) {
        accessPolicyService.deleteHealthWorkerAccessPolicy(healthWorkerAccessPolicyId);
        return Response.ok()
                .entity("{\"message\":\"Health worker access policy deleted successfully\"}")
                .build();
    }

    @GET
    @Path("/clinic/check-access")
    public Response hasClinicAccess(
            @QueryParam("healthUserCi") String healthUserCi,
            @QueryParam("clinicName") String clinicName) {
        boolean hasAccess = accessPolicyService.hasClinicAccess(healthUserCi, clinicName);
        return Response.ok()
                .entity("{\"hasAccess\":" + hasAccess + "}")
                .build();
    }

    @GET
    @Path("/health-worker/check-access")
    public Response hasHealthWorkerAccess(
            @QueryParam("healthUserCi") String healthUserCi,
            @QueryParam("healthWorkerCi") String healthWorkerCi) {
        boolean hasAccess = accessPolicyService.hasHealthWorkerAccess(healthUserCi, healthWorkerCi);
        return Response.ok()
                .entity("{\"hasAccess\":" + hasAccess + "}")
                .build();
    }
}
