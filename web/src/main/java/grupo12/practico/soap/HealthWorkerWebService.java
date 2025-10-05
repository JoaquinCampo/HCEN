package grupo12.practico.soap;

import grupo12.practico.dtos.HealthWorker.AddHealthWorkerDTO;
import grupo12.practico.dtos.HealthWorker.HealthWorkerDTO;
import jakarta.ejb.EJB;
import jakarta.jws.WebService;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;

import java.util.List;

@WebService(serviceName = "HealthWorkerService", portName = "HealthWorkerPort", targetNamespace = "http://soap.practico.grupo12/")
public class HealthWorkerWebService {

    @EJB
    private grupo12.practico.services.HealthWorker.HealthWorkerServiceLocal healthWorkerService;

    @WebMethod(operationName = "findAll")
    public List<HealthWorkerDTO> findAll() {
        return healthWorkerService.getAllHealthWorkers();
    }

    @WebMethod(operationName = "findById")
    public HealthWorkerDTO findById(@WebParam(name = "id") String id) {
        return healthWorkerService.findById(id);
    }

    @WebMethod(operationName = "findByName")
    public List<HealthWorkerDTO> findByName(@WebParam(name = "name") String name) {
        return healthWorkerService.findHealthWorkersByName(name);
    }

    @WebMethod(operationName = "add")
    public HealthWorkerDTO add(
            @WebParam(name = "healthWorkerData") AddHealthWorkerDTO healthWorkerData) {
        return healthWorkerService.addHealthWorker(healthWorkerData);
    }
}