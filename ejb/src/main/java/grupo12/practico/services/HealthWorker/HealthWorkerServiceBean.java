package grupo12.practico.services.HealthWorker;

import grupo12.practico.dtos.HealthWorker.AddHealthWorkerDTO;
import grupo12.practico.dtos.HealthWorker.HealthWorkerDTO;
import grupo12.practico.models.Clinic;
import grupo12.practico.models.HealthWorker;
import grupo12.practico.repositories.Clinic.ClinicRepositoryLocal;
import grupo12.practico.repositories.HealthWorker.HealthWorkerRepositoryLocal;
import grupo12.practico.services.PasswordUtil;
import jakarta.ejb.EJB;
import jakarta.ejb.Local;
import jakarta.ejb.Remote;
import jakarta.ejb.Stateless;
import jakarta.validation.ValidationException;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Stateless
@Local(HealthWorkerServiceLocal.class)
@Remote(HealthWorkerServiceRemote.class)
public class HealthWorkerServiceBean implements HealthWorkerServiceRemote {

    @EJB
    private HealthWorkerRepositoryLocal repository;

    @EJB
    private ClinicRepositoryLocal clinicRepository;

    @Override
    public HealthWorkerDTO add(AddHealthWorkerDTO addHealthWorkerDTO) {
        validateHealthWorker(addHealthWorkerDTO);
        HealthWorker healthWorker = createHealthWorkerFromDTO(addHealthWorkerDTO);
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
        if (isBlank(addHealthWorkerDTO.getPassword())) {
            throw new ValidationException("HealthWorker password is required");
        }
    }

    private HealthWorker createHealthWorkerFromDTO(AddHealthWorkerDTO dto) {
        HealthWorker worker = new HealthWorker();

        worker.setDocument(dto.getDocument());
        worker.setDocumentType(dto.getDocumentType());
        worker.setFirstName(dto.getFirstName());
        worker.setLastName(dto.getLastName());
        worker.setGender(dto.getGender());
        worker.setEmail(dto.getEmail());
        worker.setPhone(dto.getPhone());
        worker.setImageUrl(dto.getImageUrl());
        worker.setAddress(dto.getAddress());
        worker.setDateOfBirth(dto.getDateOfBirth());
        worker.setLicenseNumber(dto.getLicenseNumber());

        String salt = PasswordUtil.generateSalt();
        String hashedPassword = PasswordUtil.hashPassword(dto.getPassword(), salt);

        worker.setPasswordSalt(salt);
        worker.setPasswordHash(hashedPassword);
        worker.setPasswordUpdatedAt(LocalDate.now());

        if (dto.getClinicIds() != null && !dto.getClinicIds().isEmpty()) {
            Set<Clinic> clinics = new HashSet<>();
            for (String clinicId : dto.getClinicIds()) {
                Clinic clinic = clinicRepository.findById(clinicId);
                if (clinic != null) {
                    clinics.add(clinic);
                }
            }
            worker.setClinics(clinics);
        }

        return worker;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
