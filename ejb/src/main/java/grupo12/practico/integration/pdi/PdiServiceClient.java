package grupo12.practico.integration.pdi;

import grupo12.practico.integration.pdi.dto.*;
import jakarta.ejb.Stateless;
import jakarta.ejb.Local;
import jakarta.xml.ws.Service;
import jakarta.xml.ws.WebServiceException;
import javax.xml.namespace.QName;
import java.net.URL;
import java.time.LocalDate;
import java.util.logging.Logger;
import java.util.logging.Level;

@Stateless
@Local(PdiServiceClientLocal.class)
public class PdiServiceClient implements PdiServiceClientLocal {

    private static final Logger logger = Logger.getLogger(PdiServiceClient.class.getName());
    private static final String DEFAULT_PDI_URL = "http://localhost:8080/wsServicioDeInformacionBasico";
    private static final String PDI_URL_PROPERTY = "pdi.service.url";
    private static final String PDI_ORGANIZACION_PROPERTY = "pdi.organizacion";
    private static final String PDI_PASSWORD_PROPERTY = "pdi.password";
    private static final String DEFAULT_ORGANIZACION = "ORGANISMO_TEST";
    private static final String DEFAULT_PASSWORD = "SECRETO_TEST";

    private String pdiUrl;
    private String organizacion;
    private String password;

    public PdiServiceClient() {
        loadConfiguration();
    }

    private void loadConfiguration() {
        // Cargar desde properties file primero
        java.util.Properties props = new java.util.Properties();
        try (java.io.InputStream is = getClass().getClassLoader()
                .getResourceAsStream("META-INF/pdi-service.properties")) {
            if (is != null) {
                props.load(is);
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, "Could not load pdi-service.properties, using defaults", e);
        }

        // Prioridad: System Property > Environment Variable > Properties File > Default
        pdiUrl = System.getProperty(PDI_URL_PROPERTY,
            System.getenv("PDI_SERVICE_URL") != null ? System.getenv("PDI_SERVICE_URL") :
            props.getProperty("pdi.service.url", DEFAULT_PDI_URL));
        
        organizacion = System.getProperty(PDI_ORGANIZACION_PROPERTY,
            System.getenv("PDI_ORGANIZACION") != null ? System.getenv("PDI_ORGANIZACION") :
            props.getProperty("pdi.organizacion", DEFAULT_ORGANIZACION));
        
        password = System.getProperty(PDI_PASSWORD_PROPERTY,
            System.getenv("PDI_PASSWORD") != null ? System.getenv("PDI_PASSWORD") :
            props.getProperty("pdi.password", DEFAULT_PASSWORD));
        
        logger.info("PDI Service Client configured - URL: " + pdiUrl + ", Organizacion: " + organizacion);
    }

    @Override
    public PersonaInfo obtenerPersonaPorDoc(String ci, String tipoDocumento) throws PdiServiceException {
        if (ci == null || ci.trim().isEmpty()) {
            throw new PdiServiceException("CI no puede ser nulo o vacío", 10001);
        }

        if (tipoDocumento == null || tipoDocumento.trim().isEmpty()) {
            tipoDocumento = "CI";
        }

        // Normalizar CI (solo dígitos)
        String ciNormalized = ci.trim().replaceAll("[^0-9]", "");
        if (!ciNormalized.matches("^[0-9]{7,8}$")) {
            throw new PdiServiceException("Formato de CI inválido", 10001);
        }

        try {
            // Crear servicio SOAP dinámico
            URL wsdlUrl = new URL(pdiUrl + "?wsdl");
            QName serviceName = new QName("http://wsDNIC/", "wsServicioDeInformacionBasico");
            QName portName = new QName("http://wsDNIC/", "wsServicioDeInformacionBasicoSoap");

            Service service = Service.create(wsdlUrl, serviceName);
            Object port = service.getPort(portName, Object.class);

            // Crear request
            ObtPersonaPorDocRequest request = createRequest(ciNormalized, tipoDocumento);
            
            // Invocar método usando reflection (necesario porque el port es Object)
            java.lang.reflect.Method method = port.getClass().getMethod("obtPersonaPorDoc", ObtPersonaPorDocRequest.class);
            ObtPersonaPorDocResponse response = (ObtPersonaPorDocResponse) method.invoke(port, request);

            // Procesar respuesta
            return processResponse(response, ciNormalized);

        } catch (java.net.MalformedURLException e) {
            logger.log(Level.SEVERE, "Invalid PDI service URL: " + pdiUrl, e);
            throw new PdiServiceException("URL del servicio PDI inválida: " + pdiUrl, e);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error calling PDI service", e);
            Throwable cause = e.getCause();
            if (cause instanceof WebServiceException) {
                throw new PdiServiceException("Error de comunicación con servicio PDI: " + cause.getMessage(), cause);
            }
            throw new PdiServiceException("Error al consultar servicio PDI: " + e.getMessage(), e);
        }
    }

