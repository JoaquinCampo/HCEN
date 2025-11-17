package grupo12.practico.repositories.Clinic;

import grupo12.practico.dtos.Clinic.AddClinicDTO;
import grupo12.practico.dtos.Clinic.ClinicAdminDTO;
import grupo12.practico.dtos.Clinic.ClinicDTO;
import grupo12.practico.dtos.HealthWorker.HealthWorkerDTO;
import grupo12.practico.repositories.NodosPerifericosConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ClinicRepositoryBean Tests")
class ClinicRepositoryBeanTest {

    @Mock
    private NodosPerifericosConfig config;

    @Mock
    private HttpClient httpClient;

    @Mock
    private HttpResponse<String> httpResponse;

    private ClinicRepositoryBean repository;

    private AddClinicDTO addClinicDTO;
    private ClinicAdminDTO clinicAdminDTO;
    private ClinicDTO clinicDTO;

    @BeforeEach
    void setUp() throws Exception {
        repository = new ClinicRepositoryBean();
        Field configField = ClinicRepositoryBean.class.getDeclaredField("config");
        configField.setAccessible(true);
        configField.set(repository, config);

        Field httpClientField = ClinicRepositoryBean.class.getDeclaredField("httpClient");
        httpClientField.setAccessible(true);
        httpClientField.set(repository, httpClient);

        LocalDate now = LocalDate.of(2023, 6, 15);

        clinicAdminDTO = new ClinicAdminDTO();
        clinicAdminDTO.setCi("11111111");
        clinicAdminDTO.setFirstName("Admin");
        clinicAdminDTO.setLastName("User");
        clinicAdminDTO.setEmail("admin@clinic.com");
        clinicAdminDTO.setPhone("123456789");
        clinicAdminDTO.setAddress("123 Admin St");
        clinicAdminDTO.setDateOfBirth(LocalDate.of(1980, 1, 1));

        addClinicDTO = new AddClinicDTO();
        addClinicDTO.setName("Test Clinic");
        addClinicDTO.setEmail("clinic@test.com");
        addClinicDTO.setPhone("987654321");
        addClinicDTO.setAddress("456 Clinic Ave");
        addClinicDTO.setProviderName("Test Provider");
        addClinicDTO.setClinicAdmin(clinicAdminDTO);

        clinicDTO = new ClinicDTO();
        clinicDTO.setId("clinic-123");
        clinicDTO.setName("Test Clinic");
        clinicDTO.setEmail("clinic@test.com");
        clinicDTO.setPhone("987654321");
        clinicDTO.setAddress("456 Clinic Ave");
        clinicDTO.setCreatedAt(now);
        clinicDTO.setUpdatedAt(now);

        HealthWorkerDTO hw1 = new HealthWorkerDTO();
        hw1.setCi("22222222");
        hw1.setFirstName("Dr.");
        hw1.setLastName("Smith");
        hw1.setEmail("dr.smith@clinic.com");

        HealthWorkerDTO hw2 = new HealthWorkerDTO();
        hw2.setCi("33333333");
        hw2.setFirstName("Dr.");
        hw2.setLastName("Johnson");
        hw2.setEmail("dr.johnson@clinic.com");

        when(config.getClinicsApiUrl()).thenReturn("http://localhost:3000/api/clinics");
    }

