package grupo12.practico.services.HcenAdmin;

import grupo12.practico.dtos.HcenAdmin.AddHcenAdminDTO;
import grupo12.practico.dtos.HcenAdmin.HcenAdminDTO;
import grupo12.practico.models.HcenAdmin;
import grupo12.practico.repositories.HcenAdmin.HcenAdminRepositoryLocal;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.validation.ValidationException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for HcenAdmin operations
 */
@Stateless
public class HcenAdminServiceBean implements HcenAdminServiceLocal {

    @PersistenceContext(unitName = "practicoPersistenceUnit")
    private EntityManager em;

    @EJB
    private HcenAdminRepositoryLocal hcenAdminRepository;

    @Override
    public HcenAdminDTO create(AddHcenAdminDTO addHcenAdminDTO) {
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

        HcenAdmin createdAdmin = hcenAdminRepository.create(hcenAdmin);
        return createdAdmin.toDto();
    }

    @Override
    public List<HcenAdminDTO> findAll() {
        List<HcenAdmin> admins = hcenAdminRepository.findAll();
        return admins.stream()
                .map(HcenAdmin::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public HcenAdminDTO findByCi(String ci) {
        HcenAdmin admin = hcenAdminRepository.findByCi(ci);
        return admin != null ? admin.toDto() : null;
    }

    @Override
    public boolean isHcenAdmin(String ci) {
        return hcenAdminRepository.findByCi(ci) != null;
    }

    private void validateAddHcenAdminDTO(AddHcenAdminDTO addHcenAdminDTO) {
        if (addHcenAdminDTO == null) {
            throw new ValidationException("Hcen Admin data must not be null");
        }
        if (isBlank(addHcenAdminDTO.getFirstName()) || isBlank(addHcenAdminDTO.getLastName())) {
            throw new ValidationException("Hcen Admin first name and last name are required");
        }
        if (isBlank(addHcenAdminDTO.getCi())) {
            throw new ValidationException("Hcen Admin document is required");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}