package grupo12.practico.dtos.HealthUser;

import java.util.Set;

import grupo12.practico.dtos.User.UserDTO;

public class HealthUserDTO extends UserDTO {
    private static final long serialVersionUID = 1L;

    private Set<String> clinicIds;
    private Set<String> clinicalDocumentIds;

    public Set<String> getClinicIds() {
        return clinicIds;
    }

    public void setClinicIds(Set<String> clinicIds) {
        this.clinicIds = clinicIds;
    }

    public Set<String> getClinicalDocumentIds() {
        return clinicalDocumentIds;
    }

    public void setClinicalDocumentIds(Set<String> clinicalDocumentIds) {
        this.clinicalDocumentIds = clinicalDocumentIds;
    }
}
