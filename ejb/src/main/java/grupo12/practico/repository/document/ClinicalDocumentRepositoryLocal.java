package grupo12.practico.repository.document;

import grupo12.practico.model.ClinicalDocument;
import jakarta.ejb.Local;

import java.util.List;

@Local
public interface ClinicalDocumentRepositoryLocal {
    ClinicalDocument add(ClinicalDocument doc);
    List<ClinicalDocument> findAll();
    List<ClinicalDocument> findByPatientId(String userId);
    List<ClinicalDocument> findByAuthorId(String healthWorkerId);
    List<ClinicalDocument> findByProviderId(String providerId);
}
