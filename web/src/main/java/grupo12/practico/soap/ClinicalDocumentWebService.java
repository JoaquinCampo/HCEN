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
    private grupo12.practico.soap.ClinicalDocumentSoapService clinicalDocumentSoapService;

    @WebMethod(operationName = "getAllClinicalDocuments")
    public List<ClinicalDocumentDTO> getAllClinicalDocuments() {
        return clinicalDocumentSoapService.getAllClinicalDocuments();
    }

    @WebMethod(operationName = "getClinicalDocumentById")
    public ClinicalDocumentDTO getClinicalDocumentById(@WebParam(name = "id") String id) {
        return clinicalDocumentSoapService.getClinicalDocumentById(id);
    }

    @WebMethod(operationName = "searchClinicalDocumentsByTitle")
    public List<ClinicalDocumentDTO> searchClinicalDocumentsByTitle(@WebParam(name = "title") String title) {
        return clinicalDocumentSoapService.searchClinicalDocumentsByTitle(title);
    }

    @WebMethod(operationName = "createClinicalDocument")
    public ClinicalDocumentDTO createClinicalDocument(
            @WebParam(name = "clinicalDocumentData") AddClinicalDocumentDTO clinicalDocumentData) {
        return clinicalDocumentSoapService.createClinicalDocument(clinicalDocumentData);
    }
}
