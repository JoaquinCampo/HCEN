package grupo12.practico.soap;

import grupo12.practico.dtos.HealthUser.AddHealthUserDTO;
import grupo12.practico.dtos.HealthUser.HealthUserDTO;
import jakarta.ejb.EJB;
import jakarta.jws.WebService;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;

import java.util.List;

@WebService(serviceName = "HealthUserService", portName = "HealthUserPort", targetNamespace = "http://soap.practico.grupo12/")
public class HealthUserWebService {

    @EJB
    private grupo12.practico.services.HealthUser.HealthUserServiceLocal healthUserService;

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
    public HealthUserDTO add(@WebParam(name = "healthUserData") AddHealthUserDTO healthUserData) {
        return healthUserService.add(healthUserData);
    }
}
