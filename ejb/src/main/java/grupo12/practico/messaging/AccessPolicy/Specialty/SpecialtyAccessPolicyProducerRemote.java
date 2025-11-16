package grupo12.practico.messaging.AccessPolicy.Specialty;

import jakarta.ejb.Remote;

import grupo12.practico.dtos.AccessPolicy.AddSpecialtyAccessPolicyDTO;

@Remote
public interface SpecialtyAccessPolicyProducerRemote {
    void enqueue(AddSpecialtyAccessPolicyDTO dto);
}

