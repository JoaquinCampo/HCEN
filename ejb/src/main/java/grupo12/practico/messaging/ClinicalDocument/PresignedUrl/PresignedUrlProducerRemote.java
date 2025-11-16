package grupo12.practico.messaging.ClinicalDocument.PresignedUrl;

import jakarta.ejb.Remote;

import grupo12.practico.dtos.ClinicalDocument.PresignedUrlRequestDTO;

@Remote
public interface PresignedUrlProducerRemote {
    void enqueue(PresignedUrlRequestDTO dto);
}

