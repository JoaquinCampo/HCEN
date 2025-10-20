package grupo12.practico.dtos.HealthWorker;

import java.util.Set;

import grupo12.practico.dtos.User.UserDTO;

public class HealthWorkerDTO extends UserDTO {
    private static final long serialVersionUID = 1L;

    private String licenseNumber;
    private Set<String> clinicIds;
    private Set<String> specialtyIds;
    private String bloodType;

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

    public String getBloodType() {
        return bloodType;
    }

    public void setBloodType(String bloodType) {
        this.bloodType = bloodType;
    }
}
