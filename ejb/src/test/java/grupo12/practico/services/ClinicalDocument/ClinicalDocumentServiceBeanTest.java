package grupo12.practico.services.ClinicalDocument;

import grupo12.practico.dtos.ClinicalDocument.AddClinicalDocumentDTO;
import grupo12.practico.dtos.ClinicalDocument.PresignedUrlRequestDTO;
import grupo12.practico.dtos.ClinicalDocument.PresignedUrlResponseDTO;
import grupo12.practico.dtos.ClinicalHistory.ChatRequestDTO;
import grupo12.practico.dtos.ClinicalHistory.ChatResponseDTO;
import grupo12.practico.dtos.ClinicalHistory.MessageDTO;
import grupo12.practico.repositories.ClinicalDocument.ClinicalDocumentRepositoryLocal;
import grupo12.practico.services.AccessPolicy.AccessPolicyServiceLocal;
import grupo12.practico.services.Logger.LoggerServiceLocal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ClinicalDocumentServiceBean Tests")
class ClinicalDocumentServiceBeanTest {

    @Mock
    private AccessPolicyServiceLocal accessPolicyService;

    @Mock
    private ClinicalDocumentRepositoryLocal clinicalDocumentRepository;

    @Mock
    private LoggerServiceLocal loggerService;

    private ClinicalDocumentServiceBean service;

    private AddClinicalDocumentDTO addClinicalDocumentDTO;
    private PresignedUrlRequestDTO presignedUrlRequestDTO;
    private ChatRequestDTO chatRequestDTO;
    private PresignedUrlResponseDTO presignedUrlResponseDTO;
    private ChatResponseDTO chatResponseDTO;

    @BeforeEach
    void setUp() throws Exception {
        service = new ClinicalDocumentServiceBean();

        // Use reflection to inject mocked dependencies
        Field accessPolicyField = ClinicalDocumentServiceBean.class.getDeclaredField("accessPolicyService");
        accessPolicyField.setAccessible(true);
        accessPolicyField.set(service, accessPolicyService);

        Field repositoryField = ClinicalDocumentServiceBean.class.getDeclaredField("clinicalDocumentRepository");
        repositoryField.setAccessible(true);
        repositoryField.set(service, clinicalDocumentRepository);

        Field loggerField = ClinicalDocumentServiceBean.class.getDeclaredField("loggerService");
        loggerField.setAccessible(true);
        loggerField.set(service, loggerService);

        // Setup test data
        addClinicalDocumentDTO = new AddClinicalDocumentDTO();
        addClinicalDocumentDTO.setTitle("Test Document");
        addClinicalDocumentDTO.setDescription("Test Description");
        addClinicalDocumentDTO.setContent("Test Content");
        addClinicalDocumentDTO.setContentType("application/pdf");
        addClinicalDocumentDTO.setContentUrl("https://example.com/document.pdf");
        addClinicalDocumentDTO.setHealthWorkerCi("12345678");
        addClinicalDocumentDTO.setHealthUserCi("87654321");
        addClinicalDocumentDTO.setClinicName("Test Clinic");
        addClinicalDocumentDTO.setProviderName("Test Provider");
        addClinicalDocumentDTO.setSpecialtyNames(List.of("Cardiology"));

        presignedUrlRequestDTO = new PresignedUrlRequestDTO();
        presignedUrlRequestDTO.setFileName("test.pdf");
        presignedUrlRequestDTO.setContentType("application/pdf");
        presignedUrlRequestDTO.setHealthUserCi("87654321");
        presignedUrlRequestDTO.setHealthWorkerCi("12345678");
        presignedUrlRequestDTO.setClinicName("Test Clinic");
        presignedUrlRequestDTO.setProviderName("Test Provider");
        presignedUrlRequestDTO.setSpecialtyNames(List.of("Cardiology"));

        chatRequestDTO = new ChatRequestDTO();
        chatRequestDTO.setQuery("What is the patient's condition?");
        chatRequestDTO.setHealthUserCi("87654321");
        chatRequestDTO.setDocumentId("doc-123");

        MessageDTO message = new MessageDTO();
        message.setRole("user");
        message.setContent("Hello");
        chatRequestDTO.setConversationHistory(List.of(message));

        presignedUrlResponseDTO = new PresignedUrlResponseDTO();
        presignedUrlResponseDTO.setUploadUrl("https://s3.amazonaws.com/bucket/test.pdf?signature=abc123");
        presignedUrlResponseDTO.setObjectKey("documents/test.pdf");

        chatResponseDTO = new ChatResponseDTO();
        chatResponseDTO.setAnswer("The patient is in stable condition.");
    }

