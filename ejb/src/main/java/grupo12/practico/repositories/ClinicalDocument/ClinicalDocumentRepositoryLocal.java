package grupo12.practico.repositories.ClinicalDocument;

import jakarta.ejb.Local;

import java.util.List;

import grupo12.practico.models.ClinicalDocument;

@Local
public interface ClinicalDocumentRepositoryLocal {
    ClinicalDocument add(ClinicalDocument doc);

    List<ClinicalDocument> findAll();

    List<ClinicalDocument> findByPatientId(String userId);

    List<ClinicalDocument> findByAuthorId(String healthWorkerId);

    List<ClinicalDocument> findByProviderId(String providerId);

    List<ClinicalDocument> findByPatientName(String query);

    List<ClinicalDocument> findByAuthorName(String query);

    List<ClinicalDocument> findByProviderName(String query);

    List<ClinicalDocument> findByAnyName(String query);
}
