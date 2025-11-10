package grupo12.practico.dtos.ClinicalDocument;

import java.io.Serializable;

public class PresignedUrlResponseDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String uploadUrl;
    private String s3Url;
    private String objectKey;
    private Integer expiresInSeconds;

    public String getUploadUrl() {
        return uploadUrl;
    }

    public void setUploadUrl(String uploadUrl) {
        this.uploadUrl = uploadUrl;
    }

    public String getS3Url() {
        return s3Url;
    }

    public void setS3Url(String s3Url) {
        this.s3Url = s3Url;
    }

    public String getObjectKey() {
        return objectKey;
    }

    public void setObjectKey(String objectKey) {
        this.objectKey = objectKey;
    }

    public Integer getExpiresInSeconds() {
        return expiresInSeconds;
    }

    public void setExpiresInSeconds(Integer expiresInSeconds) {
        this.expiresInSeconds = expiresInSeconds;
    }
}
