package grupo12.practico.rest;

import grupo12.practico.messaging.HealthUser.HealthUserRegistrationProducerLocal;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@DisplayName("HealthUserResource Tests")
class HealthUserResourceTest {

    @Mock
    private HealthUserServiceLocal healthUserService;

    @Mock
    private HealthUserRegistrationProducerLocal healthUserRegistrationProducer;

    @InjectMocks
    private HealthUserResource resource;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Should find all health users")
    void testFindAllHealthUsers() {
        when(healthUserService.findAllHealthUsers(anyString(), anyString(), anyString(), any(), any()))
                .thenReturn(null);

        Response response = resource.findAllHealthUsers("Clinic A", "John", "12345678", 0, 10);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        verify(healthUserService, times(1)).findAllHealthUsers("Clinic A", "John", "12345678", 0, 10);
    }

    @Test
    @DisplayName("Should find health user by CI")
    void testFindByCi() {
        when(healthUserService.findHealthUserByCi(anyString())).thenReturn(null);

        Response response = resource.findByCi("12345678");

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
        verify(healthUserService, times(1)).findHealthUserByCi("12345678");
    }

    @Test
    @DisplayName("Should create health user and return 202 Accepted")
    void testCreate() {
        Response response = resource.create(any());

        assertEquals(Response.Status.ACCEPTED.getStatusCode(), response.getStatus());
        verify(healthUserRegistrationProducer, times(1)).enqueue(any());
    }

    @Test
    @DisplayName("Should link clinic to health user")
    void testLinkClinicToHealthUser() {
        when(healthUserService.linkClinicToHealthUser(anyString(), anyString())).thenReturn(null);

        Response response = resource.linkClinicToHealthUser("user123", "Clinic A");

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        verify(healthUserService, times(1)).linkClinicToHealthUser("user123", "Clinic A");
    }
}
