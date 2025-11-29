package grupo12.practico.integration.pdi;

import grupo12.practico.integration.pdi.dto.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.time.LocalDate;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("PdiServiceClient Tests")
class PdiServiceClientTest {

    private PdiServiceClient client;

    @BeforeEach
    void setUp() {
        client = new PdiServiceClient();
    }

    @Nested
    @DisplayName("Input Validation Tests - obtenerPersonaPorDoc")
    class InputValidationTests {

        @Test
        @DisplayName("Should throw exception when CI is null")
        void shouldThrowExceptionWhenCiIsNull() {
            PdiServiceException exception = assertThrows(PdiServiceException.class, () -> {
                client.obtenerPersonaPorDoc(null, "CI");
            });

            assertEquals("CI no puede ser nulo o vacío", exception.getMessage());
            assertEquals(10001, exception.getErrorCode());
        }

        @Test
        @DisplayName("Should throw exception when CI is empty")
        void shouldThrowExceptionWhenCiIsEmpty() {
            PdiServiceException exception = assertThrows(PdiServiceException.class, () -> {
                client.obtenerPersonaPorDoc("", "CI");
            });

            assertEquals("CI no puede ser nulo o vacío", exception.getMessage());
            assertEquals(10001, exception.getErrorCode());
        }

        @Test
        @DisplayName("Should throw exception when CI is blank")
        void shouldThrowExceptionWhenCiIsBlank() {
            PdiServiceException exception = assertThrows(PdiServiceException.class, () -> {
                client.obtenerPersonaPorDoc("   ", "CI");
            });

            assertEquals("CI no puede ser nulo o vacío", exception.getMessage());
            assertEquals(10001, exception.getErrorCode());
        }

        @Test
        @DisplayName("Should throw exception when CI has invalid format - too short")
        void shouldThrowExceptionWhenCiIsTooShort() {
            PdiServiceException exception = assertThrows(PdiServiceException.class, () -> {
                client.obtenerPersonaPorDoc("123456", "CI"); // 6 digits, needs 7-8
            });

            assertEquals("Formato de CI inválido", exception.getMessage());
            assertEquals(10001, exception.getErrorCode());
        }

        @Test
        @DisplayName("Should throw exception when CI has invalid format - too long")
        void shouldThrowExceptionWhenCiIsTooLong() {
            PdiServiceException exception = assertThrows(PdiServiceException.class, () -> {
                client.obtenerPersonaPorDoc("123456789", "CI"); // 9 digits, needs 7-8
            });

            assertEquals("Formato de CI inválido", exception.getMessage());
            assertEquals(10001, exception.getErrorCode());
        }

        @Test
        @DisplayName("Should throw exception when CI contains only non-digit characters")
        void shouldThrowExceptionWhenCiContainsOnlyNonDigits() {
            PdiServiceException exception = assertThrows(PdiServiceException.class, () -> {
                client.obtenerPersonaPorDoc("abcdefgh", "CI");
            });

            assertEquals("Formato de CI inválido", exception.getMessage());
            assertEquals(10001, exception.getErrorCode());
        }

        @Test
        @DisplayName("Should normalize CI by removing non-digit characters - dashes")
        void shouldNormalizeCiWithDashes() throws Exception {
            // This will fail on the SOAP call but will pass CI validation
            // The CI "1234-5678" should normalize to "12345678"
            try {
                client.obtenerPersonaPorDoc("1234-5678", "CI");
            } catch (PdiServiceException e) {
                // Should NOT be "Formato de CI inválido" - CI is valid after normalization
                assertNotEquals("Formato de CI inválido", e.getMessage());
            }
        }

        @Test
        @DisplayName("Should normalize CI by removing non-digit characters - dots")
        void shouldNormalizeCiWithDots() throws Exception {
            // This will fail on the SOAP call but will pass CI validation
            // The CI "1.234.567-8" should normalize to "12345678"
            try {
                client.obtenerPersonaPorDoc("1.234.567-8", "CI");
            } catch (PdiServiceException e) {
                // Should NOT be "Formato de CI inválido"
                assertNotEquals("Formato de CI inválido", e.getMessage());
            }
        }

        @Test
        @DisplayName("Should accept 7-digit CI")
        void shouldAccept7DigitCi() throws Exception {
            try {
                client.obtenerPersonaPorDoc("1234567", "CI");
            } catch (PdiServiceException e) {
                // Should NOT be "Formato de CI inválido"
                assertNotEquals("Formato de CI inválido", e.getMessage());
            }
        }