    // getPresignedUploadUrl Tests
    @Test
    @DisplayName("getPresignedUploadUrl - Should return presigned URL when user has clinic access")
    void getPresignedUploadUrl_ShouldReturnPresignedUrlWhenUserHasClinicAccess() {
        when(accessPolicyService.hasClinicAccess("87654321", "Test Clinic")).thenReturn(true);
        when(accessPolicyService.hasHealthWorkerAccess("87654321", "12345678")).thenReturn(false);
        when(accessPolicyService.hasSpecialtyAccess("87654321", List.of("Cardiology"))).thenReturn(false);
        when(clinicalDocumentRepository.getPresignedUploadUrl(presignedUrlRequestDTO))
                .thenReturn(presignedUrlResponseDTO);

        PresignedUrlResponseDTO result = service.getPresignedUploadUrl(presignedUrlRequestDTO);

        assertNotNull(result);
        assertEquals("https://s3.amazonaws.com/bucket/test.pdf?signature=abc123", result.getUploadUrl());
        assertEquals("documents/test.pdf", result.getObjectKey());
        verify(clinicalDocumentRepository).getPresignedUploadUrl(presignedUrlRequestDTO);
    }

    @Test
    @DisplayName("getPresignedUploadUrl - Should return presigned URL when user has health worker access")
    void getPresignedUploadUrl_ShouldReturnPresignedUrlWhenUserHasHealthWorkerAccess() {
        when(accessPolicyService.hasClinicAccess("87654321", "Test Clinic")).thenReturn(false);
        when(accessPolicyService.hasHealthWorkerAccess("87654321", "12345678")).thenReturn(true);
        when(accessPolicyService.hasSpecialtyAccess("87654321", List.of("Cardiology"))).thenReturn(false);
        when(clinicalDocumentRepository.getPresignedUploadUrl(presignedUrlRequestDTO))
                .thenReturn(presignedUrlResponseDTO);

        PresignedUrlResponseDTO result = service.getPresignedUploadUrl(presignedUrlRequestDTO);

        assertNotNull(result);
        verify(clinicalDocumentRepository).getPresignedUploadUrl(presignedUrlRequestDTO);
    }

    @Test
    @DisplayName("getPresignedUploadUrl - Should return presigned URL when user has specialty access")
    void getPresignedUploadUrl_ShouldReturnPresignedUrlWhenUserHasSpecialtyAccess() {
        when(accessPolicyService.hasClinicAccess("87654321", "Test Clinic")).thenReturn(false);
        when(accessPolicyService.hasHealthWorkerAccess("87654321", "12345678")).thenReturn(false);
        when(accessPolicyService.hasSpecialtyAccess("87654321", List.of("Cardiology"))).thenReturn(true);
        when(clinicalDocumentRepository.getPresignedUploadUrl(presignedUrlRequestDTO))
                .thenReturn(presignedUrlResponseDTO);

        PresignedUrlResponseDTO result = service.getPresignedUploadUrl(presignedUrlRequestDTO);

        assertNotNull(result);
        verify(clinicalDocumentRepository).getPresignedUploadUrl(presignedUrlRequestDTO);
    }

