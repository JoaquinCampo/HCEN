package grupo12.practico.dtos.ClinicalHistory;

import java.io.Serializable;

public class MessageDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String role;
    private String content;

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
