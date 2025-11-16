package grupo12.practico.dtos.ClinicalHistory;

import java.io.Serializable;
import java.util.List;

public class ChatRequestDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String query;
    private List<MessageDTO> conversationHistory;
    private String healthUserCi;
    private String documentId;

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public List<MessageDTO> getConversationHistory() {
        return conversationHistory;
    }

    public void setConversationHistory(List<MessageDTO> conversationHistory) {
        this.conversationHistory = conversationHistory;
    }

    public String getHealthUserCi() {
        return healthUserCi;
    }

    public void setHealthUserCi(String healthUserCi) {
        this.healthUserCi = healthUserCi;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }
}
