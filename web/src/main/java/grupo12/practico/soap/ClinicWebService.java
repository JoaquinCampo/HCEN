package grupo12.practico.soap;

import grupo12.practico.dtos.Clinic.AddClinicDTO;
import grupo12.practico.dtos.Clinic.ClinicDTO;
import grupo12.practico.messaging.Clinic.ClinicRegistrationProducerLocal;
import jakarta.ejb.EJB;
import jakarta.jws.WebService;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;

import java.util.List;

@WebService(serviceName = "ClinicService", portName = "ClinicPort", targetNamespace = "http://soap.practico.grupo12/")
public class ClinicWebService {

    @EJB
    private grupo12.practico.services.Clinic.ClinicServiceLocal clinicService;

    @EJB
    private ClinicRegistrationProducerLocal registrationProducer;

    @WebMethod(operationName = "findAll")
    public List<ClinicDTO> findAll() {
        return clinicService.findAll();
    }

    @WebMethod(operationName = "findById")
    public ClinicDTO findById(@WebParam(name = "id") String id) {
        return clinicService.findById(id);
    }

    @WebMethod(operationName = "findByName")
    public List<ClinicDTO> findByName(@WebParam(name = "name") String name) {
        return clinicService.findByName(name);
    }

    @WebMethod(operationName = "add")
    public String add(@WebParam(name = "clinicData") AddClinicDTO clinicData) {
        try {
            registrationProducer.enqueue(clinicData);
            return "Clinic registration request accepted; the clinic will be created shortly";
        } catch (Exception ex) {
            throw new RuntimeException("Failed to enqueue clinic registration request: " + ex.getMessage(), ex);
        }
    }
}
