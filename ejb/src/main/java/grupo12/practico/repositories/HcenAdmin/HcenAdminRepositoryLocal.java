package grupo12.practico.repositories.HcenAdmin;

import jakarta.ejb.Local;
import grupo12.practico.models.HcenAdmin;
import java.util.List;

@Local
public interface HcenAdminRepositoryLocal {
    HcenAdmin create(HcenAdmin hcenAdmin);

    HcenAdmin findByCi(String ci);

    HcenAdmin findById(String id);

    List<HcenAdmin> findAll();
}