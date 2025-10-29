package grupo12.practico.services.Auth;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import java.util.logging.Logger;

/**
 * Configuration service for OpenID Connect parameters.
 * Reads configuration from environment variables.
 */
@Singleton
@Startup
public class OidcConfigurationService {

    private static final Logger LOGGER = Logger.getLogger(OidcConfigurationService.class.getName());

    private String authorizeUrl;
    private String tokenUrl;
    private String userinfoUrl;
    private String jwksUrl;
    private String clientId;
    private String clientSecret;
    private String redirectUri;
    private String scope;
    private String issuer;
    private String logoutUrl;
    private String postLogoutRedirectUri;

    @PostConstruct
    public void init() {
        authorizeUrl = getEnvOrDefault("OIDC_AUTHORIZE_URL", "https://auth.gub.uy/oidc/v1/authorize");
        tokenUrl = getEnvOrDefault("OIDC_TOKEN_URL", "https://auth.gub.uy/oidc/v1/token");
        userinfoUrl = getEnvOrDefault("OIDC_USERINFO_URL", "https://auth.gub.uy/oidc/v1/userinfo");
        // JWKS URL can be provided as OIDC_JWKS_URL or OIDC_JWT_VERIFY_URL
        jwksUrl = getFirstEnvPresent(new String[]{"OIDC_JWKS_URL", "OIDC_JWT_VERIFY_URL"},
                "https://auth.gub.uy/oidc/v1/jwks");
        clientId = getEnvOrDefault("OIDC_CLIENT_ID", "");
        clientSecret = getEnvOrDefault("OIDC_CLIENT_SECRET", "");
        redirectUri = getEnvOrDefault("OIDC_REDIRECT_URI", "http://localhost:8080/api/auth/gubuy/callback");
        scope = getEnvOrDefault("OIDC_SCOPE", "openid profile email");
        issuer = getEnvOrDefault("OIDC_ISSUER", "");
        // Logout endpoint can vary (logout / end_session). Allow override.
        logoutUrl = getEnvOrDefault("OIDC_LOGOUT_URL", "https://auth.gub.uy/oidc/v1/logout");
        postLogoutRedirectUri = getEnvOrDefault("OIDC_POST_LOGOUT_REDIRECT_URI", redirectUri);

        LOGGER.info("OIDC Configuration loaded:");
        LOGGER.info("  Authorize URL: " + authorizeUrl);
        LOGGER.info("  Token URL: " + tokenUrl);
        LOGGER.info("  Userinfo URL: " + userinfoUrl);
        LOGGER.info("  JWKS URL: " + jwksUrl);
        LOGGER.info("  Client ID: " + (clientId.isEmpty() ? "NOT SET" : "***"));
        LOGGER.info("  Redirect URI: " + redirectUri);
        LOGGER.info("  Scope: " + scope);
        LOGGER.info("  Issuer: " + (issuer.isEmpty() ? "NOT SET" : issuer));
        LOGGER.info("  Logout URL: " + logoutUrl);
        LOGGER.info("  Post-logout Redirect URI: " + postLogoutRedirectUri);
    }

    private String getEnvOrDefault(String key, String defaultValue) {
        String value = System.getenv(key);
        return value != null ? value : defaultValue;
    }
    
    private String getFirstEnvPresent(String[] keys, String defaultValue) {
        for (String k : keys) {
            String v = System.getenv(k);
            if (v != null && !v.isEmpty()) return v;
        }
        return defaultValue;
    }

    public String getAuthorizeUrl() {
        return authorizeUrl;
    }

    public String getTokenUrl() {
        return tokenUrl;
    }

    public String getUserinfoUrl() {
        return userinfoUrl;
    }
    
    public String getJwksUrl() {
        return jwksUrl;
    }

    public String getClientId() {
        return clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public String getRedirectUri() {
        return redirectUri;
    }

    public String getScope() {
        return scope;
    }

    public boolean isConfigured() {
        return clientId != null && !clientId.isEmpty()
                && clientSecret != null && !clientSecret.isEmpty();
    }

    public String getIssuer() {
        return issuer;
    }

    public String getLogoutUrl() {
        return logoutUrl;
    }

    public String getPostLogoutRedirectUri() {
        return postLogoutRedirectUri;
    }
}
