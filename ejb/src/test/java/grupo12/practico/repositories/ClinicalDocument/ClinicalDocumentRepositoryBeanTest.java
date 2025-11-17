package grupo12.practico.repositories.ClinicalDocument;

import grupo12.practico.dtos.ClinicalDocument.AddClinicalDocumentDTO;
import grupo12.practico.dtos.ClinicalDocument.PresignedUrlRequestDTO;
import grupo12.practico.dtos.ClinicalDocument.PresignedUrlResponseDTO;
import grupo12.practico.dtos.ClinicalHistory.ChatRequestDTO;
import grupo12.practico.dtos.ClinicalHistory.ChatResponseDTO;
import grupo12.practico.dtos.ClinicalHistory.MessageDTO;
import grupo12.practico.repositories.NodoDocumentosConfig;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ClinicalDocumentRepositoryBean Tests")
class ClinicalDocumentRepositoryBeanTest {

    @Mock
    private NodoDocumentosConfig config;

    @Mock
    private HttpClient httpClient;

    @Mock
    private HttpResponse<String> httpResponse;

    private ClinicalDocumentRepositoryBean repository;

    private AddClinicalDocumentDTO addClinicalDocumentDTO;
    private PresignedUrlRequestDTO presignedUrlRequestDTO;
    private ChatRequestDTO chatRequestDTO;

    @BeforeEach
    void setUp() throws Exception {
        repository = new ClinicalDocumentRepositoryBean();
        Field configField = ClinicalDocumentRepositoryBean.class.getDeclaredField("config");
        configField.setAccessible(true);
        configField.set(repository, config);

        Field httpClientField = ClinicalDocumentRepositoryBean.class.getDeclaredField("httpClient");
        httpClientField.setAccessible(true);
        httpClientField.set(repository, httpClient);

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

        when(config.getDocumentsApiBaseUrl()).thenReturn("http://localhost:3001/api");
        when(config.getDocumentsApiKey()).thenReturn("test-api-key");
    }

