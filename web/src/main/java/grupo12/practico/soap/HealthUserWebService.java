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
    private grupo12.practico.soap.HealthUserSoapService healthUserSoapService;

    @WebMethod(operationName = "getAllHealthUsers")
    public List<HealthUserDTO> getAllHealthUsers() {
        return healthUserSoapService.getAllHealthUsers();
    }

    @WebMethod(operationName = "getHealthUserById")
    public HealthUserDTO getHealthUserById(@WebParam(name = "id") String id) {
        return healthUserSoapService.getHealthUserById(id);
    }

    @WebMethod(operationName = "searchHealthUsersByName")
    public List<HealthUserDTO> searchHealthUsersByName(@WebParam(name = "name") String name) {
        return healthUserSoapService.searchHealthUsersByName(name);
    }

    @WebMethod(operationName = "createHealthUser")
    public HealthUserDTO createHealthUser(@WebParam(name = "healthUserData") AddHealthUserDTO healthUserData) {
        return healthUserSoapService.createHealthUser(healthUserData);
    }
}
