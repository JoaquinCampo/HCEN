package grupo12.practico.soap;

import grupo12.practico.dtos.ClinicalDocument.AddClinicalDocumentDTO;
import grupo12.practico.dtos.ClinicalDocument.ClinicalDocumentDTO;
import grupo12.practico.services.ClinicalDocument.ClinicalDocumentServiceLocal;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import java.util.List;

@Stateless
public class ClinicalDocumentSoapService {

    @EJB
    private ClinicalDocumentServiceLocal clinicalDocumentService;

    public List<ClinicalDocumentDTO> getAllClinicalDocuments() {
        return clinicalDocumentService.findAll();
    }

    public ClinicalDocumentDTO getClinicalDocumentById(String id) {
        return clinicalDocumentService.findById(id);
    }

    public List<ClinicalDocumentDTO> searchClinicalDocumentsByTitle(String title) {
        return clinicalDocumentService.findByTitle(title);
    }

    public ClinicalDocumentDTO createClinicalDocument(AddClinicalDocumentDTO clinicalDocumentData) {
        return clinicalDocumentService.add(clinicalDocumentData);
    }
}