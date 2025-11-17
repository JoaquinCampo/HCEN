package grupo12.practico.services.NotificationToken;

import grupo12.practico.dtos.HealthUser.HealthUserDTO;
import grupo12.practico.dtos.NotificationToken.NotificationSubscriptionDTO;
import grupo12.practico.dtos.NotificationToken.NotificationTokenDTO;
import grupo12.practico.models.NotificationToken;
import grupo12.practico.models.NotificationType;
import grupo12.practico.models.NotificationUnsubscription;
import grupo12.practico.models.HealthUser;
import grupo12.practico.repositories.NotificationToken.NotificationTokenRepositoryLocal;
import grupo12.practico.repositories.NotificationUnsubscription.NotificationUnsubscriptionRepositoryLocal;
import grupo12.practico.services.HealthUser.HealthUserServiceLocal;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("NotificationTokenServiceBean Tests")
class NotificationTokenServiceBeanTest {

    @Mock
    private NotificationTokenRepositoryLocal notificationTokenRepository;

    @Mock
    private NotificationUnsubscriptionRepositoryLocal notificationUnsubscriptionRepository;

    @Mock
    private HealthUserServiceLocal healthUserService;

    private NotificationTokenServiceBean service;

    private NotificationTokenDTO notificationTokenDTO;
    private HealthUserDTO healthUserDTO;
    private NotificationToken notificationToken;
    private HealthUser user;

    @BeforeEach
    void setUp() throws Exception {
        service = new NotificationTokenServiceBean();

        // Use reflection to inject mocked dependencies
        injectField("notificationTokenRepository", notificationTokenRepository);
        injectField("notificationUnsubscriptionRepository", notificationUnsubscriptionRepository);
        injectField("healthUserService", healthUserService);

        // Setup test data
        healthUserDTO = new HealthUserDTO();
        healthUserDTO.setId("user-id");
        healthUserDTO.setCi("12345678");
        healthUserDTO.setFirstName("John");
        healthUserDTO.setLastName("Doe");

        user = new HealthUser();
        user.setId("user-id");
        user.setCi("12345678");

        notificationToken = new NotificationToken();
        notificationToken.setId("token-id");
        notificationToken.setUser(user);
        notificationToken.setToken("device-token-123");
        notificationToken.setLastUsedAt(LocalDateTime.of(2023, 1, 1, 10, 0));

        notificationTokenDTO = new NotificationTokenDTO();
        notificationTokenDTO.setId("token-id");
        notificationTokenDTO.setUserCi("12345678");
        notificationTokenDTO.setToken("device-token-123");
    }

    private void injectField(String fieldName, Object value) throws Exception {
        Field field = NotificationTokenServiceBean.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(service, value);
    }

    @Test
    @DisplayName("add - Should add new notification token successfully")
    void add_ShouldAddNewNotificationTokenSuccessfully() {
        when(healthUserService.findHealthUserByCi("12345678")).thenReturn(healthUserDTO);
        when(notificationTokenRepository.findByToken("device-token-123")).thenReturn(null);
        when(notificationTokenRepository.add(any(NotificationToken.class))).thenReturn(notificationToken);

        NotificationTokenDTO result = service.add(notificationTokenDTO);

        assertNotNull(result);
        assertEquals("token-id", result.getId());
        assertEquals("12345678", result.getUserCi());
        assertEquals("device-token-123", result.getToken());

        verify(notificationTokenRepository).add(any(NotificationToken.class));
    }

    @Test
    @DisplayName("add - Should update existing token when token already exists")
    void add_ShouldUpdateExistingTokenWhenTokenAlreadyExists() {
        when(healthUserService.findHealthUserByCi("12345678")).thenReturn(healthUserDTO);
        when(notificationTokenRepository.findByToken("device-token-123")).thenReturn(notificationToken);
        when(notificationTokenRepository.updateLastUsedAt("device-token-123")).thenReturn(notificationToken);

        NotificationTokenDTO result = service.add(notificationTokenDTO);

        assertNotNull(result);
        assertEquals("token-id", result.getId());
        assertEquals("12345678", result.getUserCi());
        assertEquals("device-token-123", result.getToken());

        verify(notificationTokenRepository).updateLastUsedAt("device-token-123");
        verify(notificationTokenRepository, never()).add(any());
    }

    @Test
    @DisplayName("add - Should throw ValidationException for null DTO")
    void add_ShouldThrowValidationExceptionForNullDto() {
        ValidationException exception = assertThrows(ValidationException.class,
                () -> service.add(null));

        assertEquals("Token payload is required", exception.getMessage());
    }

