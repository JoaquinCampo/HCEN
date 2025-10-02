package grupo12.practico.messaging;

import jakarta.ejb.Remote;

import grupo12.practico.dtos.HealthUser.AddHealthUserDTO;

@Remote
public interface HealthUserRegistrationProducerRemote {
    void enqueue(AddHealthUserDTO dto);
}
