package grupo12.practico.rest.dto;

public class LinkHealthUserRequest {
    private String clinicName;
    private String document;

    public LinkHealthUserRequest() {
    }

    public String getClinicName() {
        return clinicName;
    }

    public void setClinicName(String clinicName) {
        this.clinicName = clinicName;
    }

    public String getDocument() {
        return document;
    }

    public void setDocument(String document) {
        this.document = document;
    }
}