    @Test
    @DisplayName("add - Should throw ValidationException for null user CI")
    void add_ShouldThrowValidationExceptionForNullUserCi() {
        notificationTokenDTO.setUserCi(null);

        ValidationException exception = assertThrows(ValidationException.class,
                () -> service.add(notificationTokenDTO));

        assertEquals("userCi is required", exception.getMessage());
    }

    @Test
    @DisplayName("add - Should throw ValidationException for blank user CI")
    void add_ShouldThrowValidationExceptionForBlankUserCi() {
        notificationTokenDTO.setUserCi("");

        ValidationException exception = assertThrows(ValidationException.class,
                () -> service.add(notificationTokenDTO));

        assertEquals("userCi is required", exception.getMessage());
    }

    @Test
    @DisplayName("add - Should throw ValidationException for null token")
    void add_ShouldThrowValidationExceptionForNullToken() {
        notificationTokenDTO.setToken(null);

        ValidationException exception = assertThrows(ValidationException.class,
                () -> service.add(notificationTokenDTO));

        assertEquals("token is required", exception.getMessage());
    }

    @Test
    @DisplayName("add - Should throw ValidationException when user not found")
    void add_ShouldThrowValidationExceptionWhenUserNotFound() {
        when(healthUserService.findHealthUserByCi("12345678")).thenReturn(null);

        ValidationException exception = assertThrows(ValidationException.class,
                () -> service.add(notificationTokenDTO));

        assertEquals("User not found with CI: 12345678", exception.getMessage());
    }

    @Test
    @DisplayName("findByUserCi - Should return list of notification token DTOs")
    void findByUserCi_ShouldReturnListOfNotificationTokenDTOs() {
        List<NotificationToken> tokens = Arrays.asList(notificationToken);
        when(healthUserService.findHealthUserByCi("12345678")).thenReturn(healthUserDTO);
        when(notificationTokenRepository.findByUserId("user-id")).thenReturn(tokens);

        List<NotificationTokenDTO> result = service.findByUserCi("12345678");

        assertNotNull(result);
        assertEquals(1, result.size());
        NotificationTokenDTO dto = result.get(0);
        assertEquals("token-id", dto.getId());
        assertEquals("12345678", dto.getUserCi());
        assertEquals("device-token-123", dto.getToken());
    }

    @Test
    @DisplayName("findByUserCi - Should throw ValidationException when user not found")
    void findByUserCi_ShouldThrowValidationExceptionWhenUserNotFound() {
        when(healthUserService.findHealthUserByCi("12345678")).thenReturn(null);

        ValidationException exception = assertThrows(ValidationException.class,
                () -> service.findByUserCi("12345678"));

        assertEquals("User not found with CI: 12345678", exception.getMessage());
    }

    @Test
    @DisplayName("delete - Should delete notification token successfully")
    void delete_ShouldDeleteNotificationTokenSuccessfully() {
        List<NotificationToken> tokens = Arrays.asList(notificationToken);
        when(healthUserService.findHealthUserByCi("12345678")).thenReturn(healthUserDTO);
        when(notificationTokenRepository.findByUserId("user-id")).thenReturn(tokens);

        service.delete(notificationTokenDTO);

        verify(notificationTokenRepository).delete(notificationToken);
    }

    @Test
    @DisplayName("delete - Should throw ValidationException for null DTO")
    void delete_ShouldThrowValidationExceptionForNullDto() {
        ValidationException exception = assertThrows(ValidationException.class,
                () -> service.delete(null));

        assertEquals("userCi and token are required", exception.getMessage());
    }

    @Test
    @DisplayName("delete - Should throw ValidationException for null user CI")
    void delete_ShouldThrowValidationExceptionForNullUserCi() {
        notificationTokenDTO.setUserCi(null);

        ValidationException exception = assertThrows(ValidationException.class,
                () -> service.delete(notificationTokenDTO));

        assertEquals("userCi and token are required", exception.getMessage());
    }

    @Test
    @DisplayName("delete - Should throw ValidationException for null token")
    void delete_ShouldThrowValidationExceptionForNullToken() {
        notificationTokenDTO.setToken(null);

        ValidationException exception = assertThrows(ValidationException.class,
                () -> service.delete(notificationTokenDTO));

        assertEquals("userCi and token are required", exception.getMessage());
    }

    @Test
    @DisplayName("delete - Should throw ValidationException when user not found")
    void delete_ShouldThrowValidationExceptionWhenUserNotFound() {
        notificationTokenDTO.setToken("device-token-123");
        when(healthUserService.findHealthUserByCi("12345678")).thenReturn(null);

        ValidationException exception = assertThrows(ValidationException.class,
                () -> service.delete(notificationTokenDTO));

        assertEquals("User not found with CI: 12345678", exception.getMessage());
    }

