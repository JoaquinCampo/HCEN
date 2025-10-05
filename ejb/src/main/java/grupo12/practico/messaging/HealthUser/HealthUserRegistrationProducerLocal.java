package grupo12.practico.messaging.HealthUser;

import jakarta.ejb.Local;

import grupo12.practico.dtos.HealthUser.AddHealthUserDTO;

@Local
public interface HealthUserRegistrationProducerLocal {
    void enqueue(AddHealthUserDTO dto);
}
