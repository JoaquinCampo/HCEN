package grupo12.practico.web.jsf;

import grupo12.practico.models.*;
import grupo12.practico.services.ClinicalDocument.ClinicalDocumentServiceLocal;
import grupo12.practico.services.HealthProvider.HealthProviderServiceLocal;
import grupo12.practico.services.HealthWorker.HealthWorkerServiceLocal;
import grupo12.practico.services.User.UserServiceLocal;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Named("clinicalDocumentBean")
@ViewScoped
public class ClinicalDocumentBean implements Serializable {
    private static final long serialVersionUID = 1L;

    @EJB
    private ClinicalDocumentServiceLocal docService;
    @EJB
    private UserServiceLocal userService;
    @EJB
    private HealthWorkerServiceLocal workerService;
    @EJB
    private HealthProviderServiceLocal providerService;

    private List<ClinicalDocument> documents;
    private ClinicalDocument newDocument;
    private String searchQuery;

    private String selectedPatientId;
    private String selectedAuthorId;
    private String selectedProviderId;

    private List<User> users;
    private List<HealthWorker> workers;
    private List<HealthProvider> providers;

    @PostConstruct
    public void init() {
        newDocument = new ClinicalDocument();
        documents = new ArrayList<>();
        users = userService.findAll();
        workers = workerService.getAllHealthWorkers();
        providers = providerService.findAll();
        loadAll();
    }

    public void loadAll() {
        documents = docService.getAllDocuments();
    }

    public void search() {
        if (searchQuery == null || searchQuery.trim().isEmpty()) {
            loadAll();
        } else {
            documents = docService.searchByAnyName(searchQuery.trim());
        }
    }

    public String save() {
        try {
            User patient = selectedPatientId != null && !selectedPatientId.isEmpty()
                    ? userService.findById(selectedPatientId)
                    : null;
            if (patient == null) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Patient is required", null));
                return null;
            }
            ClinicalHistory history = new ClinicalHistory();
            history.setPatient(patient);
            newDocument.setClinicalHistory(history);

            if (selectedAuthorId != null && !selectedAuthorId.isEmpty()) {
                newDocument.setAuthor(workerService.findById(selectedAuthorId));
            }
            if (selectedProviderId != null && !selectedProviderId.isEmpty()) {
                newDocument.setProvider(providerService.findById(selectedProviderId));
            }

            docService.addClinicalDocument(newDocument);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Clinical document created", null));

            newDocument = new ClinicalDocument();
            selectedPatientId = selectedAuthorId = selectedProviderId = null;
            return "list?faces-redirect=true";
        } catch (RuntimeException ex) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage(), null));
            return null;
        }
    }

    // getters/setters
    public List<ClinicalDocument> getDocuments() {
        return documents;
    }

    public ClinicalDocument getNewDocument() {
        return newDocument;
    }

    public void setNewDocument(ClinicalDocument doc) {
        this.newDocument = doc;
    }

    public String getSearchQuery() {
        return searchQuery;
    }

    public void setSearchQuery(String q) {
        this.searchQuery = q;
    }

    public String getSelectedPatientId() {
        return selectedPatientId;
    }

    public void setSelectedPatientId(String id) {
        this.selectedPatientId = id;
    }

    public String getSelectedAuthorId() {
        return selectedAuthorId;
    }

    public void setSelectedAuthorId(String id) {
        this.selectedAuthorId = id;
    }

    public String getSelectedProviderId() {
        return selectedProviderId;
    }

    public void setSelectedProviderId(String id) {
        this.selectedProviderId = id;
    }

    public List<User> getUsers() {
        return users;
    }

    public List<HealthWorker> getWorkers() {
        return workers;
    }

    public List<HealthProvider> getProviders() {
        return providers;
    }
}
