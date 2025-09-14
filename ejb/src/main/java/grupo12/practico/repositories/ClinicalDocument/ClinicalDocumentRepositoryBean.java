package grupo12.practico.repositories.ClinicalDocument;

import jakarta.ejb.Local;
import jakarta.ejb.Remote;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import grupo12.practico.models.ClinicalDocument;

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
        if (userId == null || userId.isBlank())
            return findAll();
        return documents.stream()
                .filter(d -> d.getClinicalHistory() != null
                        && d.getClinicalHistory().getPatient() != null
                        && userId.equals(d.getClinicalHistory().getPatient().getId()))
                .collect(Collectors.toList());
    }

    @Override
    public List<ClinicalDocument> findByAuthorId(String healthWorkerId) {
        if (healthWorkerId == null || healthWorkerId.isBlank())
            return findAll();
        return documents.stream()
                .filter(d -> d.getAuthor() != null && healthWorkerId.equals(d.getAuthor().getId()))
                .collect(Collectors.toList());
    }

    @Override
    public List<ClinicalDocument> findByProviderId(String providerId) {
        if (providerId == null || providerId.isBlank())
            return findAll();
        return documents.stream()
                .filter(d -> d.getProvider() != null && providerId.equals(d.getProvider().getId()))
                .collect(Collectors.toList());
    }

    @Override
    public List<ClinicalDocument> findByPatientName(String query) {
        String q = normalize(query);
        return documents.stream()
                .filter(d -> d.getClinicalHistory() != null
                        && d.getClinicalHistory().getPatient() != null
                        && nameMatches(d.getClinicalHistory().getPatient().getFirstName(),
                                d.getClinicalHistory().getPatient().getLastName(), q))
                .collect(Collectors.toList());
    }

    @Override
    public List<ClinicalDocument> findByAuthorName(String query) {
        String q = normalize(query);
        return documents.stream()
                .filter(d -> d.getAuthor() != null
                        && nameMatches(d.getAuthor().getFirstName(), d.getAuthor().getLastName(), q))
                .collect(Collectors.toList());
    }

    @Override
    public List<ClinicalDocument> findByProviderName(String query) {
        String q = normalize(query);
        return documents.stream()
                .filter(d -> d.getProvider() != null && containsIgnoreCase(d.getProvider().getName(), q))
                .collect(Collectors.toList());
    }

    @Override
    public List<ClinicalDocument> findByAnyName(String query) {
        String q = normalize(query);
        return documents.stream()
                .filter(d -> (d.getClinicalHistory() != null
                        && d.getClinicalHistory().getPatient() != null
                        && nameMatches(d.getClinicalHistory().getPatient().getFirstName(),
                                d.getClinicalHistory().getPatient().getLastName(), q))
                        || (d.getAuthor() != null
                                && nameMatches(d.getAuthor().getFirstName(), d.getAuthor().getLastName(), q))
                        || (d.getProvider() != null && containsIgnoreCase(d.getProvider().getName(), q)))
                .collect(Collectors.toList());
    }

    private String normalize(String s) {
        return s == null ? "" : s.trim().toLowerCase();
    }

    private boolean nameMatches(String first, String last, String needle) {
        String f = normalize(first);
        String l = normalize(last);
        return f.contains(needle) || l.contains(needle)
                || (l + ", " + f).contains(needle)
                || (f + " " + l).contains(needle);
    }

    private boolean containsIgnoreCase(String value, String needle) {
        return normalize(value).contains(needle);
    }
}
