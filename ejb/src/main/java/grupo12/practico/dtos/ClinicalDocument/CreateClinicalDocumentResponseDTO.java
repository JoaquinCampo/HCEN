package grupo12.practico.dtos.ClinicalDocument;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;

public class CreateClinicalDocumentResponseDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @JsonProperty("doc_id")
    private String docId;

    public CreateClinicalDocumentResponseDTO() {
    }

    public CreateClinicalDocumentResponseDTO(String docId) {
        this.docId = docId;
    }

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }
}

