package grupo12.practico.web.jsf;

import grupo12.practico.dtos.Auth.OidcAuthorizationResponseDTO;
import grupo12.practico.services.Auth.OidcAuthenticationServiceLocal;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;

import java.io.IOException;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * JSF Managed Bean for handling OIDC authentication with gub.uy
 */
@Named("authBean")
@RequestScoped
public class AuthenticationBean implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(AuthenticationBean.class.getName());

    @EJB
    private OidcAuthenticationServiceLocal oidcAuthenticationService;

    private boolean configured;
    private String errorMessage;
    private boolean showHcenAdminError;

    @PostConstruct
    public void init() {
        checkConfiguration();
        checkForErrorParameters();
    }

    /**
     * Checks for error parameters in the request URL
     */
    private void checkForErrorParameters() {
        try {
            FacesContext facesContext = FacesContext.getCurrentInstance();
            if (facesContext != null) {
                ExternalContext externalContext = facesContext.getExternalContext();
                String error = externalContext.getRequestParameterMap().get("error");
                if ("hcen_admin_required".equals(error)) {
                    showHcenAdminError = true;
                    errorMessage = "Acceso denegado: el usuario no está registrado como administrador HCEN.";
                    LOGGER.info("Mostrando mensaje de acceso denegado HcenAdmin");
                } else {
                    showHcenAdminError = false;
                }
            }
        } catch (Exception e) {
            LOGGER.warning("Error verificando parámetros de error: " + e.getMessage());
            showHcenAdminError = false;
        }
    }

    /**
     * Checks if OIDC is properly configured
     */
    private void checkConfiguration() {
        try {
            // Try to initiate authorization to check if configured
            oidcAuthenticationService.initiateAuthorization();
            configured = true;
        } catch (IllegalStateException e) {
            configured = false;
            errorMessage = e.getMessage();
            LOGGER.warning("OIDC not configured: " + e.getMessage());
        } catch (Exception e) {
            configured = false;
            errorMessage = "Error checking OIDC configuration";
            LOGGER.log(Level.SEVERE, "Error checking OIDC configuration", e);
        }
    }

    /**
     * Initiates the OIDC login flow with gub.uy
     * This method will redirect the user to the gub.uy authorization page
     */
    public void loginWithGubUy() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ExternalContext externalContext = facesContext.getExternalContext();

        try {
            LOGGER.info("Initiating OIDC authorization flow");

            // Get authorization URL from the service
            OidcAuthorizationResponseDTO authResponse = oidcAuthenticationService.initiateAuthorization();

            LOGGER.info("Authorization URL generated, redirecting user to gub.uy");
            LOGGER.info("State: " + authResponse.getState());

            // Store state in session for later validation (optional, already stored in
            // service)
            externalContext.getSessionMap().put("oidc_state", authResponse.getState());

            // Redirect to gub.uy authorization URL
            externalContext.redirect(authResponse.getAuthorizationUrl());
            facesContext.responseComplete();

        } catch (IllegalStateException e) {
            LOGGER.log(Level.SEVERE, "OIDC not configured", e);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Authentication Error",
                            "OIDC authentication is not configured. Please contact the administrator."));
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to redirect to authorization URL", e);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Redirect Error",
                            "Failed to redirect to gub.uy. Please try again."));
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error initiating OIDC authorization", e);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Authentication Error",
                            "An error occurred while initiating authentication. Please try again."));
        }
    }

    /**
     * Returns whether OIDC is properly configured
     */
    public boolean isConfigured() {
        return configured;
    }

    /**
     * Returns the configuration error message
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * Returns whether to show the HcenAdmin error message
     */
    public boolean isShowHcenAdminError() {
        return showHcenAdminError;
    }

    /**
     * Logs out the current user by invalidating the session and redirecting to the
     * identity provider logout endpoint if available in session (set by REST
     * callback).
     */
    public void logout() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ExternalContext externalContext = facesContext.getExternalContext();
        try {
            Object logoutUrl = externalContext.getSessionMap().get("logout_url");
            externalContext.invalidateSession();
            if (logoutUrl != null) {
                externalContext.redirect(logoutUrl.toString());
            }
            facesContext.responseComplete();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to logout", e);
        }
    }
}