        @Test
        @DisplayName("Should accept 8-digit CI")
        void shouldAccept8DigitCi() throws Exception {
            try {
                client.obtenerPersonaPorDoc("12345678", "CI");
            } catch (PdiServiceException e) {
                // Should NOT be "Formato de CI inválido"
                assertNotEquals("Formato de CI inválido", e.getMessage());
            }
        }

        @Test
        @DisplayName("Should default tipoDocumento to CI when null")
        void shouldDefaultTipoDocumentoWhenNull() throws Exception {
            // This will fail on SOAP call but should pass validation with null
            // tipoDocumento
            try {
                client.obtenerPersonaPorDoc("12345678", null);
            } catch (PdiServiceException e) {
                // Should NOT be related to tipoDocumento
                assertFalse(e.getMessage().toLowerCase().contains("tipo"));
            }
        }

        @Test
        @DisplayName("Should default tipoDocumento to CI when empty")
        void shouldDefaultTipoDocumentoWhenEmpty() throws Exception {
            try {
                client.obtenerPersonaPorDoc("12345678", "");
            } catch (PdiServiceException e) {
                // Should NOT be related to tipoDocumento
                assertFalse(e.getMessage().toLowerCase().contains("tipo"));
            }
        }
    }

    @Nested
    @DisplayName("Date Parsing Tests - parseFechaNacimiento")
    class DateParsingTests {

        private LocalDate invokeParseFechaNacimiento(String fechaStr) throws Exception {
            Method method = PdiServiceClient.class.getDeclaredMethod("parseFechaNacimiento", String.class);
            method.setAccessible(true);
            return (LocalDate) method.invoke(client, fechaStr);
        }

        @Test
        @DisplayName("Should parse yyyyMMdd format")
        void shouldParseYyyyMmDdFormat() throws Exception {
            LocalDate result = invokeParseFechaNacimiento("19900515");

            assertEquals(LocalDate.of(1990, 5, 15), result);
        }

        @Test
        @DisplayName("Should parse yyyy-MMdd format")
        void shouldParseYyyyDashMmDdFormat() throws Exception {
            LocalDate result = invokeParseFechaNacimiento("1990-0515");

            assertEquals(LocalDate.of(1990, 5, 15), result);
        }

        @Test
        @DisplayName("Should parse yyyyMM format (year and month only)")
        void shouldParseYyyyMmFormat() throws Exception {
            LocalDate result = invokeParseFechaNacimiento("199005");

            assertEquals(LocalDate.of(1990, 5, 1), result);
        }

        @Test
        @DisplayName("Should parse yyyy format (year only)")
        void shouldParseYyyyFormat() throws Exception {
            LocalDate result = invokeParseFechaNacimiento("1990");

            assertEquals(LocalDate.of(1990, 1, 1), result);
        }

        @Test
        @DisplayName("Should return null for null input")
        void shouldReturnNullForNullInput() throws Exception {
            LocalDate result = invokeParseFechaNacimiento(null);

            assertNull(result);
        }

        @Test
        @DisplayName("Should return null for empty string")
        void shouldReturnNullForEmptyString() throws Exception {
            LocalDate result = invokeParseFechaNacimiento("");

            assertNull(result);
        }

        @Test
        @DisplayName("Should return null for whitespace string")
        void shouldReturnNullForWhitespaceString() throws Exception {
            LocalDate result = invokeParseFechaNacimiento("   ");

            assertNull(result);
        }

        @Test
        @DisplayName("Should return null for invalid format")
        void shouldReturnNullForInvalidFormat() throws Exception {
            LocalDate result = invokeParseFechaNacimiento("15/05/1990");

            assertNull(result);
        }

        @Test
        @DisplayName("Should return null for invalid date values")
        void shouldReturnNullForInvalidDateValues() throws Exception {
            // Invalid month (13)
            LocalDate result = invokeParseFechaNacimiento("19901315");

            assertNull(result);
        }

        @Test
        @DisplayName("Should trim whitespace from input")
        void shouldTrimWhitespaceFromInput() throws Exception {
            LocalDate result = invokeParseFechaNacimiento("  19900515  ");

            assertEquals(LocalDate.of(1990, 5, 15), result);
        }

        @Test
        @DisplayName("Should parse date at beginning of year")
        void shouldParseDateAtBeginningOfYear() throws Exception {
            LocalDate result = invokeParseFechaNacimiento("19900101");

            assertEquals(LocalDate.of(1990, 1, 1), result);
        }

