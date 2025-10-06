package grupo12.practico.web.jsf;

import grupo12.practico.dtos.Clinic.ClinicDTO;
import grupo12.practico.dtos.HealthUser.AddHealthUserDTO;
import grupo12.practico.dtos.HealthUser.HealthUserDTO;
import grupo12.practico.messaging.HealthUser.HealthUserRegistrationProducerLocal;
import grupo12.practico.models.DocumentType;
import grupo12.practico.models.Gender;
import grupo12.practico.services.HealthUser.HealthUserServiceLocal;
import grupo12.practico.services.Clinic.ClinicServiceLocal;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import jakarta.validation.ValidationException;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Named("healthUserBean")
@ViewScoped
public class HealthUserBean implements Serializable {
    private static final long serialVersionUID = 1L;

    @EJB
    private HealthUserServiceLocal userService;

    @EJB
    private HealthUserRegistrationProducerLocal registrationProducer;

    @EJB
    private ClinicServiceLocal clinicService;

    private List<HealthUserDTO> users;
    private AddHealthUserDTO newUser;
    private String searchQuery;
    private LocalDate maxAdultBirthDate;
    private List<ClinicDTO> clinics;
    private String[] selectedClinicIds;
    private Map<String, String> clinicNameLookup;

    @PostConstruct
    public void init() {
        newUser = new AddHealthUserDTO();
        users = new ArrayList<>();
        clinics = new ArrayList<>();
        clinicNameLookup = new HashMap<>();
        selectedClinicIds = null;
        maxAdultBirthDate = LocalDate.now().minusYears(18);
        refreshClinics();
        loadAll();
    }

    public void loadAll() {
        users = userService.findAll();
    }

    public void search() {
        if (searchQuery == null || searchQuery.trim().isEmpty()) {
            loadAll();
        } else {
            users = userService.findByName(searchQuery.trim());
        }
    }

    public String save() {
        try {
            newUser.setClinicIds(extractSelectedClinicIds());
            registrationProducer.enqueue(newUser);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO,
                            "Request accepted; the user will be created shortly", null));
            newUser = new AddHealthUserDTO();
            selectedClinicIds = null;
            return "list?faces-redirect=true";
        } catch (ValidationException ex) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage(), null));
            return null;
        } catch (RuntimeException ex) {
            String message = ex.getMessage() != null ? ex.getMessage()
                    : "Unexpected error while queueing the user creation request";
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, message, null));
            return null;
        }
    }

    public Gender[] getGenders() {
        return Gender.values();
    }

    public DocumentType[] getDocumentTypes() {
        return DocumentType.values();
    }

    public List<HealthUserDTO> getUsers() {
        return users;
    }

    public AddHealthUserDTO getNewUser() {
        return newUser;
    }

    public void setNewUser(AddHealthUserDTO newUser) {
        this.newUser = newUser;
    }

    public String getSearchQuery() {
        return searchQuery;
    }

    public void setSearchQuery(String searchQuery) {
        this.searchQuery = searchQuery;
    }

    public LocalDate getMaxAdultBirthDate() {
        return maxAdultBirthDate;
    }

    public List<ClinicDTO> getClinics() {
        return clinics;
    }

    public String[] getSelectedClinicIds() {
        return selectedClinicIds;
    }

    public void setSelectedClinicIds(String[] selectedClinicIds) {
        this.selectedClinicIds = selectedClinicIds;
    }

    public String clinicName(String id) {
        if (id == null) {
            return "";
        }
        return clinicNameLookup.getOrDefault(id, id);
    }

    private Set<String> extractSelectedClinicIds() {
        if (selectedClinicIds == null || selectedClinicIds.length == 0) {
            return Collections.emptySet();
        }
        return new HashSet<>(Arrays.asList(selectedClinicIds));
    }

    private void refreshClinics() {
        List<ClinicDTO> fetchedClinics = clinicService.findAll();
        clinics = fetchedClinics != null ? fetchedClinics : new ArrayList<>();
        clinicNameLookup = clinics.stream()
                .filter(clinic -> clinic.getId() != null)
                .collect(Collectors.toMap(ClinicDTO::getId,
                        clinic -> clinic.getName() != null ? clinic.getName() : clinic.getId(),
                        (existing, ignored) -> existing,
                        HashMap::new));
    }
}
