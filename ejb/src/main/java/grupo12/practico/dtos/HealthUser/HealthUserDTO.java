package grupo12.practico.dtos.HealthUser;

import java.util.List;

import grupo12.practico.dtos.Clinic.ClinicDTO;
import grupo12.practico.dtos.User.UserDTO;

public class HealthUserDTO extends UserDTO {
    private static final long serialVersionUID = 1L;

    private List<ClinicDTO> clinics;

    public List<ClinicDTO> getClinics() {
        return clinics;
    }

    public void setClinics(List<ClinicDTO> clinics) {
        this.clinics = clinics;
    }
}
