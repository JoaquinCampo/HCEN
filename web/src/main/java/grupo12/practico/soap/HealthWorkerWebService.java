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
    private grupo12.practico.soap.HealthWorkerSoapService healthWorkerSoapService;

    @WebMethod(operationName = "getAllHealthWorkers")
    public List<HealthWorkerDTO> getAllHealthWorkers() {
        return healthWorkerSoapService.getAllHealthWorkers();
    }

    @WebMethod(operationName = "getHealthWorkerById")
    public HealthWorkerDTO getHealthWorkerById(@WebParam(name = "id") String id) {
        return healthWorkerSoapService.getHealthWorkerById(id);
    }

    @WebMethod(operationName = "searchHealthWorkersByName")
    public List<HealthWorkerDTO> searchHealthWorkersByName(@WebParam(name = "name") String name) {
        return healthWorkerSoapService.searchHealthWorkersByName(name);
    }

    @WebMethod(operationName = "createHealthWorker")
    public HealthWorkerDTO createHealthWorker(
            @WebParam(name = "healthWorkerData") AddHealthWorkerDTO healthWorkerData) {
        return healthWorkerSoapService.createHealthWorker(healthWorkerData);
    }
}