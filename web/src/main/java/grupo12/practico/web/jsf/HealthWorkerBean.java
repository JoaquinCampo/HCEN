package grupo12.practico.web.jsf;

import grupo12.practico.dtos.HealthWorker.AddHealthWorkerDTO;
import grupo12.practico.dtos.HealthWorker.HealthWorkerDTO;
import grupo12.practico.models.DocumentType;
import grupo12.practico.models.Gender;
import grupo12.practico.services.HealthWorker.HealthWorkerServiceLocal;
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
import java.util.List;

@Named("healthWorkerBean")
@ViewScoped
public class HealthWorkerBean implements Serializable {
    private static final long serialVersionUID = 1L;

    @EJB
    private HealthWorkerServiceLocal service;

    @EJB
    private HealthWorkerRegistrationProducerLocal registrationProducer;

    private List<HealthWorkerDTO> workers;
    private AddHealthWorkerDTO newWorker;
    private String searchQuery;
    private LocalDate maxAdultBirthDate;

    @PostConstruct
    public void init() {
        newWorker = new AddHealthWorkerDTO();
        workers = new ArrayList<>();
        maxAdultBirthDate = LocalDate.now().minusYears(18);
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
            registrationProducer.enqueue(newWorker);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO,
                            "Request accepted; the health worker will be created shortly", null));
            newWorker = new AddHealthWorkerDTO();
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
}
