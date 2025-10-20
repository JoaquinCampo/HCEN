package grupo12.practico.web.jsf;

import grupo12.practico.dtos.Clinic.AddClinicDTO;
import grupo12.practico.dtos.Clinic.ClinicAdminInfoDTO;
import grupo12.practico.dtos.Clinic.ClinicDTO;
import grupo12.practico.messaging.Clinic.ClinicRegistrationProducerLocal;
import grupo12.practico.services.Clinic.ClinicServiceLocal;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import jakarta.validation.ValidationException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Named("clinicBean")
@ViewScoped
public class ClinicBean implements Serializable {
    private static final long serialVersionUID = 1L;

    @EJB
    private ClinicServiceLocal service;

    @EJB
    private ClinicRegistrationProducerLocal registrationProducer;

    private List<ClinicDTO> providers;
    private AddClinicDTO newProvider;
    private String searchQuery;

    @PostConstruct
    public void init() {
        newProvider = new AddClinicDTO();
        newProvider.setClinicAdmin(new ClinicAdminInfoDTO());
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
            registrationProducer.enqueue(newProvider);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO,
                            "Request accepted; the clinic will be created shortly", null));
            newProvider = new AddClinicDTO();
            newProvider.setClinicAdmin(new ClinicAdminInfoDTO());
            return "list?faces-redirect=true";
        } catch (ValidationException ex) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage(), null));
            return null;
        } catch (RuntimeException ex) {
            String message = ex.getMessage() != null ? ex.getMessage()
                    : "Unexpected error while queueing the clinic creation request";
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, message, null));
            return null;
        }
    }

    public List<ClinicDTO> getProviders() {
        return providers;
    }

    public AddClinicDTO getNewProvider() {
        return newProvider;
    }

    public void setNewProvider(AddClinicDTO hp) {
        this.newProvider = hp;
        if (this.newProvider != null && this.newProvider.getClinicAdmin() == null) {
            this.newProvider.setClinicAdmin(new ClinicAdminInfoDTO());
        }
    }

    public String getSearchQuery() {
        return searchQuery;
    }

    public void setSearchQuery(String q) {
        this.searchQuery = q;
    }
}
