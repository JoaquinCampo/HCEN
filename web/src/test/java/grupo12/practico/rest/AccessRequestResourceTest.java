package grupo12.practico.rest;

import grupo12.practico.messaging.AccessRequest.AccessRequestProducerLocal;
import grupo12.practico.services.AccessRequest.AccessRequestServiceLocal;
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

@DisplayName("AccessRequestResource Tests")
class AccessRequestResourceTest {

    @Mock
    private AccessRequestServiceLocal accessRequestService;

    @Mock
    private AccessRequestProducerLocal accessRequestProducer;

    @InjectMocks
    private AccessRequestResource resource;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Should create access request and return 202 Accepted")
    void testCreateAccessRequest() {
        Response response = resource.createAccessRequest(any());

        assertEquals(Response.Status.ACCEPTED.getStatusCode(), response.getStatus());
        verify(accessRequestProducer, times(1)).enqueue(any());
    }

    @Test
    @DisplayName("Should find all access requests")
    void testFindAllAccessRequests() {
        when(accessRequestService.findAllAccessRequests(anyString(), anyString(), anyString()))
                .thenReturn(Collections.emptyList());

        Response response = resource.findAllAccessRequests("12345678", "87654321", "Clinic A");

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        verify(accessRequestService, times(1)).findAllAccessRequests("12345678", "87654321", "Clinic A");
    }

    @Test
    @DisplayName("Should delete access request")
    void testDeleteAccessRequest() {
        doNothing().when(accessRequestService).deleteAccessRequest(anyString());

        Response response = resource.deleteAccessRequest("request123");

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        verify(accessRequestService, times(1)).deleteAccessRequest("request123");
    }
}
