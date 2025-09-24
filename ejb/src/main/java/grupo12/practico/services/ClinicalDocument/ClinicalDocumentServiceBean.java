package grupo12.practico.services.ClinicalDocument;

import grupo12.practico.models.ClinicalDocument;
import grupo12.practico.models.ClinicalHistory;
import grupo12.practico.models.Clinic;
import grupo12.practico.models.HealthWorker;
import grupo12.practico.repositories.ClinicalDocument.ClinicalDocumentRepositoryLocal;
import jakarta.ejb.EJB;
import jakarta.ejb.Local;
import jakarta.ejb.Remote;
import jakarta.ejb.Stateless;
import jakarta.validation.ValidationException;

import java.util.List;
import java.util.Set;

@Stateless
@Local(ClinicalDocumentServiceLocal.class)
@Remote(ClinicalDocumentServiceRemote.class)
public class ClinicalDocumentServiceBean implements ClinicalDocumentServiceRemote {

    @EJB
    private ClinicalDocumentRepositoryLocal repository;

    @Override
    public ClinicalDocument addClinicalDocument(ClinicalDocument doc) {
        validate(doc);

        ClinicalHistory history = doc.getClinicalHistory();
        HealthWorker author = doc.getAuthor();
        Set<HealthWorker> authors = doc.getHealthWorkers();
        Clinic provider = doc.getProvider();

        if (history != null)
            history.addDocument(doc);

        // Handle all authors (both single author and healthWorkers set)
        if (author != null)
            author.addAuthoredDocument(doc);
        if (authors != null) {
            for (HealthWorker auth : authors) {
                if (auth != null) {
                    auth.addAuthoredDocument(doc);
                }
            }
        }

        if (provider != null)
            provider.addClinicalDocument(doc);

        return repository.add(doc);
    }

    @Override
    public List<ClinicalDocument> getAllDocuments() {
        return repository.findAll();
    }

    @Override
    public List<ClinicalDocument> getDocumentsByPatient(String userId) {
        return repository.findByPatientId(userId);
    }

    @Override
    public List<ClinicalDocument> getDocumentsByAuthor(String healthWorkerId) {
        return repository.findByAuthorId(healthWorkerId);
    }

    @Override
    public List<ClinicalDocument> getDocumentsByProvider(String providerId) {
        return repository.findByProviderId(providerId);
    }

    // --- Name-based search delegations ---
    @Override
    public List<ClinicalDocument> searchByPatientName(String query) {
        return repository.findByPatientName(query);
    }

    @Override
    public List<ClinicalDocument> searchByAuthorName(String query) {
        return repository.findByAuthorName(query);
    }

    @Override
    public List<ClinicalDocument> searchByProviderName(String query) {
        return repository.findByProviderName(query);
    }

    @Override
    public List<ClinicalDocument> searchByAnyName(String query) {
        return repository.findByAnyName(query);
    }

    private void validate(ClinicalDocument doc) {
        if (doc == null) {
            throw new ValidationException("ClinicalDocument must not be null");
        }
        if (isBlank(doc.getTitle())) {
            throw new ValidationException("Title is required");
        }
        if (doc.getClinicalHistory() == null || doc.getClinicalHistory().getPatient() == null) {
            throw new ValidationException("Clinical history with patient is required");
        }

        // At least one author is required (context specifies 1..* authors)
        boolean hasAuthor = (doc.getAuthor() != null) ||
                (doc.getHealthWorkers() != null && !doc.getHealthWorkers().isEmpty());
        if (!hasAuthor) {
            throw new ValidationException("At least one author is required for clinical documents");
        }
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
