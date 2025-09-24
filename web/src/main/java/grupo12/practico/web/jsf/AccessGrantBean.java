package grupo12.practico.web.jsf;

import grupo12.practico.models.AccessGrant;
import grupo12.practico.services.AccessGrant.AccessGrantServiceLocal;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Named("accessGrantBean")
@ViewScoped
public class AccessGrantBean implements Serializable {
    private static final long serialVersionUID = 1L;

    @EJB
    private AccessGrantServiceLocal service;

    private List<AccessGrant> grants;
    private AccessGrant newGrant;

    @PostConstruct
    public void init() {
        this.newGrant = new AccessGrant();
        this.grants = new ArrayList<>();
        loadAll();
    }

    public void loadAll() {
        grants = service.findAll();
    }

    public void reset() {
        newGrant = new AccessGrant();
    }

    public void save() {
        try {
            normalizeDates();
            service.grantAccess(newGrant);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Access grant created", null));
            reset();
            loadAll();
        } catch (RuntimeException ex) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage(), null));
        }
    }

    private void normalizeDates() {
        LocalDate startsAt = newGrant.getStartsAt();
        LocalDate endsAt = newGrant.getEndsAt();
        if (startsAt != null && endsAt != null && endsAt.isBefore(startsAt)) {
            throw new IllegalArgumentException("End date cannot be before start date");
        }
    }

    public void revoke(String grantId) {
        service.revokeAccess(grantId);
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Access grant revoked", null));
        loadAll();
    }

    public List<AccessGrant> getGrants() {
        return grants;
    }

    public AccessGrant getNewGrant() {
        return newGrant;
    }

    public void setNewGrant(AccessGrant newGrant) {
        this.newGrant = newGrant;
    }

    public List<String> getSubjectTypes() {
        return List.of("CLINIC", "SPECIALTY", "WORKER");
    }

    public List<String> getScopes() {
        return List.of("READ", "READ_WRITE", "ADMIN");
    }
}
