package grupo12.practico.repositories.ClinicalDocument;

import jakarta.ejb.Local;
import jakarta.ejb.Remote;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import grupo12.practico.models.ClinicalDocument;

@Singleton
@Startup
@Local(ClinicalDocumentRepositoryLocal.class)
@Remote(ClinicalDocumentRepositoryRemote.class)
public class ClinicalDocumentRepositoryBean implements ClinicalDocumentRepositoryRemote {

    private final Map<String, ClinicalDocument> documents = new HashMap<>();

    @Override
    public ClinicalDocument add(ClinicalDocument doc) {
        documents.put(doc.getId(), doc);
        return doc;
    }

    @Override
    public List<ClinicalDocument> findAll() {
        return new ArrayList<>(documents.values());
    }

    @Override
    public ClinicalDocument findById(String id) {
        return documents.get(id);
    }

    @Override
    public List<ClinicalDocument> findByTitle(String title) {
        return documents.values().stream()
                .filter(d -> d.getTitle() != null
                        && d.getTitle().toLowerCase(Locale.ROOT).contains(title.toLowerCase(Locale.ROOT)))
                .collect(Collectors.toList());
    }
}
