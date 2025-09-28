package grupo12.practico.dtos;

import java.io.Serializable;

import grupo12.practico.models.DocumentType;

public class DocumentTypeDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private DocumentType type;

    public DocumentTypeDTO() {
    }

    public DocumentTypeDTO(DocumentType type) {
        this.type = type;
    }

    public DocumentType getType() {
        return type;
    }

    public void setType(DocumentType type) {
        this.type = type;
    }
}
