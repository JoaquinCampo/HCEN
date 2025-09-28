package grupo12.practico.dtos.HealthUser;

import grupo12.practico.dtos.User.AddUserDTO;

import java.util.Set;

public class AddHealthUserDTO extends AddUserDTO {
    private static final long serialVersionUID = 1L;

    private Set<String> clinicIds;

    public Set<String> getClinicIds() {
        return clinicIds;
    }

    public void setClinicIds(Set<String> clinicIds) {
        this.clinicIds = clinicIds;
    }
}
