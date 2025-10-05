package grupo12.practico.soap;

import grupo12.practico.dtos.HealthWorker.AddHealthWorkerDTO;
import grupo12.practico.dtos.HealthWorker.HealthWorkerDTO;
import grupo12.practico.messaging.HealthWorker.HealthWorkerRegistrationProducerLocal;
import jakarta.ejb.EJB;
import jakarta.jws.WebService;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;

import java.util.List;

@WebService(serviceName = "HealthWorkerService", portName = "HealthWorkerPort", targetNamespace = "http://soap.practico.grupo12/")
public class HealthWorkerWebService {

    @EJB
    private grupo12.practico.services.HealthWorker.HealthWorkerServiceLocal healthWorkerService;

    @EJB
    private HealthWorkerRegistrationProducerLocal healthWorkerRegistrationProducer;

    @WebMethod(operationName = "findAll")
    public List<HealthWorkerDTO> findAll() {
        return healthWorkerService.findAll();
    }

    @WebMethod(operationName = "findById")
    public HealthWorkerDTO findById(@WebParam(name = "id") String id) {
        return healthWorkerService.findById(id);
    }

    @WebMethod(operationName = "findByName")
    public List<HealthWorkerDTO> findByName(@WebParam(name = "name") String name) {
        return healthWorkerService.findByName(name);
    }

    @WebMethod(operationName = "add")
    public String add(
            @WebParam(name = "healthWorkerData") AddHealthWorkerDTO healthWorkerData) {
        healthWorkerRegistrationProducer.enqueue(healthWorkerData);
        return "Health worker registration request queued successfully for document: " + healthWorkerData.getDocument();
    }
}