    @Test
    @DisplayName("unsubscribe - Should create unsubscription record for new user")
    void unsubscribe_ShouldCreateUnsubscriptionRecordForNewUser() {
        when(healthUserService.findHealthUserByCi("12345678")).thenReturn(healthUserDTO);
        when(notificationUnsubscriptionRepository.findByUserId("user-id")).thenReturn(null);

        service.unsubscribe("12345678", NotificationType.ACCESS_REQUEST);

        verify(notificationUnsubscriptionRepository).add(any(NotificationUnsubscription.class));
    }

    @Test
    @DisplayName("unsubscribe - Should update existing unsubscription record")
    void unsubscribe_ShouldUpdateExistingUnsubscriptionRecord() {
        NotificationUnsubscription existing = new NotificationUnsubscription();
        when(healthUserService.findHealthUserByCi("12345678")).thenReturn(healthUserDTO);
        when(notificationUnsubscriptionRepository.findByUserId("user-id")).thenReturn(existing);

        service.unsubscribe("12345678", NotificationType.ACCESS_REQUEST);

        verify(notificationUnsubscriptionRepository).updateSubscription("user-id", NotificationType.ACCESS_REQUEST,
                false);
    }

    @Test
    @DisplayName("unsubscribe - Should throw ValidationException for null user CI")
    void unsubscribe_ShouldThrowValidationExceptionForNullUserCi() {
        ValidationException exception = assertThrows(ValidationException.class,
                () -> service.unsubscribe(null, NotificationType.ACCESS_REQUEST));

        assertEquals("userCi is required", exception.getMessage());
    }

    @Test
    @DisplayName("unsubscribe - Should throw ValidationException for blank user CI")
    void unsubscribe_ShouldThrowValidationExceptionForBlankUserCi() {
        ValidationException exception = assertThrows(ValidationException.class,
                () -> service.unsubscribe("", NotificationType.ACCESS_REQUEST));

        assertEquals("userCi is required", exception.getMessage());
    }

    @Test
    @DisplayName("unsubscribe - Should throw ValidationException for null notification type")
    void unsubscribe_ShouldThrowValidationExceptionForNullNotificationType() {
        ValidationException exception = assertThrows(ValidationException.class,
                () -> service.unsubscribe("12345678", null));

        assertEquals("notificationType is required", exception.getMessage());
    }

    @Test
    @DisplayName("unsubscribe - Should throw ValidationException when user not found")
    void unsubscribe_ShouldThrowValidationExceptionWhenUserNotFound() {
        when(healthUserService.findHealthUserByCi("12345678")).thenReturn(null);

        ValidationException exception = assertThrows(ValidationException.class,
                () -> service.unsubscribe("12345678", NotificationType.ACCESS_REQUEST));

        assertEquals("User not found with CI: 12345678", exception.getMessage());
    }

    @Test
    @DisplayName("subscribe - Should do nothing when no existing preferences")
    void subscribe_ShouldDoNothingWhenNoExistingPreferences() {
        when(healthUserService.findHealthUserByCi("12345678")).thenReturn(healthUserDTO);
        when(notificationUnsubscriptionRepository.findByUserId("user-id")).thenReturn(null);

        service.subscribe("12345678", NotificationType.ACCESS_REQUEST);

        verify(notificationUnsubscriptionRepository, never()).updateSubscription(anyString(),
                any(NotificationType.class), anyBoolean());
        verify(notificationUnsubscriptionRepository, never()).add(any());
    }

    @Test
    @DisplayName("subscribe - Should update existing unsubscription record")
    void subscribe_ShouldUpdateExistingUnsubscriptionRecord() {
        NotificationUnsubscription existing = new NotificationUnsubscription();
        when(healthUserService.findHealthUserByCi("12345678")).thenReturn(healthUserDTO);
        when(notificationUnsubscriptionRepository.findByUserId("user-id")).thenReturn(existing);

        service.subscribe("12345678", NotificationType.ACCESS_REQUEST);

        verify(notificationUnsubscriptionRepository).updateSubscription("user-id", NotificationType.ACCESS_REQUEST,
                true);
    }

    @Test
    @DisplayName("isUserSubscribedToNotificationType - Should return true by default when no preferences")
    void isUserSubscribedToNotificationType_ShouldReturnTrueByDefaultWhenNoPreferences() {
        when(healthUserService.findHealthUserByCi("12345678")).thenReturn(healthUserDTO);
        when(notificationUnsubscriptionRepository.findByUserId("user-id")).thenReturn(null);

        boolean result = service.isUserSubscribedToNotificationType("12345678", NotificationType.ACCESS_REQUEST);

        assertTrue(result);
    }

