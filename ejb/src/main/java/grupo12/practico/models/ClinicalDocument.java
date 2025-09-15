package grupo12.practico.models;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class ClinicalDocument {
    private String id;
    private String title;
    private String content;
    private LocalDateTime issuedAt;

    private ClinicalHistory clinicalHistory;
    private HealthWorker author;
    private HealthProvider provider;

    public ClinicalDocument() {
        this.id = UUID.randomUUID().toString();
        this.issuedAt = LocalDateTime.now();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getIssuedAt() {
        return issuedAt;
    }

    public void setIssuedAt(LocalDateTime issuedAt) {
        this.issuedAt = issuedAt;
    }

    public ClinicalHistory getClinicalHistory() {
        return clinicalHistory;
    }

    public void setClinicalHistory(ClinicalHistory clinicalHistory) {
        this.clinicalHistory = clinicalHistory;
    }

    public HealthWorker getAuthor() {
        return author;
    }

    public void setAuthor(HealthWorker author) {
        this.author = author;
    }

    public HealthProvider getProvider() {
        return provider;
    }

    public void setProvider(HealthProvider provider) {
        this.provider = provider;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        ClinicalDocument that = (ClinicalDocument) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "ClinicalDocument{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", issuedAt=" + issuedAt +
                '}';
    }
}
