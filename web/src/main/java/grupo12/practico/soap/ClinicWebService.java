package grupo12.practico.soap;

import grupo12.practico.dtos.Clinic.AddClinicDTO;
import grupo12.practico.dtos.Clinic.ClinicDTO;
import jakarta.ejb.EJB;
import jakarta.jws.WebService;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;

import java.util.List;

@WebService(serviceName = "ClinicService", portName = "ClinicPort", targetNamespace = "http://soap.practico.grupo12/")
public class ClinicWebService {

    @EJB
    private grupo12.practico.services.Clinic.ClinicServiceLocal clinicService;

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
    public ClinicDTO add(@WebParam(name = "clinicData") AddClinicDTO clinicData) {
        return clinicService.addClinic(clinicData);
    }
}
