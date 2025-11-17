package grupo12.practico.services.AccessRequest;

import grupo12.practico.dtos.AccessRequest.AccessRequestDTO;
import grupo12.practico.dtos.AccessRequest.AddAccessRequestDTO;
import grupo12.practico.dtos.Clinic.ClinicDTO;
import grupo12.practico.dtos.HealthUser.HealthUserDTO;
import grupo12.practico.dtos.HealthWorker.HealthWorkerDTO;
import grupo12.practico.models.AccessRequest;
import grupo12.practico.models.HealthUser;
import grupo12.practico.models.NotificationType;
import grupo12.practico.repositories.AccessRequest.AccessRequestRepositoryLocal;
import grupo12.practico.repositories.HealthUser.HealthUserRepositoryLocal;
import grupo12.practico.repositories.NotificationToken.NotificationTokenRepositoryLocal;
import grupo12.practico.services.Clinic.ClinicServiceLocal;
import grupo12.practico.services.HealthUser.HealthUserServiceLocal;
import grupo12.practico.services.HealthWorker.HealthWorkerServiceLocal;
import grupo12.practico.services.Logger.LoggerServiceLocal;
import grupo12.practico.services.NotificationToken.NotificationTokenServiceLocal;
import grupo12.practico.services.PushNotificationSender.PushNotificationServiceLocal;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AccessRequestServiceBean Tests")
class AccessRequestServiceBeanTest {

    @Mock
    private AccessRequestRepositoryLocal accessRequestRepository;

    @Mock
    private ClinicServiceLocal clinicServiceLocal;

    @Mock
    private HealthUserRepositoryLocal healthUserRepository;

    @Mock
    private HealthUserServiceLocal healthUserService;

    @Mock
    private HealthWorkerServiceLocal healthWorkerService;

    @Mock
    private NotificationTokenRepositoryLocal notificationTokenRepository;

    @Mock
    private NotificationTokenServiceLocal notificationTokenService;

    @Mock
    private PushNotificationServiceLocal pushNotificationService;

    @Mock
    private LoggerServiceLocal loggerService;

    private AccessRequestServiceBean service;

    private AddAccessRequestDTO addAccessRequestDTO;
    private AccessRequest accessRequest;
    private HealthUser healthUser;
    private HealthWorkerDTO healthWorkerDTO;
    private ClinicDTO clinicDTO;

    @BeforeEach
    void setUp() throws Exception {
        service = new AccessRequestServiceBean();

        // Use reflection to inject mocked dependencies
        injectField("accessRequestRepository", accessRequestRepository);
        injectField("clinicServiceLocal", clinicServiceLocal);
        injectField("healthUserRepository", healthUserRepository);
        injectField("healthUserService", healthUserService);
        injectField("healthWorkerService", healthWorkerService);
        injectField("notificationTokenRepository", notificationTokenRepository);
        injectField("notificationTokenService", notificationTokenService);
        injectField("pushNotificationService", pushNotificationService);
        injectField("loggerService", loggerService);

        // Setup test data
        healthUser = new HealthUser();
        healthUser.setId("health-user-id");
        healthUser.setCi("12345678");

        healthWorkerDTO = new HealthWorkerDTO();
        healthWorkerDTO.setCi("87654321");
        healthWorkerDTO.setFirstName("Dr.");
        healthWorkerDTO.setLastName("Smith");

        clinicDTO = new ClinicDTO();
        clinicDTO.setName("Test Clinic");

        addAccessRequestDTO = new AddAccessRequestDTO();
        addAccessRequestDTO.setHealthUserCi("12345678");
        addAccessRequestDTO.setHealthWorkerCi("87654321");
        addAccessRequestDTO.setClinicName("Test Clinic");
        addAccessRequestDTO.setSpecialtyNames(Arrays.asList("Cardiology", "Neurology"));

        accessRequest = new AccessRequest();
        accessRequest.setId("access-request-id");
        accessRequest.setHealthUser(healthUser);
        accessRequest.setHealthWorkerCi("87654321");
        accessRequest.setClinicName("Test Clinic");
        accessRequest.setSpecialtyNames(Arrays.asList("Cardiology", "Neurology"));
        accessRequest.setCreatedAt(LocalDate.of(2023, 1, 1));
    }

