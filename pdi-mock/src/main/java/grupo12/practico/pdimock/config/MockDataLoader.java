package grupo12.practico.pdimock.config;

import grupo12.practico.pdimock.dto.ObjPersona;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class MockDataLoader {

    private static final Logger logger = LoggerFactory.getLogger(MockDataLoader.class);
    private static final String MOCK_DATA_FILE = "/mock-data.properties";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MMdd");

    private final Map<String, ObjPersona> personaData = new HashMap<>();
    private final Map<String, Integer> errorCodes = new HashMap<>(); // CI -> error code
    private String validOrganizacion = "ORGANISMO_TEST";
    private String validPasswordEntidad = "SECRETO_TEST";

    public MockDataLoader() {
        loadMockData();
    }

    private void loadMockData() {
        try (InputStream is = getClass().getResourceAsStream(MOCK_DATA_FILE)) {
            if (is == null) {
                logger.warn("Mock data file not found, using default test data");
                loadDefaultData();
                return;
            }

            Properties props = new Properties();
            props.load(is);

            // Load authentication credentials
            validOrganizacion = props.getProperty("auth.organizacion", validOrganizacion);
            validPasswordEntidad = props.getProperty("auth.password", validPasswordEntidad);

            // Load person data
            int index = 1;
            while (true) {
                String ci = props.getProperty("person." + index + ".ci");
                if (ci == null) break;

                String errorCode = props.getProperty("person." + index + ".error");
                if (errorCode != null) {
                    errorCodes.put(ci, Integer.parseInt(errorCode));
                    index++;
                    continue;
                }

                ObjPersona persona = new ObjPersona();
                persona.setCodTipoDocumento(props.getProperty("person." + index + ".tipoDocumento", "CI"));
                persona.setNroDocumento(ci);
                persona.setNombre1(props.getProperty("person." + index + ".nombre1", ""));
                persona.setNombre2(props.getProperty("person." + index + ".nombre2", ""));
                persona.setApellido1(props.getProperty("person." + index + ".apellido1", ""));
                persona.setApellido2(props.getProperty("person." + index + ".apellido2", ""));
                persona.setApellidoAdoptivo1(props.getProperty("person." + index + ".apellidoAdoptivo1", ""));
                persona.setApellidoAdoptivo2(props.getProperty("person." + index + ".apellidoAdoptivo2", ""));
                
                String sexo = props.getProperty("person." + index + ".sexo", "1");
                persona.setSexo(Integer.parseInt(sexo));
                
                String fechaNac = props.getProperty("person." + index + ".fechaNacimiento", "");
                persona.setFechaNacimiento(fechaNac);
                
                String nacionalidad = props.getProperty("person." + index + ".nacionalidad", "1");
                persona.setCodNacionalidad(Integer.parseInt(nacionalidad));
                
                String nombreEnCedula = props.getProperty("person." + index + ".nombreEnCedula", "");
                persona.setNombreEnCedula(nombreEnCedula);

                personaData.put(ci, persona);
                index++;
            }

            logger.info("Loaded {} person records and {} error codes", personaData.size(), errorCodes.size());

        } catch (Exception e) {
            logger.error("Error loading mock data, using defaults", e);
            loadDefaultData();
        }
    }

    private void loadDefaultData() {
        // CI válida - mayor de edad
        ObjPersona persona1 = new ObjPersona();
        persona1.setCodTipoDocumento("CI");
        persona1.setNroDocumento("12345678");
        persona1.setNombre1("JUAN");
        persona1.setNombre2("PABLO");
        persona1.setApellido1("PEREZ");
        persona1.setApellido2("GONZALEZ");
        persona1.setSexo(1);
        persona1.setFechaNacimiento("1980-0503");
        persona1.setCodNacionalidad(1);
        persona1.setNombreEnCedula("JUAN PABLO PEREZ GONZALEZ");
        personaData.put("12345678", persona1);

        // CI válida - menor de edad
        ObjPersona persona2 = new ObjPersona();
        persona2.setCodTipoDocumento("CI");
        persona2.setNroDocumento("87654321");
        persona2.setNombre1("MARIA");
        persona2.setNombre2("ANA");
        persona2.setApellido1("LOPEZ");
        persona2.setApellido2("SILVA");
        persona2.setSexo(2);
        LocalDate fechaMenor = LocalDate.now().minusYears(15);
        persona2.setFechaNacimiento(fechaMenor.format(DATE_FORMATTER));
        persona2.setCodNacionalidad(1);
        persona2.setNombreEnCedula("MARIA ANA LOPEZ SILVA");
        personaData.put("87654321", persona2);

        // CI anulada
        errorCodes.put("11111111", 1003);

        logger.info("Loaded default mock data");
    }

    public ObjPersona getPersona(String ci) {
        return personaData.get(ci);
    }

    public Integer getErrorCode(String ci) {
        return errorCodes.get(ci);
    }

    public boolean isValidAuth(String organizacion, String password) {
        return validOrganizacion.equals(organizacion) && validPasswordEntidad.equals(password);
    }
}

