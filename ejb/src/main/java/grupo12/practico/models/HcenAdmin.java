package grupo12.practico.models;

import grupo12.practico.dtos.HcenAdmin.HcenAdminDTO;

public class HcenAdmin extends User {

    public HcenAdmin() {
        super();
    }

    public HcenAdminDTO toDto() {
        HcenAdminDTO dto = new HcenAdminDTO();
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