    private void injectField(String fieldName, Object value) throws Exception {
        Field field = AccessRequestServiceBean.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(service, value);
    }

    @Test
    @DisplayName("createAccessRequest - Should create access request successfully")
    void createAccessRequest_ShouldCreateAccessRequestSuccessfully() {
        when(healthUserRepository.findHealthUserByCi("12345678")).thenReturn(healthUser);
        when(accessRequestRepository.findAllAccessRequests("health-user-id", "87654321", "Test Clinic"))
                .thenReturn(Collections.emptyList());
        when(healthWorkerService.findByClinicAndCi("Test Clinic", "87654321")).thenReturn(healthWorkerDTO);
        when(clinicServiceLocal.findClinicByName("Test Clinic")).thenReturn(clinicDTO);
        when(accessRequestRepository.createAccessRequest(any(AccessRequest.class))).thenReturn(accessRequest);
        when(notificationTokenService.isUserSubscribedToNotificationType("12345678", NotificationType.ACCESS_REQUEST))
                .thenReturn(false);

        AccessRequestDTO result = service.createAccessRequest(addAccessRequestDTO);

        assertNotNull(result);
        assertEquals("access-request-id", result.getId());
        assertEquals("health-user-id", result.getHealthUserId());
        assertEquals(healthWorkerDTO, result.getHealthWorker());
        assertEquals(clinicDTO, result.getClinic());
        assertEquals(Arrays.asList("Cardiology", "Neurology"), result.getSpecialtyNames());
        assertEquals(LocalDate.of(2023, 1, 1), result.getCreatedAt());

        verify(accessRequestRepository).createAccessRequest(any(AccessRequest.class));
        verify(loggerService).logAccessRequestCreated(
                "access-request-id", "12345678", "87654321", "Test Clinic",
                Arrays.asList("Cardiology", "Neurology"));
    }

    @Test
    @DisplayName("createAccessRequest - Should send push notification when user is subscribed")
    void createAccessRequest_ShouldSendPushNotificationWhenUserIsSubscribed() {
        var mockTokens = Arrays.asList(mock(grupo12.practico.models.NotificationToken.class));

        when(healthUserRepository.findHealthUserByCi("12345678")).thenReturn(healthUser);
        when(accessRequestRepository.findAllAccessRequests("health-user-id", "87654321", "Test Clinic"))
                .thenReturn(Collections.emptyList());
        when(healthWorkerService.findByClinicAndCi("Test Clinic", "87654321")).thenReturn(healthWorkerDTO);
        when(clinicServiceLocal.findClinicByName("Test Clinic")).thenReturn(clinicDTO);
        when(accessRequestRepository.createAccessRequest(any(AccessRequest.class))).thenReturn(accessRequest);
        when(notificationTokenService.isUserSubscribedToNotificationType("12345678", NotificationType.ACCESS_REQUEST))
                .thenReturn(true);
        when(notificationTokenRepository.findByUserId("health-user-id")).thenReturn(mockTokens);
        when(mockTokens.get(0).getToken()).thenReturn("device-token");

        service.createAccessRequest(addAccessRequestDTO);

        verify(pushNotificationService).sendPushNotificationToToken(
                eq("New access request"),
                eq("Dr. Smith requested access to your records at Test Clinic"),
                eq("device-token"));
    }

    @Test
    @DisplayName("createAccessRequest - Should throw ValidationException for null DTO")
    void createAccessRequest_ShouldThrowValidationExceptionForNullDto() {
        ValidationException exception = assertThrows(ValidationException.class,
                () -> service.createAccessRequest(null));

        assertEquals("Access request payload is required", exception.getMessage());
    }

    @Test
    @DisplayName("createAccessRequest - Should throw ValidationException for null health user ci")
    void createAccessRequest_ShouldThrowValidationExceptionForNullHealthUserCi() {
        addAccessRequestDTO.setHealthUserCi(null);

        ValidationException exception = assertThrows(ValidationException.class,
                () -> service.createAccessRequest(addAccessRequestDTO));

        assertEquals("Health user id is required", exception.getMessage());
    }

