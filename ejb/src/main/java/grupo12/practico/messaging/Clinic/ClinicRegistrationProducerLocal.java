package grupo12.practico.messaging.Clinic;

import jakarta.ejb.Local;

import grupo12.practico.dtos.Clinic.AddClinicDTO;

@Local
public interface ClinicRegistrationProducerLocal {
    void enqueue(AddClinicDTO dto);
}
