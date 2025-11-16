package grupo12.practico.messaging.AccessPolicy.HealthWorker;

import jakarta.ejb.Local;

import grupo12.practico.dtos.AccessPolicy.AddHealthWorkerAccessPolicyDTO;

@Local
public interface HealthWorkerAccessPolicyProducerLocal {
    void enqueue(AddHealthWorkerAccessPolicyDTO dto);
}

