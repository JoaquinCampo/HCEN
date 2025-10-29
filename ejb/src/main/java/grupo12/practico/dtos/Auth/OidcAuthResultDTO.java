package grupo12.practico.dtos.Auth;

/**
 * Aggregates the outcome of a successful OIDC callback
 */
public class OidcAuthResultDTO {
    private boolean verified;
    private String idToken;
    private String accessToken;
    private Integer expiresIn;
    private String scope;
    private OidcIdTokenDTO idTokenClaims;
    private OidcUserInfoDTO userInfo;
    private String logoutUrl;

    public OidcAuthResultDTO() {}

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public Integer getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Integer expiresIn) {
        this.expiresIn = expiresIn;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public OidcIdTokenDTO getIdTokenClaims() {
        return idTokenClaims;
    }

    public void setIdTokenClaims(OidcIdTokenDTO idTokenClaims) {
        this.idTokenClaims = idTokenClaims;
    }

    public OidcUserInfoDTO getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(OidcUserInfoDTO userInfo) {
        this.userInfo = userInfo;
    }

    public String getLogoutUrl() {
        return logoutUrl;
    }

    public void setLogoutUrl(String logoutUrl) {
        this.logoutUrl = logoutUrl;
    }
}

