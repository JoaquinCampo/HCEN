package grupo12.practico.repositories.Provider;

import grupo12.practico.models.Provider;
import grupo12.practico.repositories.NodosPerifericosConfig;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProviderRepositoryBean Tests")
class ProviderRepositoryBeanTest {

    @Mock
    private EntityManager entityManager;

    @Mock
    private NodosPerifericosConfig config;

    @Mock
    private TypedQuery<Provider> providerQuery;

    private ProviderRepositoryBean repository;

    private Provider testProvider;

    @BeforeEach
    void setUp() throws Exception {
        // Setup test data
        testProvider = new Provider();
        testProvider.setId("provider-id-123");
        testProvider.setName("Test Provider");
        testProvider.setCreatedAt(LocalDate.of(2023, 1, 1));
        testProvider.setUpdatedAt(LocalDate.of(2023, 1, 1));

        // Create repository instance and inject mocks
        repository = new ProviderRepositoryBean();

        // Inject dependencies using reflection
        java.lang.reflect.Field emField = ProviderRepositoryBean.class.getDeclaredField("em");
        emField.setAccessible(true);
        emField.set(repository, entityManager);

        java.lang.reflect.Field configField = ProviderRepositoryBean.class.getDeclaredField("config");
        configField.setAccessible(true);
        configField.set(repository, config);
    }

    @Test
    @DisplayName("create - Should persist provider")
    void testCreate_Success() {
        // Arrange
        // No stubbing needed for persist

        // Act
        Provider result = repository.create(testProvider);

        // Assert
        assertNotNull(result);
        assertEquals(testProvider, result);

        verify(entityManager).persist(testProvider);
    }

    @Test
    @DisplayName("create - Should throw ValidationException for null provider")
    void testCreate_NullProvider() {
        // Act & Assert
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> repository.create(null));

        assertEquals("Provider must not be null", exception.getMessage());
        verify(entityManager, never()).persist(any());
    }

    @Test
    @DisplayName("findById - Should return provider by ID")
    void testFindById_Success() {
        // Arrange
        when(entityManager.find(Provider.class, "provider-id-123")).thenReturn(testProvider);

        // Act
        Provider result = repository.findById("provider-id-123");

        // Assert
        assertNotNull(result);
        assertEquals(testProvider, result);

        verify(entityManager).find(Provider.class, "provider-id-123");
    }

    @Test
    @DisplayName("findById - Should return null for null ID")
    void testFindById_NullId() {
        // Act
        Provider result = repository.findById(null);

        // Assert
        assertNull(result);
        verify(entityManager, never()).find(any(), any());
    }

    @Test
    @DisplayName("findById - Should return null for empty ID")
    void testFindById_EmptyId() {
        // Act
        Provider result = repository.findById("");

        // Assert
        assertNull(result);
        verify(entityManager, never()).find(any(), any());
    }

    @Test
    @DisplayName("findById - Should return null for blank ID")
    void testFindById_BlankId() {
        // Act
        Provider result = repository.findById("   ");

        // Assert
        assertNull(result);
        verify(entityManager, never()).find(any(), any());
    }

    @Test
    @DisplayName("findById - Should return null when provider not found")
    void testFindById_NotFound() {
        // Arrange
        when(entityManager.find(Provider.class, "non-existent-id")).thenReturn(null);

        // Act
        Provider result = repository.findById("non-existent-id");

        // Assert
        assertNull(result);

        verify(entityManager).find(Provider.class, "non-existent-id");
    }

    @Test
    @DisplayName("findByName - Should return provider by name")
    void testFindByName_Success() {
        // Arrange
        List<Provider> providers = List.of(testProvider);
        when(entityManager.createQuery(anyString(), eq(Provider.class))).thenReturn(providerQuery);
        when(providerQuery.setParameter(anyString(), any())).thenReturn(providerQuery);
        when(providerQuery.getResultList()).thenReturn(providers);

        // Act
        Provider result = repository.findByName("Test Provider");

        // Assert
        assertNotNull(result);
        assertEquals(testProvider, result);

        verify(entityManager).createQuery(
                "SELECT p FROM Provider p WHERE p.providerName = :name", Provider.class);
        verify(providerQuery).setParameter("name", "Test Provider");
        verify(providerQuery).getResultList();
    }

    @Test
    @DisplayName("findByName - Should return null when no provider found")
    void testFindByName_NotFound() {
        // Arrange
        List<Provider> emptyList = List.of();
        when(entityManager.createQuery(anyString(), eq(Provider.class))).thenReturn(providerQuery);
        when(providerQuery.setParameter(anyString(), any())).thenReturn(providerQuery);
        when(providerQuery.getResultList()).thenReturn(emptyList);

        // Act
        Provider result = repository.findByName("Non-existent Provider");

        // Assert
        assertNull(result);

        verify(providerQuery).getResultList();
    }

    @Test
    @DisplayName("findByName - Should return null for null name")
    void testFindByName_NullName() {
        // Act
        Provider result = repository.findByName(null);

        // Assert
        assertNull(result);
        verify(entityManager, never()).createQuery(anyString(), any());
    }

    @Test
    @DisplayName("findByName - Should return null for empty name")
    void testFindByName_EmptyName() {
        // Act
        Provider result = repository.findByName("");

        // Assert
        assertNull(result);
        verify(entityManager, never()).createQuery(anyString(), any());
    }

    @Test
    @DisplayName("findByName - Should return null for blank name")
    void testFindByName_BlankName() {
        // Act
        Provider result = repository.findByName("   ");

        // Assert
        assertNull(result);
        verify(entityManager, never()).createQuery(anyString(), any());
    }

    @Test
    @DisplayName("findAll - Should return all providers")
    void testFindAll_Success() {
        // Arrange
        List<Provider> providers = List.of(testProvider);
        when(entityManager.createQuery(anyString(), eq(Provider.class))).thenReturn(providerQuery);
        when(providerQuery.getResultList()).thenReturn(providers);

        // Act
        List<Provider> result = repository.findAll();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testProvider, result.get(0));

        verify(entityManager).createQuery(
                "SELECT p FROM Provider p ORDER BY p.createdAt DESC", Provider.class);
        verify(providerQuery).getResultList();
    }

    @Test
    @DisplayName("findAll - Should return empty list when no providers exist")
    void testFindAll_Empty() {
        // Arrange
        List<Provider> emptyList = List.of();
        when(entityManager.createQuery(anyString(), eq(Provider.class))).thenReturn(providerQuery);
        when(providerQuery.getResultList()).thenReturn(emptyList);

        // Act
        List<Provider> result = repository.findAll();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(providerQuery).getResultList();
    }
}