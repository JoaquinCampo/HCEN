package grupo12.practico.dtos.Auth;

/**
 * DTO for OIDC authorization request to gub.uy
 */
public class OidcAuthorizationRequestDTO {
    private String state;
    private String nonce;

    public OidcAuthorizationRequestDTO() {
    }

    public OidcAuthorizationRequestDTO(String state, String nonce) {
        this.state = state;
        this.nonce = nonce;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getNonce() {
        return nonce;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }
}
