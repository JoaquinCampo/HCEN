package grupo12.practico.services.HcenAdmin;

import grupo12.practico.dtos.HcenAdmin.AddHcenAdminDTO;
import grupo12.practico.dtos.HcenAdmin.HcenAdminDTO;
import grupo12.practico.models.Gender;
import grupo12.practico.models.HcenAdmin;
import grupo12.practico.repositories.HcenAdmin.HcenAdminRepositoryLocal;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("HcenAdminServiceBean Tests")
class HcenAdminServiceBeanTest {

    @Mock
    private HcenAdminRepositoryLocal hcenAdminRepository;

    @InjectMocks
    private HcenAdminServiceBean hcenAdminService;

    private HcenAdmin testHcenAdmin;
    private AddHcenAdminDTO testAddHcenAdminDTO;
    private HcenAdminDTO testHcenAdminDTO;

    @BeforeEach
    void setUp() {
        // Setup test data
        testHcenAdmin = new HcenAdmin();
        testHcenAdmin.setId("admin-id-123");
        testHcenAdmin.setCi("12345678");
        testHcenAdmin.setFirstName("Admin");
        testHcenAdmin.setLastName("User");
        testHcenAdmin.setGender(Gender.MALE);
        testHcenAdmin.setEmail("admin@example.com");
        testHcenAdmin.setPhone("+598123456789");
        testHcenAdmin.setAddress("123 Admin St");
        testHcenAdmin.setDateOfBirth(LocalDate.of(1980, 1, 1));
        testHcenAdmin.setCreatedAt(LocalDate.now());
        testHcenAdmin.setUpdatedAt(LocalDate.now());

        testAddHcenAdminDTO = new AddHcenAdminDTO();
        testAddHcenAdminDTO.setCi("12345678");
        testAddHcenAdminDTO.setFirstName("Admin");
        testAddHcenAdminDTO.setLastName("User");
        testAddHcenAdminDTO.setGender(Gender.MALE);
        testAddHcenAdminDTO.setEmail("admin@example.com");
        testAddHcenAdminDTO.setPhone("+598123456789");
        testAddHcenAdminDTO.setAddress("123 Admin St");
        testAddHcenAdminDTO.setDateOfBirth(LocalDate.of(1980, 1, 1));

        testHcenAdminDTO = testHcenAdmin.toDto();
    }

    @Test
    @DisplayName("create - Should create and return HcenAdmin DTO")
    void testCreate_Success() {
        // Arrange
        when(hcenAdminRepository.create(any(HcenAdmin.class))).thenReturn(testHcenAdmin);

        // Act
        HcenAdminDTO result = hcenAdminService.create(testAddHcenAdminDTO);

        // Assert
        assertNotNull(result);
        assertEquals(testHcenAdminDTO.getId(), result.getId());
        assertEquals(testHcenAdminDTO.getCi(), result.getCi());
        assertEquals(testHcenAdminDTO.getFirstName(), result.getFirstName());
        assertEquals(testHcenAdminDTO.getLastName(), result.getLastName());

        verify(hcenAdminRepository).create(any(HcenAdmin.class));
    }

    @Test
    @DisplayName("create - Should throw ValidationException for null DTO")
    void testCreate_NullDTO() {
        // Act & Assert
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> hcenAdminService.create(null));

        assertEquals("Hcen Admin data must not be null", exception.getMessage());
        verify(hcenAdminRepository, never()).create(any());
    }

    @Test
    @DisplayName("create - Should throw ValidationException for missing first name")
    void testCreate_MissingFirstName() {
        // Arrange
        testAddHcenAdminDTO.setFirstName("");

        // Act & Assert
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> hcenAdminService.create(testAddHcenAdminDTO));

        assertEquals("Hcen Admin first name and last name are required", exception.getMessage());
        verify(hcenAdminRepository, never()).create(any());
    }

    @Test
    @DisplayName("create - Should throw ValidationException for missing CI")
    void testCreate_MissingCi() {
        // Arrange
        testAddHcenAdminDTO.setCi("");

        // Act & Assert
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> hcenAdminService.create(testAddHcenAdminDTO));

        assertEquals("Hcen Admin document is required", exception.getMessage());
        verify(hcenAdminRepository, never()).create(any());
    }

    @Test
    @DisplayName("findAll - Should return list of HcenAdmin DTOs")
    void testFindAll_Success() {
        // Arrange
        List<HcenAdmin> admins = Arrays.asList(testHcenAdmin);
        when(hcenAdminRepository.findAll()).thenReturn(admins);

        // Act
        List<HcenAdminDTO> result = hcenAdminService.findAll();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testHcenAdminDTO.getId(), result.get(0).getId());

        verify(hcenAdminRepository).findAll();
    }

    @Test
    @DisplayName("findAll - Should return empty list when no admins exist")
    void testFindAll_Empty() {
        // Arrange
        when(hcenAdminRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<HcenAdminDTO> result = hcenAdminService.findAll();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(hcenAdminRepository).findAll();
    }

    @Test
    @DisplayName("findByCi - Should return HcenAdmin DTO when found")
    void testFindByCi_Found() {
        // Arrange
        String ci = "12345678";
        when(hcenAdminRepository.findByCi(ci)).thenReturn(testHcenAdmin);

        // Act
        HcenAdminDTO result = hcenAdminService.findByCi(ci);

        // Assert
        assertNotNull(result);
        assertEquals(testHcenAdminDTO.getCi(), result.getCi());

        verify(hcenAdminRepository).findByCi(ci);
    }

    @Test
    @DisplayName("findByCi - Should return null when not found")
    void testFindByCi_NotFound() {
        // Arrange
        String ci = "99999999";
        when(hcenAdminRepository.findByCi(ci)).thenReturn(null);

        // Act
        HcenAdminDTO result = hcenAdminService.findByCi(ci);

        // Assert
        assertNull(result);

        verify(hcenAdminRepository).findByCi(ci);
    }
}