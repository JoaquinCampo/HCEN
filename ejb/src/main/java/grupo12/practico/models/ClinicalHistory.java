package grupo12.practico.models;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class ClinicalHistory implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;
    private String summary;
    private LocalDate createdOn;
    private int version;

    private User patient;
    private Set<ClinicalDocument> documents = new HashSet<>();

    public ClinicalHistory() {
        this.id = UUID.randomUUID().toString();
        this.createdOn = LocalDate.now();
        this.version = 1;
        this.summary = "Initial history";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public LocalDate getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(LocalDate createdOn) {
        this.createdOn = createdOn;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public User getPatient() {
        return patient;
    }

    public void setPatient(User patient) {
        this.patient = patient;
    }

    public Set<ClinicalDocument> getDocuments() {
        return documents;
    }

    public void addDocument(ClinicalDocument document) {
        if (document == null)
            return;
        this.documents.add(document);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        ClinicalHistory that = (ClinicalHistory) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
