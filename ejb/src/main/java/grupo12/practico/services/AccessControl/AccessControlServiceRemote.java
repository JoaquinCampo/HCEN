package grupo12.practico.services.AccessControl;

import grupo12.practico.dtos.AccessControl.AccessCheckRequestDTO;
import grupo12.practico.dtos.AccessControl.AccessDecisionDTO;
import jakarta.ejb.Remote;

@Remote
public interface AccessControlServiceRemote extends AccessControlServiceLocal {
}
