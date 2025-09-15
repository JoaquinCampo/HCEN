package grupo12.practico.web.jsf;

import grupo12.practico.models.Gender;
import grupo12.practico.models.HealthWorker;
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

@Named("healthWorkerBean")
@ViewScoped
public class HealthWorkerBean implements Serializable {
    private static final long serialVersionUID = 1L;

    @EJB
    private HealthWorkerServiceLocal service;

    private List<HealthWorker> workers;
    private HealthWorker newWorker;
    private String searchQuery;

    @PostConstruct
    public void init() {
        newWorker = new HealthWorker();
        workers = new ArrayList<>();
        loadAll();
    }

    public void loadAll() {
        workers = service.getAllHealthWorkers();
    }

    public void search() {
        if (searchQuery == null || searchQuery.trim().isEmpty()) {
            loadAll();
        } else {
            workers = service.findHealthWorkersByName(searchQuery.trim());
        }
    }

    public String save() {
        try {
            service.addHealthWorker(newWorker);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Health Worker created", null));
            newWorker = new HealthWorker();
            return "list?faces-redirect=true";
        } catch (RuntimeException ex) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage(), null));
            return null;
        }
    }

    public Gender[] getGenders() {
        return Gender.values();
    }

    public List<HealthWorker> getWorkers() {
        return workers;
    }

    public HealthWorker getNewWorker() {
        return newWorker;
    }

    public void setNewWorker(HealthWorker hw) {
        this.newWorker = hw;
    }

    public String getSearchQuery() {
        return searchQuery;
    }

    public void setSearchQuery(String q) {
        this.searchQuery = q;
    }
}