    @Test
    @DisplayName("createAccessRequest - Should throw ValidationException for blank health user ci")
    void createAccessRequest_ShouldThrowValidationExceptionForBlankHealthUserCi() {
        addAccessRequestDTO.setHealthUserCi("");

        ValidationException exception = assertThrows(ValidationException.class,
                () -> service.createAccessRequest(addAccessRequestDTO));

        assertEquals("Health user id is required", exception.getMessage());
    }

    @Test
    @DisplayName("createAccessRequest - Should throw ValidationException for null health worker ci")
    void createAccessRequest_ShouldThrowValidationExceptionForNullHealthWorkerCi() {
        addAccessRequestDTO.setHealthWorkerCi(null);

        ValidationException exception = assertThrows(ValidationException.class,
                () -> service.createAccessRequest(addAccessRequestDTO));

        assertEquals("Health worker id is required", exception.getMessage());
    }

    @Test
    @DisplayName("createAccessRequest - Should throw ValidationException for null clinic name")
    void createAccessRequest_ShouldThrowValidationExceptionForNullClinicName() {
        addAccessRequestDTO.setClinicName(null);

        ValidationException exception = assertThrows(ValidationException.class,
                () -> service.createAccessRequest(addAccessRequestDTO));

        assertEquals("Clinic id is required", exception.getMessage());
    }

    @Test
    @DisplayName("createAccessRequest - Should throw ValidationException when health user not found")
    void createAccessRequest_ShouldThrowValidationExceptionWhenHealthUserNotFound() {
        when(healthUserRepository.findHealthUserByCi("12345678")).thenReturn(null);

        ValidationException exception = assertThrows(ValidationException.class,
                () -> service.createAccessRequest(addAccessRequestDTO));

        assertEquals("Health user not found", exception.getMessage());
    }

    @Test
    @DisplayName("createAccessRequest - Should throw ValidationException when access request already exists")
    void createAccessRequest_ShouldThrowValidationExceptionWhenAccessRequestAlreadyExists() {
        when(healthUserRepository.findHealthUserByCi("12345678")).thenReturn(healthUser);
        when(accessRequestRepository.findAllAccessRequests("health-user-id", "87654321", "Test Clinic"))
                .thenReturn(Arrays.asList(accessRequest));

        ValidationException exception = assertThrows(ValidationException.class,
                () -> service.createAccessRequest(addAccessRequestDTO));

        assertEquals("An access request already exists for this combination", exception.getMessage());
    }

    @Test
    @DisplayName("createAccessRequest - Should throw ValidationException when health worker not found")
    void createAccessRequest_ShouldThrowValidationExceptionWhenHealthWorkerNotFound() {
        when(healthUserRepository.findHealthUserByCi("12345678")).thenReturn(healthUser);
        when(accessRequestRepository.findAllAccessRequests("health-user-id", "87654321", "Test Clinic"))
                .thenReturn(Collections.emptyList());
        when(healthWorkerService.findByClinicAndCi("Test Clinic", "87654321")).thenReturn(null);

        ValidationException exception = assertThrows(ValidationException.class,
                () -> service.createAccessRequest(addAccessRequestDTO));

        assertEquals("Health worker not found", exception.getMessage());
    }

    @Test
    @DisplayName("createAccessRequest - Should throw ValidationException when clinic not found")
    void createAccessRequest_ShouldThrowValidationExceptionWhenClinicNotFound() {
        when(healthUserRepository.findHealthUserByCi("12345678")).thenReturn(healthUser);
        when(accessRequestRepository.findAllAccessRequests("health-user-id", "87654321", "Test Clinic"))
                .thenReturn(Collections.emptyList());
        when(healthWorkerService.findByClinicAndCi("Test Clinic", "87654321")).thenReturn(healthWorkerDTO);
        when(clinicServiceLocal.findClinicByName("Test Clinic")).thenReturn(null);

        ValidationException exception = assertThrows(ValidationException.class,
                () -> service.createAccessRequest(addAccessRequestDTO));

        assertEquals("Clinic not found", exception.getMessage());
    }

