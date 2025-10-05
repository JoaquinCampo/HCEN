package grupo12.practico.soap;

import grupo12.practico.dtos.ClinicalDocument.AddClinicalDocumentDTO;
import grupo12.practico.dtos.ClinicalDocument.ClinicalDocumentDTO;
import jakarta.ejb.EJB;
import jakarta.jws.WebService;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;

import java.util.List;

@WebService(serviceName = "ClinicalDocumentService", portName = "ClinicalDocumentPort", targetNamespace = "http://soap.practico.grupo12/")
public class ClinicalDocumentWebService {

    @EJB
    private grupo12.practico.services.ClinicalDocument.ClinicalDocumentServiceLocal clinicalDocumentService;

    @WebMethod(operationName = "findAll")
    public List<ClinicalDocumentDTO> findAll() {
        return clinicalDocumentService.findAll();
    }

    @WebMethod(operationName = "findById")
    public ClinicalDocumentDTO findById(@WebParam(name = "id") String id) {
        return clinicalDocumentService.findById(id);
    }

    @WebMethod(operationName = "findByTitle")
    public List<ClinicalDocumentDTO> findByTitle(@WebParam(name = "title") String title) {
        return clinicalDocumentService.findByTitle(title);
    }

    @WebMethod(operationName = "add")
    public ClinicalDocumentDTO add(
            @WebParam(name = "clinicalDocumentData") AddClinicalDocumentDTO clinicalDocumentData) {
        return clinicalDocumentService.add(clinicalDocumentData);
    }
}
