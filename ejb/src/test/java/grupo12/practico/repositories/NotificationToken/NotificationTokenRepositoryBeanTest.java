package grupo12.practico.repositories.NotificationToken;

import grupo12.practico.models.HealthUser;
import grupo12.practico.models.NotificationToken;
import grupo12.practico.models.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("NotificationTokenRepositoryBean Tests")
class NotificationTokenRepositoryBeanTest {

    @Mock
    private EntityManager entityManager;

    @Mock
    private TypedQuery<NotificationToken> typedQuery;

    private NotificationTokenRepositoryBean repository;

    private NotificationToken notificationToken;
    private User user;

    @BeforeEach
    void setUp() throws Exception {
        repository = new NotificationTokenRepositoryBean();

        // Use reflection to inject mocked EntityManager
        var emField = NotificationTokenRepositoryBean.class.getDeclaredField("em");
        emField.setAccessible(true);
        emField.set(repository, entityManager);

        // Setup test data
        user = new HealthUser(); // Use concrete subclass
        user.setId("user-id");
        user.setCi("12345678");

        notificationToken = new NotificationToken();
        notificationToken.setId("token-id");
        notificationToken.setUser(user);
        notificationToken.setToken("device-token-123");
        notificationToken.setLastUsedAt(LocalDateTime.of(2023, 1, 1, 10, 0));
        // createdAt and updatedAt are set automatically by @PrePersist/@PreUpdate
    }

    @Test
    @DisplayName("add - Should persist and return notification token")
    void add_ShouldPersistAndReturnNotificationToken() {
        doNothing().when(entityManager).persist(notificationToken);

        NotificationToken result = repository.add(notificationToken);

        assertEquals(notificationToken, result);
        verify(entityManager).persist(notificationToken);
    }

    @Test
    @DisplayName("findByUserId - Should return list of notification tokens for user")
    void findByUserId_ShouldReturnListOfNotificationTokensForUser() {
        List<NotificationToken> expectedTokens = Arrays.asList(notificationToken);
        when(entityManager.createQuery(anyString(), eq(NotificationToken.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter("userId", "user-id")).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(expectedTokens);

        List<NotificationToken> result = repository.findByUserId("user-id");

        assertEquals(expectedTokens, result);
        verify(entityManager).createQuery("SELECT t FROM NotificationToken t WHERE t.user.id = :userId",
                NotificationToken.class);
        verify(typedQuery).setParameter("userId", "user-id");
        verify(typedQuery).getResultList();
    }

    @Test
    @DisplayName("findByUserId - Should return empty list when no tokens found")
    void findByUserId_ShouldReturnEmptyListWhenNoTokensFound() {
        when(entityManager.createQuery(anyString(), eq(NotificationToken.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter("userId", "user-id")).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(Collections.emptyList());

        List<NotificationToken> result = repository.findByUserId("user-id");

        assertTrue(result.isEmpty());
        verify(typedQuery).getResultList();
    }

    @Test
    @DisplayName("findByToken - Should return notification token when found")
    void findByToken_ShouldReturnNotificationTokenWhenFound() {
        List<NotificationToken> tokens = Arrays.asList(notificationToken);
        when(entityManager.createQuery(anyString(), eq(NotificationToken.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter("token", "device-token-123")).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(tokens);

        NotificationToken result = repository.findByToken("device-token-123");

        assertEquals(notificationToken, result);
        verify(entityManager).createQuery("SELECT t FROM NotificationToken t WHERE t.token = :token",
                NotificationToken.class);
        verify(typedQuery).setParameter("token", "device-token-123");
        verify(typedQuery).getResultList();
    }

    @Test
    @DisplayName("findByToken - Should return null when token not found")
    void findByToken_ShouldReturnNullWhenTokenNotFound() {
        when(entityManager.createQuery(anyString(), eq(NotificationToken.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter("token", "non-existent-token")).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(Collections.emptyList());

        NotificationToken result = repository.findByToken("non-existent-token");

        assertNull(result);
        verify(typedQuery).getResultList();
    }

    @Test
    @DisplayName("updateLastUsedAt - Should update last used timestamp and return token")
    void updateLastUsedAt_ShouldUpdateLastUsedTimestampAndReturnToken() {
        when(entityManager.createQuery(anyString(), eq(NotificationToken.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter("token", "device-token-123")).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(Arrays.asList(notificationToken));
        when(entityManager.merge(notificationToken)).thenReturn(notificationToken);

        NotificationToken result = repository.updateLastUsedAt("device-token-123");

        assertEquals(notificationToken, result);
        verify(entityManager).merge(notificationToken);
        // Note: lastUsedAt would be updated by the merge operation
    }

    @Test
    @DisplayName("updateLastUsedAt - Should return null when token not found")
    void updateLastUsedAt_ShouldReturnNullWhenTokenNotFound() {
        when(entityManager.createQuery(anyString(), eq(NotificationToken.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter("token", "non-existent-token")).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(Collections.emptyList());

        NotificationToken result = repository.updateLastUsedAt("non-existent-token");

        assertNull(result);
        verify(entityManager, never()).merge(any());
    }

    @Test
    @DisplayName("delete - Should merge and remove notification token")
    void delete_ShouldMergeAndRemoveNotificationToken() {
        NotificationToken managedToken = new NotificationToken();
        when(entityManager.merge(notificationToken)).thenReturn(managedToken);

        repository.delete(notificationToken);

        verify(entityManager).merge(notificationToken);
        verify(entityManager).remove(managedToken);
    }
}