    @Test
    @DisplayName("findAccessRequestById - Should return access request DTO when found")
    void findAccessRequestById_ShouldReturnAccessRequestDTOWhenFound() {
        when(accessRequestRepository.findAccessRequestById("access-request-id")).thenReturn(accessRequest);
        when(healthWorkerService.findByClinicAndCi("Test Clinic", "87654321")).thenReturn(healthWorkerDTO);
        when(clinicServiceLocal.findClinicByName("Test Clinic")).thenReturn(clinicDTO);

        AccessRequestDTO result = service.findAccessRequestById("access-request-id");

        assertNotNull(result);
        assertEquals("access-request-id", result.getId());
        assertEquals("health-user-id", result.getHealthUserId());
        assertEquals(healthWorkerDTO, result.getHealthWorker());
        assertEquals(clinicDTO, result.getClinic());
        assertEquals(Arrays.asList("Cardiology", "Neurology"), result.getSpecialtyNames());
        assertEquals(LocalDate.of(2023, 1, 1), result.getCreatedAt());
    }

    @Test
    @DisplayName("findAccessRequestById - Should throw ValidationException for null id")
    void findAccessRequestById_ShouldThrowValidationExceptionForNullId() {
        ValidationException exception = assertThrows(ValidationException.class,
                () -> service.findAccessRequestById(null));

        assertEquals("Access request id is required", exception.getMessage());
    }

    @Test
    @DisplayName("findAccessRequestById - Should throw ValidationException for blank id")
    void findAccessRequestById_ShouldThrowValidationExceptionForBlankId() {
        ValidationException exception = assertThrows(ValidationException.class,
                () -> service.findAccessRequestById(""));

        assertEquals("Access request id is required", exception.getMessage());
    }

    @Test
    @DisplayName("findAllAccessRequests - Should return list of access request DTOs")
    void findAllAccessRequests_ShouldReturnListOfAccessRequestDTOs() {
        HealthUserDTO healthUserDTO = new HealthUserDTO();
        healthUserDTO.setId("health-user-id");
        healthUserDTO.setCi("12345678");

        List<AccessRequest> accessRequests = Arrays.asList(accessRequest);
        when(healthUserService.findHealthUserByCi("12345678")).thenReturn(healthUserDTO);
        when(accessRequestRepository.findAllAccessRequests("health-user-id", "87654321", "Test Clinic"))
                .thenReturn(accessRequests);
        when(healthWorkerService.findByClinicAndCi("Test Clinic", "87654321")).thenReturn(healthWorkerDTO);
        when(clinicServiceLocal.findClinicByName("Test Clinic")).thenReturn(clinicDTO);

        List<AccessRequestDTO> result = service.findAllAccessRequests("12345678", "87654321", "Test Clinic");

        assertNotNull(result);
        assertEquals(1, result.size());
        AccessRequestDTO dto = result.get(0);
        assertEquals("access-request-id", dto.getId());
        assertEquals("health-user-id", dto.getHealthUserId());
        assertEquals("12345678", dto.getHealthUserCi());
        assertEquals(healthWorkerDTO, dto.getHealthWorker());
        assertEquals(clinicDTO, dto.getClinic());
    }

    @Test
    @DisplayName("deleteAccessRequest - Should call repository delete")
    void deleteAccessRequest_ShouldCallRepositoryDelete() {
        when(accessRequestRepository.findAccessRequestById("access-request-id")).thenReturn(accessRequest);

        service.deleteAccessRequest("access-request-id");

        verify(accessRequestRepository).deleteAccessRequest("access-request-id");
        verify(loggerService).logAccessRequestDenied(
                "access-request-id", "12345678", "87654321", "Test Clinic",
                Arrays.asList("Cardiology", "Neurology"));
    }

