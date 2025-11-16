package grupo12.practico.dtos.ClinicalDocument;

import java.io.Serializable;

public class AddClinicalDocumentResponseDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}

