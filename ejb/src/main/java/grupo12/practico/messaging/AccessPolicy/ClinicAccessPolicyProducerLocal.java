package grupo12.practico.messaging.AccessPolicy;

import jakarta.ejb.Local;

import grupo12.practico.dtos.AccessPolicy.AddClinicAccessPolicyDTO;

@Local
public interface ClinicAccessPolicyProducerLocal {
    void enqueue(AddClinicAccessPolicyDTO dto);
}

