package grupo12.practico.dto;

import java.util.Set;

public class ClinicAdminDTO extends UserDTO {
    private static final long serialVersionUID = 1L;

    private Set<String> clinicIds;

    public Set<String> getClinicIds() {
        return clinicIds;
    }

    public void setClinicIds(Set<String> clinicIds) {
        this.clinicIds = clinicIds;
    }
}
