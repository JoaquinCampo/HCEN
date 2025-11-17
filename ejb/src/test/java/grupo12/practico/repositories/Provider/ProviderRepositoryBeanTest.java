package grupo12.practico.repositories.Provider;

import grupo12.practico.models.Provider;
import grupo12.practico.repositories.NodosPerifericosConfig;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
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
        Provider result = repository.createProvider(testProvider);

        // Assert
        assertNotNull(result);
        assertEquals(testProvider, result);

        verify(entityManager).persist(testProvider);
    }

    @Test
    @DisplayName("create - Allows null provider payload and delegates to EntityManager")
    void testCreate_NullProvider() {
        Provider result = repository.createProvider(null);

        assertNull(result);
        verify(entityManager).persist(null);
    }

    @Test
    @DisplayName("findById - Should return provider by ID")
    void testFindById_Success() {
        // Arrange
        when(entityManager.find(Provider.class, "provider-id-123")).thenReturn(testProvider);

        // Act
        Provider result = repository.findProviderById("provider-id-123");

        // Assert
        assertNotNull(result);
        assertEquals(testProvider, result);

        verify(entityManager).find(Provider.class, "provider-id-123");
    }

    @ParameterizedTest(name = "findById - Should return null for ID \"{0}\"")
    @NullSource
    @ValueSource(strings = { "", "   " })
    void testFindById_InvalidInputs(String id) {
        Provider result = repository.findProviderById(id);

        assertNull(result);
        verify(entityManager).find(Provider.class, id);
    }

    @Test
    @DisplayName("findById - Should return null when provider not found")
    void testFindById_NotFound() {
        // Arrange
        when(entityManager.find(Provider.class, "non-existent-id")).thenReturn(null);

        // Act
        Provider result = repository.findProviderById("non-existent-id");

        // Assert
        assertNull(result);

        verify(entityManager).find(Provider.class, "non-existent-id");
    }

    @Test
    @DisplayName("findByName - Should return provider by name")
    void testFindByName_Success() {
        // Arrange
        when(entityManager.createQuery(anyString(), eq(Provider.class))).thenReturn(providerQuery);
        when(providerQuery.setParameter(anyString(), any())).thenReturn(providerQuery);
        when(providerQuery.getSingleResult()).thenReturn(testProvider);

        // Act
        Provider result = repository.findProviderByName("Test Provider");

        // Assert
        assertNotNull(result);
        assertEquals(testProvider, result);

        verify(entityManager).createQuery(
                "SELECT p FROM Provider p WHERE p.providerName = :providerName", Provider.class);
        verify(providerQuery).setParameter("providerName", "Test Provider");
        verify(providerQuery).getSingleResult();
    }

    @Test
    @DisplayName("findByName - Should return null when no provider found")
    void testFindByName_NotFound() {
        when(entityManager.createQuery(anyString(), eq(Provider.class))).thenReturn(providerQuery);
        when(providerQuery.setParameter(anyString(), any())).thenReturn(providerQuery);
        when(providerQuery.getSingleResult()).thenThrow(new NoResultException());

        // Act
        Provider result = repository.findProviderByName("Non-existent Provider");

        // Assert
        assertNull(result);
        verify(providerQuery).getSingleResult();
    }

    @Test
    @DisplayName("findByName - Should return null for null name")
    void testFindByName_NullName() {
        when(entityManager.createQuery(anyString(), eq(Provider.class))).thenReturn(providerQuery);
        doThrow(new IllegalArgumentException("name null"))
                .when(providerQuery)
                .setParameter(eq("providerName"), isNull());

        Provider result = repository.findProviderByName(null);

        assertNull(result);
        verify(entityManager).createQuery(
                "SELECT p FROM Provider p WHERE p.providerName = :providerName", Provider.class);
    }

    @ParameterizedTest(name = "findByName - Should return null for name \"{0}\"")
    @ValueSource(strings = { "", "   " })
    void testFindByName_InvalidWhitespace(String providerName) {
        when(entityManager.createQuery(anyString(), eq(Provider.class))).thenReturn(providerQuery);
        when(providerQuery.setParameter(anyString(), any())).thenReturn(providerQuery);
        when(providerQuery.getSingleResult()).thenThrow(new NoResultException());

        Provider result = repository.findProviderByName(providerName);

        assertNull(result);
        verify(entityManager).createQuery(
                "SELECT p FROM Provider p WHERE p.providerName = :providerName", Provider.class);
    }

    @Test
    @DisplayName("findAll - Should return all providers")
    void testFindAll_Success() {
        // Arrange
        List<Provider> providers = List.of(testProvider);
        when(entityManager.createQuery(anyString(), eq(Provider.class))).thenReturn(providerQuery);
        when(providerQuery.getResultList()).thenReturn(providers);

        // Act
        List<Provider> result = repository.findAllProviders();

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
        List<Provider> result = repository.findAllProviders();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(providerQuery).getResultList();
    }
}