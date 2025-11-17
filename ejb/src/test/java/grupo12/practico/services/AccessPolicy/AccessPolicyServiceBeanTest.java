package grupo12.practico.services.AccessPolicy;

import grupo12.practico.dtos.AccessPolicy.AddClinicAccessPolicyDTO;
import grupo12.practico.dtos.AccessPolicy.AddHealthWorkerAccessPolicyDTO;
import grupo12.practico.dtos.AccessPolicy.AddSpecialtyAccessPolicyDTO;
import grupo12.practico.dtos.AccessPolicy.ClinicAccessPolicyDTO;
import grupo12.practico.dtos.AccessPolicy.HealthWorkerAccessPolicyDTO;
import grupo12.practico.dtos.AccessPolicy.SpecialtyAccessPolicyDTO;
import grupo12.practico.dtos.Clinic.ClinicDTO;
import grupo12.practico.dtos.HealthWorker.HealthWorkerDTO;
import grupo12.practico.models.AccessRequest;
import grupo12.practico.models.ClinicAccessPolicy;
import grupo12.practico.models.HealthUser;
import grupo12.practico.models.HealthWorkerAccessPolicy;
import grupo12.practico.models.SpecialtyAccessPolicy;
import grupo12.practico.repositories.AccessPolicy.AccessPolicyRepositoryLocal;
import grupo12.practico.repositories.AccessRequest.AccessRequestRepositoryLocal;
import grupo12.practico.repositories.HealthUser.HealthUserRepositoryLocal;
import grupo12.practico.services.AccessRequest.AccessRequestServiceLocal;
import grupo12.practico.services.Clinic.ClinicServiceLocal;
import grupo12.practico.services.HealthWorker.HealthWorkerServiceLocal;
import grupo12.practico.services.Logger.LoggerServiceLocal;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AccessPolicyServiceBean Tests")
class AccessPolicyServiceBeanTest {

    @Mock
    private HealthUserRepositoryLocal healthUserRepository;

    @Mock
    private HealthWorkerServiceLocal healthWorkerService;

    @Mock
    private AccessPolicyRepositoryLocal accessPolicyRepository;

    @Mock
    private ClinicServiceLocal clinicService;

    @Mock
    private AccessRequestServiceLocal accessRequestService;

    @Mock
    private LoggerServiceLocal loggerService;

    @Mock
    private AccessRequestRepositoryLocal accessRequestRepository;

    @InjectMocks
    private AccessPolicyServiceBean service;

    private HealthUser healthUser;
    private ClinicAccessPolicy clinicPolicy;
    private HealthWorkerAccessPolicy healthWorkerPolicy;
    private SpecialtyAccessPolicy specialtyPolicy;
    private ClinicDTO clinicDTO;
    private HealthWorkerDTO healthWorkerDTO;
    private AccessRequest accessRequest;

