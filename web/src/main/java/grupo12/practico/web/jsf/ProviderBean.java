package grupo12.practico.web.jsf;

import grupo12.practico.dtos.Provider.ProviderDTO;
import grupo12.practico.dtos.Clinic.ClinicDTO;
import grupo12.practico.dtos.Provider.AddProviderDTO;
import grupo12.practico.services.Provider.ProviderServiceLocal;
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

@Named("providerBean")
@ViewScoped
public class ProviderBean implements Serializable {
    private static final long serialVersionUID = 1L;

    @EJB
    private ProviderServiceLocal providerService;

    private AddProviderDTO newProvider;
    private List<ProviderDTO> providers;
    private ProviderDTO selectedProvider;
    private List<ClinicDTO> providerClinics;
    private String providerName;

    @PostConstruct
    public void init() {
        newProvider = new AddProviderDTO();
        providers = new ArrayList<>();
        providerClinics = new ArrayList<>();
        loadAll();
    }

    public void loadAll() {
        providers = providerService.findAll();
    }

    public void loadProviderByName() {
        if (providerName == null || providerName.trim().isEmpty()) {
            selectedProvider = null;
            providerClinics = new ArrayList<>();
            return;
        }

        selectedProvider = providerService.findByName(providerName.trim());
        providerClinics = new ArrayList<>();

        if (selectedProvider != null) {
            // Fetch clinics from external API
            providerClinics = providerService.fetchClinicsByProvider(selectedProvider.getProviderName());
        }
    }

    public String save() {
        try {
            providerService.create(newProvider);

            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO,
                            "Provider created successfully", null));

            newProvider = new AddProviderDTO();
            loadAll();

            return null;
        } catch (ValidationException ex) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage(), null));
            return null;
        } catch (RuntimeException ex) {
            String message = ex.getMessage() != null ? ex.getMessage()
                    : "Unexpected error while creating the provider";
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, message, null));
            return null;
        }
    }

    public List<ProviderDTO> getProviders() {
        return providers;
    }

    public ProviderDTO getSelectedProvider() {
        return selectedProvider;
    }

    public List<ClinicDTO> getProviderClinics() {
        return providerClinics;
    }

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    public AddProviderDTO getNewProvider() {
        return newProvider;
    }

    public void setNewProvider(AddProviderDTO newProvider) {
        this.newProvider = newProvider;
    }
}
