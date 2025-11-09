package grupo12.practico.web.jsf;

import grupo12.practico.dtos.HcenAdmin.AddHcenAdminDTO;
import grupo12.practico.dtos.HcenAdmin.HcenAdminDTO;
import grupo12.practico.services.HcenAdmin.HcenAdminServiceLocal;
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

@Named("hcenAdminBean")
@ViewScoped
public class HcenAdminBean implements Serializable {
    private static final long serialVersionUID = 1L;

    @EJB
    private HcenAdminServiceLocal hcenAdminService;

    private List<HcenAdminDTO> admins;
    private AddHcenAdminDTO newAdmin;
    private String searchQuery;

    @PostConstruct
    public void init() {
        try {
            newAdmin = new AddHcenAdminDTO();
            newAdmin.setDateOfBirth(null); // Explicitly set to null to avoid conversion issues
            admins = new ArrayList<>();
            loadAll();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadAll() {
        admins = hcenAdminService.findAll();
    }

    public void search() {
        if (searchQuery == null || searchQuery.trim().isEmpty()) {
            loadAll();
        } else {
            loadAll();
        }
    }

    public String save() {
        try {
            hcenAdminService.create(newAdmin);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO,
                            "HCEN Admin created successfully", null));
            newAdmin = new AddHcenAdminDTO();
            loadAll();
            return "list?faces-redirect=true";
        } catch (ValidationException ex) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage(), null));
            return null;
        } catch (RuntimeException ex) {
            String message = ex.getMessage() != null ? ex.getMessage()
                    : "Unexpected error while creating the HCEN admin";
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, message, null));
            return null;
        }
    }

    public List<HcenAdminDTO> getAdmins() {
        return admins;
    }

    public AddHcenAdminDTO getNewAdmin() {
        return newAdmin;
    }

    public void setNewAdmin(AddHcenAdminDTO newAdmin) {
        this.newAdmin = newAdmin;
    }

    public String getSearchQuery() {
        return searchQuery;
    }

    public void setSearchQuery(String searchQuery) {
        this.searchQuery = searchQuery;
    }
}