        @Test
        @DisplayName("Should parse date at end of year")
        void shouldParseDateAtEndOfYear() throws Exception {
            LocalDate result = invokeParseFechaNacimiento("19901231");

            assertEquals(LocalDate.of(1990, 12, 31), result);
        }

        @Test
        @DisplayName("Should parse leap year date")
        void shouldParseLeapYearDate() throws Exception {
            LocalDate result = invokeParseFechaNacimiento("20000229");

            assertEquals(LocalDate.of(2000, 2, 29), result);
        }

        @Test
        @DisplayName("Should parse historical date")
        void shouldParseHistoricalDate() throws Exception {
            LocalDate result = invokeParseFechaNacimiento("19200315");

            assertEquals(LocalDate.of(1920, 3, 15), result);
        }

        @Test
        @DisplayName("Should return null for string with wrong length")
        void shouldReturnNullForWrongLength() throws Exception {
            // 10 characters - not 4, 6, 8, or 9
            LocalDate result = invokeParseFechaNacimiento("1990-05-15");

            assertNull(result);
        }
    }

    @Nested
    @DisplayName("Response Processing Tests - processResponse")
    class ResponseProcessingTests {

        private PersonaInfo invokeProcessResponse(ObtPersonaPorDocResponse response, String ci) throws Exception {
            Method method = PdiServiceClient.class.getDeclaredMethod("processResponse", ObtPersonaPorDocResponse.class,
                    String.class);
            method.setAccessible(true);
            return (PersonaInfo) method.invoke(client, response, ci);
        }

        @Test
        @DisplayName("Should throw exception when response has errors")
        void shouldThrowExceptionWhenResponseHasErrors() {
            ObtPersonaPorDocResponse response = new ObtPersonaPorDocResponse();
            ObtPersonaPorDocResult result = new ObtPersonaPorDocResult();

            ArrayOfMensaje errores = new ArrayOfMensaje();
            Mensaje errorMsg = new Mensaje(404, "Persona no encontrada", null);
            errores.getMensaje().add(errorMsg);
            result.setErrores(errores);

            response.setObtPersonaPorDocResult(result);

            Exception exception = assertThrows(Exception.class, () -> {
                invokeProcessResponse(response, "12345678");
            });

            // Should be PdiServiceException wrapped in InvocationTargetException
            assertTrue(exception.getCause() instanceof PdiServiceException);
            PdiServiceException pdiException = (PdiServiceException) exception.getCause();
            assertEquals("Persona no encontrada", pdiException.getMessage());
            assertEquals(404, pdiException.getErrorCode());
        }

        @Test
        @DisplayName("Should throw exception when objPersona is null")
        void shouldThrowExceptionWhenObjPersonaIsNull() {
            ObtPersonaPorDocResponse response = new ObtPersonaPorDocResponse();
            ObtPersonaPorDocResult result = new ObtPersonaPorDocResult();
            result.setObjPersona(null);
            result.setErrores(new ArrayOfMensaje()); // Empty errors
            response.setObtPersonaPorDocResult(result);

            Exception exception = assertThrows(Exception.class, () -> {
                invokeProcessResponse(response, "12345678");
            });

            assertTrue(exception.getCause() instanceof PdiServiceException);
            PdiServiceException pdiException = (PdiServiceException) exception.getCause();
            assertEquals("Persona inexistente", pdiException.getMessage());
            assertEquals(500, pdiException.getErrorCode());
        }

        @Test
        @DisplayName("Should process successful response with all fields")
        void shouldProcessSuccessfulResponseWithAllFields() throws Exception {
            ObtPersonaPorDocResponse response = createSuccessResponse(
                    "Juan", "Carlos", "Pérez", "García", "19900515", 1, 858);

            PersonaInfo result = invokeProcessResponse(response, "12345678");

            assertNotNull(result);
            assertEquals("12345678", result.getCi());
            assertEquals("Juan Carlos Pérez García", result.getNombreCompleto());
            assertEquals(LocalDate.of(1990, 5, 15), result.getFechaNacimiento());
            assertEquals(1, result.getSexo());
            assertEquals(858, result.getCodNacionalidad());
        }

        @Test
        @DisplayName("Should process response with only first name and first surname")
        void shouldProcessResponseWithMinimalNames() throws Exception {
            ObtPersonaPorDocResponse response = createSuccessResponse(
                    "María", null, "López", null, "19850101", 2, 858);

            PersonaInfo result = invokeProcessResponse(response, "87654321");

            assertNotNull(result);
            assertEquals("87654321", result.getCi());
            assertEquals("María López", result.getNombreCompleto());
            assertEquals(2, result.getSexo());
        }

