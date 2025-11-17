package grupo12.practico.pdimock;

import grupo12.practico.pdimock.config.MockDataLoader;
import grupo12.practico.pdimock.dto.*;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebResult;
import jakarta.jws.WebService;
import jakarta.xml.ws.BindingType;
import jakarta.xml.ws.soap.SOAPBinding;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebService(
    name = "wsServicioDeInformacionBasicoSoap",
    targetNamespace = "http://wsDNIC/",
    serviceName = "wsServicioDeInformacionBasico",
    portName = "wsServicioDeInformacionBasicoSoap"
)
@BindingType(SOAPBinding.SOAP11HTTP_BINDING)
public class PdiMockService {

    private static final Logger logger = LoggerFactory.getLogger(PdiMockService.class);
    private final MockDataLoader mockDataLoader;

    public PdiMockService() {
        this.mockDataLoader = new MockDataLoader();
    }

    @WebMethod(operationName = "ObtPersonaPorDoc", action = "http://wsDNIC/ObtPersonaPorDoc")
    @WebResult(name = "ObtPersonaPorDocResponse", targetNamespace = "http://wsDNIC/")
    public ObtPersonaPorDocResponse obtPersonaPorDoc(
            @WebParam(name = "ObtPersonaPorDoc", targetNamespace = "http://wsDNIC/")
            ObtPersonaPorDocRequest request) {

        logger.info("Received ObtPersonaPorDoc request");

        ObtPersonaPorDocResponse response = new ObtPersonaPorDocResponse();
        ObtPersonaPorDocResult result = new ObtPersonaPorDocResult();
        response.setObtPersonaPorDocResult(result);

        if (request == null || request.getParamObtPersonaPorDoc() == null) {
            addError(result, 10001, "Parámetros incorrectos: request o paramObtPersonaPorDoc es nulo");
            return response;
        }

        ObjParamObtPersonaPorDoc params = request.getParamObtPersonaPorDoc();

        // Validar autenticación
        String organizacion = params.getOrganizacion();
        String password = params.getPasswordEntidad();
        if (!mockDataLoader.isValidAuth(organizacion, password)) {
            logger.warn("Invalid authentication: organizacion={}", organizacion);
            addError(result, 10002, "Acceso No Autorizado");
            return response;
        }

        // Validar NroDocumento
        String nroDocumento = params.getNrodocumento();
        if (nroDocumento == null || nroDocumento.trim().isEmpty()) {
            addError(result, 10001, "Parámetros incorrectos: NroDocumento es requerido");
            return response;
        }

        // Validar formato de CI (solo dígitos)
        String ciNormalized = nroDocumento.trim().replaceAll("[^0-9]", "");
        if (!ciNormalized.matches("^[0-9]{7,8}$")) {
            logger.warn("Invalid CI format: {}", nroDocumento);
            addError(result, 10001, "Parámetros incorrectos: formato de CI inválido");
            return response;
        }

        // Validar TipoDocumento
        String tipoDocumento = params.getTipoDocumento();
        if (tipoDocumento == null || tipoDocumento.trim().isEmpty()) {
            tipoDocumento = "CI";
        }
        if (!tipoDocumento.equals("CI") && !tipoDocumento.equals("DO")) {
            addError(result, 10001, "Parámetros incorrectos: TipoDocumento debe ser CI o DO");
            return response;
        }

        // Verificar si hay un error específico para esta CI
        Integer errorCode = mockDataLoader.getErrorCode(ciNormalized);
        if (errorCode != null) {
            String errorMessage = getErrorMessage(errorCode);
            addError(result, errorCode, errorMessage);
            return response;
        }

        // Buscar persona
        ObjPersona persona = mockDataLoader.getPersona(ciNormalized);
        if (persona == null) {
            logger.info("Person not found for CI: {}", ciNormalized);
            addError(result, 500, "Persona inexistente");
            return response;
        }

        // Respuesta exitosa
        result.setObjPersona(persona);
        logger.info("Successfully retrieved person data for CI: {}", ciNormalized);
        return response;
    }

    @WebMethod(operationName = "ProductDesc", action = "http://wsDNIC/ProductDesc")
    @WebResult(name = "ProductDescResponse", targetNamespace = "http://wsDNIC/")
    public ProductDescResponse productDesc() {
        logger.info("Received ProductDesc request");

        ProductDescResponse response = new ProductDescResponse();
        ProductDescResponse.ObtProductInfo productInfo = new ProductDescResponse.ObtProductInfo();
        productInfo.setModalidad("Testing");
        productInfo.setVersion("1.0");
        productInfo.setDescripcion("Servicio de Información D.N.I.C.");
        response.setProductDescResult(productInfo);

        return response;
    }

    private void addError(ObtPersonaPorDocResult result, int codMensaje, String descripcion) {
        Mensaje error = new Mensaje(codMensaje, descripcion, "");
        result.getErrores().getMensaje().add(error);
    }

    private String getErrorMessage(int errorCode) {
        return switch (errorCode) {
            case 500 -> "Persona inexistente";
            case 701 -> "Datos de persona a regularizar";
            case 1001 -> "No se pudo completar la consulta";
            case 1002 -> "Límite de consultas excedido";
            case 1003 -> "Número de cédula anulado";
            case 10001 -> "Parámetros incorrectos";
            case 10002 -> "Acceso No Autorizado";
            default -> "Error desconocido";
        };
    }
}

