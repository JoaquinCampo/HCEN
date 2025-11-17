package grupo12.practico.rest;

import grupo12.practico.services.Clinic.ClinicServiceLocal;
import grupo12.practico.services.HealthWorker.HealthWorkerServiceLocal;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@DisplayName("ClinicResource Tests")
class ClinicResourceTest {

    @Mock
    private ClinicServiceLocal clinicService;

    @Mock
    private HealthWorkerServiceLocal healthWorkerService;

    @InjectMocks
    private ClinicResource resource;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Should find all clinics")
    void testFindAllClinics() {
        when(clinicService.findAllClinics(anyString())).thenReturn(Collections.emptyList());

        Response response = resource.findAllClinics("Provider 1");

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        verify(clinicService, times(1)).findAllClinics("Provider 1");
    }

    @Test
    @DisplayName("Should find clinic by name")
    void testFindClinicByName() {
        when(clinicService.findClinicByName(anyString())).thenReturn(null);

        Response response = resource.findClinicByName("Clinic A");

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
        verify(clinicService, times(1)).findClinicByName("Clinic A");
    }

    @Test
    @DisplayName("Should create clinic")
    void testCreateClinic() {
        when(clinicService.createClinic(any())).thenReturn(null);

        Response response = resource.createClinic(any());

        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        verify(clinicService, times(1)).createClinic(any());
    }

    @Test
    @DisplayName("Should find health workers by clinic")
    void testFindHealthWorkersByClinic() {
        when(healthWorkerService.findByClinic(anyString())).thenReturn(Collections.emptyList());

        Response response = resource.findHealthWorkersByClinic("Clinic A");

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        verify(healthWorkerService, times(1)).findByClinic("Clinic A");
    }
}
