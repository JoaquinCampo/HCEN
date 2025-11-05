package grupo12.practico.messaging.AccessRequest;

import jakarta.ejb.Local;

import grupo12.practico.dtos.AccessRequest.AddAccessRequestDTO;

@Local
public interface AccessRequestProducerLocal {
    void enqueue(AddAccessRequestDTO dto);
}

