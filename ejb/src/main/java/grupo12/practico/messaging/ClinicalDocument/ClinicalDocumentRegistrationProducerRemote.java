package grupo12.practico.messaging.ClinicalDocument;

import jakarta.ejb.Remote;
import grupo12.practico.dtos.ClinicalDocument.AddClinicalDocumentDTO;

@Remote
public interface ClinicalDocumentRegistrationProducerRemote {
    void enqueue(AddClinicalDocumentDTO dto);
}