    @BeforeEach
    void setUp() {
        healthUser = new HealthUser();
        healthUser.setId("health-user-id-123");
        healthUser.setCi("55555555");
        healthUser.setFirstName("Alicia");
        healthUser.setLastName("Perez");
        healthUser.setCreatedAt(LocalDate.of(2023, 1, 10));
        healthUser.setUpdatedAt(LocalDate.of(2023, 1, 11));

        clinicPolicy = new ClinicAccessPolicy();
        clinicPolicy.setId("clinic-policy-id");
        clinicPolicy.setHealthUser(healthUser);
        clinicPolicy.setClinicName("Clinic One");
        clinicPolicy.setCreatedAt(LocalDate.of(2023, 2, 1));

        healthWorkerPolicy = new HealthWorkerAccessPolicy();
        healthWorkerPolicy.setId("hw-policy-id");
        healthWorkerPolicy.setHealthUser(healthUser);
        healthWorkerPolicy.setHealthWorkerCi("33333333");
        healthWorkerPolicy.setClinicName("Clinic One");
        healthWorkerPolicy.setCreatedAt(LocalDate.of(2023, 2, 2));

        specialtyPolicy = new SpecialtyAccessPolicy();
        specialtyPolicy.setId("specialty-policy-id");
        specialtyPolicy.setHealthUser(healthUser);
        specialtyPolicy.setSpecialtyName("Cardiology");
        specialtyPolicy.setCreatedAt(LocalDate.of(2023, 2, 3));

        clinicDTO = new ClinicDTO();
        clinicDTO.setId("clinic-id-1");
        clinicDTO.setName("Clinic One");
        clinicDTO.setEmail("clinic@demo.com");
        clinicDTO.setCreatedAt(LocalDate.of(2023, 1, 1));
        clinicDTO.setUpdatedAt(LocalDate.of(2023, 1, 2));

        healthWorkerDTO = new HealthWorkerDTO();
        healthWorkerDTO.setCi("33333333");
        healthWorkerDTO.setFirstName("Laura");
        healthWorkerDTO.setLastName("Sosa");
        healthWorkerDTO.setDateOfBirth(LocalDate.of(1990, 5, 5));

        accessRequest = new AccessRequest();
        accessRequest.setId("access-request-id");
        accessRequest.setHealthUser(healthUser);
        accessRequest.setHealthWorkerCi("33333333");
        accessRequest.setClinicName("Clinic One");
        accessRequest.setSpecialtyNames(List.of("Cardiology"));
        accessRequest.setCreatedAt(LocalDate.of(2023, 1, 15));
    }

    @Test
    @DisplayName("createClinicAccessPolicy - Should persist policy, return DTO, and log access request")
    void createClinicAccessPolicy_ShouldCreatePolicyAndLog() {
        AddClinicAccessPolicyDTO dto = new AddClinicAccessPolicyDTO();
        dto.setHealthUserCi(healthUser.getCi());
        dto.setClinicName("Clinic One");
        dto.setAccessRequestId(accessRequest.getId());

        when(healthUserRepository.findHealthUserByCi(healthUser.getCi())).thenReturn(healthUser);
        when(accessPolicyRepository.createClinicAccessPolicy(any(ClinicAccessPolicy.class))).thenReturn(clinicPolicy);
        when(clinicService.findClinicByName("Clinic One")).thenReturn(clinicDTO);
        when(accessRequestRepository.findAccessRequestById(accessRequest.getId())).thenReturn(accessRequest);

        ClinicAccessPolicyDTO result = service.createClinicAccessPolicy(dto);

        assertEquals(clinicPolicy.getId(), result.getId());
        assertEquals(healthUser.getCi(), result.getHealthUserCi());
        assertEquals(clinicDTO, result.getClinic());

        verify(accessPolicyRepository).createClinicAccessPolicy(any(ClinicAccessPolicy.class));
        verify(loggerService).logAccessRequestAcceptedByClinic(
                accessRequest.getId(),
                healthUser.getCi(),
                accessRequest.getHealthWorkerCi(),
                dto.getClinicName(),
                accessRequest.getSpecialtyNames());
        verify(accessRequestService).deleteAccessRequest(accessRequest.getId());
    }

    @Test
    @DisplayName("createClinicAccessPolicy - Should throw when dto is null")
    void createClinicAccessPolicy_ShouldThrowWhenDtoNull() {
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> service.createClinicAccessPolicy(null));

