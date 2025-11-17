package grupo12.practico.rest;

import grupo12.practico.messaging.AccessPolicy.Clinic.ClinicAccessPolicyProducerLocal;
import grupo12.practico.messaging.AccessPolicy.HealthWorker.HealthWorkerAccessPolicyProducerLocal;
import grupo12.practico.messaging.AccessPolicy.Specialty.SpecialtyAccessPolicyProducerLocal;
import grupo12.practico.services.AccessPolicy.AccessPolicyServiceLocal;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@DisplayName("AccessPolicyResource Tests")
class AccessPolicyResourceTest {

    @Mock
    private AccessPolicyServiceLocal accessPolicyService;

    @Mock
    private ClinicAccessPolicyProducerLocal clinicAccessPolicyProducer;

    @Mock
    private HealthWorkerAccessPolicyProducerLocal healthWorkerAccessPolicyProducer;

    @Mock
    private SpecialtyAccessPolicyProducerLocal specialtyAccessPolicyProducer;

    @InjectMocks
    private AccessPolicyResource resource;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Should create clinic access policy and return 202 Accepted")
    void testCreateClinicAccessPolicy() {
        Response response = resource.createClinicAccessPolicy(any());

        assertEquals(Response.Status.ACCEPTED.getStatusCode(), response.getStatus());
        verify(clinicAccessPolicyProducer, times(1)).enqueue(any());
    }

    @Test
    @DisplayName("Should create health worker access policy and return 202 Accepted")
    void testCreateHealthWorkerAccessPolicy() {
        Response response = resource.createHealthWorkerAccessPolicy(any());

        assertEquals(Response.Status.ACCEPTED.getStatusCode(), response.getStatus());
        verify(healthWorkerAccessPolicyProducer, times(1)).enqueue(any());
    }

    @Test
    @DisplayName("Should create specialty access policy and return 202 Accepted")
    void testCreateSpecialtyAccessPolicy() {
        Response response = resource.createSpecialtyAccessPolicy(any());

        assertEquals(Response.Status.ACCEPTED.getStatusCode(), response.getStatus());
        verify(specialtyAccessPolicyProducer, times(1)).enqueue(any());
    }

    @Test
    @DisplayName("Should find clinic access policies for health user")
    void testFindAllClinicAccessPolicies() {
        when(accessPolicyService.findAllClinicAccessPolicies(anyString())).thenReturn(Collections.emptyList());

        Response response = resource.findAllClinicAccessPolicies("12345678");

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        verify(accessPolicyService, times(1)).findAllClinicAccessPolicies("12345678");
    }

    @Test
    @DisplayName("Should find health worker access policies for health user")
    void testFindAllHealthWorkerAccessPolicies() {
        when(accessPolicyService.findAllHealthWorkerAccessPolicies(anyString())).thenReturn(Collections.emptyList());

        Response response = resource.findAllHealthWorkerAccessPolicies("12345678");

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        verify(accessPolicyService, times(1)).findAllHealthWorkerAccessPolicies("12345678");
    }

    @Test
    @DisplayName("Should find specialty access policies for health user")
    void testFindAllSpecialtyAccessPolicies() {
        when(accessPolicyService.findAllSpecialtyAccessPolicies(anyString())).thenReturn(Collections.emptyList());

        Response response = resource.findAllSpecialtyAccessPolicies("12345678");

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        verify(accessPolicyService, times(1)).findAllSpecialtyAccessPolicies("12345678");
    }

    @Test
    @DisplayName("Should delete clinic access policy")
    void testDeleteClinicAccessPolicy() {
        doNothing().when(accessPolicyService).deleteClinicAccessPolicy(anyString());

        Response response = resource.deleteClinicAccessPolicy("policy123");

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        verify(accessPolicyService, times(1)).deleteClinicAccessPolicy("policy123");
    }

    @Test
    @DisplayName("Should delete health worker access policy")
    void testDeleteHealthWorkerAccessPolicy() {
        doNothing().when(accessPolicyService).deleteHealthWorkerAccessPolicy(anyString());

        Response response = resource.deleteHealthWorkerAccessPolicy("policy123");

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        verify(accessPolicyService, times(1)).deleteHealthWorkerAccessPolicy("policy123");
    }

    @Test
    @DisplayName("Should delete specialty access policy")
    void testDeleteSpecialtyAccessPolicy() {
        doNothing().when(accessPolicyService).deleteSpecialtyAccessPolicy(anyString());

        Response response = resource.deleteSpecialtyAccessPolicy("policy123");

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        verify(accessPolicyService, times(1)).deleteSpecialtyAccessPolicy("policy123");
    }

    @Test
    @DisplayName("Should check clinic access")
    void testHasClinicAccess() {
        when(accessPolicyService.hasClinicAccess(anyString(), anyString())).thenReturn(true);

        Response response = resource.hasClinicAccess("12345678", "Clinic A");

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity().toString().contains("true"));
    }

    @Test
    @DisplayName("Should check health worker access")
    void testHasHealthWorkerAccess() {
        when(accessPolicyService.hasHealthWorkerAccess(anyString(), anyString())).thenReturn(false);

        Response response = resource.hasHealthWorkerAccess("12345678", "87654321");

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity().toString().contains("false"));
    }

    @Test
    @DisplayName("Should check specialty access")
    void testHasSpecialtyAccess() {
        when(accessPolicyService.hasSpecialtyAccess(anyString(), any())).thenReturn(true);

        Response response = resource.hasSpecialtyAccess("12345678", Arrays.asList("Cardiology"));

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity().toString().contains("true"));
    }
}
