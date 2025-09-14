package grupo12.practico.services.ClinicalDocument;

import grupo12.practico.models.ClinicalDocument;
import grupo12.practico.models.ClinicalHistory;
import grupo12.practico.models.HealthProvider;
import grupo12.practico.models.HealthWorker;
import grupo12.practico.repositories.ClinicalDocument.ClinicalDocumentRepositoryLocal;
import jakarta.ejb.EJB;
import jakarta.ejb.Local;
import jakarta.ejb.Remote;
import jakarta.ejb.Stateless;
import jakarta.validation.ValidationException;

import java.util.List;

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
        HealthProvider provider = doc.getProvider();
        if (history != null)
            history.addDocument(doc);
        if (author != null)
            author.addAuthoredDocument(doc);
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
        if (doc.getAuthor() == null) {
            throw new ValidationException("Author (HealthWorker) is required");
        }
        if (doc.getProvider() == null) {
            throw new ValidationException("Provider (HealthProvider) is required");
        }
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
