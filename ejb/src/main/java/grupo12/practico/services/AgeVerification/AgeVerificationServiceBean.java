package grupo12.practico.services.AgeVerification;

import grupo12.practico.integration.pdi.PdiServiceClientLocal;
import grupo12.practico.integration.pdi.PdiServiceException;
import grupo12.practico.integration.pdi.PersonaInfo;
import jakarta.ejb.EJB;
import jakarta.ejb.Local;
import jakarta.ejb.Stateless;
import java.time.LocalDate;
import java.time.Period;
import java.util.logging.Logger;
import java.util.logging.Level;

@Stateless
@Local(AgeVerificationServiceLocal.class)
public class AgeVerificationServiceBean implements AgeVerificationServiceLocal {

    private static final Logger logger = Logger.getLogger(AgeVerificationServiceBean.class.getName());
    private static final int EDAD_MINIMA = 18;

    @EJB
    private PdiServiceClientLocal pdiServiceClient;

    @Override
    public boolean verificarMayorDeEdad(String ci) throws AgeVerificationException {
        if (ci == null || ci.trim().isEmpty()) {
            throw new AgeVerificationException("CI no puede ser nulo o vacío");
        }

        try {
            logger.info("Verificando edad para CI: " + ci);
            
            // Consultar servicio PDI
            PersonaInfo personaInfo = pdiServiceClient.obtenerPersonaPorDoc(ci, "CI");
            
            if (personaInfo == null) {
                throw new AgeVerificationException("No se pudo obtener información de la persona con CI: " + ci);
            }

            LocalDate fechaNacimiento = personaInfo.getFechaNacimiento();
            if (fechaNacimiento == null) {
                throw new AgeVerificationException("No se pudo obtener la fecha de nacimiento para CI: " + ci);
            }

            // Calcular edad
            LocalDate hoy = LocalDate.now();
            Period periodo = Period.between(fechaNacimiento, hoy);
            int edad = periodo.getYears();

            logger.info("CI: " + ci + ", Fecha nacimiento: " + fechaNacimiento + ", Edad: " + edad);

            // Verificar si es mayor de edad
            boolean esMayorDeEdad = edad >= EDAD_MINIMA;
            
            if (!esMayorDeEdad) {
                logger.warning("Usuario con CI " + ci + " no es mayor de edad (edad: " + edad + ")");
            }

            return esMayorDeEdad;

        } catch (PdiServiceException e) {
            logger.log(Level.SEVERE, "Error al consultar servicio PDI para CI: " + ci, e);
            throw new AgeVerificationException("Error al consultar servicio PDI: " + e.getMessage(), e);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error inesperado al verificar edad para CI: " + ci, e);
            throw new AgeVerificationException("Error inesperado al verificar edad: " + e.getMessage(), e);
        }
    }
}

