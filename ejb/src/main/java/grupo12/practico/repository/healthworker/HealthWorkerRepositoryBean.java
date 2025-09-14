package grupo12.practico.repository.healthworker;

import grupo12.practico.model.HealthWorker;
import jakarta.ejb.Local;
import jakarta.ejb.Remote;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;

import java.util.ArrayList;
import java.util.List;

@Singleton
@Startup
@Local(HealthWorkerRepositoryLocal.class)
@Remote(HealthWorkerRepositoryRemote.class)
public class HealthWorkerRepositoryBean implements HealthWorkerRepositoryRemote {

    private final List<HealthWorker> healthWorkers = new ArrayList<>();

    @Override
    public HealthWorker add(HealthWorker healthWorker) {
        healthWorkers.add(healthWorker);
        return healthWorker;
    }

    @Override
    public List<HealthWorker> findAll() {
        return new ArrayList<>(healthWorkers);
    }
}


