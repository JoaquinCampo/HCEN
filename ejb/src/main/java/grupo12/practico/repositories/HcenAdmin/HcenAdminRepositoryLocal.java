package grupo12.practico.repositories.HcenAdmin;

import jakarta.ejb.Local;
import grupo12.practico.models.HcenAdmin;
import java.util.List;

@Local
public interface HcenAdminRepositoryLocal {
    HcenAdmin createHcenAdmin(HcenAdmin hcenAdmin);

    HcenAdmin findHcenAdminByCi(String ci);

    HcenAdmin findHcenAdminById(String id);

    List<HcenAdmin> findAllHcenAdmins();
}