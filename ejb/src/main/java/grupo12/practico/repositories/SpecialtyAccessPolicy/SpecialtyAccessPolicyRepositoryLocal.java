package grupo12.practico.repositories.SpecialtyAccessPolicy;

import java.util.Optional;

import grupo12.practico.models.SpecialtyAccessPolicy;
import jakarta.ejb.Local;

@Local
public interface SpecialtyAccessPolicyRepositoryLocal {
    SpecialtyAccessPolicy add(SpecialtyAccessPolicy policy);

    Optional<SpecialtyAccessPolicy> findByHealthUserAndSpecialty(String healthUserId, String specialtyId);
}