    @Test
    @DisplayName("getPresignedUploadUrl - Should throw ValidationException when user has no access")
    void getPresignedUploadUrl_ShouldThrowValidationExceptionWhenUserHasNoAccess() {
        when(accessPolicyService.hasClinicAccess("87654321", "Test Clinic")).thenReturn(false);
        when(accessPolicyService.hasHealthWorkerAccess("87654321", "12345678")).thenReturn(false);
        when(accessPolicyService.hasSpecialtyAccess("87654321", List.of("Cardiology"))).thenReturn(false);

        jakarta.validation.ValidationException exception = assertThrows(
                jakarta.validation.ValidationException.class,
                () -> service.getPresignedUploadUrl(presignedUrlRequestDTO));

        assertEquals("Health worker does not have access to upload documents for the specified health user.",
                exception.getMessage());
        verify(clinicalDocumentRepository, never()).getPresignedUploadUrl(any());
    }

    @Test
    @DisplayName("getPresignedUploadUrl - Should throw ValidationException when request is null")
    void getPresignedUploadUrl_ShouldThrowValidationExceptionWhenRequestIsNull() {
        jakarta.validation.ValidationException exception = assertThrows(
                jakarta.validation.ValidationException.class,
                () -> service.getPresignedUploadUrl(null));

        assertEquals("Presigned URL request must not be null", exception.getMessage());
        verify(clinicalDocumentRepository, never()).getPresignedUploadUrl(any());
    }

    @Test
    @DisplayName("getPresignedUploadUrl - Should throw ValidationException when fileName is null")
    void getPresignedUploadUrl_ShouldThrowValidationExceptionWhenFileNameIsNull() {
        presignedUrlRequestDTO.setFileName(null);

        jakarta.validation.ValidationException exception = assertThrows(
                jakarta.validation.ValidationException.class,
                () -> service.getPresignedUploadUrl(presignedUrlRequestDTO));

        assertEquals("File name is required", exception.getMessage());
        verify(clinicalDocumentRepository, never()).getPresignedUploadUrl(any());
    }

    @Test
    @DisplayName("getPresignedUploadUrl - Should throw ValidationException when fileName is empty")
    void getPresignedUploadUrl_ShouldThrowValidationExceptionWhenFileNameIsEmpty() {
        presignedUrlRequestDTO.setFileName("");

        jakarta.validation.ValidationException exception = assertThrows(
                jakarta.validation.ValidationException.class,
                () -> service.getPresignedUploadUrl(presignedUrlRequestDTO));

        assertEquals("File name is required", exception.getMessage());
        verify(clinicalDocumentRepository, never()).getPresignedUploadUrl(any());
    }

    @Test
    @DisplayName("getPresignedUploadUrl - Should throw ValidationException when contentType is null")
    void getPresignedUploadUrl_ShouldThrowValidationExceptionWhenContentTypeIsNull() {
        presignedUrlRequestDTO.setContentType(null);

        jakarta.validation.ValidationException exception = assertThrows(
                jakarta.validation.ValidationException.class,
                () -> service.getPresignedUploadUrl(presignedUrlRequestDTO));

        assertEquals("Content type is required", exception.getMessage());
        verify(clinicalDocumentRepository, never()).getPresignedUploadUrl(any());
    }

    @Test
    @DisplayName("getPresignedUploadUrl - Should throw ValidationException when clinicName is null")
    void getPresignedUploadUrl_ShouldThrowValidationExceptionWhenClinicNameIsNull() {
        presignedUrlRequestDTO.setClinicName(null);

        jakarta.validation.ValidationException exception = assertThrows(
                jakarta.validation.ValidationException.class,
                () -> service.getPresignedUploadUrl(presignedUrlRequestDTO));

        assertEquals("Clinic name is required", exception.getMessage());
        verify(clinicalDocumentRepository, never()).getPresignedUploadUrl(any());
    }

    // createClinicalDocument Tests
    @Test
    @DisplayName("createClinicalDocument - Should create document and log successfully")
    void createClinicalDocument_ShouldCreateDocumentAndLogSuccessfully() {
        when(clinicalDocumentRepository.createClinicalDocument(addClinicalDocumentDTO)).thenReturn("doc-123");

        String result = service.createClinicalDocument(addClinicalDocumentDTO);

        assertEquals("doc-123", result);
        verify(clinicalDocumentRepository).createClinicalDocument(addClinicalDocumentDTO);
        verify(loggerService).logDocumentCreated("doc-123", "87654321", "12345678", "Test Clinic");
    }

