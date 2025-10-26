package grupo12.practico.services.Authorization;

import grupo12.practico.dtos.Authorization.AuthorizationCheckRequestDTO;
import grupo12.practico.dtos.Authorization.AuthorizationDecisionDTO;
import jakarta.ejb.Remote;

@Remote
public interface AuthorizationServiceRemote extends AuthorizationServiceLocal {
}
