package grupo12.practico.dtos.HealthUser;

import java.util.Set;

import grupo12.practico.dtos.User.UserDTO;

public class HealthUserDTO extends UserDTO {
    private static final long serialVersionUID = 1L;

    private Set<String> clinicNames;

    public Set<String> getClinicNames() {
        return clinicNames;
    }

    public void setClinicNames(Set<String> clinicNames) {
        this.clinicNames = clinicNames;
    }
}
