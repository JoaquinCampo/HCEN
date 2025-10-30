package grupo12.practico.models;

import grupo12.practico.dtos.HcenAdmin.HcenAdminDTO;
import jakarta.persistence.*;

@Entity
@Table(name = "hcen_admins")
public class HcenAdmin extends User {

    public HcenAdmin() {
        super();
    }

    public HcenAdminDTO toDto() {
        HcenAdminDTO dto = new HcenAdminDTO();
        dto.setId(getId());
        dto.setCi(getCi());
        dto.setFirstName(getFirstName());
        dto.setLastName(getLastName());
        dto.setGender(getGender());
        dto.setEmail(getEmail());
        dto.setPhone(getPhone());
        dto.setAddress(getAddress());
        dto.setDateOfBirth(getDateOfBirth());
        dto.setCreatedAt(getCreatedAt());
        dto.setUpdatedAt(getUpdatedAt());
        return dto;
    }
}
