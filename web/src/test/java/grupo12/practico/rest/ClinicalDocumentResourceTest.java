package grupo12.practico.rest;

import grupo12.practico.messaging.ClinicalDocument.CreateDocumentProducerLocal;
import grupo12.practico.services.ClinicalDocument.ClinicalDocumentServiceLocal;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("ClinicalDocumentResource Tests")
class ClinicalDocumentResourceTest {

    @Mock
    private ClinicalDocumentServiceLocal clinicalDocumentService;

    @Mock
    private CreateDocumentProducerLocal createDocumentProducer;

    @InjectMocks
    private ClinicalDocumentResource resource;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Should get presigned upload URL")
    void testGetPresignedUploadUrl() {
        when(clinicalDocumentService.getPresignedUploadUrl(any())).thenReturn(null);

        Response response = resource.getPresignedUploadUrl(any());

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        verify(clinicalDocumentService, times(1)).getPresignedUploadUrl(any());
    }

    @Test
    @DisplayName("Should create clinical document and return 202 Accepted")
    void testCreateClinicalDocument() {
        Response response = resource.createClinicalDocument(any());

        assertEquals(Response.Status.ACCEPTED.getStatusCode(), response.getStatus());
        verify(createDocumentProducer, times(1)).enqueue(any());
    }

    @Test
    @DisplayName("Should handle chat request")
    void testChat() {
        when(clinicalDocumentService.chat(any())).thenReturn(null);

        Response response = resource.chat(any());

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        verify(clinicalDocumentService, times(1)).chat(any());
    }
}
