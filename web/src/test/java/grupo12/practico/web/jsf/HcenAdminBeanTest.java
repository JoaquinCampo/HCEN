package grupo12.practico.web.jsf;

import grupo12.practico.dtos.HcenAdmin.AddHcenAdminDTO;
import grupo12.practico.dtos.HcenAdmin.HcenAdminDTO;
import grupo12.practico.services.HcenAdmin.HcenAdminServiceLocal;
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
@DisplayName("HcenAdminBean Tests")
class HcenAdminBeanTest {

    @Mock
    private HcenAdminServiceLocal hcenAdminService;

    @Mock
    private FacesContext facesContext;

    private HcenAdminBean hcenAdminBean;

    @BeforeEach
    void setUp() throws Exception {
        hcenAdminBean = new HcenAdminBean();

        // Use reflection to inject mocked dependency
        var serviceField = HcenAdminBean.class.getDeclaredField("hcenAdminService");
        serviceField.setAccessible(true);
        serviceField.set(hcenAdminBean, hcenAdminService);
    }

    @Test
    @DisplayName("init - Should initialize fields and load all admins")
    void init_ShouldInitializeFieldsAndLoadAllAdmins() {
        List<HcenAdminDTO> mockAdmins = Arrays.asList(
                createHcenAdminDTO("1", "Admin 1"),
                createHcenAdminDTO("2", "Admin 2"));
        when(hcenAdminService.findAllHcenAdmins()).thenReturn(mockAdmins);

        hcenAdminBean.init();

        verify(hcenAdminService).findAllHcenAdmins();
        assertNotNull(hcenAdminBean.getNewAdmin());
        assertNotNull(hcenAdminBean.getAdmins());
    }

    @Test
    @DisplayName("loadAll - Should load all HCEN admins")
    void loadAll_ShouldLoadAllHcenAdmins() {
        List<HcenAdminDTO> mockAdmins = Arrays.asList(
                createHcenAdminDTO("1", "Admin 1"),
                createHcenAdminDTO("2", "Admin 2"));
        when(hcenAdminService.findAllHcenAdmins()).thenReturn(mockAdmins);

        hcenAdminBean.loadAll();

        verify(hcenAdminService).findAllHcenAdmins();
        assertEquals(mockAdmins, hcenAdminBean.getAdmins());
    }

    @Test
    @DisplayName("search - Should load all when search query is null")
    void search_ShouldLoadAllWhenSearchQueryIsNull() {
        List<HcenAdminDTO> mockAdmins = Arrays.asList(createHcenAdminDTO("1", "Admin"));
        when(hcenAdminService.findAllHcenAdmins()).thenReturn(mockAdmins);

        hcenAdminBean.setSearchQuery(null);
        hcenAdminBean.search();

        verify(hcenAdminService).findAllHcenAdmins();
    }

    @Test
    @DisplayName("search - Should load all when search query is empty")
    void search_ShouldLoadAllWhenSearchQueryIsEmpty() {
        List<HcenAdminDTO> mockAdmins = Arrays.asList(createHcenAdminDTO("1", "Admin"));
        when(hcenAdminService.findAllHcenAdmins()).thenReturn(mockAdmins);

        hcenAdminBean.setSearchQuery("");
        hcenAdminBean.search();

        verify(hcenAdminService).findAllHcenAdmins();
    }

    @Test
    @DisplayName("search - Should load all when search query is blank")
    void search_ShouldLoadAllWhenSearchQueryIsBlank() {
        List<HcenAdminDTO> mockAdmins = Arrays.asList(createHcenAdminDTO("1", "Admin"));
        when(hcenAdminService.findAllHcenAdmins()).thenReturn(mockAdmins);

        hcenAdminBean.setSearchQuery("   ");
        hcenAdminBean.search();

        verify(hcenAdminService).findAllHcenAdmins();
    }

