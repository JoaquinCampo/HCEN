package grupo12.practico.rest;

import grupo12.practico.services.ClinicalDocument.ClinicalDocumentServiceLocal;
import grupo12.practico.services.HealthUser.HealthUserServiceLocal;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@DisplayName("ClinicalHistoryResource Tests")
class ClinicalHistoryResourceTest {

    @Mock
    private ClinicalDocumentServiceLocal clinicalDocumentService;

    @Mock
    private HealthUserServiceLocal healthUserService;

    @InjectMocks
    private ClinicalHistoryResource resource;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Should find clinical history access history")
    void testFindClinicalHistoryAccessHistory() {
        when(healthUserService.findHealthUserAccessHistory(anyString(), any(), any())).thenReturn(null);

        Response response = resource.findClinicalHistoryAccessHistory("12345678", 0, 10);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        verify(healthUserService, times(1)).findHealthUserAccessHistory("12345678", 0, 10);
    }

    @Test
    @DisplayName("Should find clinical history")
    void testFindClinicalHistory() {
        when(healthUserService.findHealthUserClinicalHistory(any())).thenReturn(null);

        Response response = resource.findClinicalHistory("12345678", null, null, null);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        verify(healthUserService, times(1)).findHealthUserClinicalHistory(any());
    }

    @Test
    @DisplayName("Should handle chat request")
    void testChat() {
        when(clinicalDocumentService.chat(any())).thenReturn(null);

        Response response = resource.chat(any());

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        verify(clinicalDocumentService, times(1)).chat(any());
    }

    @Test
    @DisplayName("Should get clinical history access history with default pagination")
    void testGetClinicalHistoryAccessHistory() {
        when(healthUserService.findHealthUserAccessHistory(anyString(), anyInt(), anyInt())).thenReturn(null);

        Response response = resource.getClinicalHistoryAccessHistory("12345678", 0, 50);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        verify(healthUserService, times(1)).findHealthUserAccessHistory("12345678", 0, 50);
    }
}
