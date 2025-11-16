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

    HcenAdminDTO createHcenAdmin(AddHcenAdminDTO addHcenAdminDTO);

    List<HcenAdminDTO> findAllHcenAdmins();

    HcenAdminDTO findHcenAdminByCi(String ci);
}