    @Test
    @DisplayName("save - Should create admin successfully and redirect")
    void save_ShouldCreateAdminSuccessfullyAndRedirect() {
        AddHcenAdminDTO newAdmin = new AddHcenAdminDTO();
        newAdmin.setFirstName("New");
        newAdmin.setLastName("Admin");
        hcenAdminBean.setNewAdmin(newAdmin);

        List<HcenAdminDTO> updatedAdmins = Arrays.asList(createHcenAdminDTO("1", "New Admin"));
        when(hcenAdminService.findAllHcenAdmins()).thenReturn(updatedAdmins);

        try (MockedStatic<FacesContext> facesContextMockedStatic = mockStatic(FacesContext.class)) {
            facesContextMockedStatic.when(FacesContext::getCurrentInstance).thenReturn(facesContext);

            String result = hcenAdminBean.save();

            verify(hcenAdminService).createHcenAdmin(newAdmin);
            verify(hcenAdminService).findAllHcenAdmins();
            verify(facesContext).addMessage(eq(null), any(FacesMessage.class));
            assertEquals("list?faces-redirect=true", result);
        }
    }

    @Test
    @DisplayName("save - Should handle ValidationException and return null")
    void save_ShouldHandleValidationExceptionAndReturnNull() {
        AddHcenAdminDTO newAdmin = new AddHcenAdminDTO();
        hcenAdminBean.setNewAdmin(newAdmin);

        ValidationException validationException = new ValidationException("Invalid admin data");
        when(hcenAdminService.createHcenAdmin(newAdmin)).thenThrow(validationException);

        try (MockedStatic<FacesContext> facesContextMockedStatic = mockStatic(FacesContext.class)) {
            facesContextMockedStatic.when(FacesContext::getCurrentInstance).thenReturn(facesContext);

            String result = hcenAdminBean.save();

            verify(facesContext).addMessage(eq(null), any(FacesMessage.class));
            assertNull(result);
        }
    }

    @Test
    @DisplayName("save - Should handle RuntimeException and return null")
    void save_ShouldHandleRuntimeExceptionAndReturnNull() {
        AddHcenAdminDTO newAdmin = new AddHcenAdminDTO();
        hcenAdminBean.setNewAdmin(newAdmin);

        RuntimeException runtimeException = new RuntimeException("Database error");
        when(hcenAdminService.createHcenAdmin(newAdmin)).thenThrow(runtimeException);

        try (MockedStatic<FacesContext> facesContextMockedStatic = mockStatic(FacesContext.class)) {
            facesContextMockedStatic.when(FacesContext::getCurrentInstance).thenReturn(facesContext);

            String result = hcenAdminBean.save();

            verify(facesContext).addMessage(eq(null), any(FacesMessage.class));
            assertNull(result);
        }
    }

    @Test
    @DisplayName("Getters and setters - Should work correctly")
    void gettersAndSetters_ShouldWorkCorrectly() {
        // Test newAdmin
        AddHcenAdminDTO newAdmin = new AddHcenAdminDTO();
        hcenAdminBean.setNewAdmin(newAdmin);
        assertEquals(newAdmin, hcenAdminBean.getNewAdmin());

        // Test searchQuery
        hcenAdminBean.setSearchQuery("test query");
        assertEquals("test query", hcenAdminBean.getSearchQuery());

        // Test admins (set via reflection since it's private)
        assertDoesNotThrow(() -> {
            var adminsField = HcenAdminBean.class.getDeclaredField("admins");
            adminsField.setAccessible(true);
            List<HcenAdminDTO> admins = Arrays.asList(createHcenAdminDTO("1", "Admin"));
            adminsField.set(hcenAdminBean, admins);
            assertEquals(admins, hcenAdminBean.getAdmins());
        });
    }

    private HcenAdminDTO createHcenAdminDTO(String id, String name) {
        HcenAdminDTO dto = new HcenAdminDTO();
        dto.setId(id);
        // Handle names with or without spaces
        String[] parts = name.split(" ", 2);
        dto.setFirstName(parts[0]);
        dto.setLastName(parts.length > 1 ? parts[1] : "LastName");
        return dto;
    }
}