    private ObtPersonaPorDocRequest createRequest(String ci, String tipoDocumento) {
        ObjParamObtPersonaPorDoc param = new ObjParamObtPersonaPorDoc();
        param.setOrganizacion(organizacion);
        param.setPasswordEntidad(password);
        param.setNrodocumento(ci);
        param.setTipoDocumento(tipoDocumento);

        ObtPersonaPorDocRequest request = new ObtPersonaPorDocRequest();
        request.setParamObtPersonaPorDoc(param);

        return request;
    }

    private PersonaInfo processResponse(ObtPersonaPorDocResponse response, String ci) throws PdiServiceException {
        ObtPersonaPorDocResult result = response.getObtPersonaPorDocResult();
        
        // Verificar errores
        if (result.getErrores() != null && result.getErrores().getMensaje() != null 
                && !result.getErrores().getMensaje().isEmpty()) {
            Mensaje mensaje = result.getErrores().getMensaje().get(0);
            throw new PdiServiceException(mensaje.getDescripcion(), mensaje.getCodMensaje());
        }

        // Obtener datos de la persona
        ObjPersona objPersona = result.getObjPersona();
        if (objPersona == null) {
            throw new PdiServiceException("Persona inexistente", 500);
        }

        // Extraer información
        String fechaNacStr = objPersona.getFechaNacimiento();
        LocalDate fechaNacimiento = parseFechaNacimiento(fechaNacStr);

        String nombre1 = objPersona.getNombre1();
        String nombre2 = objPersona.getNombre2();
        String apellido1 = objPersona.getApellido1();
        String apellido2 = objPersona.getApellido2();

        StringBuilder nombreCompleto = new StringBuilder();
        if (nombre1 != null) nombreCompleto.append(nombre1);
        if (nombre2 != null && !nombre2.isEmpty()) nombreCompleto.append(" ").append(nombre2);
        if (apellido1 != null) nombreCompleto.append(" ").append(apellido1);
        if (apellido2 != null && !apellido2.isEmpty()) nombreCompleto.append(" ").append(apellido2);

        return new PersonaInfo(ci, nombreCompleto.toString().trim(), fechaNacimiento, 
                objPersona.getSexo(), objPersona.getCodNacionalidad());
    }

    private LocalDate parseFechaNacimiento(String fechaStr) {
        if (fechaStr == null || fechaStr.trim().isEmpty()) {
            return null;
        }

        try {
            // Formato: yyyy-MMdd (ej: "1980-0503")
            String normalized = fechaStr.trim();
            if (normalized.length() == 8) {
                // yyyyMMdd
                int year = Integer.parseInt(normalized.substring(0, 4));
                int month = Integer.parseInt(normalized.substring(4, 6));
                int day = Integer.parseInt(normalized.substring(6, 8));
                return LocalDate.of(year, month, day);
            } else if (normalized.length() == 9 && normalized.charAt(4) == '-') {
                // yyyy-MMdd
                int year = Integer.parseInt(normalized.substring(0, 4));
                int month = Integer.parseInt(normalized.substring(5, 7));
                int day = Integer.parseInt(normalized.substring(7, 9));
                return LocalDate.of(year, month, day);
            } else if (normalized.length() == 6) {
                // yyyyMM (solo año y mes)
                int year = Integer.parseInt(normalized.substring(0, 4));
                int month = Integer.parseInt(normalized.substring(4, 6));
                return LocalDate.of(year, month, 1);
            } else if (normalized.length() == 4) {
                // yyyy (solo año)
                int year = Integer.parseInt(normalized);
                return LocalDate.of(year, 1, 1);
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error parsing fecha nacimiento: " + fechaStr, e);
        }

        return null;
    }
}

