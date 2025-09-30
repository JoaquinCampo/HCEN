package grupo12.practico.dtos.HealthWorker;

import grupo12.practico.dtos.User.AddUserDTO;

import java.util.Set;

public class AddHealthWorkerDTO extends AddUserDTO {
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
