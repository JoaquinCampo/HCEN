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
    private grupo12.practico.soap.ClinicSoapService clinicSoapService;

    @WebMethod(operationName = "getAllClinics")
    public List<ClinicDTO> getAllClinics() {
        return clinicSoapService.getAllClinics();
    }

    @WebMethod(operationName = "getClinicById")
    public ClinicDTO getClinicById(@WebParam(name = "id") String id) {
        return clinicSoapService.getClinicById(id);
    }

    @WebMethod(operationName = "searchClinicsByName")
    public List<ClinicDTO> searchClinicsByName(@WebParam(name = "name") String name) {
        return clinicSoapService.searchClinicsByName(name);
    }

    @WebMethod(operationName = "createClinic")
    public ClinicDTO createClinic(@WebParam(name = "clinicData") AddClinicDTO clinicData) {
        return clinicSoapService.createClinic(clinicData);
    }
}
