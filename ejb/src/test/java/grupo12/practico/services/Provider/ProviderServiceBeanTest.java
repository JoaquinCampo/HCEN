package grupo12.practico.services.Provider;

import grupo12.practico.dtos.Clinic.ClinicDTO;
import grupo12.practico.dtos.Provider.AddProviderDTO;
import grupo12.practico.dtos.Provider.ProviderDTO;
import grupo12.practico.models.Provider;
import grupo12.practico.repositories.Provider.ProviderRepositoryLocal;
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
@DisplayName("ProviderServiceBean Tests")
class ProviderServiceBeanTest {

    @Mock
    private ProviderRepositoryLocal providerRepository;

    @InjectMocks
    private ProviderServiceBean providerService;

    private Provider testProvider;
    private AddProviderDTO testAddProviderDTO;
    private ProviderDTO testProviderDTO;
    private ClinicDTO testClinicDTO;

    @BeforeEach
    void setUp() {
        // Setup test data
        testProvider = new Provider();
        testProvider.setId("provider-id-123");
        testProvider.setName("Test Provider");
        testProvider.setCreatedAt(LocalDate.now());
        testProvider.setUpdatedAt(LocalDate.now());

        testAddProviderDTO = new AddProviderDTO();
        testAddProviderDTO.setProviderName("Test Provider");

        testProviderDTO = testProvider.toDTO();

        testClinicDTO = new ClinicDTO();
        testClinicDTO.setId("clinic-id-123");
        testClinicDTO.setName("Test Clinic");
        testClinicDTO.setEmail("clinic@example.com");
        testClinicDTO.setCreatedAt(LocalDate.now());
        testClinicDTO.setUpdatedAt(LocalDate.now());
    }

    @Test
    @DisplayName("create - Should create and return Provider DTO")
    void testCreate_Success() {
        // Arrange
        when(providerRepository.createProvider(any(Provider.class))).thenReturn(testProvider);

        // Act
        ProviderDTO result = providerService.createProvider(testAddProviderDTO);

        // Assert
        assertNotNull(result);
        assertEquals(testProviderDTO.getId(), result.getId());
        assertEquals(testProviderDTO.getProviderName(), result.getProviderName());

        verify(providerRepository).createProvider(any(Provider.class));
    }

    @Test
    @DisplayName("create - Should throw ValidationException for null DTO")
    void testCreate_NullDTO() {
        // Act & Assert
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> providerService.createProvider(null));

        assertEquals("Provider data must not be null", exception.getMessage());
        verify(providerRepository, never()).createProvider(any());
    }

    @Test
    @DisplayName("create - Should throw ValidationException for null provider name")
    void testCreate_NullProviderName() {
        // Arrange
        testAddProviderDTO.setProviderName(null);

        // Act & Assert
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> providerService.createProvider(testAddProviderDTO));

        assertEquals("Provider name is required", exception.getMessage());
        verify(providerRepository, never()).createProvider(any());
    }

    @Test
    @DisplayName("create - Should throw ValidationException for empty provider name")
    void testCreate_EmptyProviderName() {
        // Arrange
        testAddProviderDTO.setProviderName("");

        // Act & Assert
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> providerService.createProvider(testAddProviderDTO));

        assertEquals("Provider name is required", exception.getMessage());
        verify(providerRepository, never()).createProvider(any());
    }

    @Test
    @DisplayName("findAll - Should return list of Provider DTOs")
    void testFindAll_Success() {
        // Arrange
        List<Provider> providers = Arrays.asList(testProvider);
        when(providerRepository.findAllProviders()).thenReturn(providers);

        // Act
        List<ProviderDTO> result = providerService.findAllProviders();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testProviderDTO.getId(), result.get(0).getId());
        assertEquals(testProviderDTO.getProviderName(), result.get(0).getProviderName());

        verify(providerRepository).findAllProviders();
    }

    @Test
    @DisplayName("findAll - Should return empty list when no providers exist")
    void testFindAll_Empty() {
        // Arrange
        when(providerRepository.findAllProviders()).thenReturn(Collections.emptyList());

        // Act
        List<ProviderDTO> result = providerService.findAllProviders();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(providerRepository).findAllProviders();
    }

    @Test
    @DisplayName("findByName - Should return Provider DTO when found")
    void testFindByName_Found() {
        // Arrange
        String providerName = "Test Provider";
        when(providerRepository.findProviderByName(providerName)).thenReturn(testProvider);

        // Act
        ProviderDTO result = providerService.findProviderByName(providerName);

        // Assert
        assertNotNull(result);
        assertEquals(testProviderDTO.getProviderName(), result.getProviderName());

        verify(providerRepository).findProviderByName(providerName);
    }

    @Test
    @DisplayName("findByName - Should return null when not found")
    void testFindByName_NotFound() {
        // Arrange
        String providerName = "Nonexistent Provider";
        when(providerRepository.findProviderByName(providerName)).thenReturn(null);

        // Act
        ProviderDTO result = providerService.findProviderByName(providerName);

        // Assert
        assertNull(result);

        verify(providerRepository).findProviderByName(providerName);
    }

    @Test
    @DisplayName("findByName - Should return null for null input")
    void testFindByName_NullInput() {
        // Act
        ProviderDTO result = providerService.findProviderByName(null);

        // Assert
        assertNull(result);

        verify(providerRepository, never()).findProviderByName(any());
    }

    @Test
    @DisplayName("findByName - Should return null for empty input")
    void testFindByName_EmptyInput() {
        // Act
        ProviderDTO result = providerService.findProviderByName("");

        // Assert
        assertNull(result);

        verify(providerRepository, never()).findProviderByName(any());
    }

}