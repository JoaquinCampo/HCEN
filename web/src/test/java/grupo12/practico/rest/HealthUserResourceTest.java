package grupo12.practico.rest;

import grupo12.practico.dtos.HealthUser.AddHealthUserDTO;
import grupo12.practico.messaging.HealthUser.HealthUserRegistrationProducerLocal;
import grupo12.practico.models.Gender;
import grupo12.practico.services.AgeVerification.AgeVerificationException;
import grupo12.practico.services.AgeVerification.AgeVerificationServiceLocal;
import grupo12.practico.services.HealthUser.HealthUserServiceLocal;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Set;

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

    @Mock
    private AgeVerificationServiceLocal ageVerificationService;

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
    @DisplayName("Should create health user and return 202 Accepted when validation passes")
    void testCreate_Success() throws AgeVerificationException {
        // Arrange
        AddHealthUserDTO dto = createValidAddHealthUserDTO();
        when(ageVerificationService.verificarMayorDeEdad("12345678")).thenReturn(true);

        // Act
        Response response = resource.create(dto);

        // Assert
        assertEquals(Response.Status.ACCEPTED.getStatusCode(), response.getStatus());
        verify(ageVerificationService, times(1)).verificarMayorDeEdad("12345678");
        verify(healthUserRegistrationProducer, times(1)).enqueue(dto);
    }

    @Test
    @DisplayName("Should return 400 Bad Request when CI is null")
    void testCreate_CINull() {
        // Arrange
        AddHealthUserDTO dto = new AddHealthUserDTO();
        dto.setCi(null);

        // Act
        Response response = resource.create(dto);

        // Assert
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        verifyNoInteractions(ageVerificationService);
        verifyNoInteractions(healthUserRegistrationProducer);
    }

    @Test
    @DisplayName("Should return 400 Bad Request when CI is blank")
    void testCreate_CIBlank() {
        // Arrange
        AddHealthUserDTO dto = new AddHealthUserDTO();
        dto.setCi("   ");

        // Act
        Response response = resource.create(dto);

        // Assert
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        verifyNoInteractions(ageVerificationService);
        verifyNoInteractions(healthUserRegistrationProducer);
    }

    @Test
    @DisplayName("Should return 400 Bad Request when DTO is null")
    void testCreate_DTONull() {
        // Act
        Response response = resource.create(null);

        // Assert
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        verifyNoInteractions(ageVerificationService);
        verifyNoInteractions(healthUserRegistrationProducer);
    }

    @Test
    @DisplayName("Should return 400 Bad Request when user is not of legal age")
    void testCreate_UserNotOfLegalAge() throws AgeVerificationException {
        // Arrange
        AddHealthUserDTO dto = createValidAddHealthUserDTO();
        when(ageVerificationService.verificarMayorDeEdad("12345678")).thenReturn(false);

        // Act
        Response response = resource.create(dto);

        // Assert
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity().toString().contains("mayor de edad"));
        verify(ageVerificationService, times(1)).verificarMayorDeEdad("12345678");
        verify(healthUserRegistrationProducer, never()).enqueue(any());
    }

    @Test
    @DisplayName("Should return 400 Bad Request when CI does not exist in PDI")
    void testCreate_CINotFoundInPDI() throws AgeVerificationException {
        // Arrange
        AddHealthUserDTO dto = createValidAddHealthUserDTO();
        when(ageVerificationService.verificarMayorDeEdad("12345678"))
                .thenThrow(new AgeVerificationException("Persona inexistente"));

        // Act
        Response response = resource.create(dto);

        // Assert
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity().toString().contains("Persona inexistente"));
        verify(ageVerificationService, times(1)).verificarMayorDeEdad("12345678");
        verify(healthUserRegistrationProducer, never()).enqueue(any());
    }

    @Test
    @DisplayName("Should return 400 Bad Request when PDI service returns error")
    void testCreate_PDIServiceError() throws AgeVerificationException {
        // Arrange
        AddHealthUserDTO dto = createValidAddHealthUserDTO();
        when(ageVerificationService.verificarMayorDeEdad("12345678"))
                .thenThrow(new AgeVerificationException("Error al consultar servicio PDI: Timeout"));

        // Act
        Response response = resource.create(dto);

        // Assert
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity().toString().contains("Error al consultar servicio PDI"));
        verify(ageVerificationService, times(1)).verificarMayorDeEdad("12345678");
        verify(healthUserRegistrationProducer, never()).enqueue(any());
    }

    @Test
    @DisplayName("Should return 400 Bad Request when CI is cancelled in PDI")
    void testCreate_CICancelled() throws AgeVerificationException {
        // Arrange
        AddHealthUserDTO dto = createValidAddHealthUserDTO();
        when(ageVerificationService.verificarMayorDeEdad("11111111"))
                .thenThrow(new AgeVerificationException("Número de cédula anulado"));

        dto.setCi("11111111");

        // Act
        Response response = resource.create(dto);

        // Assert
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity().toString().contains("anulado"));
        verify(ageVerificationService, times(1)).verificarMayorDeEdad("11111111");
        verify(healthUserRegistrationProducer, never()).enqueue(any());
    }

    private AddHealthUserDTO createValidAddHealthUserDTO() {
        AddHealthUserDTO dto = new AddHealthUserDTO();
        dto.setCi("12345678");
        dto.setFirstName("Juan");
        dto.setLastName("Perez");
        dto.setGender(Gender.MALE);
        dto.setEmail("juan.perez@example.com");
        dto.setDateOfBirth(LocalDate.of(1990, 1, 1));
        dto.setClinicNames(Set.of("Clinic A"));
        return dto;
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