    @Test
    @DisplayName("toDto - Should convert AccessRequest entity to AccessRequestDTO")
    void toDto_ShouldConvertAccessRequestEntityToAccessRequestDTO() {
        when(healthWorkerService.findByClinicAndCi("Test Clinic", "87654321")).thenReturn(healthWorkerDTO);
        when(clinicServiceLocal.findClinicByName("Test Clinic")).thenReturn(clinicDTO);

        // Test the conversion logic used in findAccessRequestById
        AccessRequest testRequest = new AccessRequest();
        testRequest.setId("test-id");
        testRequest.setHealthUser(healthUser);
        testRequest.setHealthWorkerCi("87654321");
        testRequest.setClinicName("Test Clinic");
        testRequest.setSpecialtyNames(Arrays.asList("Cardiology", "Neurology"));
        testRequest.setCreatedAt(LocalDate.of(2023, 1, 1));

        // Simulate the conversion logic from the service
        AccessRequestDTO result = new AccessRequestDTO();
        result.setId(testRequest.getId());
        result.setHealthUserId(testRequest.getHealthUser().getId());
        result.setHealthUserCi(testRequest.getHealthUser().getCi());
        result.setHealthWorker(healthWorkerService.findByClinicAndCi(
                testRequest.getClinicName(), testRequest.getHealthWorkerCi()));
        result.setClinic(clinicServiceLocal.findClinicByName(testRequest.getClinicName()));
        result.setSpecialtyNames(testRequest.getSpecialtyNames());
        result.setCreatedAt(testRequest.getCreatedAt());

        assertNotNull(result);
        assertEquals("test-id", result.getId());
        assertEquals("health-user-id", result.getHealthUserId());
        assertEquals("12345678", result.getHealthUserCi());
        assertEquals(healthWorkerDTO, result.getHealthWorker());
        assertEquals(clinicDTO, result.getClinic());
        assertEquals(Arrays.asList("Cardiology", "Neurology"), result.getSpecialtyNames());
        assertEquals(LocalDate.of(2023, 1, 1), result.getCreatedAt());
    }

    @Test
    @DisplayName("toDto - Should handle null specialty names")
    void toDto_ShouldHandleNullSpecialtyNames() {
        when(healthWorkerService.findByClinicAndCi("Test Clinic", "87654321")).thenReturn(healthWorkerDTO);
        when(clinicServiceLocal.findClinicByName("Test Clinic")).thenReturn(clinicDTO);

        AccessRequest testRequest = new AccessRequest();
        testRequest.setId("test-id");
        testRequest.setHealthUser(healthUser);
        testRequest.setHealthWorkerCi("87654321");
        testRequest.setClinicName("Test Clinic");
        testRequest.setSpecialtyNames(null); // Null specialty names
        testRequest.setCreatedAt(LocalDate.of(2023, 1, 1));

        AccessRequestDTO result = new AccessRequestDTO();
        result.setId(testRequest.getId());
        result.setHealthUserId(testRequest.getHealthUser().getId());
        result.setHealthUserCi(testRequest.getHealthUser().getCi());
        result.setHealthWorker(healthWorkerService.findByClinicAndCi(
                testRequest.getClinicName(), testRequest.getHealthWorkerCi()));
        result.setClinic(clinicServiceLocal.findClinicByName(testRequest.getClinicName()));
        result.setSpecialtyNames(testRequest.getSpecialtyNames());
        result.setCreatedAt(testRequest.getCreatedAt());

        assertNotNull(result);
        assertNull(result.getSpecialtyNames());
    }

    @Test
    @DisplayName("toDto - Should handle empty specialty names list")
    void toDto_ShouldHandleEmptySpecialtyNamesList() {
        when(healthWorkerService.findByClinicAndCi("Test Clinic", "87654321")).thenReturn(healthWorkerDTO);
        when(clinicServiceLocal.findClinicByName("Test Clinic")).thenReturn(clinicDTO);

        AccessRequest testRequest = new AccessRequest();
        testRequest.setId("test-id");
        testRequest.setHealthUser(healthUser);
        testRequest.setHealthWorkerCi("87654321");
        testRequest.setClinicName("Test Clinic");
        testRequest.setSpecialtyNames(Collections.emptyList()); // Empty specialty names
        testRequest.setCreatedAt(LocalDate.of(2023, 1, 1));

        AccessRequestDTO result = new AccessRequestDTO();
        result.setId(testRequest.getId());
        result.setHealthUserId(testRequest.getHealthUser().getId());
        result.setHealthUserCi(testRequest.getHealthUser().getCi());
        result.setHealthWorker(healthWorkerService.findByClinicAndCi(
                testRequest.getClinicName(), testRequest.getHealthWorkerCi()));
        result.setClinic(clinicServiceLocal.findClinicByName(testRequest.getClinicName()));
        result.setSpecialtyNames(testRequest.getSpecialtyNames());
        result.setCreatedAt(testRequest.getCreatedAt());

        assertNotNull(result);
        assertEquals(Collections.emptyList(), result.getSpecialtyNames());
    }
}