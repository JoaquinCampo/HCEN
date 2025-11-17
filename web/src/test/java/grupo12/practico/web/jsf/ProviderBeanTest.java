package grupo12.practico.web.jsf;

import grupo12.practico.dtos.Clinic.ClinicDTO;
import grupo12.practico.dtos.Provider.AddProviderDTO;
import grupo12.practico.dtos.Provider.ProviderDTO;
import grupo12.practico.services.Clinic.ClinicServiceLocal;
import grupo12.practico.services.Provider.ProviderServiceLocal;
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
@DisplayName("ProviderBean Tests")
class ProviderBeanTest {

    @Mock
    private ProviderServiceLocal providerService;

    @Mock
    private ClinicServiceLocal clinicService;

    @Mock
    private FacesContext facesContext;

    private ProviderBean providerBean;

    @BeforeEach
    void setUp() throws Exception {
        providerBean = new ProviderBean();

        // Use reflection to inject mocked dependencies
        var providerServiceField = ProviderBean.class.getDeclaredField("providerService");
        providerServiceField.setAccessible(true);
        providerServiceField.set(providerBean, providerService);

        var clinicServiceField = ProviderBean.class.getDeclaredField("clinicService");
        clinicServiceField.setAccessible(true);
        clinicServiceField.set(providerBean, clinicService);
    }

    @Test
    @DisplayName("init - Should initialize fields and load all providers")
    void init_ShouldInitializeFieldsAndLoadAllProviders() {
        List<ProviderDTO> mockProviders = Arrays.asList(
                createProviderDTO("1", "Provider 1"),
                createProviderDTO("2", "Provider 2"));
        when(providerService.findAllProviders()).thenReturn(mockProviders);

        providerBean.init();

        verify(providerService).findAllProviders();
        assertNotNull(providerBean.getNewProvider());
        assertNotNull(providerBean.getProviders());
        assertNotNull(providerBean.getProviderClinics());
    }

    @Test
    @DisplayName("loadAll - Should load all providers")
    void loadAll_ShouldLoadAllProviders() {
        List<ProviderDTO> mockProviders = Arrays.asList(
                createProviderDTO("1", "Provider 1"),
                createProviderDTO("2", "Provider 2"));
        when(providerService.findAllProviders()).thenReturn(mockProviders);

        providerBean.loadAll();

        verify(providerService).findAllProviders();
        assertEquals(mockProviders, providerBean.getProviders());
    }

    @Test
    @DisplayName("loadProviderByName - Should load provider and clinics when name is provided")
    void loadProviderByName_ShouldLoadProviderAndClinicsWhenNameIsProvided() {
        String providerName = "Test Provider";
        ProviderDTO mockProvider = createProviderDTO("1", providerName);
        List<ClinicDTO> mockClinics = Arrays.asList(
                createClinicDTO("1", "Clinic 1"),
                createClinicDTO("2", "Clinic 2"));

        when(providerService.findProviderByName(providerName)).thenReturn(mockProvider);
        when(clinicService.findAllClinics(providerName)).thenReturn(mockClinics);

        providerBean.setProviderName(providerName);
        providerBean.loadProviderByName();

        verify(providerService).findProviderByName(providerName);
        verify(clinicService).findAllClinics(providerName);
        assertEquals(mockProvider, providerBean.getSelectedProvider());
        assertEquals(mockClinics, providerBean.getProviderClinics());
    }

    @Test
    @DisplayName("loadProviderByName - Should clear selection when name is null")
    void loadProviderByName_ShouldClearSelectionWhenNameIsNull() {
        providerBean.setProviderName(null);
        providerBean.loadProviderByName();

        assertNull(providerBean.getSelectedProvider());
        assertTrue(providerBean.getProviderClinics().isEmpty());
        verifyNoInteractions(providerService);
        verifyNoInteractions(clinicService);
    }

    @Test
    @DisplayName("loadProviderByName - Should clear selection when name is empty")
    void loadProviderByName_ShouldClearSelectionWhenNameIsEmpty() {
        providerBean.setProviderName("");
        providerBean.loadProviderByName();

        assertNull(providerBean.getSelectedProvider());
        assertTrue(providerBean.getProviderClinics().isEmpty());
        verifyNoInteractions(providerService);
        verifyNoInteractions(clinicService);
    }

