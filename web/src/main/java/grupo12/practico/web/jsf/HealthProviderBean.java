package grupo12.practico.web.jsf;

import grupo12.practico.models.HealthProvider;
import grupo12.practico.services.HealthProvider.HealthProviderServiceLocal;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Named("healthProviderBean")
@ViewScoped
public class HealthProviderBean implements Serializable {
    private static final long serialVersionUID = 1L;

    @EJB
    private HealthProviderServiceLocal service;

    private List<HealthProvider> providers;
    private HealthProvider newProvider;
    private String searchQuery;

    @PostConstruct
    public void init() {
        newProvider = new HealthProvider();
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
            service.addHealthProvider(newProvider);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Health Provider created", null));
            newProvider = new HealthProvider();
            return "list?faces-redirect=true";
        } catch (RuntimeException ex) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage(), null));
            return null;
        }
    }

    public List<HealthProvider> getProviders() {
        return providers;
    }

    public HealthProvider getNewProvider() {
        return newProvider;
    }

    public void setNewProvider(HealthProvider hp) {
        this.newProvider = hp;
    }

    public String getSearchQuery() {
        return searchQuery;
    }

    public void setSearchQuery(String q) {
        this.searchQuery = q;
    }
}