    // getPresignedUploadUrl Tests
    @Test
    @DisplayName("getPresignedUploadUrl - Should return presigned URL successfully")
    void getPresignedUploadUrl_ShouldReturnPresignedUrlSuccessfully() throws Exception {
        String responseBody = """
                {
                    "upload_url": "https://s3.amazonaws.com/bucket/test.pdf?signature=abc123",
                    "object_key": "documents/test.pdf"
                }
                """;

        when(httpResponse.statusCode()).thenReturn(200);
        when(httpResponse.body()).thenReturn(responseBody);
        when(httpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
                .thenReturn(httpResponse);

        PresignedUrlResponseDTO result = repository.getPresignedUploadUrl(presignedUrlRequestDTO);

        assertNotNull(result);
        assertEquals("https://s3.amazonaws.com/bucket/test.pdf?signature=abc123", result.getUploadUrl());
        assertEquals("documents/test.pdf", result.getObjectKey());
    }

    @Test
    @DisplayName("getPresignedUploadUrl - Should throw when HTTP error occurs")
    void getPresignedUploadUrl_ShouldThrowWhenHttpError() throws Exception {
        when(httpResponse.statusCode()).thenReturn(500);
        when(httpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
                .thenReturn(httpResponse);

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> repository.getPresignedUploadUrl(presignedUrlRequestDTO));

        assertEquals("Failed to get presigned URL: HTTP 500", exception.getMessage());
    }

    @Test
    @DisplayName("getPresignedUploadUrl - Should throw when IOException occurs")
    void getPresignedUploadUrl_ShouldThrowWhenIOException() throws Exception {
        when(httpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
                .thenThrow(new IOException("Network error"));

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> repository.getPresignedUploadUrl(presignedUrlRequestDTO));

        assertEquals("Unable to request presigned URL", exception.getMessage());
    }

    @Test
    @DisplayName("getPresignedUploadUrl - Should throw when InterruptedException occurs")
    void getPresignedUploadUrl_ShouldThrowWhenInterruptedException() throws Exception {
        when(httpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
                .thenThrow(new InterruptedException("Interrupted"));

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> repository.getPresignedUploadUrl(presignedUrlRequestDTO));

        assertEquals("Interrupted while requesting presigned URL", exception.getMessage());
    }

    // createClinicalDocument Tests
    @Test
    @DisplayName("createClinicalDocument - Should create document successfully")
    void createClinicalDocument_ShouldCreateDocumentSuccessfully() throws Exception {
        String responseBody = """
                {
                    "doc_id": "doc-123"
                }
                """;

        when(httpResponse.statusCode()).thenReturn(201);
        when(httpResponse.body()).thenReturn(responseBody);
        when(httpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
                .thenReturn(httpResponse);

        String result = repository.createClinicalDocument(addClinicalDocumentDTO);

        assertEquals("doc-123", result);
    }

    @Test
    @DisplayName("createClinicalDocument - Should throw when HTTP error occurs")
    void createClinicalDocument_ShouldThrowWhenHttpError() throws Exception {
        when(httpResponse.statusCode()).thenReturn(400);
        when(httpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
                .thenReturn(httpResponse);

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> repository.createClinicalDocument(addClinicalDocumentDTO));

        assertEquals("Failed to create clinical document: HTTP 400", exception.getMessage());
    }

    @Test
    @DisplayName("createClinicalDocument - Should throw when IOException occurs")
    void createClinicalDocument_ShouldThrowWhenIOException() throws Exception {
        when(httpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
                .thenThrow(new IOException("Network error"));

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> repository.createClinicalDocument(addClinicalDocumentDTO));

        assertEquals("Unable to create clinical document", exception.getMessage());
    }

    @Test
    @DisplayName("createClinicalDocument - Should throw when InterruptedException occurs")
    void createClinicalDocument_ShouldThrowWhenInterruptedException() throws Exception {
        when(httpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
                .thenThrow(new InterruptedException("Interrupted"));

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> repository.createClinicalDocument(addClinicalDocumentDTO));

        assertEquals("Interrupted while creating clinical document", exception.getMessage());
    }

    // chat Tests
    @Test
    @DisplayName("chat - Should return chat response successfully")
    void chat_ShouldReturnChatResponseSuccessfully() throws Exception {
        String responseBody = """
                {
                    "answer": "The patient is in stable condition.",
                    "sources": [
                        {
                            "document_id": "doc-123",
                            "chunk_id": "chunk-1",
                            "text": "Patient shows normal vitals",
                            "similarity_score": 0.95
                        }
                    ]
                }
                """;

        when(httpResponse.statusCode()).thenReturn(200);
        when(httpResponse.body()).thenReturn(responseBody);
        when(httpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
                .thenReturn(httpResponse);

        ChatResponseDTO result = repository.chat(chatRequestDTO);

        assertNotNull(result);
        assertEquals("The patient is in stable condition.", result.getAnswer());
        assertNotNull(result.getSources());
        assertEquals(1, result.getSources().size());
        assertEquals("doc-123", result.getSources().get(0).getDocumentId());
    }

    @Test
    @DisplayName("chat - Should handle chat without document ID")
    void chat_ShouldHandleChatWithoutDocumentId() throws Exception {
        chatRequestDTO.setDocumentId(null);

        String responseBody = """
                {
                    "answer": "I need more context about the patient.",
                    "sources": []
                }
                """;

        when(httpResponse.statusCode()).thenReturn(200);
        when(httpResponse.body()).thenReturn(responseBody);
        when(httpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
                .thenReturn(httpResponse);

        ChatResponseDTO result = repository.chat(chatRequestDTO);

        assertNotNull(result);
        assertEquals("I need more context about the patient.", result.getAnswer());
        assertNotNull(result.getSources());
        assertTrue(result.getSources().isEmpty());
    }

    @Test
    @DisplayName("chat - Should handle chat without conversation history")
    void chat_ShouldHandleChatWithoutConversationHistory() throws Exception {
        chatRequestDTO.setConversationHistory(null);

        String responseBody = """
                {
                    "answer": "Hello! How can I help you today?",
                    "sources": []
                }
                """;

        when(httpResponse.statusCode()).thenReturn(200);
        when(httpResponse.body()).thenReturn(responseBody);
        when(httpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
                .thenReturn(httpResponse);

        ChatResponseDTO result = repository.chat(chatRequestDTO);

        assertNotNull(result);
        assertEquals("Hello! How can I help you today?", result.getAnswer());
    }

    @Test
    @DisplayName("chat - Should throw when HTTP error occurs")
    void chat_ShouldThrowWhenHttpError() throws Exception {
        when(httpResponse.statusCode()).thenReturn(500);
        when(httpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
                .thenReturn(httpResponse);

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> repository.chat(chatRequestDTO));

        assertEquals("Failed to process chat query: HTTP 500", exception.getMessage());
    }

    @Test
    @DisplayName("chat - Should throw when IOException occurs")
    void chat_ShouldThrowWhenIOException() throws Exception {
        when(httpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
                .thenThrow(new IOException("Network error"));

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> repository.chat(chatRequestDTO));

        assertEquals("Unable to process chat query", exception.getMessage());
    }

    @Test
    @DisplayName("chat - Should throw when InterruptedException occurs")
    void chat_ShouldThrowWhenInterruptedException() throws Exception {
        when(httpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
                .thenThrow(new InterruptedException("Interrupted"));

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> repository.chat(chatRequestDTO));

        assertEquals("Interrupted while processing chat query", exception.getMessage());
    }
}