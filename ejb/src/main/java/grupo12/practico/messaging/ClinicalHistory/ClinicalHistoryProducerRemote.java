package grupo12.practico.messaging.ClinicalHistory;

import jakarta.ejb.Remote;

import grupo12.practico.dtos.ClinicalHistory.ClinicalHistoryRequestDTO;

@Remote
public interface ClinicalHistoryProducerRemote {
    void enqueue(ClinicalHistoryRequestDTO dto);
}