    @Test
    @DisplayName("createClinicalDocument - Should create document even if logging fails")
    void createClinicalDocument_ShouldCreateDocumentEvenIfLoggingFails() {
        when(clinicalDocumentRepository.createClinicalDocument(addClinicalDocumentDTO)).thenReturn("doc-123");
        doThrow(new RuntimeException("Logging failed")).when(loggerService).logDocumentCreated(any(), any(), any(),
                any());

        String result = service.createClinicalDocument(addClinicalDocumentDTO);

        assertEquals("doc-123", result);
        verify(clinicalDocumentRepository).createClinicalDocument(addClinicalDocumentDTO);
        verify(loggerService).logDocumentCreated("doc-123", "87654321", "12345678", "Test Clinic");
    }

    @Test
    @DisplayName("createClinicalDocument - Should throw ValidationException when request is null")
    void createClinicalDocument_ShouldThrowValidationExceptionWhenRequestIsNull() {
        jakarta.validation.ValidationException exception = assertThrows(
                jakarta.validation.ValidationException.class,
                () -> service.createClinicalDocument(null));

        assertEquals("Clinical document creation request must not be null", exception.getMessage());
        verify(clinicalDocumentRepository, never()).createClinicalDocument(any());
    }

    @Test
    @DisplayName("createClinicalDocument - Should throw ValidationException when title is null")
    void createClinicalDocument_ShouldThrowValidationExceptionWhenTitleIsNull() {
        addClinicalDocumentDTO.setTitle(null);

        jakarta.validation.ValidationException exception = assertThrows(
                jakarta.validation.ValidationException.class,
                () -> service.createClinicalDocument(addClinicalDocumentDTO));

        assertEquals("Title is required", exception.getMessage());
        verify(clinicalDocumentRepository, never()).createClinicalDocument(any());
    }

    @Test
    @DisplayName("createClinicalDocument - Should throw ValidationException when title is blank")
    void createClinicalDocument_ShouldThrowValidationExceptionWhenTitleIsBlank() {
        addClinicalDocumentDTO.setTitle("");

        jakarta.validation.ValidationException exception = assertThrows(
                jakarta.validation.ValidationException.class,
                () -> service.createClinicalDocument(addClinicalDocumentDTO));

        assertEquals("Title is required", exception.getMessage());
        verify(clinicalDocumentRepository, never()).createClinicalDocument(any());
    }

    @Test
    @DisplayName("createClinicalDocument - Should throw ValidationException when healthWorkerCi is null")
    void createClinicalDocument_ShouldThrowValidationExceptionWhenHealthWorkerCiIsNull() {
        addClinicalDocumentDTO.setHealthWorkerCi(null);

        jakarta.validation.ValidationException exception = assertThrows(
                jakarta.validation.ValidationException.class,
                () -> service.createClinicalDocument(addClinicalDocumentDTO));

        assertEquals("Created by (health worker CI) is required", exception.getMessage());
        verify(clinicalDocumentRepository, never()).createClinicalDocument(any());
    }

    @Test
    @DisplayName("createClinicalDocument - Should throw ValidationException when healthUserCi is null")
    void createClinicalDocument_ShouldThrowValidationExceptionWhenHealthUserCiIsNull() {
        addClinicalDocumentDTO.setHealthUserCi(null);

        jakarta.validation.ValidationException exception = assertThrows(
                jakarta.validation.ValidationException.class,
                () -> service.createClinicalDocument(addClinicalDocumentDTO));

        assertEquals("Health user CI is required", exception.getMessage());
        verify(clinicalDocumentRepository, never()).createClinicalDocument(any());
    }

