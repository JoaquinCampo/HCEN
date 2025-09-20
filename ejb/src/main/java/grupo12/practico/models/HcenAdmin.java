package grupo12.practico.models;

public class HcenAdmin extends User {

    public HcenAdmin() {
        super();
    }

    public grupo12.practico.dto.HcenAdminDTO toDto() {
        grupo12.practico.dto.HcenAdminDTO dto = new grupo12.practico.dto.HcenAdminDTO();
        dto.setId(getId());
        dto.setDocument(getDocument());
        dto.setDocumentType(getDocumentType());
        dto.setFirstName(getFirstName());
        dto.setLastName(getLastName());
        dto.setGender(getGender());
        dto.setEmail(getEmail());
        dto.setPhone(getPhone());
        dto.setImageUrl(getImageUrl());
        dto.setAddress(getAddress());
        dto.setCreatedAt(getCreatedAt());
        dto.setUpdatedAt(getUpdatedAt());
        return dto;
    }
}
