package grupo12.practico.repositories.HealthWorker;

import grupo12.practico.dtos.HealthWorker.HealthWorkerDTO;
import grupo12.practico.repositories.NodosPerifericosConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("HealthWorkerRepositoryBean Tests")
class HealthWorkerRepositoryBeanTest {

    @Mock
    private NodosPerifericosConfig config;

    @Mock
    private HttpClient httpClient;

    @InjectMocks
    private HealthWorkerRepositoryBean repository;

    private HealthWorkerDTO testHealthWorkerDTO;

    @BeforeEach
    void setUp() throws Exception {
        // Setup test data
        testHealthWorkerDTO = new HealthWorkerDTO();
        testHealthWorkerDTO.setCi("87654321");
        testHealthWorkerDTO.setFirstName("Jane");
        testHealthWorkerDTO.setLastName("Smith");
        testHealthWorkerDTO.setEmail("jane.smith@example.com");
        testHealthWorkerDTO.setPhone("+59887654321");
        testHealthWorkerDTO.setAddress("456 Health St");
        testHealthWorkerDTO.setDateOfBirth(LocalDate.of(1985, 5, 15));

        // Inject the mocked HttpClient using reflection since it's created in
        // constructor
        java.lang.reflect.Field httpClientField = HealthWorkerRepositoryBean.class.getDeclaredField("httpClient");
        httpClientField.setAccessible(true);
        httpClientField.set(repository, httpClient);
    }

    @Test
    @DisplayName("findByClinicAndCi - Should successfully fetch health worker")
    void testFindByClinicAndCi_Success() throws Exception {
        // Arrange
        String clinicName = "Clinic A";
        String healthWorkerCi = "87654321";

        String mockResponse = """
                {
                    "user": {
                        "ci": "87654321",
                        "firstName": "Jane",
                        "lastName": "Smith",
                        "email": "jane.smith@example.com",
                        "phone": "+59887654321",
                        "address": "456 Health St",
                        "dateOfBirth": "1985-05-15"
                    }
                }
                """;

        when(config.getClinicsApiUrl()).thenReturn("http://api.example.com/clinics");

        @SuppressWarnings("unchecked")
        HttpResponse<String> mockHttpResponse = mock(HttpResponse.class);
        when(mockHttpResponse.statusCode()).thenReturn(200);
        when(mockHttpResponse.body()).thenReturn(mockResponse);

        doReturn(mockHttpResponse).when(httpClient).send(any(HttpRequest.class), any());

        // Act
        HealthWorkerDTO result = repository.findByClinicAndCi(clinicName, healthWorkerCi);

        // Assert
        assertNotNull(result);
        assertEquals("87654321", result.getCi());
        assertEquals("Jane", result.getFirstName());
        assertEquals("Smith", result.getLastName());
        assertEquals("jane.smith@example.com", result.getEmail());
        assertEquals("+59887654321", result.getPhone());
        assertEquals("456 Health St", result.getAddress());
        assertEquals(LocalDate.of(1985, 5, 15), result.getDateOfBirth());

        verify(httpClient).send(any(HttpRequest.class), any());
    }

    @Test
    @DisplayName("findByClinicAndCi - Should return null for 404 response")
    void testFindByClinicAndCi_NotFound() throws Exception {
        // Arrange
        String clinicName = "Clinic A";
        String healthWorkerCi = "99999999";

        when(config.getClinicsApiUrl()).thenReturn("http://api.example.com/clinics");

        @SuppressWarnings("unchecked")
        HttpResponse<String> mockHttpResponse = mock(HttpResponse.class);
        when(mockHttpResponse.statusCode()).thenReturn(404);
        when(mockHttpResponse.body()).thenReturn("Not Found");

        doReturn(mockHttpResponse).when(httpClient).send(any(HttpRequest.class), any());

        // Act
        HealthWorkerDTO result = repository.findByClinicAndCi(clinicName, healthWorkerCi);

        // Assert
        assertNull(result);

        verify(httpClient).send(any(HttpRequest.class), any());
    }

    @Test
    @DisplayName("findByClinicAndCi - Should throw IllegalArgumentException for null clinic name")
    void testFindByClinicAndCi_NullClinicName() {
        assertThrows(IllegalArgumentException.class, () -> repository.findByClinicAndCi(null, "87654321"));
    }

    @Test
    @DisplayName("findByClinicAndCi - Should throw IllegalArgumentException for empty clinic name")
    void testFindByClinicAndCi_EmptyClinicName() {
        assertThrows(IllegalArgumentException.class, () -> repository.findByClinicAndCi("", "87654321"));
    }

    @Test
    @DisplayName("findByClinicAndCi - Should throw IllegalArgumentException for null health worker CI")
    void testFindByClinicAndCi_NullCi() {
        assertThrows(IllegalArgumentException.class, () -> repository.findByClinicAndCi("Clinic A", null));
    }

