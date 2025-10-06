package grupo12.practico.web.jsf;

import grupo12.practico.dtos.HealthUser.AddHealthUserDTO;
import grupo12.practico.dtos.HealthUser.HealthUserDTO;
import grupo12.practico.messaging.HealthUser.HealthUserRegistrationProducerLocal;
import grupo12.practico.models.DocumentType;
import grupo12.practico.models.Gender;
import grupo12.practico.services.HealthUser.HealthUserServiceLocal;
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

@Named("healthUserBean")
@ViewScoped
public class HealthUserBean implements Serializable {
    private static final long serialVersionUID = 1L;

    @EJB
    private HealthUserServiceLocal userService;

    @EJB
    private HealthUserRegistrationProducerLocal registrationProducer;

    private List<HealthUserDTO> users;
    private AddHealthUserDTO newUser;
    private String searchQuery;
    private LocalDate maxAdultBirthDate;

    @PostConstruct
    public void init() {
        newUser = new AddHealthUserDTO();
        users = new ArrayList<>();
        maxAdultBirthDate = LocalDate.now().minusYears(18);
        loadAll();
    }

    public void loadAll() {
        users = userService.findAll();
    }

    public void search() {
        if (searchQuery == null || searchQuery.trim().isEmpty()) {
            loadAll();
        } else {
            users = userService.findByName(searchQuery.trim());
        }
    }

    public String save() {
        try {
            registrationProducer.enqueue(newUser);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO,
                            "Request accepted; the user will be created shortly", null));
            newUser = new AddHealthUserDTO();
            return "list?faces-redirect=true";
        } catch (ValidationException ex) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage(), null));
            return null;
        } catch (RuntimeException ex) {
            String message = ex.getMessage() != null ? ex.getMessage()
                    : "Unexpected error while queueing the user creation request";
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

    public List<HealthUserDTO> getUsers() {
        return users;
    }

    public AddHealthUserDTO getNewUser() {
        return newUser;
    }

    public void setNewUser(AddHealthUserDTO newUser) {
        this.newUser = newUser;
    }

    public String getSearchQuery() {
        return searchQuery;
    }

    public void setSearchQuery(String searchQuery) {
        this.searchQuery = searchQuery;
    }

    public LocalDate getMaxAdultBirthDate() {
        return maxAdultBirthDate;
    }
}
