package grupo12.practico.services.Auth;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import grupo12.practico.dtos.Auth.*;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.proc.JWSKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import jakarta.ejb.EJB;
import jakarta.ejb.Local;
import jakarta.ejb.Remote;
import jakarta.ejb.Stateless;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.Form;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import java.util.Date;

@Stateless
@Local(OidcAuthenticationServiceLocal.class)
@Remote(OidcAuthenticationServiceRemote.class)
public class OidcAuthenticationService implements OidcAuthenticationServiceLocal {

    private static final Logger LOGGER = Logger.getLogger(OidcAuthenticationService.class.getName());
    private static final Map<String, String> STATE_STORE = new ConcurrentHashMap<>();
    private static final Map<String, String> NONCE_STORE = new ConcurrentHashMap<>();
    private static final Map<String, String> CODE_VERIFIER_STORE = new ConcurrentHashMap<>();

    @EJB
    private OidcConfigurationService config;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public OidcAuthorizationResponseDTO initiateAuthorization() {
        if (!config.isConfigured()) {
            throw new IllegalStateException("OIDC is not configured. Please set OIDC_CLIENT_ID and OIDC_CLIENT_SECRET");
        }

        // Generate state and nonce for CSRF protection and replay attack prevention
        String state = generateRandomString(32);
        String nonce = generateRandomString(32);
        String codeVerifier = generateRandomString(64);
        String codeChallenge = pkceS256(codeVerifier);

        // Store state and nonce for validation in callback
        STATE_STORE.put(state, state);
        NONCE_STORE.put(state, nonce);

        // Build authorization URL
        StringBuilder authUrl = new StringBuilder(config.getAuthorizeUrl());
        authUrl.append("?response_type=code");
        authUrl.append("&client_id=").append(urlEncode(config.getClientId()));
        authUrl.append("&redirect_uri=").append(urlEncode(config.getRedirectUri()));
        authUrl.append("&scope=").append(urlEncode(config.getScope()));
        authUrl.append("&state=").append(urlEncode(state));
        authUrl.append("&nonce=").append(urlEncode(nonce));
        authUrl.append("&code_challenge=").append(urlEncode(codeChallenge));
        authUrl.append("&code_challenge_method=S256");
        if (config.getAcrValues() != null && !config.getAcrValues().isEmpty()) {
            authUrl.append("&acr_values=").append(urlEncode(config.getAcrValues()));
        }

        LOGGER.info("Authorization URL created for state: " + state);
        LOGGER.info("Authorize URL: " + authUrl);

        // store verifier for later exchange
        CODE_VERIFIER_STORE.put(state, codeVerifier);

        return new OidcAuthorizationResponseDTO(authUrl.toString(), state);
    }

    public OidcAuthResultDTO handleCallback(String code, String state) throws Exception {
        // Validate state
        if (state == null || !STATE_STORE.containsKey(state)) {
            throw new IllegalArgumentException("Invalid state parameter");
        }

        // Retrieve and keep nonce for claim validation
        String expectedNonce = NONCE_STORE.get(state);

        // Exchange authorization code for tokens
        String codeVerifier = CODE_VERIFIER_STORE.get(state);
        OidcTokenResponseDTO tokenResponse = exchangeCodeForToken(code, codeVerifier);

        // Verify ID Token signature and claims via JWKS
        JWTClaimsSet idTokenClaims = verifyIdToken(tokenResponse.getIdToken(), expectedNonce);

        // After successful verification, clean up state and nonce
        STATE_STORE.remove(state);
        NONCE_STORE.remove(state);
        if (codeVerifier != null)
            CODE_VERIFIER_STORE.remove(state);

        // Get user info
        OidcUserInfoDTO userInfo = getUserInfoDTO(tokenResponse.getAccessToken());

        // Build result
        OidcAuthResultDTO result = new OidcAuthResultDTO();
        result.setVerified(true);
        result.setIdToken(tokenResponse.getIdToken());
        result.setAccessToken(tokenResponse.getAccessToken());
        result.setExpiresIn(tokenResponse.getExpiresIn());
        result.setScope(tokenResponse.getScope());

        OidcIdTokenDTO claimsDto = new OidcIdTokenDTO();
        claimsDto.setIssuer(idTokenClaims.getIssuer());
        claimsDto.setSubject(idTokenClaims.getSubject());
        claimsDto.setAudience(idTokenClaims.getAudience());
        claimsDto.setIssuedAt(
                idTokenClaims.getIssueTime() != null ? idTokenClaims.getIssueTime().toInstant().getEpochSecond()
                        : null);
        claimsDto.setExpiresAt(idTokenClaims.getExpirationTime() != null
                ? idTokenClaims.getExpirationTime().toInstant().getEpochSecond()
                : null);
        Object nonce = idTokenClaims.getClaim("nonce");
        claimsDto.setNonce(nonce != null ? nonce.toString() : null);
        result.setIdTokenClaims(claimsDto);

        result.setUserInfo(userInfo);

        // Compose logout URL
        String logoutUrl = buildLogoutUrl(tokenResponse.getIdToken());
        result.setLogoutUrl(logoutUrl);

        return result;
    }

