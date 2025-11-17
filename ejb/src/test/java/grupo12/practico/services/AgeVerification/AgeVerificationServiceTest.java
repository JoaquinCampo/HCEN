package grupo12.practico.services.AgeVerification;

import grupo12.practico.integration.pdi.PdiServiceClientLocal;
import grupo12.practico.integration.pdi.PdiServiceException;
import grupo12.practico.integration.pdi.PersonaInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AgeVerificationServiceTest {

    @Mock
    private PdiServiceClientLocal pdiServiceClient;

    @InjectMocks
    private AgeVerificationServiceBean ageVerificationService;

    @Test
    void testVerificarMayorDeEdad_UsuarioMayorDeEdad_RetornaTrue() throws Exception {
        // Arrange
        String ci = "12345678";
        LocalDate fechaNacimiento = LocalDate.now().minusYears(25);
        PersonaInfo personaInfo = new PersonaInfo(ci, "Juan Perez", fechaNacimiento, 1, 1);
        
        when(pdiServiceClient.obtenerPersonaPorDoc(ci, "CI")).thenReturn(personaInfo);

        // Act
        boolean resultado = ageVerificationService.verificarMayorDeEdad(ci);

        // Assert
        assertTrue(resultado);
        verify(pdiServiceClient, times(1)).obtenerPersonaPorDoc(ci, "CI");
    }

    @Test
    void testVerificarMayorDeEdad_UsuarioMenorDeEdad_RetornaFalse() throws Exception {
        // Arrange
        String ci = "87654321";
        LocalDate fechaNacimiento = LocalDate.now().minusYears(15);
        PersonaInfo personaInfo = new PersonaInfo(ci, "Maria Lopez", fechaNacimiento, 2, 1);
        
        when(pdiServiceClient.obtenerPersonaPorDoc(ci, "CI")).thenReturn(personaInfo);

        // Act
        boolean resultado = ageVerificationService.verificarMayorDeEdad(ci);

        // Assert
        assertFalse(resultado);
        verify(pdiServiceClient, times(1)).obtenerPersonaPorDoc(ci, "CI");
    }

    @Test
    void testVerificarMayorDeEdad_UsuarioExactamente18Anios_RetornaTrue() throws Exception {
        // Arrange
        String ci = "11111111";
        LocalDate fechaNacimiento = LocalDate.now().minusYears(18);
        PersonaInfo personaInfo = new PersonaInfo(ci, "Pedro Silva", fechaNacimiento, 1, 1);
        
        when(pdiServiceClient.obtenerPersonaPorDoc(ci, "CI")).thenReturn(personaInfo);

        // Act
        boolean resultado = ageVerificationService.verificarMayorDeEdad(ci);

        // Assert
        assertTrue(resultado);
    }

    @Test
    void testVerificarMayorDeEdad_CINulo_LanzaExcepcion() {
        // Act & Assert
        assertThrows(AgeVerificationException.class, () -> {
            ageVerificationService.verificarMayorDeEdad(null);
        });
    }

    @Test
    void testVerificarMayorDeEdad_ErrorPDI_LanzaExcepcion() throws Exception {
        // Arrange
        String ci = "99999999";
        when(pdiServiceClient.obtenerPersonaPorDoc(ci, "CI"))
                .thenThrow(new PdiServiceException("Persona inexistente", 500));

        // Act & Assert
        assertThrows(AgeVerificationException.class, () -> {
            ageVerificationService.verificarMayorDeEdad(ci);
        });
    }

    @Test
    void testVerificarMayorDeEdad_FechaNacimientoNula_LanzaExcepcion() throws Exception {
        // Arrange
        String ci = "12345678";
        PersonaInfo personaInfo = new PersonaInfo(ci, "Juan Perez", null, 1, 1);
        
        when(pdiServiceClient.obtenerPersonaPorDoc(ci, "CI")).thenReturn(personaInfo);

        // Act & Assert
        assertThrows(AgeVerificationException.class, () -> {
            ageVerificationService.verificarMayorDeEdad(ci);
        });
    }
}

