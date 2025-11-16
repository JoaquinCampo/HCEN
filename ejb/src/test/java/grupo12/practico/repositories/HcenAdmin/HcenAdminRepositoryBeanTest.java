package grupo12.practico.repositories.HcenAdmin;

import grupo12.practico.models.HcenAdmin;
import grupo12.practico.models.Gender;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("HcenAdminRepositoryBean Tests")
class HcenAdminRepositoryBeanTest {

    @Mock
    private EntityManager entityManager;

    @Mock
    private TypedQuery<HcenAdmin> hcenAdminQuery;

    private HcenAdminRepositoryBean repository;

    private HcenAdmin testHcenAdmin;

    @BeforeEach
    void setUp() throws Exception {
        // Setup test data
        testHcenAdmin = new HcenAdmin();
        testHcenAdmin.setId("admin-id-123");
        testHcenAdmin.setCi("11223344");
        testHcenAdmin.setFirstName("Admin");
        testHcenAdmin.setLastName("User");
        testHcenAdmin.setGender(Gender.MALE);
        testHcenAdmin.setEmail("admin@example.com");
        testHcenAdmin.setPhone("+59811223344");
        testHcenAdmin.setAddress("789 Admin St");
        testHcenAdmin.setDateOfBirth(LocalDate.of(1980, 1, 1));
        testHcenAdmin.setCreatedAt(LocalDate.now());
        testHcenAdmin.setUpdatedAt(LocalDate.now());

        // Create repository instance and inject mocks
        repository = new HcenAdminRepositoryBean();

        // Inject EntityManager using reflection
        java.lang.reflect.Field emField = HcenAdminRepositoryBean.class.getDeclaredField("em");
        emField.setAccessible(true);
        emField.set(repository, entityManager);
    }

    @Test
    @DisplayName("create - Should persist hcen admin")
    void testCreate_Success() {
        // Arrange
        // No stubbing needed for persist

        // Act
        HcenAdmin result = repository.create(testHcenAdmin);

        // Assert
        assertNotNull(result);
        assertEquals(testHcenAdmin, result);

        verify(entityManager).persist(testHcenAdmin);
    }

    @Test
    @DisplayName("create - Should throw ValidationException for null hcen admin")
    void testCreate_NullHcenAdmin() {
        // Act & Assert
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> repository.create(null));

