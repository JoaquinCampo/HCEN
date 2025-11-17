package grupo12.practico.web.jsf;

import grupo12.practico.dtos.Clinic.AddClinicDTO;
import grupo12.practico.dtos.Clinic.ClinicAdminDTO;
import grupo12.practico.dtos.Clinic.ClinicDTO;
import grupo12.practico.messaging.Clinic.ClinicRegistrationProducerLocal;
import grupo12.practico.services.Clinic.ClinicServiceLocal;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ClinicBean Tests")
class ClinicBeanTest {

    @Mock
    private ClinicServiceLocal clinicService;

    @Mock
    private ClinicRegistrationProducerLocal registrationProducer;

    @Mock
    private FacesContext facesContext;

    private ClinicBean clinicBean;

    @BeforeEach
    void setUp() throws Exception {
        clinicBean = new ClinicBean();

        // Use reflection to inject mocked dependencies
        var serviceField = ClinicBean.class.getDeclaredField("service");
        serviceField.setAccessible(true);
        serviceField.set(clinicBean, clinicService);

        var producerField = ClinicBean.class.getDeclaredField("registrationProducer");
        producerField.setAccessible(true);
        producerField.set(clinicBean, registrationProducer);
    }

    @Test
    @DisplayName("init - Should initialize fields")
    void init_ShouldInitializeFields() {
        clinicBean.init();

        assertNotNull(clinicBean.getnewClinic());
        assertNotNull(clinicBean.getnewClinic().getClinicAdmin());
        assertNotNull(clinicBean.getclinics());
    }

    @Test
    @DisplayName("loadClinics - Should load clinics when providerName is set")
    void loadClinics_ShouldLoadClinicsWhenProviderNameIsSet() {
        List<ClinicDTO> mockClinics = Arrays.asList(
                createClinicDTO("1", "Clinic 1"),
                createClinicDTO("2", "Clinic 2"));
        when(clinicService.findAllClinics("TestProvider")).thenReturn(mockClinics);

        clinicBean.setProviderName("TestProvider");
        clinicBean.loadClinics();

        verify(clinicService, times(2)).findAllClinics("TestProvider");
        assertEquals(mockClinics, clinicBean.getclinics());
    }

    @Test
    @DisplayName("loadClinics - Should clear clinics when providerName is null")
    void loadClinics_ShouldClearClinicsWhenProviderNameIsNull() {
        clinicBean.setProviderName(null);
        clinicBean.loadClinics();

        assertTrue(clinicBean.getclinics().isEmpty());
        verifyNoInteractions(clinicService);
    }

    @Test
    @DisplayName("loadClinics - Should clear clinics when providerName is empty")
    void loadClinics_ShouldClearClinicsWhenProviderNameIsEmpty() {
        clinicBean.setProviderName("");
        clinicBean.loadClinics();

        assertTrue(clinicBean.getclinics().isEmpty());
        verifyNoInteractions(clinicService);
    }

    @Test
    @DisplayName("loadAll - Should load all clinics for provider")
    void loadAll_ShouldLoadAllClinicsForProvider() {
        List<ClinicDTO> mockClinics = Arrays.asList(createClinicDTO("1", "Clinic"));
        when(clinicService.findAllClinics("TestProvider")).thenReturn(mockClinics);

        clinicBean.setProviderName("TestProvider");
        clinicBean.loadAll();

        verify(clinicService, times(2)).findAllClinics("TestProvider");
        assertEquals(mockClinics, clinicBean.getclinics());
    }

    @Test
    @DisplayName("search - Should load clinics when search query is null")
    void search_ShouldLoadClinicsWhenSearchQueryIsNull() {
        List<ClinicDTO> mockClinics = Arrays.asList(createClinicDTO("1", "Clinic"));
        when(clinicService.findAllClinics("TestProvider")).thenReturn(mockClinics);

        clinicBean.setProviderName("TestProvider");
        clinicBean.setSearchQuery(null);
        clinicBean.search();

        verify(clinicService, times(2)).findAllClinics("TestProvider");
    }

    @Test
    @DisplayName("search - Should load clinics when search query is empty")
    void search_ShouldLoadClinicsWhenSearchQueryIsEmpty() {
        List<ClinicDTO> mockClinics = Arrays.asList(createClinicDTO("1", "Clinic"));
        when(clinicService.findAllClinics("TestProvider")).thenReturn(mockClinics);

        clinicBean.setProviderName("TestProvider");
        clinicBean.setSearchQuery("");
        clinicBean.search();

        verify(clinicService, times(2)).findAllClinics("TestProvider");
    }

    @Test
    @DisplayName("search - Should find clinic by name when search query is provided")
    void search_ShouldFindClinicByNameWhenSearchQueryIsProvided() {
        ClinicDTO mockClinic = createClinicDTO("1", "Test Clinic");
        when(clinicService.findClinicByName("Test Clinic")).thenReturn(mockClinic);

        clinicBean.setSearchQuery("Test Clinic");
        clinicBean.search();

        verify(clinicService).findClinicByName("Test Clinic");
        assertEquals(1, clinicBean.getclinics().size());
        assertEquals(mockClinic, clinicBean.getclinics().get(0));
    }

    @Test
    @DisplayName("search - Should return empty list when clinic not found")
    void search_ShouldReturnEmptyListWhenClinicNotFound() {
        when(clinicService.findClinicByName("Nonexistent")).thenReturn(null);

        clinicBean.setSearchQuery("Nonexistent");
        clinicBean.search();

        assertTrue(clinicBean.getclinics().isEmpty());
    }

