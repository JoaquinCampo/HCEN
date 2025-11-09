package grupo12.practico.web.jsf;

import grupo12.practico.dtos.Clinic.AddClinicDTO;
import grupo12.practico.dtos.Clinic.ClinicAdminDTO;
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

    private List<ClinicDTO> clinics;
    private AddClinicDTO newClinic;
    private String searchQuery;
    private String providerName;

    @PostConstruct
    public void init() {
        try {
            newClinic = new AddClinicDTO();
            newClinic.setClinicAdmin(new ClinicAdminDTO());
            newClinic.setProviderName(providerName);
            clinics = new ArrayList<>();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void loadClinics() {
        if (providerName != null && !providerName.trim().isEmpty()) {
            loadAll();
        } else {
            clinics = new ArrayList<>();
        }
    }

    public void loadAll() {
        clinics = service.findAll();
    }

    public void search() {
        if (searchQuery == null || searchQuery.trim().isEmpty()) {
            loadClinics();
        } else {
            ClinicDTO clinic = service.findByName(searchQuery.trim());
            clinics = new ArrayList<>();
            if (clinic != null) {
                clinics.add(clinic);
            }
        }
    }

    public String save() {
        try {
            newClinic.setProviderName(providerName);

            registrationProducer.enqueue(newClinic);

            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO,
                            "Request accepted; the clinic will be created shortly", null));
            newClinic = new AddClinicDTO();
            newClinic.setClinicAdmin(new ClinicAdminDTO());
            // Preserve providerName for next clinic creation
            newClinic.setProviderName(providerName);
            loadClinics();

            if (providerName != null && !providerName.trim().isEmpty()) {
                return "/provider/detail?faces-redirect=true&name=" + providerName;
            }
            return null;
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

    public List<ClinicDTO> getclinics() {
        return clinics;
    }

    public AddClinicDTO getnewClinic() {
        return newClinic;
    }

    public void setnewClinic(AddClinicDTO hp) {
        this.newClinic = hp;
        if (this.newClinic != null && this.newClinic.getClinicAdmin() == null) {
            this.newClinic.setClinicAdmin(new ClinicAdminDTO());
        }
    }

    public String getSearchQuery() {
        return searchQuery;
    }

    public void setSearchQuery(String q) {
        this.searchQuery = q;
    }

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
        if (newClinic != null) {
            newClinic.setProviderName(providerName);
        }
        // Load clinics when providerName is set
        loadClinics();
    }
}
