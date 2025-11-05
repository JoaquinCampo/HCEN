package grupo12.practico.web;

import grupo12.practico.dtos.Auth.OidcAuthorizationResponseDTO;
import grupo12.practico.services.Auth.OidcAuthenticationServiceLocal;
import grupo12.practico.services.Auth.OidcConfigurationService;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;
import jakarta.faces.context.FacesContext;

@Named("gubuyAuthBean")
@RequestScoped
public class AuthBean {

    @EJB
    private OidcAuthenticationServiceLocal oidcService;
    @EJB
    private OidcConfigurationService oidcConfig;

    private String errorMessage;

    public boolean isConfigured() {
        return oidcConfig != null && oidcConfig.isConfigured();
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void loginWithGubUy() {
        try {
            OidcAuthorizationResponseDTO resp = oidcService.initiateAuthorization();
            FacesContext.getCurrentInstance().getExternalContext().redirect(resp.getAuthorizationUrl());
        } catch (Exception e) {
            this.errorMessage = e.getMessage();
        }
    }
}
