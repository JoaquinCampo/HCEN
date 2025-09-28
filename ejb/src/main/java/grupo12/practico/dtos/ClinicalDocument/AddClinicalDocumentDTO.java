package grupo12.practico.dtos.ClinicalDocument;

public class AddClinicalDocumentDTO {
    private String title;
    private String contentUrl;
    private String clinicalHistoryId;
    private String authorId;
    private String providerId;

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

    public String getClinicalHistoryId() {
        return clinicalHistoryId;
    }

    public void setClinicalHistoryId(String clinicalHistoryId) {
        this.clinicalHistoryId = clinicalHistoryId;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }
}
