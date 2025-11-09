package grupo12.practico.services.HcenAdmin;

import grupo12.practico.dtos.HcenAdmin.AddHcenAdminDTO;
import grupo12.practico.dtos.HcenAdmin.HcenAdminDTO;
import jakarta.ejb.Local;
import java.util.List;

/**
 * Local interface for HcenAdmin services
 */
@Local
public interface HcenAdminServiceLocal {

    HcenAdminDTO create(AddHcenAdminDTO addHcenAdminDTO);

    List<HcenAdminDTO> findAll();

    /**
     * Find HcenAdmin by CI (document)
     * 
     * @param ci The CI to search for
     * @return HcenAdminDTO if found, null otherwise
     */
    HcenAdminDTO findByCi(String ci);

    /**
     * Check if a user with the given CI is an HcenAdmin
     * 
     * @param ci The CI to check
     * @return true if the user is an HcenAdmin, false otherwise
     */
    boolean isHcenAdmin(String ci);
}