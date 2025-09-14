package grupo12.practico.service.document;

import grupo12.practico.model.ClinicalDocument;
import grupo12.practico.model.HealthProvider;
import grupo12.practico.model.HealthWorker;
import grupo12.practico.model.User;
import grupo12.practico.repository.document.ClinicalDocumentRepositoryLocal;
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

        // Maintain simple owning-side collections
        User patient = doc.getPatient();
        HealthWorker author = doc.getAuthor();
        HealthProvider provider = doc.getProvider();
        if (patient != null) patient.addClinicalDocument(doc);
        if (author != null) author.addAuthoredDocument(doc);
        if (provider != null) provider.addClinicalDocument(doc);

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

    private void validate(ClinicalDocument doc) {
        if (doc == null) {
            throw new ValidationException("ClinicalDocument must not be null");
        }
        if (isBlank(doc.getTitle())) {
            throw new ValidationException("Title is required");
        }
        if (doc.getPatient() == null) {
            throw new ValidationException("Patient is required");
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
