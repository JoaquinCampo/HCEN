package grupo12.practico.service.document;

import grupo12.practico.model.ClinicalDocument;
import jakarta.ejb.Local;

import java.util.List;

@Local
public interface ClinicalDocumentServiceLocal {
    ClinicalDocument addClinicalDocument(ClinicalDocument doc);
    List<ClinicalDocument> getAllDocuments();
    List<ClinicalDocument> getDocumentsByPatient(String userId);
    List<ClinicalDocument> getDocumentsByAuthor(String healthWorkerId);
    List<ClinicalDocument> getDocumentsByProvider(String providerId);
}
