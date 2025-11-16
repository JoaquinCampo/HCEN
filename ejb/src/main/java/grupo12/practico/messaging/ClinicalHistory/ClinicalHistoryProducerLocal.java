package grupo12.practico.messaging.ClinicalHistory;

import jakarta.ejb.Local;

import grupo12.practico.dtos.ClinicalHistory.ClinicalHistoryRequestDTO;

@Local
public interface ClinicalHistoryProducerLocal {
    void enqueue(ClinicalHistoryRequestDTO dto);
}

