package grupo12.practico.dtos.ClinicAdmin;

import java.util.Set;

import grupo12.practico.dtos.User.UserDTO;

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
