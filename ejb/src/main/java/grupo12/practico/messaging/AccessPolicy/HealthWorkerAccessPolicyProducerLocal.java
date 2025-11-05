package grupo12.practico.messaging.AccessPolicy;

import jakarta.ejb.Local;

import grupo12.practico.dtos.AccessPolicy.AddHealthWorkerAccessPolicyDTO;

@Local
public interface HealthWorkerAccessPolicyProducerLocal {
    void enqueue(AddHealthWorkerAccessPolicyDTO dto);
}

