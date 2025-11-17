package grupo12.practico.messaging.ClinicalDocument;

import grupo12.practico.dtos.ClinicalDocument.AddClinicalDocumentDTO;
import grupo12.practico.services.ClinicalDocument.ClinicalDocumentServiceLocal;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.TextMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CreateDocumentMDB Tests")
class CreateDocumentMDBTest {

    @Mock
    private ClinicalDocumentServiceLocal clinicalDocumentService;

    @Mock
    private TextMessage textMessage;

    @Mock
    private Message nonTextMessage;

    private CreateDocumentMDB mdb;

    @BeforeEach
    void setUp() throws Exception {
        mdb = new CreateDocumentMDB();

        Field serviceField = CreateDocumentMDB.class.getDeclaredField("clinicalDocumentService");
        serviceField.setAccessible(true);
        serviceField.set(mdb, clinicalDocumentService);
    }

    @Test
    @DisplayName("onMessage - Should process valid text message successfully")
    void onMessage_ShouldProcessValidTextMessageSuccessfully() throws Exception {
        String jsonPayload = """
                {
                    "title": "Test Document",
                    "description": "Test Description",
                    "healthWorkerCi": "12345678",
                    "healthUserCi": "87654321",
                    "clinicName": "Test Clinic"
                }
                """;

        when(textMessage.getText()).thenReturn(jsonPayload);

        mdb.onMessage(textMessage);

        verify(clinicalDocumentService).createClinicalDocument(any(AddClinicalDocumentDTO.class));
        verify(textMessage).getText();
    }

    @Test
    @DisplayName("onMessage - Should ignore non-text messages")
    void onMessage_ShouldIgnoreNonTextMessages() {
        mdb.onMessage(nonTextMessage);

        verifyNoInteractions(clinicalDocumentService);
        verifyNoInteractions(nonTextMessage);
    }

    @Test
    @DisplayName("onMessage - Should handle validation exception from message mapper")
    void onMessage_ShouldHandleValidationExceptionFromMessageMapper() throws Exception {
        String invalidJson = "{ invalid json }";
        when(textMessage.getText()).thenReturn(invalidJson);

        mdb.onMessage(textMessage);

        verify(clinicalDocumentService, never()).createClinicalDocument(any());
        verify(textMessage).getText();
    }

    @Test
    @DisplayName("onMessage - Should handle JMS exception when reading message")
    void onMessage_ShouldHandleJMSExceptionWhenReadingMessage() throws Exception {
        when(textMessage.getText()).thenThrow(new JMSException("JMS Error"));

        mdb.onMessage(textMessage);

        verify(clinicalDocumentService, never()).createClinicalDocument(any());
        verify(textMessage).getText();
    }

    @Test
    @DisplayName("onMessage - Should handle service exception")
    void onMessage_ShouldHandleServiceException() throws Exception {
        String jsonPayload = """
                {
                    "title": "Test Document",
                    "healthWorkerCi": "12345678",
                    "healthUserCi": "87654321"
                }
                """;

        when(textMessage.getText()).thenReturn(jsonPayload);
        doThrow(new RuntimeException("Service error")).when(clinicalDocumentService)
                .createClinicalDocument(any(AddClinicalDocumentDTO.class));

        mdb.onMessage(textMessage);

        verify(clinicalDocumentService).createClinicalDocument(any(AddClinicalDocumentDTO.class));
        verify(textMessage).getText();
    }

    @Test
    @DisplayName("onMessage - Should handle null message gracefully")
    void onMessage_ShouldHandleNullMessageGracefully() {
        mdb.onMessage(null);

        verifyNoInteractions(clinicalDocumentService);
    }

    @Test
    @DisplayName("onMessage - Should process message with all fields populated")
    void onMessage_ShouldProcessMessageWithAllFieldsPopulated() throws Exception {
        String jsonPayload = """
                {
                    "title": "Complete Test Document",
                    "description": "Complete test description",
                    "content": "Test content",
                    "contentType": "application/pdf",
                    "contentUrl": "http://example.com/doc.pdf",
                    "healthWorkerCi": "12345678",
                    "healthUserCi": "87654321",
                    "clinicName": "Test Clinic",
                    "providerName": "Test Provider",
                    "specialtyNames": ["Cardiology", "Neurology"]
                }
                """;

        when(textMessage.getText()).thenReturn(jsonPayload);

        mdb.onMessage(textMessage);

        verify(clinicalDocumentService).createClinicalDocument(any(AddClinicalDocumentDTO.class));
        verify(textMessage).getText();
    }

    @Test
    @DisplayName("onMessage - Should process message with minimal required fields")
    void onMessage_ShouldProcessMessageWithMinimalRequiredFields() throws Exception {
        String jsonPayload = """
                {
                    "healthWorkerCi": "12345678",
                    "healthUserCi": "87654321"
                }
                """;

        when(textMessage.getText()).thenReturn(jsonPayload);

        mdb.onMessage(textMessage);

        verify(clinicalDocumentService).createClinicalDocument(any(AddClinicalDocumentDTO.class));
        verify(textMessage).getText();
    }
}