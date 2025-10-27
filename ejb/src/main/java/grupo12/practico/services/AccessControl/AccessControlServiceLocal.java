package grupo12.practico.services.AccessControl;

import grupo12.practico.dtos.AccessControl.AccessCheckRequestDTO;
import grupo12.practico.dtos.AccessControl.AccessDecisionDTO;
import jakarta.ejb.Local;

@Local
public interface AccessControlServiceLocal {
    AccessDecisionDTO checkAccess(AccessCheckRequestDTO request);
}
