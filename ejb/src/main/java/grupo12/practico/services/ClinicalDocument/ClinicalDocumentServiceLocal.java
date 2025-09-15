package grupo12.practico.services.ClinicalDocument;

import jakarta.ejb.Local;

import java.util.List;

import grupo12.practico.models.ClinicalDocument;

@Local
public interface ClinicalDocumentServiceLocal {
    ClinicalDocument addClinicalDocument(ClinicalDocument doc);

    List<ClinicalDocument> getAllDocuments();

    List<ClinicalDocument> getDocumentsByPatient(String userId);

    List<ClinicalDocument> getDocumentsByAuthor(String healthWorkerId);

    List<ClinicalDocument> getDocumentsByProvider(String providerId);

    // New: name-based searches
    List<ClinicalDocument> searchByPatientName(String query);

    List<ClinicalDocument> searchByAuthorName(String query);

    List<ClinicalDocument> searchByProviderName(String query);

    List<ClinicalDocument> searchByAnyName(String query);
}
