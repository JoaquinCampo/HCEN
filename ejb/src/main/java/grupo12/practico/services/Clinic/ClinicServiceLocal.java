package grupo12.practico.services.Clinic;

import jakarta.ejb.Local;

import grupo12.practico.dtos.Clinic.AddClinicDTO;
import grupo12.practico.dtos.Clinic.ClinicDTO;

@Local
public interface ClinicServiceLocal {
    ClinicDTO addClinic(AddClinicDTO addClinicDTO);

    String linkHealthUserToClinic(String clinicName, String healthUserDocument);

    ClinicDTO findExternalClinicByName(String clinicName);
}
