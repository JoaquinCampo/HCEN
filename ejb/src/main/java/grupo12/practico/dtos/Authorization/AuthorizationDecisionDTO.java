package grupo12.practico.dtos.Authorization;

import java.io.Serializable;

public class AuthorizationDecisionDTO implements Serializable {

    private boolean allowed;
    private AuthorizationDecisionSource source;
    private String message;

    public AuthorizationDecisionDTO() {
    }

    private AuthorizationDecisionDTO(boolean allowed, AuthorizationDecisionSource source, String message) {
        this.allowed = allowed;
        this.source = source;
        this.message = message;
    }

    public static AuthorizationDecisionDTO allowed(AuthorizationDecisionSource source, String message) {
        return new AuthorizationDecisionDTO(true, source, message);
    }

    public static AuthorizationDecisionDTO denied(String message) {
        return new AuthorizationDecisionDTO(false, AuthorizationDecisionSource.NONE, message);
    }

    public boolean isAllowed() {
        return allowed;
    }

    public void setAllowed(boolean allowed) {
        this.allowed = allowed;
    }

    public AuthorizationDecisionSource getSource() {
        return source;
    }

    public void setSource(AuthorizationDecisionSource source) {
        this.source = source;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