        assertEquals("Clinic access policy payload is required", exception.getMessage());
    }

    @Test
    @DisplayName("createClinicAccessPolicy - Should throw when health user CI missing")
    void createClinicAccessPolicy_ShouldThrowWhenHealthUserMissing() {
        AddClinicAccessPolicyDTO dto = new AddClinicAccessPolicyDTO();
        dto.setClinicName("Clinic One");

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> service.createClinicAccessPolicy(dto));

        assertEquals("Health user CI is required", exception.getMessage());
    }

    @Test
    @DisplayName("createClinicAccessPolicy - Should throw when clinic name missing")
    void createClinicAccessPolicy_ShouldThrowWhenClinicMissing() {
        AddClinicAccessPolicyDTO dto = new AddClinicAccessPolicyDTO();
        dto.setHealthUserCi(healthUser.getCi());

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> service.createClinicAccessPolicy(dto));

        assertEquals("Clinic name is required", exception.getMessage());
    }

    @Test
    @DisplayName("createClinicAccessPolicy - Should throw when health user not found")
    void createClinicAccessPolicy_ShouldThrowWhenHealthUserNotFound() {
        AddClinicAccessPolicyDTO dto = new AddClinicAccessPolicyDTO();
        dto.setHealthUserCi(healthUser.getCi());
        dto.setClinicName("Clinic One");

        when(healthUserRepository.findHealthUserByCi(healthUser.getCi())).thenReturn(null);

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> service.createClinicAccessPolicy(dto));

        assertEquals("Health user not found", exception.getMessage());
    }

    @Test
    @DisplayName("createHealthWorkerAccessPolicy - Should persist policy, return DTO, and log access request")
    void createHealthWorkerAccessPolicy_ShouldCreatePolicyAndLog() {
        AddHealthWorkerAccessPolicyDTO dto = new AddHealthWorkerAccessPolicyDTO();
        dto.setHealthUserCi(healthUser.getCi());
        dto.setHealthWorkerCi("33333333");
        dto.setClinicName("Clinic One");
        dto.setAccessRequestId(accessRequest.getId());

        when(healthUserRepository.findHealthUserByCi(healthUser.getCi())).thenReturn(healthUser);
        when(accessPolicyRepository.createHealthWorkerAccessPolicy(any(HealthWorkerAccessPolicy.class)))
                .thenReturn(healthWorkerPolicy);
        when(healthWorkerService.findByClinicAndCi("Clinic One", "33333333")).thenReturn(healthWorkerDTO);
        when(clinicService.findClinicByName("Clinic One")).thenReturn(clinicDTO);
        when(accessRequestRepository.findAccessRequestById(accessRequest.getId())).thenReturn(accessRequest);

        HealthWorkerAccessPolicyDTO result = service.createHealthWorkerAccessPolicy(dto);

        assertEquals(healthWorkerPolicy.getId(), result.getId());
        assertEquals(healthUser.getCi(), result.getHealthUserCi());
        assertEquals(healthWorkerDTO, result.getHealthWorker());
        assertEquals(clinicDTO, result.getClinic());

        verify(loggerService).logAccessRequestAcceptedByHealthWorker(
                accessRequest.getId(),
                healthUser.getCi(),
                dto.getHealthWorkerCi(),
                dto.getClinicName(),
                accessRequest.getSpecialtyNames());
        verify(accessRequestService).deleteAccessRequest(accessRequest.getId());
    }

    @Test
    @DisplayName("createHealthWorkerAccessPolicy - Should throw when dto is null")
    void createHealthWorkerAccessPolicy_ShouldThrowWhenDtoNull() {
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> service.createHealthWorkerAccessPolicy(null));

        assertEquals("Health worker access policy payload is required", exception.getMessage());
    }

    @Test
    @DisplayName("createHealthWorkerAccessPolicy - Should throw when health user CI missing")
    void createHealthWorkerAccessPolicy_ShouldThrowWhenHealthUserMissing() {
        AddHealthWorkerAccessPolicyDTO dto = new AddHealthWorkerAccessPolicyDTO();
        dto.setHealthWorkerCi("33333333");
        dto.setClinicName("Clinic One");

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> service.createHealthWorkerAccessPolicy(dto));

        assertEquals("Health user CI is required", exception.getMessage());
    }

    @Test
    @DisplayName("createHealthWorkerAccessPolicy - Should throw when health worker CI missing")
    void createHealthWorkerAccessPolicy_ShouldThrowWhenHealthWorkerMissing() {
        AddHealthWorkerAccessPolicyDTO dto = new AddHealthWorkerAccessPolicyDTO();
        dto.setHealthUserCi(healthUser.getCi());
        dto.setClinicName("Clinic One");

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> service.createHealthWorkerAccessPolicy(dto));

        assertEquals("Health worker CI is required", exception.getMessage());
    }

    @Test
    @DisplayName("createHealthWorkerAccessPolicy - Should throw when health user not found")
    void createHealthWorkerAccessPolicy_ShouldThrowWhenHealthUserNotFound() {
        AddHealthWorkerAccessPolicyDTO dto = new AddHealthWorkerAccessPolicyDTO();
        dto.setHealthUserCi(healthUser.getCi());
        dto.setHealthWorkerCi("33333333");
        dto.setClinicName("Clinic One");

        when(healthUserRepository.findHealthUserByCi(healthUser.getCi())).thenReturn(null);

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> service.createHealthWorkerAccessPolicy(dto));

        assertEquals("Health user not found", exception.getMessage());
    }

    @Test
    @DisplayName("createSpecialtyAccessPolicy - Should persist policy, return DTO, and log access request")
    void createSpecialtyAccessPolicy_ShouldCreatePolicyAndLog() {
        AddSpecialtyAccessPolicyDTO dto = new AddSpecialtyAccessPolicyDTO();
        dto.setHealthUserCi(healthUser.getCi());
        dto.setSpecialtyName("Cardiology");
        dto.setAccessRequestId(accessRequest.getId());

        when(healthUserRepository.findHealthUserByCi(healthUser.getCi())).thenReturn(healthUser);
        when(accessPolicyRepository.createSpecialtyAccessPolicy(any(SpecialtyAccessPolicy.class)))
                .thenReturn(specialtyPolicy);
        when(accessRequestRepository.findAccessRequestById(accessRequest.getId())).thenReturn(accessRequest);

        SpecialtyAccessPolicyDTO result = service.createSpecialtyAccessPolicy(dto);

        assertEquals(specialtyPolicy.getId(), result.getId());
        assertEquals(healthUser.getCi(), result.getHealthUserCi());
        assertEquals("Cardiology", result.getSpecialtyName());

        verify(loggerService).logAccessRequestAcceptedBySpecialty(
                accessRequest.getId(),
                healthUser.getCi(),
                accessRequest.getHealthWorkerCi(),
                accessRequest.getClinicName(),
                accessRequest.getSpecialtyNames());
        verify(accessRequestService).deleteAccessRequest(accessRequest.getId());
    }

    @Test
    @DisplayName("createSpecialtyAccessPolicy - Should throw when dto is null")
    void createSpecialtyAccessPolicy_ShouldThrowWhenDtoNull() {
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> service.createSpecialtyAccessPolicy(null));

        assertEquals("Specialty access policy payload is required", exception.getMessage());
    }

    @Test
    @DisplayName("createSpecialtyAccessPolicy - Should throw when health user CI missing")
    void createSpecialtyAccessPolicy_ShouldThrowWhenHealthUserMissing() {
        AddSpecialtyAccessPolicyDTO dto = new AddSpecialtyAccessPolicyDTO();
        dto.setSpecialtyName("Cardiology");

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> service.createSpecialtyAccessPolicy(dto));

        assertEquals("Health user CI is required", exception.getMessage());
    }

    @Test
    @DisplayName("createSpecialtyAccessPolicy - Should throw when specialty name missing")
    void createSpecialtyAccessPolicy_ShouldThrowWhenSpecialtyMissing() {
        AddSpecialtyAccessPolicyDTO dto = new AddSpecialtyAccessPolicyDTO();
        dto.setHealthUserCi(healthUser.getCi());

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> service.createSpecialtyAccessPolicy(dto));

        assertEquals("Specialty name is required", exception.getMessage());
    }

    @Test
    @DisplayName("createSpecialtyAccessPolicy - Should throw when health user not found")
    void createSpecialtyAccessPolicy_ShouldThrowWhenHealthUserNotFound() {
        AddSpecialtyAccessPolicyDTO dto = new AddSpecialtyAccessPolicyDTO();
        dto.setHealthUserCi(healthUser.getCi());
        dto.setSpecialtyName("Cardiology");

        when(healthUserRepository.findHealthUserByCi(healthUser.getCi())).thenReturn(null);

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> service.createSpecialtyAccessPolicy(dto));

        assertEquals("Health user not found", exception.getMessage());
    }

    @Test
    @DisplayName("findAllClinicAccessPolicies - Should return DTOs for user")
    void findAllClinicAccessPolicies_ShouldReturnDtoList() {
        when(healthUserRepository.findHealthUserByCi(healthUser.getCi())).thenReturn(healthUser);
        when(accessPolicyRepository.findAllClinicAccessPolicies(healthUser.getId()))
                .thenReturn(List.of(clinicPolicy));
        when(clinicService.findClinicByName("Clinic One")).thenReturn(clinicDTO);

        List<ClinicAccessPolicyDTO> result = service.findAllClinicAccessPolicies(healthUser.getCi());

        assertEquals(1, result.size());
        assertEquals(healthUser.getCi(), result.get(0).getHealthUserCi());
        assertEquals(clinicDTO, result.get(0).getClinic());
    }

    @Test
    @DisplayName("findAllClinicAccessPolicies - Should throw when health user CI blank")
    void findAllClinicAccessPolicies_ShouldThrowWhenHealthUserBlank() {
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> service.findAllClinicAccessPolicies(" "));

        assertEquals("Health user id is required", exception.getMessage());
    }

    @Test
    @DisplayName("findAllClinicAccessPolicies - Should throw when health user not found")
    void findAllClinicAccessPolicies_ShouldThrowWhenUserMissing() {
        when(healthUserRepository.findHealthUserByCi(healthUser.getCi())).thenReturn(null);

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> service.findAllClinicAccessPolicies(healthUser.getCi()));

        assertEquals("Health user not found", exception.getMessage());
    }

    @Test
    @DisplayName("findAllHealthWorkerAccessPolicies - Should return DTOs for user")
    void findAllHealthWorkerAccessPolicies_ShouldReturnDtoList() {
        when(healthUserRepository.findHealthUserByCi(healthUser.getCi())).thenReturn(healthUser);
        when(accessPolicyRepository.findAllHealthWorkerAccessPolicies(healthUser.getId()))
                .thenReturn(List.of(healthWorkerPolicy));
        when(healthWorkerService.findByClinicAndCi("Clinic One", "33333333")).thenReturn(healthWorkerDTO);
        when(clinicService.findClinicByName("Clinic One")).thenReturn(clinicDTO);

        List<HealthWorkerAccessPolicyDTO> result = service.findAllHealthWorkerAccessPolicies(healthUser.getCi());

        assertEquals(1, result.size());
        assertEquals(healthWorkerPolicy.getId(), result.get(0).getId());
        assertEquals(healthWorkerDTO, result.get(0).getHealthWorker());
    }

    @Test
    @DisplayName("findAllHealthWorkerAccessPolicies - Should throw when health user CI blank")
    void findAllHealthWorkerAccessPolicies_ShouldThrowWhenHealthUserBlank() {
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> service.findAllHealthWorkerAccessPolicies(""));

        assertEquals("Health user id is required", exception.getMessage());
    }

    @Test
    @DisplayName("findAllHealthWorkerAccessPolicies - Should throw when health user not found")
    void findAllHealthWorkerAccessPolicies_ShouldThrowWhenUserMissing() {
        when(healthUserRepository.findHealthUserByCi(healthUser.getCi())).thenReturn(null);

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> service.findAllHealthWorkerAccessPolicies(healthUser.getCi()));

        assertEquals("Health user not found", exception.getMessage());
    }

    @Test
    @DisplayName("findAllSpecialtyAccessPolicies - Should return DTOs for user")
    void findAllSpecialtyAccessPolicies_ShouldReturnDtoList() {
        when(healthUserRepository.findHealthUserByCi(healthUser.getCi())).thenReturn(healthUser);
        when(accessPolicyRepository.findAllSpecialtyAccessPolicies(healthUser.getId()))
                .thenReturn(List.of(specialtyPolicy));

        List<SpecialtyAccessPolicyDTO> result = service.findAllSpecialtyAccessPolicies(healthUser.getCi());

        assertEquals(1, result.size());
        assertEquals("Cardiology", result.get(0).getSpecialtyName());
    }

    @Test
    @DisplayName("findAllSpecialtyAccessPolicies - Should throw when health user CI blank")
    void findAllSpecialtyAccessPolicies_ShouldThrowWhenHealthUserBlank() {
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> service.findAllSpecialtyAccessPolicies(null));

        assertEquals("Health user id is required", exception.getMessage());
    }

    @Test
    @DisplayName("findAllSpecialtyAccessPolicies - Should throw when health user not found")
    void findAllSpecialtyAccessPolicies_ShouldThrowWhenUserMissing() {
        when(healthUserRepository.findHealthUserByCi(healthUser.getCi())).thenReturn(null);

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> service.findAllSpecialtyAccessPolicies(healthUser.getCi()));

        assertEquals("Health user not found", exception.getMessage());
    }

    @Test
    @DisplayName("deleteClinicAccessPolicy - Should validate id")
    void deleteClinicAccessPolicy_ShouldValidateId() {
        ValidationException exceptionNull = assertThrows(
                ValidationException.class,
                () -> service.deleteClinicAccessPolicy(null));
        assertEquals("Clinic access policy id is required", exceptionNull.getMessage());

        ValidationException exceptionBlank = assertThrows(
                ValidationException.class,
                () -> service.deleteClinicAccessPolicy(""));
        assertEquals("Clinic access policy id is required", exceptionBlank.getMessage());
    }

    @Test
    @DisplayName("deleteClinicAccessPolicy - Should delegate to repository")
    void deleteClinicAccessPolicy_ShouldDelete() {
        service.deleteClinicAccessPolicy("policy-id");

        verify(accessPolicyRepository).deleteClinicAccessPolicy("policy-id");
    }

    @Test
    @DisplayName("deleteHealthWorkerAccessPolicy - Should validate id")
    void deleteHealthWorkerAccessPolicy_ShouldValidateId() {
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> service.deleteHealthWorkerAccessPolicy(" "));

        assertEquals("Health worker access policy id is required", exception.getMessage());
    }

    @Test
    @DisplayName("deleteHealthWorkerAccessPolicy - Should delegate to repository")
    void deleteHealthWorkerAccessPolicy_ShouldDelete() {
        service.deleteHealthWorkerAccessPolicy("hw-policy-id");

        verify(accessPolicyRepository).deleteHealthWorkerAccessPolicy("hw-policy-id");
    }

    @Test
    @DisplayName("deleteSpecialtyAccessPolicy - Should validate id")
    void deleteSpecialtyAccessPolicy_ShouldValidateId() {
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> service.deleteSpecialtyAccessPolicy(null));

        assertEquals("Specialty access policy id is required", exception.getMessage());
    }

    @Test
    @DisplayName("deleteSpecialtyAccessPolicy - Should delegate to repository")
    void deleteSpecialtyAccessPolicy_ShouldDelete() {
        service.deleteSpecialtyAccessPolicy("sp-policy-id");

        verify(accessPolicyRepository).deleteSpecialtyAccessPolicy("sp-policy-id");
    }

    @Test
    @DisplayName("hasClinicAccess - Should return true when repository confirms access")
    void hasClinicAccess_ShouldReturnTrue() {
        when(healthUserRepository.findHealthUserByCi(healthUser.getCi())).thenReturn(healthUser);
        when(accessPolicyRepository.hasClinicAccess(healthUser.getId(), "Clinic One")).thenReturn(true);

        boolean result = service.hasClinicAccess(healthUser.getCi(), "Clinic One");

        assertTrue(result);
    }

    @Test
    @DisplayName("hasClinicAccess - Should return false when health user not found")
    void hasClinicAccess_ShouldReturnFalseWhenUserMissing() {
        when(healthUserRepository.findHealthUserByCi(healthUser.getCi())).thenReturn(null);

        boolean result = service.hasClinicAccess(healthUser.getCi(), "Clinic One");

        assertFalse(result);
    }

    @Test
    @DisplayName("hasClinicAccess - Should validate inputs")
    void hasClinicAccess_ShouldValidateInputs() {
        ValidationException missingHealthUser = assertThrows(
                ValidationException.class,
                () -> service.hasClinicAccess(null, "Clinic One"));
        assertEquals("Health user CI is required", missingHealthUser.getMessage());

        ValidationException missingClinic = assertThrows(
                ValidationException.class,
                () -> service.hasClinicAccess(healthUser.getCi(), ""));
        assertEquals("Clinic name is required", missingClinic.getMessage());
    }

    @Test
    @DisplayName("hasHealthWorkerAccess - Should return true when repository confirms access")
    void hasHealthWorkerAccess_ShouldReturnTrue() {
        when(healthUserRepository.findHealthUserByCi(healthUser.getCi())).thenReturn(healthUser);
        when(accessPolicyRepository.hasHealthWorkerAccess(healthUser.getId(), "33333333"))
                .thenReturn(true);

        boolean result = service.hasHealthWorkerAccess(healthUser.getCi(), "33333333");

        assertTrue(result);
    }

    @Test
    @DisplayName("hasHealthWorkerAccess - Should return false when health user not found")
    void hasHealthWorkerAccess_ShouldReturnFalseWhenUserMissing() {
        when(healthUserRepository.findHealthUserByCi(healthUser.getCi())).thenReturn(null);

        boolean result = service.hasHealthWorkerAccess(healthUser.getCi(), "33333333");

        assertFalse(result);
    }

    @Test
    @DisplayName("hasHealthWorkerAccess - Should validate inputs")
    void hasHealthWorkerAccess_ShouldValidateInputs() {
        ValidationException missingHealthUser = assertThrows(
                ValidationException.class,
                () -> service.hasHealthWorkerAccess("", "33333333"));
        assertEquals("Health user CI is required", missingHealthUser.getMessage());

        ValidationException missingWorker = assertThrows(
                ValidationException.class,
                () -> service.hasHealthWorkerAccess(healthUser.getCi(), null));
        assertEquals("Health worker CI is required", missingWorker.getMessage());
    }

    @Test
    @DisplayName("hasSpecialtyAccess - Should return true when repository confirms access")
    void hasSpecialtyAccess_ShouldReturnTrue() {
        List<String> specialties = List.of("Cardiology");
        when(healthUserRepository.findHealthUserByCi(healthUser.getCi())).thenReturn(healthUser);
        when(accessPolicyRepository.hasSpecialtyAccess(healthUser.getId(), specialties)).thenReturn(true);

        boolean result = service.hasSpecialtyAccess(healthUser.getCi(), specialties);

        assertTrue(result);
    }

    @Test
    @DisplayName("hasSpecialtyAccess - Should throw when health user not found")
    void hasSpecialtyAccess_ShouldThrowWhenUserMissing() {
        when(healthUserRepository.findHealthUserByCi(healthUser.getCi())).thenReturn(null);

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> service.hasSpecialtyAccess(healthUser.getCi(), List.of("Cardiology")));

        assertEquals("Health user not found", exception.getMessage());
    }

    @Test
    @DisplayName("hasSpecialtyAccess - Should validate inputs")
    void hasSpecialtyAccess_ShouldValidateInputs() {
        ValidationException missingHealthUser = assertThrows(
                ValidationException.class,
                () -> service.hasSpecialtyAccess("", List.of("Cardiology")));
        assertEquals("Health user CI is required", missingHealthUser.getMessage());

        ValidationException missingList = assertThrows(
                ValidationException.class,
                () -> service.hasSpecialtyAccess(healthUser.getCi(), new ArrayList<>()));
        assertEquals("Specialty names list is required", missingList.getMessage());
    }
}
