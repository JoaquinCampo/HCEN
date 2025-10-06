package grupo12.practico.web.jsf;

import grupo12.practico.dtos.ClinicalDocument.AddClinicalDocumentDTO;
import grupo12.practico.dtos.ClinicalDocument.ClinicalDocumentDTO;
import grupo12.practico.dtos.HealthUser.HealthUserDTO;
import grupo12.practico.dtos.HealthWorker.HealthWorkerDTO;
import grupo12.practico.services.ClinicalDocument.ClinicalDocumentServiceLocal;
import grupo12.practico.messaging.ClinicalDocument.ClinicalDocumentRegistrationProducerLocal;
import grupo12.practico.services.HealthUser.HealthUserServiceLocal;
import grupo12.practico.services.HealthWorker.HealthWorkerServiceLocal;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Named("clinicalDocumentBean")
@ViewScoped
public class ClinicalDocumentBean implements Serializable {
    private static final long serialVersionUID = 1L;

    @EJB
    private ClinicalDocumentServiceLocal docService;
    @EJB
    private ClinicalDocumentRegistrationProducerLocal registrationProducer;
    @EJB
    private HealthUserServiceLocal userService;
    @EJB
    private HealthWorkerServiceLocal workerService;

    private List<ClinicalDocumentDTO> documents;
    private AddClinicalDocumentDTO newDocument;
    private String searchQuery;

    private String selectedHealthUserId;
    private String[] selectedHealthWorkerIds;

    private List<HealthUserDTO> users;
    private List<HealthWorkerDTO> workers;
    private Map<String, String> healthUserLookup;
    private Map<String, String> healthWorkerLookup;

    @PostConstruct
    public void init() {
        newDocument = new AddClinicalDocumentDTO();
        documents = new ArrayList<>();
        healthUserLookup = new HashMap<>();
        healthWorkerLookup = new HashMap<>();
        refreshParticipants();
        loadAll();
    }

    public void loadAll() {
        documents = docService.findAll();
    }

    public void search() {
        if (searchQuery == null || searchQuery.trim().isEmpty()) {
            loadAll();
        } else {
            documents = docService.findByTitle(searchQuery.trim());
        }
    }

    public String save() {
        try {
            if (selectedHealthUserId == null || selectedHealthUserId.isEmpty()) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Health user is required", null));
                return null;
            }
            if (selectedHealthWorkerIds == null || selectedHealthWorkerIds.length == 0) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "At least one health worker is required", null));
                return null;
            }

            // Set the IDs in the DTO
            newDocument.setClinicalHistoryId(selectedHealthUserId);
            newDocument.setHealthWorkerIds(java.util.Set.of(selectedHealthWorkerIds));

            registrationProducer.enqueue(newDocument);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO,
                            "Request accepted; the clinical document will be created shortly", null));

            newDocument = new AddClinicalDocumentDTO();
            selectedHealthUserId = null;
            selectedHealthWorkerIds = null;
            return "list?faces-redirect=true";
        } catch (RuntimeException ex) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage(), null));
            return null;
        }
    }

    // getters/setters
    public List<ClinicalDocumentDTO> getDocuments() {
        return documents;
    }

    public AddClinicalDocumentDTO getNewDocument() {
        return newDocument;
    }

    public void setNewDocument(AddClinicalDocumentDTO doc) {
        this.newDocument = doc;
    }

    public String getSearchQuery() {
        return searchQuery;
    }

    public void setSearchQuery(String q) {
        this.searchQuery = q;
    }

    public String getSelectedHealthUserId() {
        return selectedHealthUserId;
    }

    public void setSelectedHealthUserId(String id) {
        this.selectedHealthUserId = id;
    }

    public String[] getSelectedHealthWorkerIds() {
        return selectedHealthWorkerIds;
    }

    public void setSelectedHealthWorkerIds(String[] ids) {
        this.selectedHealthWorkerIds = ids;
    }

    public List<HealthUserDTO> getUsers() {
        return users;
    }

    public List<HealthWorkerDTO> getWorkers() {
        return workers;
    }

    public String healthUserName(String clinicalHistoryId) {
        if (clinicalHistoryId == null) {
            return "";
        }
        return healthUserLookup.getOrDefault(clinicalHistoryId, clinicalHistoryId);
    }

    public String healthWorkerName(String workerId) {
        if (workerId == null) {
            return "";
        }
        return healthWorkerLookup.getOrDefault(workerId, workerId);
    }

    private void refreshParticipants() {
        List<HealthUserDTO> fetchedUsers = userService.findAll();
        users = fetchedUsers != null ? fetchedUsers : new ArrayList<>();
        List<HealthWorkerDTO> fetchedWorkers = workerService.findAll();
        workers = fetchedWorkers != null ? fetchedWorkers : new ArrayList<>();

        healthUserLookup = new HashMap<>();
        for (HealthUserDTO user : users) {
            if (user.getClinicalHistoryId() == null) {
                continue;
            }
            String display = buildHealthUserDisplay(user);
            healthUserLookup.putIfAbsent(user.getClinicalHistoryId(), display);
        }

        healthWorkerLookup = new HashMap<>();
        for (HealthWorkerDTO worker : workers) {
            if (worker.getId() == null) {
                continue;
            }
            String display = buildHealthWorkerDisplay(worker);
            healthWorkerLookup.putIfAbsent(worker.getId(), display);
        }
    }

    private String buildHealthUserDisplay(HealthUserDTO user) {
        StringBuilder builder = new StringBuilder();
        if (user.getLastName() != null && !user.getLastName().isBlank()) {
            builder.append(user.getLastName());
        }
        if (user.getFirstName() != null && !user.getFirstName().isBlank()) {
            if (builder.length() > 0) {
                builder.append(", ");
            }
            builder.append(user.getFirstName());
        }
        if (builder.length() == 0 && user.getClinicalHistoryId() != null) {
            builder.append(user.getClinicalHistoryId());
        }
        if (user.getDocument() != null && !user.getDocument().isBlank()) {
            builder.append(" (" + user.getDocument() + ")");
        }
        return builder.toString();
    }

    private String buildHealthWorkerDisplay(HealthWorkerDTO worker) {
        StringBuilder builder = new StringBuilder();
        if (worker.getLastName() != null && !worker.getLastName().isBlank()) {
            builder.append(worker.getLastName());
        }
        if (worker.getFirstName() != null && !worker.getFirstName().isBlank()) {
            if (builder.length() > 0) {
                builder.append(", ");
            }
            builder.append(worker.getFirstName());
        }
        if (builder.length() == 0 && worker.getId() != null) {
            builder.append(worker.getId());
        }
        if (worker.getLicenseNumber() != null && !worker.getLicenseNumber().isBlank()) {
            builder.append(" (" + worker.getLicenseNumber() + ")");
        }
        return builder.toString();
    }

}
