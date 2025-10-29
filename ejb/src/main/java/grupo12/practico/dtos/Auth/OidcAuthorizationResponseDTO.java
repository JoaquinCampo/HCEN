package grupo12.practico.dtos.Auth;

/**
 * DTO for OIDC authorization response
 */
public class OidcAuthorizationResponseDTO {
    private String authorizationUrl;
    private String state;

    public OidcAuthorizationResponseDTO() {
    }

    public OidcAuthorizationResponseDTO(String authorizationUrl, String state) {
        this.authorizationUrl = authorizationUrl;
        this.state = state;
    }

    public String getAuthorizationUrl() {
        return authorizationUrl;
    }

    public void setAuthorizationUrl(String authorizationUrl) {
        this.authorizationUrl = authorizationUrl;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
