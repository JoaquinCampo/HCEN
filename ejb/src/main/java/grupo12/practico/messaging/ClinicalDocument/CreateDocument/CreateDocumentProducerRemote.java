package grupo12.practico.messaging.ClinicalDocument.CreateDocument;

import jakarta.ejb.Remote;

import grupo12.practico.dtos.ClinicalDocument.AddClinicalDocumentDTO;

@Remote
public interface CreateDocumentProducerRemote {
    void enqueue(AddClinicalDocumentDTO dto);
}

