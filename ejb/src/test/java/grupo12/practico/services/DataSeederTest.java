package grupo12.practico.services;

import grupo12.practico.models.Gender;
import grupo12.practico.models.HcenAdmin;
import grupo12.practico.models.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("DataSeeder Tests")
class DataSeederTest {

    @Mock
    private EntityManager em;

    @Mock
    private TypedQuery<User> userQuery;

    @Mock
    private TypedQuery<User> emailQuery;

    @InjectMocks
    private DataSeeder dataSeeder;

    private HcenAdmin expectedAdmin;

    @BeforeEach
    void setUp() {
        expectedAdmin = new HcenAdmin();
        expectedAdmin.setCi("52537059");
        expectedAdmin.setFirstName("Xavier");
        expectedAdmin.setLastName("Iribarnegaray");
        expectedAdmin.setGender(Gender.MALE);
        expectedAdmin.setEmail("xiribarnegara@hcen.uy");
        expectedAdmin.setPhone("+59899333456");
        expectedAdmin.setDateOfBirth(LocalDate.of(2002, 10, 24));
        expectedAdmin.setAddress("Rio Po 1234, Canelones");
    }

    @Test
    @DisplayName("init - Should skip seeding when SEED is not set to true")
    void testInit_SeedingDisabled() {
        // Act
        dataSeeder.init();

        // Assert
        verifyNoInteractions(em);
    }

    @Test
    @DisplayName("createHcenAdminIfNotExists - Should skip creation when user already exists by CI")
    void testCreateHcenAdminIfNotExists_UserExistsByCi() throws Exception {
        // Arrange
        HcenAdmin existingUser = new HcenAdmin();
        existingUser.setCi("52537059");

        when(em.createQuery(anyString(), eq(User.class))).thenReturn(userQuery);
        when(userQuery.setParameter("ci", "52537059")).thenReturn(userQuery);
        when(userQuery.getSingleResult()).thenReturn(existingUser);

        // Act
        Method method = DataSeeder.class.getDeclaredMethod("createHcenAdminIfNotExists",
                String.class, String.class, String.class, Gender.class, String.class,
                String.class, LocalDate.class, String.class);
        method.setAccessible(true);
        method.invoke(dataSeeder, "52537059", "Xavier", "Iribarnegaray", Gender.MALE,
                "xiribarnegara@hcen.uy", "+59899333456", LocalDate.of(2002, 10, 24),
                "Rio Po 1234, Canelones");

        // Assert
        verify(em, never()).persist(any(HcenAdmin.class));
        verify(userQuery).getSingleResult();
        verify(em, never()).createQuery(contains("email"), eq(User.class));
    }

    @Test
    @DisplayName("createHcenAdminIfNotExists - Should skip creation when user already exists by email")
    void testCreateHcenAdminIfNotExists_UserExistsByEmail() throws Exception {
        // Arrange
        HcenAdmin existingUser = new HcenAdmin();
        existingUser.setEmail("xiribarnegara@hcen.uy");

        when(em.createQuery(anyString(), eq(User.class))).thenReturn(userQuery).thenReturn(emailQuery);
        when(userQuery.setParameter("ci", "52537059")).thenReturn(userQuery);
        when(emailQuery.setParameter("email", "xiribarnegara@hcen.uy")).thenReturn(emailQuery);
        when(userQuery.getSingleResult()).thenThrow(NoResultException.class);
        when(emailQuery.getSingleResult()).thenReturn(existingUser);

        // Act
        Method method = DataSeeder.class.getDeclaredMethod("createHcenAdminIfNotExists",
                String.class, String.class, String.class, Gender.class, String.class,
                String.class, LocalDate.class, String.class);
        method.setAccessible(true);
        method.invoke(dataSeeder, "52537059", "Xavier", "Iribarnegaray", Gender.MALE,
                "xiribarnegara@hcen.uy", "+59899333456", LocalDate.of(2002, 10, 24),
                "Rio Po 1234, Canelones");

        // Assert
        verify(em, never()).persist(any(HcenAdmin.class));
        verify(userQuery).getSingleResult();
        verify(emailQuery).getSingleResult();
    }

    @Test
    @DisplayName("createHcenAdminIfNotExists - Should create admin when user doesn't exist")
    void testCreateHcenAdminIfNotExists_UserDoesNotExist() throws Exception {
        // Arrange
        when(em.createQuery(anyString(), eq(User.class))).thenReturn(userQuery);
        when(userQuery.setParameter("ci", "52537059")).thenReturn(userQuery);
        when(userQuery.getSingleResult()).thenThrow(NoResultException.class);

        // Act
        Method method = DataSeeder.class.getDeclaredMethod("createHcenAdminIfNotExists",
                String.class, String.class, String.class, Gender.class, String.class,
                String.class, LocalDate.class, String.class);
        method.setAccessible(true);
        method.invoke(dataSeeder, "52537059", "Xavier", "Iribarnegaray", Gender.MALE,
                "xiribarnegara@hcen.uy", "+59899333456", LocalDate.of(2002, 10, 24),
                "Rio Po 1234, Canelones");

        // Assert
        ArgumentCaptor<HcenAdmin> adminCaptor = ArgumentCaptor.forClass(HcenAdmin.class);
        verify(em).persist(adminCaptor.capture());

        HcenAdmin createdAdmin = adminCaptor.getValue();
        assertEquals("52537059", createdAdmin.getCi());
        assertEquals("Xavier", createdAdmin.getFirstName());
        assertEquals("Iribarnegaray", createdAdmin.getLastName());
        assertEquals(Gender.MALE, createdAdmin.getGender());
        assertEquals("xiribarnegara@hcen.uy", createdAdmin.getEmail());
        assertEquals("+59899333456", createdAdmin.getPhone());
        assertEquals(LocalDate.of(2002, 10, 24), createdAdmin.getDateOfBirth());
        assertEquals("Rio Po 1234, Canelones", createdAdmin.getAddress());
    }

    @Test
    @DisplayName("createHcenAdminIfNotExists - Should handle query exceptions gracefully")
    void testCreateHcenAdminIfNotExists_QueryException() throws Exception {
        // Arrange
        when(em.createQuery(anyString(), eq(User.class))).thenReturn(userQuery);
        when(userQuery.setParameter("ci", "52537059")).thenReturn(userQuery);
        when(userQuery.getSingleResult()).thenThrow(RuntimeException.class);

        // Act
        Method method = DataSeeder.class.getDeclaredMethod("createHcenAdminIfNotExists",
                String.class, String.class, String.class, Gender.class, String.class,
                String.class, LocalDate.class, String.class);
        method.setAccessible(true);

        // Should throw InvocationTargetException wrapping the RuntimeException
        InvocationTargetException exception = assertThrows(InvocationTargetException.class, () -> {
            method.invoke(dataSeeder, "52537059", "Xavier", "Iribarnegaray", Gender.MALE,
                    "xiribarnegara@hcen.uy", "+59899333456", LocalDate.of(2002, 10, 24),
                    "Rio Po 1234, Canelones");
        });

        // Verify the cause is RuntimeException
        assertInstanceOf(RuntimeException.class, exception.getCause());

        // Verify no admin was persisted
        verify(em, never()).persist(any(HcenAdmin.class));
    }
}