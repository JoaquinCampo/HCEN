package grupo12.practico.services.ClinicalDocument;

import jakarta.ejb.Local;

import java.util.List;

import grupo12.practico.dtos.ClinicalDocument.ClinicalDocumentDTO;
import grupo12.practico.dtos.ClinicalDocument.AddClinicalDocumentDTO;

@Local
public interface ClinicalDocumentServiceLocal {
    ClinicalDocumentDTO add(AddClinicalDocumentDTO addClinicalDocumentDTO);

    List<ClinicalDocumentDTO> findAll();

    ClinicalDocumentDTO findById(String id);

    List<ClinicalDocumentDTO> findByTitle(String title);
}
