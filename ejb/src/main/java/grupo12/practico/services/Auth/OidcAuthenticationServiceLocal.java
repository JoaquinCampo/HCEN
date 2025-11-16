package grupo12.practico.services.Auth;

import grupo12.practico.dtos.Auth.OidcAuthorizationResponseDTO;
import grupo12.practico.dtos.Auth.OidcAuthResultDTO;
import jakarta.ejb.Local;

@Local
public interface OidcAuthenticationServiceLocal {
    OidcAuthorizationResponseDTO initiateAuthorization();

    OidcAuthResultDTO handleCallback(String code, String state) throws Exception;

    String buildLogoutUrl(String idToken);
}
