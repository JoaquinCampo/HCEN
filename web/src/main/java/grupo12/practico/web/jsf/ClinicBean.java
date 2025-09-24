package grupo12.practico.web.jsf;

import grupo12.practico.models.Clinic;
import grupo12.practico.models.ClinicType;
import grupo12.practico.services.Clinic.ClinicServiceLocal;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Named("clinicBean")
@ViewScoped
public class ClinicBean implements Serializable {
    private static final long serialVersionUID = 1L;

    @EJB
    private ClinicServiceLocal service;

    private List<Clinic> providers;
    private Clinic newProvider;
    private String searchQuery;

    @PostConstruct
    public void init() {
        newProvider = new Clinic();
        providers = new ArrayList<>();
        loadAll();
    }

    public void loadAll() {
        providers = service.findAll();
    }

    public void search() {
        if (searchQuery == null || searchQuery.trim().isEmpty()) {
            loadAll();
        } else {
            providers = service.findByName(searchQuery.trim());
        }
    }

    public String save() {
        try {
            service.addClinic(newProvider);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Clinic created", null));
            newProvider = new Clinic();
            return "list?faces-redirect=true";
        } catch (RuntimeException ex) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage(), null));
            return null;
        }
    }

    public List<Clinic> getProviders() {
        return providers;
    }

    public Clinic getNewProvider() {
        return newProvider;
    }

    public void setNewProvider(Clinic hp) {
        this.newProvider = hp;
    }

    public String getSearchQuery() {
        return searchQuery;
    }

    public void setSearchQuery(String q) {
        this.searchQuery = q;
    }

    public ClinicType[] getClinicTypes() {
        return ClinicType.values();
    }
}
