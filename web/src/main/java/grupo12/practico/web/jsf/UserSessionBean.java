package grupo12.practico.web.jsf;

import grupo12.practico.dtos.Auth.OidcUserInfoDTO;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import java.util.Map;

@Named("userSession")
@RequestScoped
public class UserSessionBean {

    private ExternalContext ext() {
        return FacesContext.getCurrentInstance().getExternalContext();
    }

    public boolean isAuthenticated() {
        Object auth = ext().getSessionMap().get("authenticated");
        return Boolean.TRUE.equals(auth);
    }

    public String getDisplayName() {
        Object ui = ext().getSessionMap().get("user_info");
        if (ui == null)
            return null;
        if (ui instanceof OidcUserInfoDTO) {
            OidcUserInfoDTO u = (OidcUserInfoDTO) ui;
            if (notBlank(u.getFullName()))
                return u.getFullName();
            String fn = safe(u.getFirstName());
            String fl = safe(u.getFirstLastName());
            String composed = (fn + " " + fl).trim();
            if (!composed.isEmpty())
                return composed;
            if (notBlank(u.getNickname()))
                return u.getNickname();
            if (notBlank(u.getEmail()))
                return u.getEmail();
            if (notBlank(u.getId()))
                return u.getId();
            return "Usuario";
        }
        if (ui instanceof Map) { // fallback if stored as Map
            Map<?, ?> m = (Map<?, ?>) ui;
            String fullName = str(m.get("nombre_completo"));
            if (notBlank(fullName))
                return fullName;
            String fn = str(m.get("primer_nombre"));
            String fl = str(m.get("primer_apellido"));
            String composed = (fn + " " + fl).trim();
            if (!composed.isEmpty())
                return composed;
            String nick = str(m.get("nickname"));
            if (notBlank(nick))
                return nick;
            String email = str(m.get("email"));
            if (notBlank(email))
                return email;
            String id = str(m.get("numero_documento"));
            if (notBlank(id))
                return id;
            return "Usuario";
        }
        return ui.toString();
    }

    public String getLogoutHref() {
        Object lo = ext().getSessionMap().get("logout_url");
        if (lo != null)
            return lo.toString();
        // fallback to REST endpoint (it will compute/logout properly)
        return ext().getRequestContextPath() + "/api/auth/gubuy/logout";
    }

    private static boolean notBlank(String s) {
        return s != null && !s.trim().isEmpty();
    }

    private static String safe(String s) {
        return s == null ? "" : s;
    }

    private static String str(Object o) {
        return o == null ? null : o.toString();
    }
}
