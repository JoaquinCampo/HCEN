package grupo12.practico.messaging.ClinicalDocument.CreateDocument;

import jakarta.ejb.Local;

import grupo12.practico.dtos.ClinicalDocument.AddClinicalDocumentDTO;

@Local
public interface CreateDocumentProducerLocal {
    void enqueue(AddClinicalDocumentDTO dto);
}

