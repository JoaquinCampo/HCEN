package grupo12.practico.messaging.ClinicalDocument.PresignedUrl;

import jakarta.ejb.Local;

import grupo12.practico.dtos.ClinicalDocument.PresignedUrlRequestDTO;

@Local
public interface PresignedUrlProducerLocal {
    void enqueue(PresignedUrlRequestDTO dto);
}

