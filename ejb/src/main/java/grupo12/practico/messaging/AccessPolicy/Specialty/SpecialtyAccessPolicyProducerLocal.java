package grupo12.practico.messaging.AccessPolicy.Specialty;

import jakarta.ejb.Local;

import grupo12.practico.dtos.AccessPolicy.AddSpecialtyAccessPolicyDTO;

@Local
public interface SpecialtyAccessPolicyProducerLocal {
    void enqueue(AddSpecialtyAccessPolicyDTO dto);
}

