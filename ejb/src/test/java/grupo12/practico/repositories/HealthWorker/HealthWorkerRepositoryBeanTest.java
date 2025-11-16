package grupo12.practico.repositories.HealthWorker;

import grupo12.practico.dtos.HealthWorker.HealthWorkerDTO;
import grupo12.practico.repositories.NodosPerifericosConfig;
import jakarta.validation.ValidationException;
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
    @DisplayName("findByClinicAndCi - Should throw ValidationException for null clinic name")
    void testFindByClinicAndCi_NullClinicName() {
        // Act & Assert
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> repository.findByClinicAndCi(null, "87654321"));

        assertEquals("Clinic name must not be blank", exception.getMessage());
    }

    @Test
    @DisplayName("findByClinicAndCi - Should throw ValidationException for empty clinic name")
    void testFindByClinicAndCi_EmptyClinicName() {
        // Act & Assert
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> repository.findByClinicAndCi("", "87654321"));

        assertEquals("Clinic name must not be blank", exception.getMessage());
    }

    @Test
    @DisplayName("findByClinicAndCi - Should throw ValidationException for null health worker CI")
    void testFindByClinicAndCi_NullCi() {
        // Act & Assert
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> repository.findByClinicAndCi("Clinic A", null));

        assertEquals("Health worker CI must not be blank", exception.getMessage());
    }

    @Test
    @DisplayName("findByClinicAndCi - Should throw ValidationException for empty health worker CI")
    void testFindByClinicAndCi_EmptyCi() {
        // Act & Assert
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> repository.findByClinicAndCi("Clinic A", ""));

        assertEquals("Health worker CI must not be blank", exception.getMessage());
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
}