package grupo12.practico.messaging.AccessPolicy.Clinic;

import jakarta.ejb.Remote;

import grupo12.practico.dtos.AccessPolicy.AddClinicAccessPolicyDTO;

@Remote
public interface ClinicAccessPolicyProducerRemote {
    void enqueue(AddClinicAccessPolicyDTO dto);
}

