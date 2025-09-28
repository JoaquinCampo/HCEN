package grupo12.practico.services.ClinicalDocument;

import grupo12.practico.models.ClinicalDocument;
import grupo12.practico.dtos.ClinicalDocument.AddClinicalDocumentDTO;
import grupo12.practico.dtos.ClinicalDocument.ClinicalDocumentDTO;
import grupo12.practico.models.ClinicalHistory;
import grupo12.practico.models.HealthWorker;
import grupo12.practico.repositories.ClinicalDocument.ClinicalDocumentRepositoryLocal;
import grupo12.practico.repositories.ClinicalHistory.ClinicalHistoryRepositoryLocal;
import grupo12.practico.repositories.HealthWorker.HealthWorkerRepositoryLocal;
import grupo12.practico.repositories.Clinic.ClinicRepositoryLocal;
import jakarta.ejb.EJB;
import jakarta.ejb.Local;
import jakarta.ejb.Remote;
import jakarta.ejb.Stateless;
import jakarta.validation.ValidationException;

import java.util.List;
import java.util.stream.Collectors;

@Stateless
@Local(ClinicalDocumentServiceLocal.class)
@Remote(ClinicalDocumentServiceRemote.class)
public class ClinicalDocumentServiceBean implements ClinicalDocumentServiceRemote {

    @EJB
    private ClinicalDocumentRepositoryLocal repository;

    @EJB
    private ClinicalHistoryRepositoryLocal clinicalHistoryRepository;

    @EJB
    private HealthWorkerRepositoryLocal healthWorkerRepository;

    @EJB
    private ClinicRepositoryLocal clinicRepository;

    @Override
    public ClinicalDocumentDTO addClinicalDocument(AddClinicalDocumentDTO addClinicalDocumentDTO) {
        validate(addClinicalDocumentDTO);

        String clinicalHistoryId = addClinicalDocumentDTO.getClinicalHistoryId();
        String authorId = addClinicalDocumentDTO.getAuthorId();

        ClinicalHistory clinicalHistory = clinicalHistoryRepository.findById(clinicalHistoryId);

        if (clinicalHistory == null) {
            throw new ValidationException("Clinical history not found");
        }

        HealthWorker author = healthWorkerRepository.findById(authorId);

        if (author == null) {
            throw new ValidationException("Author not found");
        }

        ClinicalDocument clinicalDocument = new ClinicalDocument();
        clinicalDocument.setTitle(addClinicalDocumentDTO.getTitle());
        clinicalDocument.setContentUrl(addClinicalDocumentDTO.getContentUrl());
        clinicalDocument.setClinicalHistory(clinicalHistory);
        clinicalDocument.setAuthor(author);

        author.addAuthoredDocument(clinicalDocument);

        clinicalHistory.addDocument(clinicalDocument);

        return repository.add(clinicalDocument).toDto();
    }

    @Override
    public List<ClinicalDocumentDTO> getAllDocuments() {
        return repository.findAll().stream()
                .map(ClinicalDocument::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ClinicalDocumentDTO> getDocumentsByPatient(String userId) {
        return repository.findByPatientId(userId).stream()
                .map(ClinicalDocument::toDto)
                .collect(Collectors.toList());
    }

    private void validate(AddClinicalDocumentDTO addClinicalDocumentDTO) {
        if (addClinicalDocumentDTO == null) {
            throw new ValidationException("ClinicalDocument must not be null");
        }
        if (isBlank(addClinicalDocumentDTO.getTitle())) {
            throw new ValidationException("Title is required");
        }
        if (addClinicalDocumentDTO.getClinicalHistoryId() == null
                || addClinicalDocumentDTO.getClinicalHistoryId().isEmpty()) {
            throw new ValidationException("Clinical history with patient is required");
        }
        if (addClinicalDocumentDTO.getAuthorId() == null || addClinicalDocumentDTO.getAuthorId().isEmpty()) {
            throw new ValidationException("Author is required");
        }
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
