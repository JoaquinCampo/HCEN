package grupo12.practico.repositories.ClinicalDocument;

import jakarta.ejb.Local;

import java.util.List;

import grupo12.practico.models.ClinicalDocument;

@Local
public interface ClinicalDocumentRepositoryLocal {
    ClinicalDocument add(ClinicalDocument doc);

    List<ClinicalDocument> findAll();

    ClinicalDocument findById(String id);

    List<ClinicalDocument> findByTitle(String title);
}
