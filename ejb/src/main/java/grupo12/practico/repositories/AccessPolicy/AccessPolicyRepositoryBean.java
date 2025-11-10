package grupo12.practico.repositories.AccessPolicy;

import java.util.List;

import grupo12.practico.models.ClinicAccessPolicy;
import grupo12.practico.models.HealthWorkerAccessPolicy;
import jakarta.ejb.Local;
import jakarta.ejb.Remote;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Stateless
@Local(AccessPolicyRepositoryLocal.class)
@Remote(AccessPolicyRepositoryRemote.class)
public class AccessPolicyRepositoryBean implements AccessPolicyRepositoryRemote {

    @PersistenceContext(unitName = "practicoPersistenceUnit")
    private EntityManager em;

    @Override
    public ClinicAccessPolicy createClinicAccessPolicy(ClinicAccessPolicy clinicAccessPolicy) {
        em.persist(clinicAccessPolicy);
        return clinicAccessPolicy;
    }

    @Override
    public HealthWorkerAccessPolicy createHealthWorkerAccessPolicy(HealthWorkerAccessPolicy healthWorkerAccessPolicy) {
        em.persist(healthWorkerAccessPolicy);
        return healthWorkerAccessPolicy;
    }

    @Override
    public List<ClinicAccessPolicy> findAllClinicAccessPolicies(String healthUserId) {
        return em.createQuery("SELECT c FROM ClinicAccessPolicy c WHERE c.healthUser.id = :healthUserId", ClinicAccessPolicy.class)
                .setParameter("healthUserId", healthUserId)
                .getResultList();
    }

    @Override
    public List<HealthWorkerAccessPolicy> findAllHealthWorkerAccessPolicies(String healthUserId) {
        return em.createQuery("SELECT h FROM HealthWorkerAccessPolicy h WHERE h.healthUser.id = :healthUserId", HealthWorkerAccessPolicy.class)
                .setParameter("healthUserId", healthUserId)
                .getResultList();
    }

    @Override
    public void deleteClinicAccessPolicy(String clinicAccessPolicyId) {
        ClinicAccessPolicy clinicAccessPolicy = em.find(ClinicAccessPolicy.class, clinicAccessPolicyId);
        if (clinicAccessPolicy != null) {
            em.remove(clinicAccessPolicy);
        }
    }

    @Override
    public void deleteHealthWorkerAccessPolicy(String healthWorkerAccessPolicyId) {
        HealthWorkerAccessPolicy healthWorkerAccessPolicy = em.find(HealthWorkerAccessPolicy.class, healthWorkerAccessPolicyId);
        if (healthWorkerAccessPolicy != null) {
            em.remove(healthWorkerAccessPolicy);
        }
    }

    @Override
    public boolean hasClinicAccess(String healthUserId, String clinicName) {
        Long count = em.createQuery(
                "SELECT COUNT(c) FROM ClinicAccessPolicy c WHERE c.healthUser.id = :healthUserId AND c.clinicName = :clinicName",
                Long.class)
                .setParameter("healthUserId", healthUserId)
                .setParameter("clinicName", clinicName)
                .getSingleResult();
        return count > 0;
    }

    @Override
    public boolean hasHealthWorkerAccess(String healthUserId, String healthWorkerCi) {
        Long count = em.createQuery(
                "SELECT COUNT(h) FROM HealthWorkerAccessPolicy h WHERE h.healthUser.id = :healthUserId AND h.healthWorkerCi = :healthWorkerCi",
                Long.class)
                .setParameter("healthUserId", healthUserId)
                .setParameter("healthWorkerCi", healthWorkerCi)
                .getSingleResult();
        return count > 0;
    }
}
