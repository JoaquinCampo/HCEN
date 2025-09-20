package grupo12.practico.dto;

import java.util.Set;

public class HealthUserDTO extends UserDTO {
    private static final long serialVersionUID = 1L;

    private String clinicalHistoryId;
    private Set<String> clinicIds;

    public String getClinicalHistoryId() {
        return clinicalHistoryId;
    }

    public void setClinicalHistoryId(String clinicalHistoryId) {
        this.clinicalHistoryId = clinicalHistoryId;
    }

    public Set<String> getClinicIds() {
        return clinicIds;
    }

    public void setClinicIds(Set<String> clinicIds) {
        this.clinicIds = clinicIds;
    }
}
