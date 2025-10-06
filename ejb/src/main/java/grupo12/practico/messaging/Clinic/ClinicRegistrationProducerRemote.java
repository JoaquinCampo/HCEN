package grupo12.practico.messaging.Clinic;

import jakarta.ejb.Remote;

import grupo12.practico.dtos.Clinic.AddClinicDTO;

@Remote
public interface ClinicRegistrationProducerRemote {
    void enqueue(AddClinicDTO dto);
}
