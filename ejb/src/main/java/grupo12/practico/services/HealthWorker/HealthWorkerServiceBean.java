package grupo12.practico.services.HealthWorker;

import grupo12.practico.services.Clinic.ClinicServiceLocal;
import grupo12.practico.dtos.HealthWorker.AddHealthWorkerDTO;
import grupo12.practico.dtos.HealthWorker.HealthWorkerDTO;
import grupo12.practico.models.HealthWorker;
import grupo12.practico.repositories.HealthWorker.HealthWorkerRepositoryLocal;
import jakarta.ejb.EJB;
import jakarta.ejb.Local;
import jakarta.ejb.Remote;
import jakarta.ejb.Stateless;
import jakarta.validation.ValidationException;

import java.util.List;
import java.util.stream.Collectors;

@Stateless
@Local(HealthWorkerServiceLocal.class)
@Remote(HealthWorkerServiceRemote.class)
public class HealthWorkerServiceBean implements HealthWorkerServiceRemote {

    @EJB
    private HealthWorkerRepositoryLocal repository;

    @EJB
    private ClinicServiceLocal clinicService;

    @Override
    public HealthWorkerDTO add(AddHealthWorkerDTO addHealthWorkerDTO) {
        validateHealthWorker(addHealthWorkerDTO);
        HealthWorker healthWorker = new HealthWorker();
        healthWorker.setFirstName(addHealthWorkerDTO.getFirstName());
        healthWorker.setLastName(addHealthWorkerDTO.getLastName());
        healthWorker.setDocument(addHealthWorkerDTO.getDocument());
        healthWorker.setDocumentType(addHealthWorkerDTO.getDocumentType());
        healthWorker.setLicenseNumber(addHealthWorkerDTO.getLicenseNumber());
        return repository.add(healthWorker).toDto();
    }

    @Override
    public List<HealthWorkerDTO> findAll() {
        return repository.findAll().stream()
                .map(HealthWorker::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<HealthWorkerDTO> findByName(String name) {
        return repository.findByName(name).stream()
                .map(HealthWorker::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public HealthWorkerDTO findById(String id) {
        return repository.findById(id).toDto();
    }

    private void validateHealthWorker(AddHealthWorkerDTO addHealthWorkerDTO) {
        if (addHealthWorkerDTO == null) {
            throw new ValidationException("HealthWorker must not be null");
        }
        if (isBlank(addHealthWorkerDTO.getFirstName()) || isBlank(addHealthWorkerDTO.getLastName())) {
            throw new ValidationException("HealthWorker first name and last name are required");
        }
        if (isBlank(addHealthWorkerDTO.getDocument())) {
            throw new ValidationException("HealthWorker document is required");
        }
        if (addHealthWorkerDTO.getDocumentType() == null) {
            throw new ValidationException("HealthWorker document type is required");
        }
        if (isBlank(addHealthWorkerDTO.getLicenseNumber())) {
            throw new ValidationException("License number is required");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
