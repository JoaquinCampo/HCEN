package grupo12.practico.messaging.ClinicalDocument.Chat;

import jakarta.ejb.Remote;

import grupo12.practico.dtos.ClinicalHistory.ChatRequestDTO;

@Remote
public interface ChatProducerRemote {
    void enqueue(ChatRequestDTO dto);
}

