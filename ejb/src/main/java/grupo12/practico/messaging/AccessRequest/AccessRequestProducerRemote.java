package grupo12.practico.messaging.AccessRequest;

import jakarta.ejb.Remote;

import grupo12.practico.dtos.AccessRequest.AddAccessRequestDTO;

@Remote
public interface AccessRequestProducerRemote {
    void enqueue(AddAccessRequestDTO dto);
}

