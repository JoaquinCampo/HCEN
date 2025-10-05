package grupo12.practico.soap;

import grupo12.practico.dtos.HealthUser.AddHealthUserDTO;
import grupo12.practico.dtos.HealthUser.HealthUserDTO;
import grupo12.practico.messaging.HealthUser.HealthUserRegistrationProducerLocal;
import jakarta.ejb.EJB;
import jakarta.jws.WebService;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;

import java.util.List;

@WebService(serviceName = "HealthUserService", portName = "HealthUserPort", targetNamespace = "http://soap.practico.grupo12/")
public class HealthUserWebService {

    @EJB
    private grupo12.practico.services.HealthUser.HealthUserServiceLocal healthUserService;

    @EJB
    private HealthUserRegistrationProducerLocal healthUserRegistrationProducer;

    @WebMethod(operationName = "findAll")
    public List<HealthUserDTO> findAll() {
        return healthUserService.findAll();
    }

    @WebMethod(operationName = "findById")
    public HealthUserDTO findById(@WebParam(name = "id") String id) {
        return healthUserService.findById(id);
    }

    @WebMethod(operationName = "findByName")
    public List<HealthUserDTO> findByName(@WebParam(name = "name") String name) {
        return healthUserService.findByName(name);
    }

    @WebMethod(operationName = "add")
    public String add(@WebParam(name = "healthUserData") AddHealthUserDTO healthUserData) {
        healthUserRegistrationProducer.enqueue(healthUserData);
        return "Health user registration request queued successfully for document: " + healthUserData.getDocument();
    }
}
