package grupo12.practico.services.HcenAdmin;

import grupo12.practico.models.HcenAdmin;
import jakarta.ejb.Local;

/**
 * Local interface for HcenAdmin services
 */
@Local
public interface HcenAdminServiceLocal {

    /**
     * Find HcenAdmin by CI (document)
     * 
     * @param ci The CI to search for
     * @return HcenAdmin if found, null otherwise
     */
    HcenAdmin findByCi(String ci);

    /**
     * Check if a user with the given CI is an HcenAdmin
     * 
     * @param ci The CI to check
     * @return true if the user is an HcenAdmin, false otherwise
     */
    boolean isHcenAdmin(String ci);
}