package grupo12.practico.rest;

import grupo12.practico.dtos.Auth.OidcAuthorizationResponseDTO;
import grupo12.practico.dtos.Auth.OidcAuthResultDTO;
import grupo12.practico.services.Auth.OidcAuthenticationServiceLocal;
import jakarta.ejb.EJB;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Context;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * REST endpoint for OpenID Connect authentication with gub.uy
 */
@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthenticationResource {

    private static final Logger LOGGER = Logger.getLogger(AuthenticationResource.class.getName());

    @EJB
    private OidcAuthenticationServiceLocal oidcAuthenticationService;

    @Context
    private HttpServletRequest request;

    /**
     * Initiates the OIDC authorization flow with gub.uy
     * 
     * POST /api/auth/gubuy/authorize
     * 
     * @return Authorization URL and state
     */
    @POST
    @Path("/gubuy/authorize")
    public Response initiateGubuyAuth() {
        try {
            OidcAuthorizationResponseDTO authResponse = oidcAuthenticationService.initiateAuthorization();

            LOGGER.info("Authorization initiated with state: " + authResponse.getState());

            return Response.ok(authResponse).build();

        } catch (IllegalStateException e) {
            LOGGER.log(Level.SEVERE, "OIDC not configured", e);
            return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                    .entity("{\"error\":\"OIDC authentication is not configured. " + e.getMessage() + "\"}")
                    .build();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error initiating authorization", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"Failed to initiate authorization: " + e.getMessage() + "\"}")
                    .build();
        }
    }

    /**
     * Handles the OIDC callback from gub.uy
     * 
     * GET /api/auth/gubuy/callback?code=xxx&state=xxx
     * 
     * This endpoint receives the authorization code from gub.uy and exchanges it
     * for tokens
     * 
     * @param code  Authorization code from gub.uy
     * @param state State parameter for CSRF protection
     * @return Authentication result with JWT and user info
     */
    @GET
    @Path("/gubuy/callback")
    public Response handleGubuyCallback(
            @QueryParam("code") String code,
            @QueryParam("state") String state,
            @QueryParam("error") String error,
            @QueryParam("error_description") String errorDescription) {

        // Check for errors from the authorization server
        if (error != null) {
            LOGGER.severe("Authorization error: " + error + " - " + errorDescription);
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"Authorization failed\",\"description\":\"" + errorDescription + "\"}")
                    .build();
        }

        // Validate required parameters
        if (code == null || code.isEmpty()) {
            LOGGER.warning("Missing authorization code in callback");
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"Missing authorization code\"}")
                    .build();
        }

        if (state == null || state.isEmpty()) {
            LOGGER.warning("Missing state parameter in callback");
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"Missing state parameter\"}")
                    .build();
        }

        try {
            OidcAuthResultDTO authResult = oidcAuthenticationService.handleCallback(code, state);

            // Persist minimal auth context into session
            HttpSession session = request.getSession(true);
            session.setAttribute("authenticated", Boolean.TRUE);
            session.setAttribute("id_token", authResult.getIdToken());
            session.setAttribute("access_token", authResult.getAccessToken());
            session.setAttribute("user_info", authResult.getUserInfo());
            session.setAttribute("id_token_claims", authResult.getIdTokenClaims());
            session.setAttribute("logout_url", authResult.getLogoutUrl());

            LOGGER.info("Authentication successful; session created with user "
                    + (authResult.getUserInfo() != null ? authResult.getUserInfo().getEmail() : "unknown"));

            // If request is coming from a browser (default), redirect to home instead of
            // returning JSON
            String accept = request.getHeader("Accept");
            boolean wantsJson = accept != null && accept.contains("application/json") && !accept.contains("text/html");

            if (!wantsJson) {
                String context = request.getContextPath();
                String path = (context == null || context.isEmpty()) ? "/" : context + "/";
                String absolute = request.getScheme() + "://" + request.getServerName()
                        + ":" + request.getServerPort() + path;
                return Response.seeOther(java.net.URI.create(absolute)).build();
            }

            return Response.ok(authResult).build();

        } catch (IllegalArgumentException e) {
            LOGGER.log(Level.WARNING, "Invalid state parameter", e);
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"Invalid state parameter. Possible CSRF attack or expired session.\"}")
                    .build();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Authentication failed", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"Authentication failed: " + e.getMessage() + "\"}")
                    .build();
        }
    }

    /**
     * Returns the current authenticated user session info
     */
    @GET
    @Path("/me")
    public Response me() {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("authenticated") == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"error\":\"Not authenticated\"}").build();
        }
        Object userInfo = session.getAttribute("user_info");
        return Response.ok(userInfo).build();
    }

    /**
     * Logs out the local session and redirects to the provider logout endpoint.
     */
    @GET
    @Path("/gubuy/logout")
    public Response logout() {
        HttpSession session = request.getSession(false);
        String idToken = null;
        String logoutUrl = null;
        if (session != null) {
            Object it = session.getAttribute("id_token");
            if (it != null)
                idToken = it.toString();
            Object lu = session.getAttribute("logout_url");
            if (lu != null)
                logoutUrl = lu.toString();
            session.invalidate();
        }

        if (logoutUrl == null && idToken != null) {
            logoutUrl = oidcAuthenticationService.buildLogoutUrl(idToken);
        }

        if (logoutUrl != null) {
            return Response.seeOther(java.net.URI.create(logoutUrl)).build();
        }

        String context = request.getContextPath();
        String path = (context == null || context.isEmpty()) ? "/" : context + "/";
        String absolute = request.getScheme() + "://" + request.getServerName()
                + ":" + request.getServerPort() + path;
        return Response.seeOther(java.net.URI.create(absolute)).build();
    }

}