    // createClinic Tests
    @Test
    @DisplayName("createClinic - Should create clinic successfully")
    void createClinic_ShouldCreateClinicSuccessfully() throws Exception {
        String responseBody = """
                {
                    "id": "clinic-123",
                    "name": "Test Clinic",
                    "email": "clinic@test.com",
                    "phone": "987654321",
                    "address": "456 Clinic Ave",
                    "createdAt": "2023-06-15T00:00:00.000Z",
                    "updatedAt": "2023-06-15T00:00:00.000Z",
                    "healthWorkers": [
                        {
                            "ci": "22222222",
                            "firstName": "Dr.",
                            "lastName": "Smith",
                            "email": "dr.smith@clinic.com"
                        }
                    ]
                }
                """;

        when(httpResponse.statusCode()).thenReturn(201);
        when(httpResponse.body()).thenReturn(responseBody);
        when(httpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
                .thenReturn(httpResponse);

        ClinicDTO result = repository.createClinic(addClinicDTO);

        assertNotNull(result);
        assertEquals("clinic-123", result.getId());
        assertEquals("Test Clinic", result.getName());
        assertEquals("clinic@test.com", result.getEmail());
        assertEquals("987654321", result.getPhone());
        assertEquals("456 Clinic Ave", result.getAddress());
        assertEquals(LocalDate.of(2023, 6, 15), result.getCreatedAt());

        verify(httpClient).send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()));
    }

    @Test
    @DisplayName("createClinic - Should throw when HTTP error occurs")
    void createClinic_ShouldThrowWhenHttpError() throws Exception {
        when(httpResponse.statusCode()).thenReturn(400);
        when(httpResponse.body()).thenReturn("Bad Request");
        when(httpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
                .thenReturn(httpResponse);

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> repository.createClinic(addClinicDTO));

        assertEquals("Failed to create clinic: HTTP 400", exception.getMessage());
    }

    @Test
    @DisplayName("createClinic - Should throw when IOException occurs")
    void createClinic_ShouldThrowWhenIOException() throws Exception {
        when(httpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
                .thenThrow(new IOException("Network error"));

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> repository.createClinic(addClinicDTO));

        assertEquals("Unable to create clinic", exception.getMessage());
        assertTrue(exception.getCause() instanceof IOException);
    }

    @Test
    @DisplayName("createClinic - Should throw when InterruptedException occurs")
    void createClinic_ShouldThrowWhenInterruptedException() throws Exception {
        when(httpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
                .thenThrow(new InterruptedException("Interrupted"));

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> repository.createClinic(addClinicDTO));

        assertEquals("Interrupted while creating clinic", exception.getMessage());
        assertTrue(exception.getCause() instanceof InterruptedException);
        assertTrue(Thread.currentThread().isInterrupted());
    }

    // findClinicByName Tests
    @Test
    @DisplayName("findClinicByName - Should return clinic when found")
    void findClinicByName_ShouldReturnClinicWhenFound() throws Exception {
        String responseBody = """
                {
                    "id": "clinic-123",
                    "name": "Test Clinic",
                    "email": "clinic@test.com",
                    "phone": "987654321",
                    "address": "456 Clinic Ave",
                    "createdAt": "2023-06-15T00:00:00.000Z",
                    "updatedAt": "2023-06-15T00:00:00.000Z",
                    "healthWorkers": []
                }
                """;

        when(httpResponse.statusCode()).thenReturn(200);
        when(httpResponse.body()).thenReturn(responseBody);
        when(httpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
                .thenReturn(httpResponse);

        ClinicDTO result = repository.findClinicByName("Test Clinic");

        assertNotNull(result);
        assertEquals("clinic-123", result.getId());
        assertEquals("Test Clinic", result.getName());
    }

    @Test
    @DisplayName("findClinicByName - Should return null when clinic not found")
    void findClinicByName_ShouldReturnNullWhenNotFound() throws Exception {
        when(httpResponse.statusCode()).thenReturn(404);
        when(httpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
                .thenReturn(httpResponse);

        ClinicDTO result = repository.findClinicByName("Nonexistent Clinic");

        assertNull(result);
    }

    @Test
    @DisplayName("findClinicByName - Should throw when HTTP error occurs")
    void findClinicByName_ShouldThrowWhenHttpError() throws Exception {
        when(httpResponse.statusCode()).thenReturn(500);
        when(httpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
                .thenReturn(httpResponse);

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> repository.findClinicByName("Test Clinic"));

        assertEquals("Failed to fetch clinic: HTTP 500", exception.getMessage());
    }

    @Test
    @DisplayName("findClinicByName - Should handle URL encoding")
    void findClinicByName_ShouldHandleUrlEncoding() throws Exception {
        String clinicName = "Clinic with spaces & special chars";
        String responseBody = """
                {
                    "id": "clinic-123",
                    "name": "Clinic with spaces & special chars",
                    "email": "clinic@test.com",
                    "phone": "987654321",
                    "address": "456 Clinic Ave",
                    "createdAt": "2023-06-15T00:00:00.000Z",
                    "updatedAt": "2023-06-15T00:00:00.000Z",
                    "healthWorkers": []
                }
                """;

        when(httpResponse.statusCode()).thenReturn(200);
        when(httpResponse.body()).thenReturn(responseBody);
        when(httpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
                .thenReturn(httpResponse);

        ClinicDTO result = repository.findClinicByName(clinicName);

        assertNotNull(result);
        assertEquals("Clinic with spaces & special chars", result.getName());

        // Verify the request was made with encoded URL
        verify(httpClient).send(argThat(request -> {
            String uri = request.uri().toString();
            return uri.contains("Clinic%20with%20spaces%20%26%20special%20chars");
        }), eq(HttpResponse.BodyHandlers.ofString()));
    }

    // findAllClinics Tests
    @Test
    @DisplayName("findAllClinics - Should return all clinics without provider filter")
    void findAllClinics_ShouldReturnAllClinics() throws Exception {
        String responseBody = """
                [
                    {
                        "id": "clinic-1",
                        "name": "Clinic One",
                        "email": "clinic1@test.com",
                        "phone": "111111111",
                        "address": "Address 1",
                        "createdAt": "2023-06-15T00:00:00.000Z",
                        "updatedAt": "2023-06-15T00:00:00.000Z",
                        "healthWorkers": []
                    },
                    {
                        "id": "clinic-2",
                        "name": "Clinic Two",
                        "email": "clinic2@test.com",
                        "phone": "222222222",
                        "address": "Address 2",
                        "createdAt": "2023-06-15T00:00:00.000Z",
                        "updatedAt": "2023-06-15T00:00:00.000Z",
                        "healthWorkers": []
                    }
                ]
                """;

        when(httpResponse.statusCode()).thenReturn(200);
        when(httpResponse.body()).thenReturn(responseBody);
        when(httpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
                .thenReturn(httpResponse);

        List<ClinicDTO> result = repository.findAllClinics(null);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("clinic-1", result.get(0).getId());
        assertEquals("Clinic One", result.get(0).getName());
        assertEquals("clinic-2", result.get(1).getId());
        assertEquals("Clinic Two", result.get(1).getName());
    }

    @Test
    @DisplayName("findAllClinics - Should return clinics filtered by provider")
    void findAllClinics_ShouldReturnClinicsFilteredByProvider() throws Exception {
        String responseBody = """
                [
                    {
                        "id": "clinic-1",
                        "name": "Clinic One",
                        "email": "clinic1@test.com",
                        "phone": "111111111",
                        "address": "Address 1",
                        "createdAt": "2023-06-15T00:00:00.000Z",
                        "updatedAt": "2023-06-15T00:00:00.000Z",
                        "healthWorkers": []
                    }
                ]
                """;

        when(httpResponse.statusCode()).thenReturn(200);
        when(httpResponse.body()).thenReturn(responseBody);
        when(httpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
                .thenReturn(httpResponse);

        List<ClinicDTO> result = repository.findAllClinics("Test Provider");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("clinic-1", result.get(0).getId());

        // Verify the request includes provider query parameter
        verify(httpClient).send(argThat(request -> {
            String uri = request.uri().toString();
            return uri.contains("providerName=Test%20Provider");
        }), eq(HttpResponse.BodyHandlers.ofString()));
    }

    @Test
    @DisplayName("findAllClinics - Should return empty list when no clinics found")
    void findAllClinics_ShouldReturnEmptyListWhenNoClinics() throws Exception {
        String responseBody = "[]";

        when(httpResponse.statusCode()).thenReturn(200);
        when(httpResponse.body()).thenReturn(responseBody);
        when(httpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
                .thenReturn(httpResponse);

        List<ClinicDTO> result = repository.findAllClinics(null);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("findAllClinics - Should throw when HTTP error occurs")
    void findAllClinics_ShouldThrowWhenHttpError() throws Exception {
        when(httpResponse.statusCode()).thenReturn(500);
        when(httpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
                .thenReturn(httpResponse);

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> repository.findAllClinics(null));

        assertEquals("Failed to fetch clinics: HTTP 500", exception.getMessage());
    }

    @Test
    @DisplayName("findAllClinics - Should throw when IOException occurs")
    void findAllClinics_ShouldThrowWhenIOException() throws Exception {
        when(httpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
                .thenThrow(new IOException("Network error"));

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> repository.findAllClinics(null));

        assertEquals("Unable to fetch clinics data", exception.getMessage());
        assertTrue(exception.getCause() instanceof IOException);
    }

}