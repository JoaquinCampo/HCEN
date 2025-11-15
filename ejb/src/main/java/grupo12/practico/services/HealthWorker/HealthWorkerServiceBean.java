package grupo12.practico.services.HealthWorker;

import java.util.Collections;
import java.util.List;

import grupo12.practico.dtos.HealthWorker.HealthWorkerDTO;
import grupo12.practico.repositories.HealthWorker.HealthWorkerRepositoryLocal;
import jakarta.ejb.EJB;
import jakarta.ejb.Local;
import jakarta.ejb.Remote;
import jakarta.ejb.Stateless;

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
}
