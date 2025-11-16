package grupo12.practico.messaging.ClinicalDocument.Chat;

import jakarta.ejb.Local;

import grupo12.practico.dtos.ClinicalHistory.ChatRequestDTO;

@Local
public interface ChatProducerLocal {
    void enqueue(ChatRequestDTO dto);
}

