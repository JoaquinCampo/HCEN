package grupo12.practico.services.Clinic;

import grupo12.practico.models.Clinic;
import grupo12.practico.models.HealthUser;
import grupo12.practico.dtos.Clinic.AddClinicDTO;
import grupo12.practico.dtos.Clinic.ClinicDTO;
import grupo12.practico.dtos.Clinic.ClinicAdminInfoDTO;
import grupo12.practico.repositories.Clinic.ClinicRepositoryLocal;
import grupo12.practico.repositories.HealthUser.HealthUserRepositoryLocal;
import jakarta.ejb.EJB;
import jakarta.ejb.Local;
import jakarta.ejb.Remote;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;

import java.text.Normalizer;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Stateless
@Local(ClinicServiceLocal.class)
@Remote(ClinicServiceRemote.class)
public class ClinicServiceBean implements ClinicServiceRemote {

    @EJB
    private ClinicRepositoryLocal repository;

    @EJB
    private ClinicRegistrationNotifierLocal registrationNotifier;

    @EJB
    private HealthUserRepositoryLocal healthUserRepository;

    @Override
    public ClinicDTO addClinic(AddClinicDTO addclinicDTO) {
        validateClinic(addclinicDTO);
        Clinic clinic = new Clinic();
        clinic.setName(addclinicDTO.getName());
        clinic.setEmail(addclinicDTO.getEmail());
        clinic.setPhone(addclinicDTO.getPhone());
        clinic.setAddress(addclinicDTO.getAddress());
        Clinic persisted = repository.add(clinic);
        registrationNotifier.notifyClinicCreated(persisted, addclinicDTO.getClinicAdmin());
        return persisted.toDto();
    }

    @Override
    public List<ClinicDTO> findAll() {
        return repository.findAll().stream()
                .map(Clinic::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public ClinicDTO findById(String id) {
        return repository.findById(id).toDto();
    }

    @Override
    public List<ClinicDTO> findByName(String name) {
        return repository.findByName(name).stream()
                .map(Clinic::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public String linkHealthUserToClinic(String clinicName, String healthUserDocument) {
        if (isBlank(clinicName)) {
            throw new ValidationException("Clinic name is required");
        }
        if (isBlank(healthUserDocument)) {
            throw new ValidationException("Health user document is required");
        }

        String normalizedClinicName = clinicName.trim();
        String normalizedDocument = healthUserDocument.trim();

        List<Clinic> matches = repository.findByName(normalizedClinicName);
        if (matches == null || matches.isEmpty()) {
            matches = repository.findAll();
        }
        String normalizedClinicKey = normalizeForComparison(normalizedClinicName);
        Clinic clinic = matches.stream()
                .filter(c -> normalizedClinicKey.equals(normalizeForComparison(c.getName())))
                .findFirst()
                .orElse(null);

        if (clinic == null) {
            throw new EntityNotFoundException("Clinic not found with name: " + normalizedClinicName);
        }

        HealthUser healthUser = healthUserRepository.findByDocument(normalizedDocument);
        if (healthUser == null) {
            throw new EntityNotFoundException("Health user not found with document: " + normalizedDocument);
        }

        if (healthUser.getClinics() == null) {
            healthUser.setClinics(new HashSet<>());
        }
        if (healthUser.getClinics().contains(clinic)) {
            return "Health user is already linked to the clinic";
        }

        healthUser.getClinics().add(clinic);
        if (clinic.getHealthUsers() == null) {
            clinic.setHealthUsers(new HashSet<>());
        }
        clinic.getHealthUsers().add(healthUser);

        return "Health user linked to clinic successfully";
    }

    private void validateClinic(AddClinicDTO addClinicDTO) {
        if (addClinicDTO == null) {
            throw new ValidationException("Clinic must not be null");
        }
        if (isBlank(addClinicDTO.getName())) {
            throw new ValidationException("Clinic name is required");
        }
        if (isBlank(addClinicDTO.getEmail())) {
            throw new ValidationException("Clinic email is required");
        }
        if (isBlank(addClinicDTO.getPhone())) {
            throw new ValidationException("Clinic phone is required");
        }
        if (isBlank(addClinicDTO.getAddress())) {
            throw new ValidationException("Address is required");
        }

        ClinicAdminInfoDTO admin = addClinicDTO.getClinicAdmin();
        if (admin == null) {
            throw new ValidationException("Clinic admin information is required");
        }
        if (isBlank(admin.getName())) {
            throw new ValidationException("Clinic admin name is required");
        }
        if (isBlank(admin.getEmail())) {
            throw new ValidationException("Clinic admin email is required");
        }

    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private String normalizeForComparison(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim().toLowerCase();
        String normalized = Normalizer.normalize(trimmed, Normalizer.Form.NFD);
        return normalized.replaceAll("\\p{M}", "");
    }

}