    private OidcTokenResponseDTO exchangeCodeForToken(String code, String codeVerifier) throws Exception {
        Client client = ClientBuilder.newClient();

        try {
            Form form = new Form();
            form.param("grant_type", "authorization_code");
            form.param("code", code);
            form.param("redirect_uri", config.getRedirectUri());
            if (codeVerifier != null && !codeVerifier.isEmpty()) {
                form.param("code_verifier", codeVerifier);
            }

            LOGGER.info("Exchanging code for token at: " + config.getTokenUrl());

            String basic = Base64.getEncoder().encodeToString(
                    (config.getClientId() + ":" + config.getClientSecret()).getBytes(StandardCharsets.UTF_8));
            Response response = client.target(config.getTokenUrl())
                    .request(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Basic " + basic)
                    .post(Entity.form(form));

            if (response.getStatus() != 200) {
                String error = response.readEntity(String.class);
                LOGGER.severe("Token exchange failed: " + error);
                throw new RuntimeException("Failed to exchange code for token: " + error);
            }

            String responseBody = response.readEntity(String.class);
            LOGGER.info("Token response received");

            return objectMapper.readValue(responseBody, OidcTokenResponseDTO.class);

        } finally {
            client.close();
        }
    }

    private String getUserInfo(String accessToken) throws Exception {
        Client client = ClientBuilder.newClient();

        try {
            LOGGER.info("Fetching user info from: " + config.getUserinfoUrl());

            Response response = client.target(config.getUserinfoUrl())
                    .request(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + accessToken)
                    .get();

            if (response.getStatus() != 200) {
                String error = response.readEntity(String.class);
                LOGGER.severe("User info fetch failed: " + error);
                throw new RuntimeException("Failed to get user info: " + error);
            }

            String responseBody = response.readEntity(String.class);
            LOGGER.info("User info received");

            return responseBody;

        } finally {
            client.close();
        }
    }

    private OidcUserInfoDTO getUserInfoDTO(String accessToken) throws Exception {
        String body = getUserInfo(accessToken);
        return objectMapper.readValue(body, OidcUserInfoDTO.class);
    }

    /**
     * Generates a cryptographically secure random string
     */
    private String generateRandomString(int length) {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[length];
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    /**
     * URL encodes a string
     */
    private String urlEncode(String value) {
        try {
            return URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("UTF-8 encoding not supported", e);
        }
    }

    /**
     * Creates a PKCE S256 code challenge from the given verifier
     */
    private String pkceS256(String verifier) {
        try {
            java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(verifier.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create PKCE challenge", e);
        }
    }

    private ConfigurableJWTProcessor<com.nimbusds.jose.proc.SecurityContext> jwtProcessor;

    public void initJwtProcessor() throws Exception {
        jwtProcessor = new DefaultJWTProcessor<>();
        JWKSource<SecurityContext> keySource = buildLenientJWKSource();
        JWSAlgorithm expectedAlgorithm = JWSAlgorithm.RS256;
        JWSKeySelector<SecurityContext> keySelector = new JWSVerificationKeySelector<>(expectedAlgorithm, keySource);
        jwtProcessor.setJWSKeySelector(keySelector);
    }

    private JWTClaimsSet verifyIdToken(String idToken, String expectedNonce) throws Exception {
        if (idToken == null || idToken.isEmpty()) {
            throw new IllegalArgumentException("Missing id_token from token response");
        }

        if (idToken.startsWith("Bearer ")) {
            idToken = idToken.split(" ")[1];
        }

        if (jwtProcessor == null) {
            initJwtProcessor();
        }

        JWTClaimsSet claims = jwtProcessor.process(idToken, null);

        validateClaims(claims);
        // Optional nonce validation (recommended)
        if (expectedNonce != null) {
            Object nonce = claims.getClaim("nonce");
            if (nonce == null || !expectedNonce.equals(nonce.toString())) {
                throw new Exception("Invalid nonce");
            }
        }

        return claims;
    }

    /**
     * Builds a JWK source that ignores invalid x5c chains by constructing RSA keys
     * from modulus and exponent only.
     */
    private JWKSource<SecurityContext> buildLenientJWKSource() throws Exception {
        Client client = ClientBuilder.newClient();
        String jwksJson;
        try {
            Response response = client.target(config.getJwksUrl())
                    .request(MediaType.APPLICATION_JSON)
                    .get();
            if (response.getStatus() != 200) {
                String error = response.readEntity(String.class);
                throw new RuntimeException("Failed to fetch JWKS: " + error);
            }
            jwksJson = response.readEntity(String.class);
        } finally {
            client.close();
        }

        Map<String, Object> jwksMap = objectMapper.readValue(jwksJson, new TypeReference<Map<String, Object>>() {});
        Object keysObj = jwksMap.get("keys");
        java.util.List<JWK> jwkList = new java.util.ArrayList<>();
        if (keysObj instanceof java.util.List) {
            for (Object k : (java.util.List<?>) keysObj) {
                if (!(k instanceof Map))
                    continue;
                @SuppressWarnings("unchecked")
                Map<String, Object> keyMap = (Map<String, Object>) k;
                Object kty = keyMap.get("kty");
                if (kty == null || !"RSA".equalsIgnoreCase(kty.toString()))
                    continue;
                Object n = keyMap.get("n");
                Object e = keyMap.get("e");
                if (n == null || e == null)
                    continue;
                RSAKey.Builder builder = new RSAKey.Builder(new Base64URL(n.toString()), new Base64URL(e.toString()));
                Object kid = keyMap.get("kid");
                if (kid != null)
                    builder.keyID(kid.toString());
                Object use = keyMap.get("use");
                if (use != null) {
                    try {
                        builder.keyUse(KeyUse.parse(use.toString()));
                    } catch (Exception ignore) {
                    }
                }
                jwkList.add(builder.build());
            }
        }

        final JWKSet jwkSet = new JWKSet(jwkList);
        return (jwkSelector, context) -> jwkSelector.select(jwkSet);
    }

    private void validateClaims(JWTClaimsSet claims) throws Exception {
        // Check issuer
        String issuer = claims.getIssuer();
        String ISSUER = config.getIssuer();
        if (!ISSUER.equals(issuer)) {
            throw new Exception("Invalid issuer: " + issuer);
        }

        // Check expiration
        Date expirationTime = claims.getExpirationTime();
        if (expirationTime != null && expirationTime.before(new Date())) {
            throw new Exception("Token has expired");
        }

        // Check not before time
        Date notBeforeTime = claims.getNotBeforeTime();
        if (notBeforeTime != null && notBeforeTime.after(new Date())) {
            throw new Exception("Token not yet valid");
        }
    }

    public String buildLogoutUrl(String idToken) {
        StringBuilder sb = new StringBuilder(config.getLogoutUrl());
        sb.append("?id_token_hint=").append(urlEncode(idToken));
        sb.append("&post_logout_redirect_uri=").append(urlEncode(config.getPostLogoutRedirectUri()));
        return sb.toString();
    }
}
