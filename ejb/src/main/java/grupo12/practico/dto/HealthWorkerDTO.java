package grupo12.practico.dto;

import java.util.Set;

public class HealthWorkerDTO extends UserDTO {
    private static final long serialVersionUID = 1L;

    private String licenseNumber;
    private Set<String> clinicIds;
    private Set<String> specialtyIds;

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public Set<String> getClinicIds() {
        return clinicIds;
    }

    public void setClinicIds(Set<String> clinicIds) {
        this.clinicIds = clinicIds;
    }

    public Set<String> getSpecialtyIds() {
        return specialtyIds;
    }

    public void setSpecialtyIds(Set<String> specialtyIds) {
        this.specialtyIds = specialtyIds;
    }
}
