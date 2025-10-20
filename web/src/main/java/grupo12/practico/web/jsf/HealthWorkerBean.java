package grupo12.practico.web.jsf;

import grupo12.practico.dtos.Clinic.ClinicDTO;
import grupo12.practico.dtos.HealthWorker.AddHealthWorkerDTO;
import grupo12.practico.dtos.HealthWorker.HealthWorkerDTO;
import grupo12.practico.models.DocumentType;
import grupo12.practico.models.Gender;
import grupo12.practico.services.HealthWorker.HealthWorkerServiceLocal;
import grupo12.practico.services.Clinic.ClinicServiceLocal;
import grupo12.practico.messaging.HealthWorker.HealthWorkerRegistrationProducerLocal;
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

@Named("healthWorkerBean")
@ViewScoped
public class HealthWorkerBean implements Serializable {
    private static final long serialVersionUID = 1L;

    @EJB
    private HealthWorkerServiceLocal service;

    @EJB
    private HealthWorkerRegistrationProducerLocal registrationProducer;

    @EJB
    private ClinicServiceLocal clinicService;

    private List<HealthWorkerDTO> workers;
    private AddHealthWorkerDTO newWorker;
    private String searchQuery;
    private LocalDate maxAdultBirthDate;
    private List<ClinicDTO> clinics;
    private String[] selectedClinicIds;
    private Map<String, String> clinicNameLookup;
    private static final List<String> BLOOD_TYPES = List.of("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-");

    @PostConstruct
    public void init() {
        newWorker = new AddHealthWorkerDTO();
        workers = new ArrayList<>();
        clinics = new ArrayList<>();
        clinicNameLookup = new HashMap<>();
        selectedClinicIds = null;
        maxAdultBirthDate = LocalDate.now().minusYears(18);
        refreshClinics();
        loadAll();
    }

    public void loadAll() {
        workers = service.findAll();
    }

    public void search() {
        if (searchQuery == null || searchQuery.trim().isEmpty()) {
            loadAll();
        } else {
            workers = service.findByName(searchQuery.trim());
        }
    }

    public String save() {
        try {
            if (newWorker.getBloodType() != null) {
                newWorker.setBloodType(newWorker.getBloodType().trim().toUpperCase());
            }
            newWorker.setClinicIds(extractSelectedClinicIds());
            registrationProducer.enqueue(newWorker);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO,
                            "Request accepted; the health worker will be created shortly", null));
            newWorker = new AddHealthWorkerDTO();
            selectedClinicIds = null;
            return "list?faces-redirect=true";
        } catch (ValidationException ex) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage(), null));
            return null;
        } catch (RuntimeException ex) {
            String message = ex.getMessage() != null ? ex.getMessage()
                    : "Unexpected error while queueing the health worker creation request";
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

    public List<HealthWorkerDTO> getWorkers() {
        return workers;
    }

    public AddHealthWorkerDTO getNewWorker() {
        return newWorker;
    }

    public void setNewWorker(AddHealthWorkerDTO hw) {
        this.newWorker = hw;
    }

    public String getSearchQuery() {
        return searchQuery;
    }

    public void setSearchQuery(String q) {
        this.searchQuery = q;
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

    public List<String> getBloodTypes() {
        return BLOOD_TYPES;
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