        @Test
        @DisplayName("Should process response with empty second name")
        void shouldProcessResponseWithEmptySecondName() throws Exception {
            ObtPersonaPorDocResponse response = createSuccessResponse(
                    "Pedro", "", "Martínez", "Rodríguez", "19750620", 1, 858);

            PersonaInfo result = invokeProcessResponse(response, "11223344");

            assertNotNull(result);
            assertEquals("Pedro Martínez Rodríguez", result.getNombreCompleto());
        }

        @Test
        @DisplayName("Should process response with null birth date")
        void shouldProcessResponseWithNullBirthDate() throws Exception {
            ObtPersonaPorDocResponse response = createSuccessResponse(
                    "Ana", null, "García", null, null, 2, 858);

            PersonaInfo result = invokeProcessResponse(response, "99887766");

            assertNotNull(result);
            assertNull(result.getFechaNacimiento());
        }

        @Test
        @DisplayName("Should handle empty errors array")
        void shouldHandleEmptyErrorsArray() throws Exception {
            ObtPersonaPorDocResponse response = createSuccessResponse(
                    "Luis", null, "Fernández", null, "19800101", 1, 858);

            PersonaInfo result = invokeProcessResponse(response, "55443322");

            assertNotNull(result);
            assertEquals("Luis Fernández", result.getNombreCompleto());
        }

        @Test
        @DisplayName("Should handle null first name gracefully")
        void shouldHandleNullFirstNameGracefully() throws Exception {
            ObtPersonaPorDocResponse response = createSuccessResponse(
                    null, null, "Solo", null, "19950101", 1, 858);

            PersonaInfo result = invokeProcessResponse(response, "12121212");

            assertNotNull(result);
            assertEquals("Solo", result.getNombreCompleto());
        }

        private ObtPersonaPorDocResponse createSuccessResponse(
                String nombre1, String nombre2, String apellido1, String apellido2,
                String fechaNacimiento, Integer sexo, Integer codNacionalidad) {

            ObjPersona persona = new ObjPersona();
            persona.setNombre1(nombre1);
            persona.setNombre2(nombre2);
            persona.setApellido1(apellido1);
            persona.setApellido2(apellido2);
            persona.setFechaNacimiento(fechaNacimiento);
            persona.setSexo(sexo);
            persona.setCodNacionalidad(codNacionalidad);

            ObtPersonaPorDocResult result = new ObtPersonaPorDocResult();
            result.setObjPersona(persona);
            result.setErrores(new ArrayOfMensaje());

            ObtPersonaPorDocResponse response = new ObtPersonaPorDocResponse();
            response.setObtPersonaPorDocResult(result);

            return response;
        }
    }

    @Nested
    @DisplayName("Request Creation Tests - createRequest")
    class RequestCreationTests {

        private ObtPersonaPorDocRequest invokeCreateRequest(String ci, String tipoDocumento) throws Exception {
            Method method = PdiServiceClient.class.getDeclaredMethod("createRequest", String.class, String.class);
            method.setAccessible(true);
            return (ObtPersonaPorDocRequest) method.invoke(client, ci, tipoDocumento);
        }

        @Test
        @DisplayName("Should create request with provided CI and tipoDocumento")
        void shouldCreateRequestWithProvidedValues() throws Exception {
            ObtPersonaPorDocRequest request = invokeCreateRequest("12345678", "CI");

            assertNotNull(request);
            assertNotNull(request.getParamObtPersonaPorDoc());
            assertEquals("12345678", request.getParamObtPersonaPorDoc().getNrodocumento());
            assertEquals("CI", request.getParamObtPersonaPorDoc().getTipoDocumento());
        }

        @Test
        @DisplayName("Should create request with DO document type")
        void shouldCreateRequestWithDoDocumentType() throws Exception {
            ObtPersonaPorDocRequest request = invokeCreateRequest("12345678", "DO");

            assertNotNull(request);
            assertEquals("DO", request.getParamObtPersonaPorDoc().getTipoDocumento());
        }

