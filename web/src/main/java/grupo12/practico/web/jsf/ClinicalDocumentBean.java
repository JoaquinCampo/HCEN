package grupo12.practico.web.jsf;

import grupo12.practico.dtos.ClinicalDocument.AddClinicalDocumentDTO;
import grupo12.practico.dtos.ClinicalDocument.ClinicalDocumentDTO;
import grupo12.practico.dtos.HealthUser.HealthUserDTO;
import grupo12.practico.dtos.HealthWorker.HealthWorkerDTO;
import grupo12.practico.services.ClinicalDocument.ClinicalDocumentServiceLocal;
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
import java.util.List;

@Named("clinicalDocumentBean")
@ViewScoped
public class ClinicalDocumentBean implements Serializable {
    private static final long serialVersionUID = 1L;

    @EJB
    private ClinicalDocumentServiceLocal docService;
    @EJB
    private HealthUserServiceLocal userService;
    @EJB
    private HealthWorkerServiceLocal workerService;

    private List<ClinicalDocumentDTO> documents;
    private AddClinicalDocumentDTO newDocument;
    private String searchQuery;

    private String selectedPatientId;
    private String[] selectedHealthWorkerIds;

    private List<HealthUserDTO> users;
    private List<HealthWorkerDTO> workers;

    @PostConstruct
    public void init() {
        newDocument = new AddClinicalDocumentDTO();
        documents = new ArrayList<>();
        users = userService.findAll();
        workers = workerService.getAllHealthWorkers();
        loadAll();
    }

    public void loadAll() {
        documents = docService.findAll();
    }

    public void search() {
        if (searchQuery == null || searchQuery.trim().isEmpty()) {
            loadAll();
        } else {
            // For now, just load all documents since search methods are not available in
            // the simplified service
            loadAll();
        }
    }

    public String save() {
        try {
            if (selectedPatientId == null || selectedPatientId.isEmpty()) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Patient is required", null));
                return null;
            }
            if (selectedHealthWorkerIds == null || selectedHealthWorkerIds.length == 0) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "At least one health worker is required", null));
                return null;
            }

            // Set the IDs in the DTO
            newDocument.setClinicalHistoryId(selectedPatientId);
            newDocument.setHealthWorkerIds(java.util.Set.of(selectedHealthWorkerIds));

            docService.add(newDocument);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Clinical document created", null));

            newDocument = new AddClinicalDocumentDTO();
            selectedPatientId = null;
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

    public String getSelectedPatientId() {
        return selectedPatientId;
    }

    public void setSelectedPatientId(String id) {
        this.selectedPatientId = id;
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

}
