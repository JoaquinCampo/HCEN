package grupo12.practico.services.HealthUser;

import grupo12.practico.dtos.ClinicalHistory.ClinicalHistoryDTO;
import grupo12.practico.dtos.HealthUser.AddHealthUserDTO;
import grupo12.practico.dtos.HealthUser.HealthUserDTO;
import grupo12.practico.models.Clinic;
import grupo12.practico.models.HealthUser;
import grupo12.practico.repositories.Clinic.ClinicRepositoryLocal;
import grupo12.practico.repositories.HealthUser.HealthUserRepositoryLocal;
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
@Local(HealthUserServiceLocal.class)
@Remote(HealthUserServiceRemote.class)
public class HealthUserServiceBean implements HealthUserServiceRemote {

    @EJB
    private HealthUserRepositoryLocal userRepository;

    @EJB
    private ClinicRepositoryLocal clinicRepository;

    @Override
    public List<HealthUserDTO> findAll() {
        return userRepository.findAll().stream()
                .map(HealthUser::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public HealthUserDTO findById(String id) {
        HealthUser user = userRepository.findById(id);
        return user != null ? user.toDto() : null;
    }

    @Override
    public List<HealthUserDTO> findByName(String name) {
        return userRepository.findByName(name).stream()
                .map(HealthUser::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public HealthUserDTO add(AddHealthUserDTO addHealthUserDTO) {
        validateCreateUserDTO(addHealthUserDTO);
        HealthUser user = convertToHealthUser(addHealthUserDTO);
        HealthUser savedUser = userRepository.add(user);
        return savedUser.toDto();
    }

    public ClinicalHistoryDTO getOrCreateClinicalHistory(String userId) {
        HealthUser user = userRepository.findById(userId);
        return user.getOrCreateClinicalHistory().toDto();
    }

    private void validateCreateUserDTO(AddHealthUserDTO addHealthUserDTO) {
        if (addHealthUserDTO == null) {
            throw new ValidationException("User data must not be null");
        }
        if (isBlank(addHealthUserDTO.getFirstName()) || isBlank(addHealthUserDTO.getLastName())) {
            throw new ValidationException("User first name and last name are required");
        }
        if (isBlank(addHealthUserDTO.getDocument())) {
            throw new ValidationException("User document is required");
        }
        if (addHealthUserDTO.getDocumentType() == null) {
            throw new ValidationException("User document type is required");
        }
        if (isBlank(addHealthUserDTO.getPassword())) {
            throw new ValidationException("User password is required");
        }
    }

    private HealthUser convertToHealthUser(AddHealthUserDTO dto) {
        HealthUser user = new HealthUser();

        // Copy base user fields
        user.setDocument(dto.getDocument());
        user.setDocumentType(dto.getDocumentType());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setGender(dto.getGender());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setImageUrl(dto.getImageUrl());
        user.setAddress(dto.getAddress());
        user.setDateOfBirth(dto.getDateOfBirth());

        user.setPasswordHash(dto.getPassword());

        // Set clinics if provided
        if (dto.getClinicIds() != null && !dto.getClinicIds().isEmpty()) {
            Set<Clinic> clinics = new HashSet<>();
            for (String clinicId : dto.getClinicIds()) {
                Clinic clinic = clinicRepository.findById(clinicId);
                if (clinic != null) {
                    clinics.add(clinic);
                }
            }
            user.setClinics(clinics);
        }

        return user;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
