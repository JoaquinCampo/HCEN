package grupo12.practico.services.Clinic;

import grupo12.practico.dtos.Clinic.ClinicAdminInfoDTO;
import grupo12.practico.models.Clinic;
import jakarta.ejb.Local;

@Local
public interface ClinicRegistrationNotifierLocal {
    /**
     * Sends the clinic registration payload to the external integration endpoint.
     *
     * @param clinic the persisted clinic information
     * @param admin  minimal clinic admin credentials required by the external system
     */
    void notifyClinicCreated(Clinic clinic, ClinicAdminInfoDTO admin);
}
