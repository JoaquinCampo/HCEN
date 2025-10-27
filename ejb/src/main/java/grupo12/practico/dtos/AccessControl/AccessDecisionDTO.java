package grupo12.practico.dtos.AccessControl;

import java.io.Serializable;

public class AccessDecisionDTO implements Serializable {

    private boolean allowed;
    private AccessDecisionSource source;
    private String message;

    public AccessDecisionDTO() {
    }

    private AccessDecisionDTO(boolean allowed, AccessDecisionSource source, String message) {
        this.allowed = allowed;
        this.source = source;
        this.message = message;
    }

    public static AccessDecisionDTO allowed(AccessDecisionSource source, String message) {
        return new AccessDecisionDTO(true, source, message);
    }

    public static AccessDecisionDTO denied(String message) {
        return new AccessDecisionDTO(false, AccessDecisionSource.NONE, message);
    }

    public boolean isAllowed() {
        return allowed;
    }

    public void setAllowed(boolean allowed) {
        this.allowed = allowed;
    }

    public AccessDecisionSource getSource() {
        return source;
    }

    public void setSource(AccessDecisionSource source) {
        this.source = source;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
