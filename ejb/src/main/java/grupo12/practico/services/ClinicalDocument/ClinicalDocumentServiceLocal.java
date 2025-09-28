package grupo12.practico.services.ClinicalDocument;

import jakarta.ejb.Local;

import java.util.List;

import grupo12.practico.dtos.ClinicalDocument.ClinicalDocumentDTO;
import grupo12.practico.dtos.ClinicalDocument.AddClinicalDocumentDTO;

@Local
public interface ClinicalDocumentServiceLocal {
    ClinicalDocumentDTO addClinicalDocument(AddClinicalDocumentDTO addClinicalDocumentDTO);

    List<ClinicalDocumentDTO> getAllDocuments();

    List<ClinicalDocumentDTO> getDocumentsByPatient(String userId);
}
