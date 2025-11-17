package grupo12.practico.services.AgeVerification;

import jakarta.ejb.Local;

@Local
public interface AgeVerificationServiceLocal {

    /**
     * Verifica que una persona sea mayor de edad (≥18 años) consultando el servicio PDI
     * @param ci Número de cédula de identidad
     * @return true si la persona es mayor de edad, false en caso contrario
     * @throws AgeVerificationException Si ocurre un error al verificar la edad
     */
    boolean verificarMayorDeEdad(String ci) throws AgeVerificationException;
}