    @Test
    @DisplayName("findByClinicAndCi - Should throw IllegalArgumentException for empty health worker CI")
    void testFindByClinicAndCi_EmptyCi() {
        assertThrows(IllegalArgumentException.class, () -> repository.findByClinicAndCi("Clinic A", ""));
    }

    @Test
    @DisplayName("findByClinicAndCi - Should throw IllegalStateException for HTTP error")
    void testFindByClinicAndCi_HttpError() throws Exception {
        // Arrange
        String clinicName = "Clinic A";
        String healthWorkerCi = "87654321";

        when(config.getClinicsApiUrl()).thenReturn("http://api.example.com/clinics");

        @SuppressWarnings("unchecked")
        HttpResponse<String> mockHttpResponse = mock(HttpResponse.class);
        when(mockHttpResponse.statusCode()).thenReturn(500);
        when(mockHttpResponse.body()).thenReturn("Internal Server Error");

        doReturn(mockHttpResponse).when(httpClient).send(any(HttpRequest.class), any());

        // Act & Assert
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> repository.findByClinicAndCi(clinicName, healthWorkerCi));

        assertTrue(exception.getMessage().contains("HTTP 500"));
    }

    @Test
    @DisplayName("findByClinicAndCi - Should handle invalid JSON response")
    void testFindByClinicAndCi_InvalidJson() throws Exception {
        // Arrange
        String clinicName = "Clinic A";
        String healthWorkerCi = "87654321";

        String invalidJson = "invalid json response";

        when(config.getClinicsApiUrl()).thenReturn("http://api.example.com/clinics");

        @SuppressWarnings("unchecked")
        HttpResponse<String> mockHttpResponse = mock(HttpResponse.class);
        when(mockHttpResponse.statusCode()).thenReturn(200);
        when(mockHttpResponse.body()).thenReturn(invalidJson);

        doReturn(mockHttpResponse).when(httpClient).send(any(HttpRequest.class), any());

        // Act & Assert
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> repository.findByClinicAndCi(clinicName, healthWorkerCi));

        assertTrue(exception.getMessage().contains("Invalid JSON received"));
    }

    @Test
    @DisplayName("findByClinicAndCi - Should handle IOException")
    void testFindByClinicAndCi_IOException() throws Exception {
        // Arrange
        String clinicName = "Clinic A";
        String healthWorkerCi = "87654321";

        when(config.getClinicsApiUrl()).thenReturn("http://api.example.com/clinics");

        doThrow(new java.io.IOException("Network error")).when(httpClient).send(any(HttpRequest.class), any());

        // Act & Assert
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> repository.findByClinicAndCi(clinicName, healthWorkerCi));

        assertTrue(exception.getMessage().contains("Unable to fetch health worker data"));
    }

    @Test
    @DisplayName("findByClinicAndCi - Should handle InterruptedException")
    void testFindByClinicAndCi_InterruptedException() throws Exception {
        // Arrange
        String clinicName = "Clinic A";
        String healthWorkerCi = "87654321";

        when(config.getClinicsApiUrl()).thenReturn("http://api.example.com/clinics");

        doThrow(new InterruptedException("Interrupted")).when(httpClient).send(any(HttpRequest.class), any());

        // Act & Assert
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> repository.findByClinicAndCi(clinicName, healthWorkerCi));

        assertTrue(exception.getMessage().contains("Interrupted while fetching health worker data"));
    }

    @Test
    @DisplayName("findByClinic - Should successfully fetch health workers list")
    void testFindByClinic_Success() throws Exception {
        // Arrange
        String clinicName = "Clinic A";
        String jsonResponse = """
                [
                    {
                        "user": {
                            "ci": "87654321",
                            "firstName": "Jane",
                            "lastName": "Smith",
                            "email": "jane.smith@example.com",
                            "phone": "+59887654321",
                            "address": "456 Health St",
                            "dateOfBirth": "1985-05-15"
                        }
                    },
                    {
                        "user": {
                            "ci": "12345678",
                            "firstName": "John",
                            "lastName": "Doe",
                            "email": "john.doe@example.com",
                            "phone": "+59812345678",
                            "address": "123 Medical Ave",
                            "dateOfBirth": "1980-03-20"
                        }
                    }
                ]
                """;

        when(config.getClinicsApiUrl()).thenReturn("http://api.example.com/clinics");

        @SuppressWarnings("unchecked")
        HttpResponse<String> mockHttpResponse = mock(HttpResponse.class);
        when(mockHttpResponse.statusCode()).thenReturn(200);
        when(mockHttpResponse.body()).thenReturn(jsonResponse);

        doReturn(mockHttpResponse).when(httpClient).send(any(HttpRequest.class), any());

        // Act
        List<HealthWorkerDTO> result = repository.findByClinic(clinicName);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());

        HealthWorkerDTO firstWorker = result.get(0);
        assertEquals("87654321", firstWorker.getCi());
        assertEquals("Jane", firstWorker.getFirstName());
        assertEquals("Smith", firstWorker.getLastName());
        assertEquals("jane.smith@example.com", firstWorker.getEmail());
        assertEquals("+59887654321", firstWorker.getPhone());
        assertEquals("456 Health St", firstWorker.getAddress());
        assertEquals(LocalDate.of(1985, 5, 15), firstWorker.getDateOfBirth());

        HealthWorkerDTO secondWorker = result.get(1);
        assertEquals("12345678", secondWorker.getCi());
        assertEquals("John", secondWorker.getFirstName());
        assertEquals("Doe", secondWorker.getLastName());
    }

    @Test
    @DisplayName("findByClinic - Should return null for 404 response")
    void testFindByClinic_NotFound() throws Exception {
        // Arrange
        String clinicName = "NonExistentClinic";

        when(config.getClinicsApiUrl()).thenReturn("http://api.example.com/clinics");

        @SuppressWarnings("unchecked")
        HttpResponse<String> mockHttpResponse = mock(HttpResponse.class);
        when(mockHttpResponse.statusCode()).thenReturn(404);

        doReturn(mockHttpResponse).when(httpClient).send(any(HttpRequest.class), any());

        // Act
        List<HealthWorkerDTO> result = repository.findByClinic(clinicName);

        // Assert
        assertNull(result);
    }

    @Test
    @DisplayName("findByClinic - Should throw IllegalArgumentException for null clinic name")
    void testFindByClinic_NullClinicName() {
        assertThrows(IllegalArgumentException.class, () -> repository.findByClinic(null));
    }

    @Test
    @DisplayName("findByClinic - Should throw IllegalArgumentException for empty clinic name")
    void testFindByClinic_EmptyClinicName() {
        assertThrows(IllegalArgumentException.class, () -> repository.findByClinic(""));
    }

    @Test
    @DisplayName("findByClinic - Should throw IllegalStateException for HTTP error")
    void testFindByClinic_HttpError() throws Exception {
        // Arrange
        String clinicName = "Clinic A";

        when(config.getClinicsApiUrl()).thenReturn("http://api.example.com/clinics");

        @SuppressWarnings("unchecked")
        HttpResponse<String> mockHttpResponse = mock(HttpResponse.class);
        when(mockHttpResponse.statusCode()).thenReturn(500);

        doReturn(mockHttpResponse).when(httpClient).send(any(HttpRequest.class), any());

        // Act & Assert
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> repository.findByClinic(clinicName));

        assertTrue(exception.getMessage().contains("Failed to fetch clinic health workers: HTTP 500"));
    }

    @Test
    @DisplayName("findByClinic - Should handle IOException")
    void testFindByClinic_IOException() throws Exception {
        // Arrange
        String clinicName = "Clinic A";

        when(config.getClinicsApiUrl()).thenReturn("http://api.example.com/clinics");

        doThrow(new java.io.IOException("Network error")).when(httpClient).send(any(HttpRequest.class), any());

        // Act & Assert
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> repository.findByClinic(clinicName));

        assertTrue(exception.getMessage().contains("Unable to fetch clinic health workers"));
    }

    @Test
    @DisplayName("findByClinic - Should handle InterruptedException")
    void testFindByClinic_InterruptedException() throws Exception {
        // Arrange
        String clinicName = "Clinic A";

        when(config.getClinicsApiUrl()).thenReturn("http://api.example.com/clinics");

        doThrow(new InterruptedException("Interrupted")).when(httpClient).send(any(HttpRequest.class), any());

        // Act & Assert
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> repository.findByClinic(clinicName));

        assertTrue(exception.getMessage().contains("Interrupted while fetching clinic health workers"));
    }

    @Test
    @DisplayName("findByClinic - Should handle invalid JSON response")
    void testFindByClinic_InvalidJson() throws Exception {
        // Arrange
        String clinicName = "Clinic A";
        String invalidJson = "invalid json response";

        when(config.getClinicsApiUrl()).thenReturn("http://api.example.com/clinics");

        @SuppressWarnings("unchecked")
        HttpResponse<String> mockHttpResponse = mock(HttpResponse.class);
        when(mockHttpResponse.statusCode()).thenReturn(200);
        when(mockHttpResponse.body()).thenReturn(invalidJson);

        doReturn(mockHttpResponse).when(httpClient).send(any(HttpRequest.class), any());

        // Act & Assert
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> repository.findByClinic(clinicName));

        assertTrue(exception.getMessage().contains("Invalid JSON received for clinic health workers"));
    }

    @Test
    @DisplayName("findByClinic - Should return empty list for empty JSON response")
    void testFindByClinic_EmptyResponse() throws Exception {
        // Arrange
        String clinicName = "Clinic A";

        when(config.getClinicsApiUrl()).thenReturn("http://api.example.com/clinics");

        @SuppressWarnings("unchecked")
        HttpResponse<String> mockHttpResponse = mock(HttpResponse.class);
        when(mockHttpResponse.statusCode()).thenReturn(200);
        when(mockHttpResponse.body()).thenReturn("");

        doReturn(mockHttpResponse).when(httpClient).send(any(HttpRequest.class), any());

        // Act
        List<HealthWorkerDTO> result = repository.findByClinic(clinicName);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}