package grupo12.practico.integration.pdi;

import jakarta.ejb.Local;

@Local
public interface PdiServiceClientLocal {

    /**
     * Obtiene información de una persona por su documento de identidad
     * @param ci Número de cédula (solo dígitos)
     * @param tipoDocumento Tipo de documento (CI o DO)
     * @return Información de la persona
     * @throws PdiServiceException Si ocurre un error al consultar el servicio
     */
    PersonaInfo obtenerPersonaPorDoc(String ci, String tipoDocumento) throws PdiServiceException;
}

