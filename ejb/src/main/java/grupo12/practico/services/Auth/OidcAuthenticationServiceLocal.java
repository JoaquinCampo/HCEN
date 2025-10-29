package grupo12.practico.services.Auth;

import grupo12.practico.dtos.Auth.OidcAuthorizationResponseDTO;
import grupo12.practico.dtos.Auth.OidcAuthResultDTO;
import jakarta.ejb.Local;

/**
 * Local interface for OIDC authentication service
 */
@Local
public interface OidcAuthenticationServiceLocal {

    /**
     * Initiates the OIDC authorization flow
     * 
     * @return Authorization response with URL and state
     */
    OidcAuthorizationResponseDTO initiateAuthorization();

    /**
     * Handles the OIDC callback
     * 
     * @param code  Authorization code
     * @param state State parameter
     * @return Authentication result
     * @throws Exception if authentication fails
     */
    OidcAuthResultDTO handleCallback(String code, String state) throws Exception;

    /**
     * Builds the provider logout URL given an id_token.
     */
    String buildLogoutUrl(String idToken);
}