    @Test
    @DisplayName("createClinicalDocument - Should throw ValidationException when clinicName is null")
    void createClinicalDocument_ShouldThrowValidationExceptionWhenClinicNameIsNull() {
        addClinicalDocumentDTO.setClinicName(null);

        jakarta.validation.ValidationException exception = assertThrows(
                jakarta.validation.ValidationException.class,
                () -> service.createClinicalDocument(addClinicalDocumentDTO));

        assertEquals("Clinic name is required", exception.getMessage());
        verify(clinicalDocumentRepository, never()).createClinicalDocument(any());
    }

    @Test
    @DisplayName("createClinicalDocument - Should throw ValidationException when providerName is null")
    void createClinicalDocument_ShouldThrowValidationExceptionWhenProviderNameIsNull() {
        addClinicalDocumentDTO.setProviderName(null);

        jakarta.validation.ValidationException exception = assertThrows(
                jakarta.validation.ValidationException.class,
                () -> service.createClinicalDocument(addClinicalDocumentDTO));

        assertEquals("Provider name is required", exception.getMessage());
        verify(clinicalDocumentRepository, never()).createClinicalDocument(any());
    }

    // chat Tests
    @Test
    @DisplayName("chat - Should return chat response successfully")
    void chat_ShouldReturnChatResponseSuccessfully() {
        when(clinicalDocumentRepository.chat(chatRequestDTO)).thenReturn(chatResponseDTO);

        ChatResponseDTO result = service.chat(chatRequestDTO);

        assertNotNull(result);
        assertEquals("The patient is in stable condition.", result.getAnswer());
        verify(clinicalDocumentRepository).chat(chatRequestDTO);
    }

    @Test
    @DisplayName("chat - Should throw ValidationException when request is null")
    void chat_ShouldThrowValidationExceptionWhenRequestIsNull() {
        jakarta.validation.ValidationException exception = assertThrows(
                jakarta.validation.ValidationException.class,
                () -> service.chat(null));

        assertEquals("Chat request must not be null", exception.getMessage());
        verify(clinicalDocumentRepository, never()).chat(any());
    }

    @Test
    @DisplayName("chat - Should throw ValidationException when query is null")
    void chat_ShouldThrowValidationExceptionWhenQueryIsNull() {
        chatRequestDTO.setQuery(null);

        jakarta.validation.ValidationException exception = assertThrows(
                jakarta.validation.ValidationException.class,
                () -> service.chat(chatRequestDTO));

        assertEquals("Query is required", exception.getMessage());
        verify(clinicalDocumentRepository, never()).chat(any());
    }

    @Test
    @DisplayName("chat - Should throw ValidationException when query is empty")
    void chat_ShouldThrowValidationExceptionWhenQueryIsEmpty() {
        chatRequestDTO.setQuery("");

        jakarta.validation.ValidationException exception = assertThrows(
                jakarta.validation.ValidationException.class,
                () -> service.chat(chatRequestDTO));

        assertEquals("Query is required", exception.getMessage());
        verify(clinicalDocumentRepository, never()).chat(any());
    }

    @Test
    @DisplayName("chat - Should throw ValidationException when healthUserCi is null")
    void chat_ShouldThrowValidationExceptionWhenHealthUserCiIsNull() {
        chatRequestDTO.setHealthUserCi(null);

        jakarta.validation.ValidationException exception = assertThrows(
                jakarta.validation.ValidationException.class,
                () -> service.chat(chatRequestDTO));

        assertEquals("Health user CI is required", exception.getMessage());
        verify(clinicalDocumentRepository, never()).chat(any());
    }

    @Test
    @DisplayName("chat - Should throw ValidationException when healthUserCi is empty")
    void chat_ShouldThrowValidationExceptionWhenHealthUserCiIsEmpty() {
        chatRequestDTO.setHealthUserCi("");

        jakarta.validation.ValidationException exception = assertThrows(
                jakarta.validation.ValidationException.class,
                () -> service.chat(chatRequestDTO));

        assertEquals("Health user CI is required", exception.getMessage());
        verify(clinicalDocumentRepository, never()).chat(any());
    }
}