    @Test
    @DisplayName("save - Should enqueue clinic creation and redirect to provider detail")
    void save_ShouldEnqueueClinicCreationAndRedirectToProviderDetail() {
        AddClinicDTO newClinic = new AddClinicDTO();
        newClinic.setName("New Clinic");
        clinicBean.setnewClinic(newClinic);
        clinicBean.setProviderName("TestProvider");

        try (MockedStatic<FacesContext> facesContextMockedStatic = mockStatic(FacesContext.class)) {
            facesContextMockedStatic.when(FacesContext::getCurrentInstance).thenReturn(facesContext);

            String result = clinicBean.save();

            verify(registrationProducer).enqueue(newClinic);
            verify(facesContext).addMessage(eq(null), any(FacesMessage.class));
            assertEquals("/provider/detail?faces-redirect=true&name=TestProvider", result);
        }
    }

    @Test
    @DisplayName("save - Should enqueue clinic creation and return null when no provider")
    void save_ShouldEnqueueClinicCreationAndReturnNullWhenNoProvider() {
        AddClinicDTO newClinic = new AddClinicDTO();
        clinicBean.setnewClinic(newClinic);
        clinicBean.setProviderName(null);

        try (MockedStatic<FacesContext> facesContextMockedStatic = mockStatic(FacesContext.class)) {
            facesContextMockedStatic.when(FacesContext::getCurrentInstance).thenReturn(facesContext);

            String result = clinicBean.save();

            verify(registrationProducer).enqueue(newClinic);
            verify(facesContext).addMessage(eq(null), any(FacesMessage.class));
            assertNull(result);
        }
    }

    @Test
    @DisplayName("save - Should handle ValidationException and return null")
    void save_ShouldHandleValidationExceptionAndReturnNull() {
        AddClinicDTO newClinic = new AddClinicDTO();
        clinicBean.setnewClinic(newClinic);

        ValidationException validationException = new ValidationException("Invalid clinic data");
        doThrow(validationException).when(registrationProducer).enqueue(newClinic);

        try (MockedStatic<FacesContext> facesContextMockedStatic = mockStatic(FacesContext.class)) {
            facesContextMockedStatic.when(FacesContext::getCurrentInstance).thenReturn(facesContext);

            String result = clinicBean.save();

            verify(facesContext).addMessage(eq(null), any(FacesMessage.class));
            assertNull(result);
        }
    }

    @Test
    @DisplayName("save - Should handle RuntimeException and return null")
    void save_ShouldHandleRuntimeExceptionAndReturnNull() {
        AddClinicDTO newClinic = new AddClinicDTO();
        clinicBean.setnewClinic(newClinic);

        RuntimeException runtimeException = new RuntimeException("Queue error");
        doThrow(runtimeException).when(registrationProducer).enqueue(newClinic);

        try (MockedStatic<FacesContext> facesContextMockedStatic = mockStatic(FacesContext.class)) {
            facesContextMockedStatic.when(FacesContext::getCurrentInstance).thenReturn(facesContext);

            String result = clinicBean.save();

            verify(facesContext).addMessage(eq(null), any(FacesMessage.class));
            assertNull(result);
        }
    }

    @Test
    @DisplayName("setProviderName - Should update provider name and reload clinics")
    void setProviderName_ShouldUpdateProviderNameAndReloadClinics() {
        List<ClinicDTO> mockClinics = Arrays.asList(createClinicDTO("1", "Clinic"));
        when(clinicService.findAllClinics("NewProvider")).thenReturn(mockClinics);

        clinicBean.setProviderName("NewProvider");

        assertEquals("NewProvider", clinicBean.getProviderName());
        verify(clinicService).findAllClinics("NewProvider");
    }

    @Test
    @DisplayName("setnewClinic - Should set clinic and initialize clinic admin if null")
    void setnewClinic_ShouldSetClinicAndInitializeClinicAdminIfNull() {
        AddClinicDTO clinic = new AddClinicDTO();
        clinicBean.setnewClinic(clinic);

        assertEquals(clinic, clinicBean.getnewClinic());
        assertNotNull(clinicBean.getnewClinic().getClinicAdmin());
    }

    @Test
    @DisplayName("setnewClinic - Should set clinic with existing clinic admin")
    void setnewClinic_ShouldSetClinicWithExistingClinicAdmin() {
        AddClinicDTO clinic = new AddClinicDTO();
        ClinicAdminDTO admin = new ClinicAdminDTO();
        clinic.setClinicAdmin(admin);
        clinicBean.setnewClinic(clinic);

        assertEquals(clinic, clinicBean.getnewClinic());
        assertEquals(admin, clinicBean.getnewClinic().getClinicAdmin());
    }

    @Test
    @DisplayName("Getters and setters - Should work correctly")
    void gettersAndSetters_ShouldWorkCorrectly() {
        // Test searchQuery
        clinicBean.setSearchQuery("test query");
        assertEquals("test query", clinicBean.getSearchQuery());

        // Test providerName (already tested above)
        clinicBean.setProviderName("TestProvider");
        assertEquals("TestProvider", clinicBean.getProviderName());
    }

    private ClinicDTO createClinicDTO(String id, String name) {
        ClinicDTO dto = new ClinicDTO();
        dto.setId(id);
        dto.setName(name);
        return dto;
    }
}