    @Test
    @DisplayName("isUserSubscribedToNotificationType - Should return false when unsubscribed")
    void isUserSubscribedToNotificationType_ShouldReturnFalseWhenUnsubscribed() {
        NotificationUnsubscription prefs = new NotificationUnsubscription();
        prefs.setSubscribedToAccessRequest(false);
        when(healthUserService.findHealthUserByCi("12345678")).thenReturn(healthUserDTO);
        when(notificationUnsubscriptionRepository.findByUserId("user-id")).thenReturn(prefs);

        boolean result = service.isUserSubscribedToNotificationType("12345678", NotificationType.ACCESS_REQUEST);

        assertFalse(result);
    }

    @Test
    @DisplayName("isUserSubscribedToNotificationType - Should return false for null user CI")
    void isUserSubscribedToNotificationType_ShouldReturnFalseForNullUserCi() {
        boolean result = service.isUserSubscribedToNotificationType(null, NotificationType.ACCESS_REQUEST);

        assertFalse(result);
    }

    @Test
    @DisplayName("isUserSubscribedToNotificationType - Should return false for null notification type")
    void isUserSubscribedToNotificationType_ShouldReturnFalseForNullNotificationType() {
        boolean result = service.isUserSubscribedToNotificationType("12345678", null);

        assertFalse(result);
    }

    @Test
    @DisplayName("isUserSubscribedToNotificationType - Should return false when user not found")
    void isUserSubscribedToNotificationType_ShouldReturnFalseWhenUserNotFound() {
        when(healthUserService.findHealthUserByCi("12345678")).thenReturn(null);

        boolean result = service.isUserSubscribedToNotificationType("12345678", NotificationType.ACCESS_REQUEST);

        assertFalse(result);
    }

    @Test
    @DisplayName("getSubscriptionPreferences - Should return default preferences when no record exists")
    void getSubscriptionPreferences_ShouldReturnDefaultPreferencesWhenNoRecordExists() {
        when(healthUserService.findHealthUserByCi("12345678")).thenReturn(healthUserDTO);
        when(notificationUnsubscriptionRepository.findByUserId("user-id")).thenReturn(null);

        NotificationSubscriptionDTO result = service.getSubscriptionPreferences("12345678");

        assertNotNull(result);
        assertEquals("12345678", result.getUserCi());
        assertTrue(result.isSubscribedToAccessRequest());
        assertTrue(result.isSubscribedToClinicalHistoryAccess());
    }

    @Test
    @DisplayName("getSubscriptionPreferences - Should return stored preferences")
    void getSubscriptionPreferences_ShouldReturnStoredPreferences() {
        NotificationUnsubscription prefs = new NotificationUnsubscription();
        prefs.setSubscribedToAccessRequest(false);
        prefs.setSubscribedToClinicalHistoryAccess(true);
        when(healthUserService.findHealthUserByCi("12345678")).thenReturn(healthUserDTO);
        when(notificationUnsubscriptionRepository.findByUserId("user-id")).thenReturn(prefs);

        NotificationSubscriptionDTO result = service.getSubscriptionPreferences("12345678");

        assertNotNull(result);
        assertEquals("12345678", result.getUserCi());
        assertFalse(result.isSubscribedToAccessRequest());
        assertTrue(result.isSubscribedToClinicalHistoryAccess());
    }

    @Test
    @DisplayName("getSubscriptionPreferences - Should throw ValidationException for null user CI")
    void getSubscriptionPreferences_ShouldThrowValidationExceptionForNullUserCi() {
        ValidationException exception = assertThrows(ValidationException.class,
                () -> service.getSubscriptionPreferences(null));

        assertEquals("userCi is required", exception.getMessage());
    }

    @Test
    @DisplayName("getSubscriptionPreferences - Should throw ValidationException for blank user CI")
    void getSubscriptionPreferences_ShouldThrowValidationExceptionForBlankUserCi() {
        ValidationException exception = assertThrows(ValidationException.class,
                () -> service.getSubscriptionPreferences(""));

        assertEquals("userCi is required", exception.getMessage());
    }

    @Test
    @DisplayName("getSubscriptionPreferences - Should throw ValidationException when user not found")
    void getSubscriptionPreferences_ShouldThrowValidationExceptionWhenUserNotFound() {
        when(healthUserService.findHealthUserByCi("12345678")).thenReturn(null);

        ValidationException exception = assertThrows(ValidationException.class,
                () -> service.getSubscriptionPreferences("12345678"));

        assertEquals("User not found with CI: 12345678", exception.getMessage());
    }
}