    @Test
    @DisplayName("save - Should create provider successfully and redirect")
    void save_ShouldCreateProviderSuccessfullyAndRedirect() {
        AddProviderDTO newProvider = new AddProviderDTO();
        newProvider.setProviderName("New Provider");
        providerBean.setNewProvider(newProvider);

        List<ProviderDTO> updatedProviders = Arrays.asList(createProviderDTO("1", "New Provider"));
        when(providerService.findAllProviders()).thenReturn(updatedProviders);

        try (MockedStatic<FacesContext> facesContextMockedStatic = mockStatic(FacesContext.class)) {
            facesContextMockedStatic.when(FacesContext::getCurrentInstance).thenReturn(facesContext);

            String result = providerBean.save();

            verify(providerService).createProvider(newProvider);
            verify(providerService).findAllProviders();
            verify(facesContext).addMessage(eq(null), any(FacesMessage.class));
            assertEquals("/provider/list?faces-redirect=true", result);
        }
    }

    @Test
    @DisplayName("save - Should handle ValidationException and return null")
    void save_ShouldHandleValidationExceptionAndReturnNull() {
        AddProviderDTO newProvider = new AddProviderDTO();
        providerBean.setNewProvider(newProvider);

        ValidationException validationException = new ValidationException("Invalid provider data");
        when(providerService.createProvider(newProvider)).thenThrow(validationException);

        try (MockedStatic<FacesContext> facesContextMockedStatic = mockStatic(FacesContext.class)) {
            facesContextMockedStatic.when(FacesContext::getCurrentInstance).thenReturn(facesContext);

            String result = providerBean.save();

            verify(facesContext).addMessage(eq(null), any(FacesMessage.class));
            assertNull(result);
        }
    }

    @Test
    @DisplayName("save - Should handle RuntimeException and return null")
    void save_ShouldHandleRuntimeExceptionAndReturnNull() {
        AddProviderDTO newProvider = new AddProviderDTO();
        providerBean.setNewProvider(newProvider);

        RuntimeException runtimeException = new RuntimeException("Database error");
        when(providerService.createProvider(newProvider)).thenThrow(runtimeException);

        try (MockedStatic<FacesContext> facesContextMockedStatic = mockStatic(FacesContext.class)) {
            facesContextMockedStatic.when(FacesContext::getCurrentInstance).thenReturn(facesContext);

            String result = providerBean.save();

            verify(facesContext).addMessage(eq(null), any(FacesMessage.class));
            assertNull(result);
        }
    }

    @Test
    @DisplayName("Getters and setters - Should work correctly")
    void gettersAndSetters_ShouldWorkCorrectly() {
        // Test providerName
        providerBean.setProviderName("Test Provider");
        assertEquals("Test Provider", providerBean.getProviderName());

        // Test newProvider
        AddProviderDTO newProvider = new AddProviderDTO();
        providerBean.setNewProvider(newProvider);
        assertEquals(newProvider, providerBean.getNewProvider());

        // Test providers (set via reflection since it's private)
        assertDoesNotThrow(() -> {
            var providersField = ProviderBean.class.getDeclaredField("providers");
            providersField.setAccessible(true);
            List<ProviderDTO> providers = Arrays.asList(createProviderDTO("1", "Provider"));
            providersField.set(providerBean, providers);
            assertEquals(providers, providerBean.getProviders());
        });

        // Test selectedProvider (set via reflection since it's private)
        assertDoesNotThrow(() -> {
            var selectedProviderField = ProviderBean.class.getDeclaredField("selectedProvider");
            selectedProviderField.setAccessible(true);
            ProviderDTO selectedProvider = createProviderDTO("1", "Selected Provider");
            selectedProviderField.set(providerBean, selectedProvider);
            assertEquals(selectedProvider, providerBean.getSelectedProvider());
        });

        // Test providerClinics (set via reflection since it's private)
        assertDoesNotThrow(() -> {
            var providerClinicsField = ProviderBean.class.getDeclaredField("providerClinics");
            providerClinicsField.setAccessible(true);
            List<ClinicDTO> clinics = Arrays.asList(createClinicDTO("1", "Clinic"));
            providerClinicsField.set(providerBean, clinics);
            assertEquals(clinics, providerBean.getProviderClinics());
        });
    }

    private ProviderDTO createProviderDTO(String id, String name) {
        ProviderDTO dto = new ProviderDTO();
        dto.setId(id);
        dto.setProviderName(name);
        return dto;
    }

    private ClinicDTO createClinicDTO(String id, String name) {
        ClinicDTO dto = new ClinicDTO();
        dto.setId(id);
        dto.setName(name);
        return dto;
    }
}
