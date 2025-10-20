package grupo12.practico.dtos.ClinicalDocument;

import java.util.Set;

public class AddClinicalDocumentDTO {
    private String title;
    private String contentUrl;
    private String healthUserId;
    private Set<String> healthWorkerIds;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContentUrl() {
        return contentUrl;
    }

    public void setContentUrl(String contentUrl) {
        this.contentUrl = contentUrl;
    }

    public String getHealthUserId() {
        return healthUserId;
    }

    public void setHealthUserId(String healthUserId) {
        this.healthUserId = healthUserId;
    }

    public Set<String> getHealthWorkerIds() {
        return healthWorkerIds;
    }

    public void setHealthWorkerIds(Set<String> healthWorkerIds) {
        this.healthWorkerIds = healthWorkerIds;
    }
}