        @Test
        @DisplayName("Should set organizacion and password from configuration")
        void shouldSetOrganizacionAndPasswordFromConfiguration() throws Exception {
            ObtPersonaPorDocRequest request = invokeCreateRequest("12345678", "CI");

            assertNotNull(request);
            // These should be set from configuration (defaults in this test environment)
            assertNotNull(request.getParamObtPersonaPorDoc().getOrganizacion());
            assertNotNull(request.getParamObtPersonaPorDoc().getPasswordEntidad());
        }
    }

    @Nested
    @DisplayName("Full Name Composition Tests")
    class FullNameCompositionTests {

        private PersonaInfo invokeProcessResponse(ObtPersonaPorDocResponse response, String ci) throws Exception {
            Method method = PdiServiceClient.class.getDeclaredMethod("processResponse", ObtPersonaPorDocResponse.class,
                    String.class);
            method.setAccessible(true);
            return (PersonaInfo) method.invoke(client, response, ci);
        }

        @Test
        @DisplayName("Should compose full name with all parts")
        void shouldComposeFullNameWithAllParts() throws Exception {
            ObtPersonaPorDocResponse response = createResponseWithNames("Juan", "Carlos", "Pérez", "García");

            PersonaInfo result = invokeProcessResponse(response, "12345678");

            assertEquals("Juan Carlos Pérez García", result.getNombreCompleto());
        }

        @Test
        @DisplayName("Should compose full name without second name")
        void shouldComposeFullNameWithoutSecondName() throws Exception {
            ObtPersonaPorDocResponse response = createResponseWithNames("Juan", null, "Pérez", "García");

            PersonaInfo result = invokeProcessResponse(response, "12345678");

            assertEquals("Juan Pérez García", result.getNombreCompleto());
        }

        @Test
        @DisplayName("Should compose full name without second surname")
        void shouldComposeFullNameWithoutSecondSurname() throws Exception {
            ObtPersonaPorDocResponse response = createResponseWithNames("Juan", "Carlos", "Pérez", null);

            PersonaInfo result = invokeProcessResponse(response, "12345678");

            assertEquals("Juan Carlos Pérez", result.getNombreCompleto());
        }

        @Test
        @DisplayName("Should compose full name with only first name and surname")
        void shouldComposeFullNameWithOnlyFirstNameAndSurname() throws Exception {
            ObtPersonaPorDocResponse response = createResponseWithNames("Juan", null, "Pérez", null);

            PersonaInfo result = invokeProcessResponse(response, "12345678");

            assertEquals("Juan Pérez", result.getNombreCompleto());
        }

        @Test
        @DisplayName("Should handle empty second name as not present")
        void shouldHandleEmptySecondNameAsNotPresent() throws Exception {
            ObtPersonaPorDocResponse response = createResponseWithNames("Juan", "", "Pérez", "García");

            PersonaInfo result = invokeProcessResponse(response, "12345678");

            assertEquals("Juan Pérez García", result.getNombreCompleto());
        }

        @Test
        @DisplayName("Should handle empty second surname as not present")
        void shouldHandleEmptySecondSurnameAsNotPresent() throws Exception {
            ObtPersonaPorDocResponse response = createResponseWithNames("Juan", "Carlos", "Pérez", "");

            PersonaInfo result = invokeProcessResponse(response, "12345678");

            assertEquals("Juan Carlos Pérez", result.getNombreCompleto());
        }

        @Test
        @DisplayName("Should trim resulting full name")
        void shouldTrimResultingFullName() throws Exception {
            ObtPersonaPorDocResponse response = createResponseWithNames("Juan", null, null, null);

            PersonaInfo result = invokeProcessResponse(response, "12345678");

            assertEquals("Juan", result.getNombreCompleto());
            assertFalse(result.getNombreCompleto().startsWith(" "));
            assertFalse(result.getNombreCompleto().endsWith(" "));
        }

        private ObtPersonaPorDocResponse createResponseWithNames(
                String nombre1, String nombre2, String apellido1, String apellido2) {

            ObjPersona persona = new ObjPersona();
            persona.setNombre1(nombre1);
            persona.setNombre2(nombre2);
            persona.setApellido1(apellido1);
            persona.setApellido2(apellido2);
            persona.setFechaNacimiento("19900101");
            persona.setSexo(1);
            persona.setCodNacionalidad(858);

            ObtPersonaPorDocResult result = new ObtPersonaPorDocResult();
            result.setObjPersona(persona);
            result.setErrores(new ArrayOfMensaje());

            ObtPersonaPorDocResponse response = new ObtPersonaPorDocResponse();
            response.setObtPersonaPorDocResult(result);

            return response;
        }
    }
}
