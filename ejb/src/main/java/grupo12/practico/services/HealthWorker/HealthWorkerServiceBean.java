package grupo12.practico.services.HealthWorker;

import grupo12.practico.dtos.HealthWorker.HealthWorkerDTO;
import grupo12.practico.repositories.HealthWorker.HealthWorkerRepositoryLocal;
import jakarta.ejb.EJB;
import jakarta.ejb.Local;
import jakarta.ejb.Remote;
import jakarta.ejb.Stateless;
import jakarta.validation.ValidationException;
import java.util.List;

@Stateless
@Local(HealthWorkerServiceLocal.class)
@Remote(HealthWorkerServiceRemote.class)
public class HealthWorkerServiceBean implements HealthWorkerServiceRemote {

    @EJB
    private HealthWorkerRepositoryLocal healthWorkerRepository;

    @Override
    public HealthWorkerDTO findByClinicAndCi(String clinicName, String healthWorkerCi) {
        return healthWorkerRepository.findByClinicAndCi(clinicName, healthWorkerCi);
    }

    @Override
    public List<HealthWorkerDTO> findByClinic(String clinicName) {
        if (clinicName == null || clinicName.isBlank()) {
            throw new ValidationException("Clinic name is required");
        }

        return healthWorkerRepository.findByClinic(clinicName);
    }
}
