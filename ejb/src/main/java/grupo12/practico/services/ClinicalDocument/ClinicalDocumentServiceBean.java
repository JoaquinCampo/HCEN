package grupo12.practico.services.ClinicalDocument;

import grupo12.practico.dtos.ClinicalDocument.AddClinicalDocumentDTO;
import grupo12.practico.dtos.ClinicalDocument.ClinicalDocumentDTO;
import grupo12.practico.models.ClinicalDocument;
import grupo12.practico.models.HealthUser;
import grupo12.practico.models.HealthWorker;
import grupo12.practico.repositories.ClinicalDocument.ClinicalDocumentRepositoryLocal;
import grupo12.practico.repositories.HealthUser.HealthUserRepositoryLocal;
import grupo12.practico.repositories.HealthWorker.HealthWorkerRepositoryLocal;
import jakarta.ejb.EJB;
import jakarta.ejb.Local;
import jakarta.ejb.Remote;
import jakarta.ejb.Stateless;
import jakarta.validation.ValidationException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Stateless
@Local(ClinicalDocumentServiceLocal.class)
@Remote(ClinicalDocumentServiceRemote.class)
public class ClinicalDocumentServiceBean implements ClinicalDocumentServiceRemote {

    @EJB
    private ClinicalDocumentRepositoryLocal repository;

    @EJB
    private HealthUserRepositoryLocal healthUserRepository;

    @EJB
    private HealthWorkerRepositoryLocal healthWorkerRepository;

    @Override
    public ClinicalDocumentDTO add(AddClinicalDocumentDTO addClinicalDocumentDTO) {
        validate(addClinicalDocumentDTO);

        String healthUserId = addClinicalDocumentDTO.getHealthUserId();
        Set<String> healthWorkerIds = addClinicalDocumentDTO.getHealthWorkerIds();

        HealthUser healthUser = healthUserRepository.findById(healthUserId);
        if (healthUser == null) {
            throw new ValidationException("Health user not found");
        }

        Set<HealthWorker> healthWorkers = new HashSet<>();
        for (String healthWorkerId : healthWorkerIds) {
            HealthWorker healthWorker = healthWorkerRepository.findById(healthWorkerId);
            if (healthWorker == null) {
                throw new ValidationException("Health worker not found");
            }
            healthWorkers.add(healthWorker);
        }

        ClinicalDocument clinicalDocument = new ClinicalDocument();

        clinicalDocument.setTitle(addClinicalDocumentDTO.getTitle());
        clinicalDocument.setContentUrl(addClinicalDocumentDTO.getContentUrl());
        clinicalDocument.setHealthUser(healthUser);
        clinicalDocument.setHealthWorkers(healthWorkers);

        Set<ClinicalDocument> userDocuments = healthUser.getClinicalDocuments();
        if (userDocuments == null) {
            userDocuments = new HashSet<>();
            healthUser.setClinicalDocuments(userDocuments);
        }
        userDocuments.add(clinicalDocument);

        healthWorkers.forEach(healthWorker -> {
            Set<ClinicalDocument> healthWorkerClinicalDocuments = healthWorker.getClinicalDocuments();
            if (healthWorkerClinicalDocuments == null) {
                healthWorkerClinicalDocuments = new HashSet<>();
                healthWorker.setClinicalDocuments(healthWorkerClinicalDocuments);
            }
            healthWorkerClinicalDocuments.add(clinicalDocument);
            healthWorker.setClinicalDocuments(healthWorkerClinicalDocuments);
        });

        return repository.add(clinicalDocument).toDto();
    }

    @Override
    public List<ClinicalDocumentDTO> findAll() {
        return repository.findAll().stream()
                .map(ClinicalDocument::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public ClinicalDocumentDTO findById(String id) {
        return repository.findById(id).toDto();
    }

    @Override
    public List<ClinicalDocumentDTO> findByTitle(String title) {
        return repository.findByTitle(title).stream()
                .map(ClinicalDocument::toDto)
                .collect(Collectors.toList());
    }

    private void validate(AddClinicalDocumentDTO addClinicalDocumentDTO) {
        if (addClinicalDocumentDTO == null) {
            throw new ValidationException("Clinical document is required");
        }
        if (isBlank(addClinicalDocumentDTO.getTitle())) {
            throw new ValidationException("Title is required");
        }
        if (isBlank(addClinicalDocumentDTO.getContentUrl())) {
            throw new ValidationException("Content URL is required");
        }
        if (addClinicalDocumentDTO.getHealthUserId() == null
                || addClinicalDocumentDTO.getHealthUserId().isEmpty()) {
            throw new ValidationException("Health user is required");
        }
        if (addClinicalDocumentDTO.getHealthWorkerIds() == null
                || addClinicalDocumentDTO.getHealthWorkerIds().isEmpty()) {
            throw new ValidationException("Health workers are required");
        }
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
