package grupo12.practico.repository.document;

import grupo12.practico.model.ClinicalDocument;
import jakarta.ejb.Local;
import jakarta.ejb.Remote;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Singleton
@Startup
@Local(ClinicalDocumentRepositoryLocal.class)
@Remote(ClinicalDocumentRepositoryRemote.class)
public class ClinicalDocumentRepositoryBean implements ClinicalDocumentRepositoryRemote {

    private final List<ClinicalDocument> documents = new ArrayList<>();

    @Override
    public ClinicalDocument add(ClinicalDocument doc) {
        documents.add(doc);
        return doc;
    }

    @Override
    public List<ClinicalDocument> findAll() {
        return new ArrayList<>(documents);
    }

    @Override
    public List<ClinicalDocument> findByPatientId(String userId) {
        if (userId == null || userId.isBlank()) return findAll();
        return documents.stream()
                .filter(d -> d.getPatient() != null && userId.equals(d.getPatient().getId()))
                .collect(Collectors.toList());
    }

    @Override
    public List<ClinicalDocument> findByAuthorId(String healthWorkerId) {
        if (healthWorkerId == null || healthWorkerId.isBlank()) return findAll();
        return documents.stream()
                .filter(d -> d.getAuthor() != null && healthWorkerId.equals(d.getAuthor().getId()))
                .collect(Collectors.toList());
    }

    @Override
    public List<ClinicalDocument> findByProviderId(String providerId) {
        if (providerId == null || providerId.isBlank()) return findAll();
        return documents.stream()
                .filter(d -> d.getProvider() != null && providerId.equals(d.getProvider().getId()))
                .collect(Collectors.toList());
    }
}
