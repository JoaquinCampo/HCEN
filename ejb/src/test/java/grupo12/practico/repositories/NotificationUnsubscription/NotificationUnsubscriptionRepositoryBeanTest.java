package grupo12.practico.repositories.NotificationUnsubscription;

import grupo12.practico.models.HealthUser;
import grupo12.practico.models.NotificationUnsubscription;
import grupo12.practico.models.NotificationType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("NotificationUnsubscriptionRepositoryBean Tests")
class NotificationUnsubscriptionRepositoryBeanTest {

    @Mock
    private EntityManager em;

    @Mock
    private TypedQuery<NotificationUnsubscription> entityQuery;

    @Mock
    private TypedQuery<Long> countQuery;

    private NotificationUnsubscriptionRepositoryBean repository;

    private NotificationUnsubscription unsubscription;
    private HealthUser user;

    @BeforeEach
    void setUp() throws Exception {
        repository = new NotificationUnsubscriptionRepositoryBean();

        Field emField = NotificationUnsubscriptionRepositoryBean.class.getDeclaredField("em");
        emField.setAccessible(true);
        emField.set(repository, em);

        user = new HealthUser();
        user.setId("user123");
        user.setCi("12345678");

        unsubscription = new NotificationUnsubscription();
        unsubscription.setUser(user);
        unsubscription.setSubscribedToAccessRequest(true);
        unsubscription.setSubscribedToClinicalHistoryAccess(false);
    }

    @Test
    @DisplayName("add - Should persist entity successfully")
    void add_ShouldPersistEntitySuccessfully() {
        NotificationUnsubscription result = repository.add(unsubscription);

        assertNotNull(result);
        verify(em).persist(unsubscription);
    }

    @Test
    @DisplayName("remove - Should remove entity by userId")
    void remove_ShouldRemoveEntityByUserId() {
        when(em.createQuery(anyString(), eq(NotificationUnsubscription.class))).thenReturn(entityQuery);
        when(entityQuery.setParameter(anyString(), anyString())).thenReturn(entityQuery);
        when(entityQuery.getResultList()).thenReturn(Arrays.asList(unsubscription));

        NotificationUnsubscription result = repository.remove("user123");

        assertNotNull(result);
        verify(em).remove(unsubscription);
    }

    @Test
    @DisplayName("remove - Should return null when userId is null")
    void remove_ShouldReturnNullWhenUserIdIsNull() {
        NotificationUnsubscription result = repository.remove(null);

        assertNull(result);
        verify(em, never()).createQuery(anyString(), eq(NotificationUnsubscription.class));
    }

    @Test
    @DisplayName("remove - Should return null when userId is empty")
    void remove_ShouldReturnNullWhenUserIdIsEmpty() {
        NotificationUnsubscription result = repository.remove("  ");

        assertNull(result);
        verify(em, never()).createQuery(anyString(), eq(NotificationUnsubscription.class));
    }

    @Test
    @DisplayName("remove - Should return null when entity not found")
    void remove_ShouldReturnNullWhenEntityNotFound() {
        when(em.createQuery(anyString(), eq(NotificationUnsubscription.class))).thenReturn(entityQuery);
        when(entityQuery.setParameter(anyString(), anyString())).thenReturn(entityQuery);
        when(entityQuery.getResultList()).thenReturn(Collections.emptyList());

        NotificationUnsubscription result = repository.remove("user123");

        assertNull(result);
        verify(em, never()).remove(any());
    }

    @Test
    @DisplayName("existsByUserId - Should return true when entity exists")
    void existsByUserId_ShouldReturnTrueWhenEntityExists() {
        when(em.createQuery(anyString(), eq(Long.class))).thenReturn(countQuery);
        when(countQuery.setParameter(anyString(), anyString())).thenReturn(countQuery);
        when(countQuery.getSingleResult()).thenReturn(1L);

        boolean result = repository.existsByUserId("user123");

        assertTrue(result);
    }

    @Test
    @DisplayName("existsByUserId - Should return false when entity does not exist")
    void existsByUserId_ShouldReturnFalseWhenEntityDoesNotExist() {
        when(em.createQuery(anyString(), eq(Long.class))).thenReturn(countQuery);
        when(countQuery.setParameter(anyString(), anyString())).thenReturn(countQuery);
        when(countQuery.getSingleResult()).thenReturn(0L);

        boolean result = repository.existsByUserId("user123");

        assertFalse(result);
    }

    @Test
    @DisplayName("existsByUserId - Should return false when userId is null")
    void existsByUserId_ShouldReturnFalseWhenUserIdIsNull() {
        boolean result = repository.existsByUserId(null);

        assertFalse(result);
        verify(em, never()).createQuery(anyString(), eq(Long.class));
    }

    @Test
    @DisplayName("existsByUserId - Should return false when userId is empty")
    void existsByUserId_ShouldReturnFalseWhenUserIdIsEmpty() {
        boolean result = repository.existsByUserId("  ");

        assertFalse(result);
        verify(em, never()).createQuery(anyString(), eq(Long.class));
    }

