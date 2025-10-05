package grupo12.practico.messaging.HealthWorker;

import jakarta.ejb.Local;

import grupo12.practico.dtos.HealthWorker.AddHealthWorkerDTO;

@Local
public interface HealthWorkerRegistrationProducerLocal {
    void enqueue(AddHealthWorkerDTO dto);
}
