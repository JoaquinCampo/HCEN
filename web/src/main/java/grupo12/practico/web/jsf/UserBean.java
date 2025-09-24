package grupo12.practico.web.jsf;

import grupo12.practico.models.DocumentType;
import grupo12.practico.models.Gender;
import grupo12.practico.models.HealthUser;
import grupo12.practico.services.HealthUser.HealthUserServiceLocal;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Named("userBean")
@ViewScoped
public class UserBean implements Serializable {
    private static final long serialVersionUID = 1L;

    @EJB
    private HealthUserServiceLocal userService;

    private List<HealthUser> users;
    private HealthUser newUser;
    private String searchQuery;
    private String password;

    @PostConstruct
    public void init() {
        newUser = new HealthUser();
        users = new ArrayList<>();
        loadAll();
    }

    public void loadAll() {
        users = userService.getAllUsers();
    }

    public void search() {
        if (searchQuery == null || searchQuery.trim().isEmpty()) {
            loadAll();
        } else {
            users = userService.findUsersByName(searchQuery.trim());
        }
    }

    public String save() {
        try {
            if (password != null && !password.isBlank()) {
                newUser.setPassword(password);
            }
            userService.addUser(newUser);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "User created successfully", null));
            newUser = new HealthUser();
            password = null;
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

    public DocumentType[] getDocumentTypes() {
        return DocumentType.values();
    }

    // Getters and setters
    public List<HealthUser> getUsers() {
        return users;
    }

    public HealthUser getNewUser() {
        return newUser;
    }

    public void setNewUser(HealthUser newUser) {
        this.newUser = newUser;
    }

    public String getSearchQuery() {
        return searchQuery;
    }

    public void setSearchQuery(String searchQuery) {
        this.searchQuery = searchQuery;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