    @Test
    @DisplayName("findByUserId - Should return entity when found")
    void findByUserId_ShouldReturnEntityWhenFound() {
        when(em.createQuery(anyString(), eq(NotificationUnsubscription.class))).thenReturn(entityQuery);
        when(entityQuery.setParameter(anyString(), anyString())).thenReturn(entityQuery);
        when(entityQuery.getResultList()).thenReturn(Arrays.asList(unsubscription));

        NotificationUnsubscription result = repository.findByUserId("user123");

        assertNotNull(result);
        assertEquals(unsubscription, result);
    }

    @Test
    @DisplayName("findByUserId - Should return null when not found")
    void findByUserId_ShouldReturnNullWhenNotFound() {
        when(em.createQuery(anyString(), eq(NotificationUnsubscription.class))).thenReturn(entityQuery);
        when(entityQuery.setParameter(anyString(), anyString())).thenReturn(entityQuery);
        when(entityQuery.getResultList()).thenReturn(Collections.emptyList());

        NotificationUnsubscription result = repository.findByUserId("user123");

        assertNull(result);
    }

    @Test
    @DisplayName("findByUserId - Should return null when userId is null")
    void findByUserId_ShouldReturnNullWhenUserIdIsNull() {
        NotificationUnsubscription result = repository.findByUserId(null);

        assertNull(result);
        verify(em, never()).createQuery(anyString(), eq(NotificationUnsubscription.class));
    }

    @Test
    @DisplayName("findByUserId - Should return null when userId is empty")
    void findByUserId_ShouldReturnNullWhenUserIdIsEmpty() {
        NotificationUnsubscription result = repository.findByUserId("  ");

        assertNull(result);
        verify(em, never()).createQuery(anyString(), eq(NotificationUnsubscription.class));
    }

    @Test
    @DisplayName("updateSubscription - Should update ACCESS_REQUEST subscription")
    void updateSubscription_ShouldUpdateAccessRequestSubscription() {
        when(em.createQuery(anyString(), eq(NotificationUnsubscription.class))).thenReturn(entityQuery);
        when(entityQuery.setParameter(anyString(), anyString())).thenReturn(entityQuery);
        when(entityQuery.getResultList()).thenReturn(Arrays.asList(unsubscription));
        when(em.merge(any(NotificationUnsubscription.class))).thenReturn(unsubscription);

        NotificationUnsubscription result = repository.updateSubscription("user123", NotificationType.ACCESS_REQUEST,
                false);

        assertNotNull(result);
        verify(em).merge(any(NotificationUnsubscription.class));
    }

    @Test
    @DisplayName("updateSubscription - Should update CLINICAL_HISTORY_ACCESS subscription")
    void updateSubscription_ShouldUpdateClinicalHistoryAccessSubscription() {
        when(em.createQuery(anyString(), eq(NotificationUnsubscription.class))).thenReturn(entityQuery);
        when(entityQuery.setParameter(anyString(), anyString())).thenReturn(entityQuery);
        when(entityQuery.getResultList()).thenReturn(Arrays.asList(unsubscription));
        when(em.merge(any(NotificationUnsubscription.class))).thenReturn(unsubscription);

        NotificationUnsubscription result = repository.updateSubscription("user123",
                NotificationType.CLINICAL_HISTORY_ACCESS, true);

        assertNotNull(result);
        verify(em).merge(any(NotificationUnsubscription.class));
    }

    @Test
    @DisplayName("updateSubscription - Should return null when userId is null")
    void updateSubscription_ShouldReturnNullWhenUserIdIsNull() {
        NotificationUnsubscription result = repository.updateSubscription(null, NotificationType.ACCESS_REQUEST, true);

        assertNull(result);
        verify(em, never()).merge(any());
    }

    @Test
    @DisplayName("updateSubscription - Should return null when userId is empty")
    void updateSubscription_ShouldReturnNullWhenUserIdIsEmpty() {
        NotificationUnsubscription result = repository.updateSubscription("  ", NotificationType.ACCESS_REQUEST, true);

        assertNull(result);
        verify(em, never()).merge(any());
    }

    @Test
    @DisplayName("updateSubscription - Should return null when type is null")
    void updateSubscription_ShouldReturnNullWhenTypeIsNull() {
        NotificationUnsubscription result = repository.updateSubscription("user123", null, true);

        assertNull(result);
        verify(em, never()).merge(any());
    }

    @Test
    @DisplayName("updateSubscription - Should return null when entity not found")
    void updateSubscription_ShouldReturnNullWhenEntityNotFound() {
        when(em.createQuery(anyString(), eq(NotificationUnsubscription.class))).thenReturn(entityQuery);
        when(entityQuery.setParameter(anyString(), anyString())).thenReturn(entityQuery);
        when(entityQuery.getResultList()).thenReturn(Collections.emptyList());

        NotificationUnsubscription result = repository.updateSubscription("user123", NotificationType.ACCESS_REQUEST,
                true);

        assertNull(result);
        verify(em, never()).merge(any());
    }
}
