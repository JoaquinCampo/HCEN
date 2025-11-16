package grupo12.practico.messaging.AccessPolicy.Clinic;

import jakarta.ejb.Local;

import grupo12.practico.dtos.AccessPolicy.AddClinicAccessPolicyDTO;

@Local
public interface ClinicAccessPolicyProducerLocal {
    void enqueue(AddClinicAccessPolicyDTO dto);
}

