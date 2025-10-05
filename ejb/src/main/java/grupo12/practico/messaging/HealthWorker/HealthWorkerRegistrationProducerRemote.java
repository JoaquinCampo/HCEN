package grupo12.practico.messaging.HealthWorker;

import jakarta.ejb.Remote;

import grupo12.practico.dtos.HealthWorker.AddHealthWorkerDTO;

@Remote
public interface HealthWorkerRegistrationProducerRemote {
    void enqueue(AddHealthWorkerDTO dto);
}
