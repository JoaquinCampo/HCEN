package grupo12.practico.messaging.AccessPolicy;

import jakarta.ejb.Remote;

import grupo12.practico.dtos.AccessPolicy.AddHealthWorkerAccessPolicyDTO;

@Remote
public interface HealthWorkerAccessPolicyProducerRemote {
    void enqueue(AddHealthWorkerAccessPolicyDTO dto);
}

