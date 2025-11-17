package grupo12.practico.services.HcenAdmin;

import grupo12.practico.dtos.HcenAdmin.AddHcenAdminDTO;
import grupo12.practico.dtos.HcenAdmin.HcenAdminDTO;
import grupo12.practico.models.HcenAdmin;
import grupo12.practico.repositories.HcenAdmin.HcenAdminRepositoryLocal;
import jakarta.ejb.EJB;
import jakarta.ejb.Local;
import jakarta.ejb.Remote;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.validation.ValidationException;
import java.util.List;


@Stateless
@Local(HcenAdminServiceLocal.class)
@Remote(HcenAdminServiceRemote.class)
public class HcenAdminServiceBean implements HcenAdminServiceRemote {

    @PersistenceContext(unitName = "practicoPersistenceUnit")
    private EntityManager em;

    @EJB
    private HcenAdminRepositoryLocal hcenAdminRepository;

    @Override
    public HcenAdminDTO createHcenAdmin(AddHcenAdminDTO addHcenAdminDTO) {
        validateAddHcenAdminDTO(addHcenAdminDTO);

        HcenAdmin hcenAdmin = new HcenAdmin();
        hcenAdmin.setCi(addHcenAdminDTO.getCi());
        hcenAdmin.setFirstName(addHcenAdminDTO.getFirstName());
        hcenAdmin.setLastName(addHcenAdminDTO.getLastName());
        hcenAdmin.setGender(addHcenAdminDTO.getGender());
        hcenAdmin.setEmail(addHcenAdminDTO.getEmail());
        hcenAdmin.setPhone(addHcenAdminDTO.getPhone());
        hcenAdmin.setAddress(addHcenAdminDTO.getAddress());
        hcenAdmin.setDateOfBirth(addHcenAdminDTO.getDateOfBirth());

        HcenAdmin createdAdmin = hcenAdminRepository.createHcenAdmin(hcenAdmin);
        return createdAdmin.toDto();
    }

    @Override
    public List<HcenAdminDTO> findAllHcenAdmins() {
        List<HcenAdmin> admins = hcenAdminRepository.findAllHcenAdmins();
        return admins.stream()
                .map(HcenAdmin::toDto)
                .toList();
    }

    @Override
    public HcenAdminDTO findHcenAdminByCi(String ci) {
        HcenAdmin admin = hcenAdminRepository.findHcenAdminByCi(ci);
        return admin != null ? admin.toDto() : null;
    }

    private void validateAddHcenAdminDTO(AddHcenAdminDTO addHcenAdminDTO) {
        if (addHcenAdminDTO == null) {
            throw new ValidationException("Hcen Admin data must not be null");
        }
        if (addHcenAdminDTO.getFirstName() == null || addHcenAdminDTO.getFirstName().isBlank() || addHcenAdminDTO.getLastName() == null || addHcenAdminDTO.getLastName().isBlank()) {
            throw new ValidationException("Hcen Admin first name and last name are required");
        }
        if (addHcenAdminDTO.getCi() == null || addHcenAdminDTO.getCi().isBlank()) {
            throw new ValidationException("Hcen Admin document is required");
        }
    }
}