        assertEquals("HcenAdmin must not be null", exception.getMessage());
        verify(entityManager, never()).persist(any());
    }

    @Test
    @DisplayName("findByCi - Should return hcen admin by CI")
    void testFindByCi_Success() {
        // Arrange
        when(entityManager.createQuery(anyString(), eq(HcenAdmin.class))).thenReturn(hcenAdminQuery);
        when(hcenAdminQuery.setParameter(anyString(), any())).thenReturn(hcenAdminQuery);
        when(hcenAdminQuery.getSingleResult()).thenReturn(testHcenAdmin);

        // Act
        HcenAdmin result = repository.findByCi("11223344");

        // Assert
        assertNotNull(result);
        assertEquals(testHcenAdmin, result);

        verify(entityManager).createQuery(
                "SELECT h FROM HcenAdmin h WHERE h.ci = :ci", HcenAdmin.class);
        verify(hcenAdminQuery).setParameter("ci", "11223344");
        verify(hcenAdminQuery).getSingleResult();
    }

    @Test
    @DisplayName("findByCi - Should return null when no admin found")
    void testFindByCi_NotFound() {
        // Arrange
        when(entityManager.createQuery(anyString(), eq(HcenAdmin.class))).thenReturn(hcenAdminQuery);
        when(hcenAdminQuery.setParameter(anyString(), any())).thenReturn(hcenAdminQuery);
        when(hcenAdminQuery.getSingleResult()).thenThrow(new NoResultException());

        // Act
        HcenAdmin result = repository.findByCi("99999999");

        // Assert
        assertNull(result);

        verify(hcenAdminQuery).getSingleResult();
    }

    @Test
    @DisplayName("findByCi - Should return null for null CI")
    void testFindByCi_NullCi() {
        // Act
        HcenAdmin result = repository.findByCi(null);

        // Assert
        assertNull(result);
        verify(entityManager, never()).createQuery(anyString(), any());
    }

    @Test
    @DisplayName("findByCi - Should return null for empty CI")
    void testFindByCi_EmptyCi() {
        // Act
        HcenAdmin result = repository.findByCi("");

        // Assert
        assertNull(result);
        verify(entityManager, never()).createQuery(anyString(), any());
    }

    @Test
    @DisplayName("findByCi - Should return null for blank CI")
    void testFindByCi_BlankCi() {
        // Act
        HcenAdmin result = repository.findByCi("   ");

        // Assert
        assertNull(result);
        verify(entityManager, never()).createQuery(anyString(), any());
    }

    @Test
    @DisplayName("findById - Should return hcen admin by ID")
    void testFindById_Success() {
        // Arrange
        when(entityManager.find(HcenAdmin.class, "admin-id-123")).thenReturn(testHcenAdmin);

        // Act
        HcenAdmin result = repository.findById("admin-id-123");

        // Assert
        assertNotNull(result);
        assertEquals(testHcenAdmin, result);

        verify(entityManager).find(HcenAdmin.class, "admin-id-123");
    }

    @Test
    @DisplayName("findById - Should return null for null ID")
    void testFindById_NullId() {
        // Act
        HcenAdmin result = repository.findById(null);

        // Assert
        assertNull(result);
        verify(entityManager, never()).find(any(), any());
    }

    @Test
    @DisplayName("findById - Should return null for empty ID")
    void testFindById_EmptyId() {
        // Act
        HcenAdmin result = repository.findById("");

        // Assert
        assertNull(result);
        verify(entityManager, never()).find(any(), any());
    }

    @Test
    @DisplayName("findById - Should return null for blank ID")
    void testFindById_BlankId() {
        // Act
        HcenAdmin result = repository.findById("   ");

        // Assert
        assertNull(result);
        verify(entityManager, never()).find(any(), any());
    }

    @Test
    @DisplayName("findById - Should return null when entity not found")
    void testFindById_NotFound() {
        // Arrange
        when(entityManager.find(HcenAdmin.class, "non-existent-id")).thenReturn(null);

        // Act
        HcenAdmin result = repository.findById("non-existent-id");

        // Assert
        assertNull(result);

        verify(entityManager).find(HcenAdmin.class, "non-existent-id");
    }

    @Test
    @DisplayName("findAll - Should return all hcen admins")
    void testFindAll_Success() {
        // Arrange
        java.util.List<HcenAdmin> admins = java.util.Arrays.asList(testHcenAdmin);
        when(entityManager.createQuery(anyString(), eq(HcenAdmin.class))).thenReturn(hcenAdminQuery);
        when(hcenAdminQuery.getResultList()).thenReturn(admins);

        // Act
        java.util.List<HcenAdmin> result = repository.findAll();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testHcenAdmin, result.get(0));

        verify(entityManager).createQuery(
                "SELECT h FROM HcenAdmin h ORDER BY h.createdAt DESC", HcenAdmin.class);
        verify(hcenAdminQuery).getResultList();
    }

    @Test
    @DisplayName("findAll - Should return empty list when no admins exist")
    void testFindAll_Empty() {
        // Arrange
        java.util.List<HcenAdmin> emptyList = java.util.Collections.emptyList();
        when(entityManager.createQuery(anyString(), eq(HcenAdmin.class))).thenReturn(hcenAdminQuery);
        when(hcenAdminQuery.getResultList()).thenReturn(emptyList);

        // Act
        java.util.List<HcenAdmin> result = repository.findAll();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(hcenAdminQuery).getResultList();
    }
}