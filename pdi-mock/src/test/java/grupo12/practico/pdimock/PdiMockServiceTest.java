package grupo12.practico.pdimock;

import grupo12.practico.pdimock.dto.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PdiMockServiceTest {

    private PdiMockService service;

    @BeforeEach
    void setUp() {
        service = new PdiMockService();
    }

    @Test
    void testObtPersonaPorDoc_CIValida_RetornaDatosPersona() {
        // Arrange
        ObtPersonaPorDocRequest request = new ObtPersonaPorDocRequest();
        ObjParamObtPersonaPorDoc param = new ObjParamObtPersonaPorDoc();
        param.setOrganizacion("ORGANISMO_TEST");
        param.setPasswordEntidad("SECRETO_TEST");
        param.setNrodocumento("12345678");
        param.setTipoDocumento("CI");
        request.setParamObtPersonaPorDoc(param);

        // Act
        ObtPersonaPorDocResponse response = service.obtPersonaPorDoc(request);

        // Assert
        assertNotNull(response);
        assertNotNull(response.getObtPersonaPorDocResult());
        assertTrue(response.getObtPersonaPorDocResult().getErrores() == null 
                || response.getObtPersonaPorDocResult().getErrores().getMensaje() == null
                || response.getObtPersonaPorDocResult().getErrores().getMensaje().isEmpty());
        assertNotNull(response.getObtPersonaPorDocResult().getObjPersona());
        assertEquals("12345678", response.getObtPersonaPorDocResult().getObjPersona().getNroDocumento());
    }

    @Test
    void testObtPersonaPorDoc_CIInexistente_RetornaError500() {
        // Arrange
        ObtPersonaPorDocRequest request = new ObtPersonaPorDocRequest();
        ObjParamObtPersonaPorDoc param = new ObjParamObtPersonaPorDoc();
        param.setOrganizacion("ORGANISMO_TEST");
        param.setPasswordEntidad("SECRETO_TEST");
        param.setNrodocumento("99999999");
        param.setTipoDocumento("CI");
        request.setParamObtPersonaPorDoc(param);

        // Act
        ObtPersonaPorDocResponse response = service.obtPersonaPorDoc(request);

        // Assert
        assertNotNull(response);
        assertNotNull(response.getObtPersonaPorDocResult());
        assertNotNull(response.getObtPersonaPorDocResult().getErrores());
        assertFalse(response.getObtPersonaPorDocResult().getErrores().getMensaje().isEmpty());
        assertEquals(500, response.getObtPersonaPorDocResult().getErrores().getMensaje().get(0).getCodMensaje());
    }

    @Test
    void testObtPersonaPorDoc_AutenticacionInvalida_RetornaError10002() {
        // Arrange
        ObtPersonaPorDocRequest request = new ObtPersonaPorDocRequest();
        ObjParamObtPersonaPorDoc param = new ObjParamObtPersonaPorDoc();
        param.setOrganizacion("ORGANISMO_INVALIDO");
        param.setPasswordEntidad("PASSWORD_INVALIDO");
        param.setNrodocumento("12345678");
        param.setTipoDocumento("CI");
        request.setParamObtPersonaPorDoc(param);

        // Act
        ObtPersonaPorDocResponse response = service.obtPersonaPorDoc(request);

        // Assert
        assertNotNull(response);
        assertNotNull(response.getObtPersonaPorDocResult());
        assertNotNull(response.getObtPersonaPorDocResult().getErrores());
        assertFalse(response.getObtPersonaPorDocResult().getErrores().getMensaje().isEmpty());
        assertEquals(10002, response.getObtPersonaPorDocResult().getErrores().getMensaje().get(0).getCodMensaje());
    }

    @Test
    void testObtPersonaPorDoc_FormatoCIInvalido_RetornaError10001() {
        // Arrange
        ObtPersonaPorDocRequest request = new ObtPersonaPorDocRequest();
        ObjParamObtPersonaPorDoc param = new ObjParamObtPersonaPorDoc();
        param.setOrganizacion("ORGANISMO_TEST");
        param.setPasswordEntidad("SECRETO_TEST");
        param.setNrodocumento("ABC123");
        param.setTipoDocumento("CI");
        request.setParamObtPersonaPorDoc(param);

        // Act
        ObtPersonaPorDocResponse response = service.obtPersonaPorDoc(request);

        // Assert
        assertNotNull(response);
        assertNotNull(response.getObtPersonaPorDocResult());
        assertNotNull(response.getObtPersonaPorDocResult().getErrores());
        assertFalse(response.getObtPersonaPorDocResult().getErrores().getMensaje().isEmpty());
        assertEquals(10001, response.getObtPersonaPorDocResult().getErrores().getMensaje().get(0).getCodMensaje());
    }

    @Test
    void testProductDesc_RetornaInformacionServicio() {
        // Act
        ProductDescResponse response = service.productDesc();

        // Assert
        assertNotNull(response);
        assertNotNull(response.getProductDescResult());
        assertEquals("Testing", response.getProductDescResult().getModalidad());
        assertEquals("1.0", response.getProductDescResult().getVersion());
        assertNotNull(response.getProductDescResult().getDescripcion());
    }
}

