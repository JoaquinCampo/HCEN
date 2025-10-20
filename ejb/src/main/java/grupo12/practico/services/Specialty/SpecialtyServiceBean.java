package grupo12.practico.services.Specialty;

import grupo12.practico.dtos.Specialty.AddSpecialtyDTO;
import grupo12.practico.dtos.SpecialtyDTO;
import grupo12.practico.models.Specialty;
import grupo12.practico.repositories.Specialty.SpecialtyRepositoryLocal;
import jakarta.ejb.EJB;
import jakarta.ejb.Local;
import jakarta.ejb.Remote;
import jakarta.ejb.Stateless;
import jakarta.validation.ValidationException;

import java.util.List;
import java.util.stream.Collectors;

@Stateless
@Local(SpecialtyServiceLocal.class)
@Remote(SpecialtyServiceRemote.class)
public class SpecialtyServiceBean implements SpecialtyServiceRemote {

    @EJB
    private SpecialtyRepositoryLocal specialtyRepository;

    @Override
    public List<SpecialtyDTO> findAll() {
        return specialtyRepository.findAll().stream()
                .map(Specialty::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public SpecialtyDTO findById(String id) {
        Specialty specialty = specialtyRepository.findById(id);
        return specialty != null ? specialty.toDto() : null;
    }

    @Override
    public List<SpecialtyDTO> findByName(String name) {
        return specialtyRepository.findByName(name).stream()
                .map(Specialty::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public SpecialtyDTO add(AddSpecialtyDTO addSpecialtyDTO) {
        validateAddSpecialtyDTO(addSpecialtyDTO);

        Specialty specialty = createSpecialtyFromDTO(addSpecialtyDTO);

        return specialtyRepository.add(specialty).toDto();
    }

    private void validateAddSpecialtyDTO(AddSpecialtyDTO addSpecialtyDTO) {
        if (addSpecialtyDTO == null) {
            throw new ValidationException("Specialty data must not be null");
        }
        if (isBlank(addSpecialtyDTO.getName())) {
            throw new ValidationException("Specialty name is required");
        }
    }

    private Specialty createSpecialtyFromDTO(AddSpecialtyDTO dto) {
        Specialty specialty = new Specialty();
        specialty.setName(dto.getName());
        return specialty;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
