package grupo12.practico.dtos.HealthUser;

import grupo12.practico.dtos.User.UserDTO;

import java.util.Set;

public class AddHealthUserDTO extends UserDTO {
    private static final long serialVersionUID = 1L;

    private Set<String> clinicNames;

    public Set<String> getClinicNames() {
        return clinicNames;
    }

    public void setClinicNames(Set<String> clinicNames) {
        this.clinicNames = clinicNames;
    }
}
