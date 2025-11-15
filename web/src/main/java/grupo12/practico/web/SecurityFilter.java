package grupo12.practico.web;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebFilter("/*")
public class SecurityFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        String path = req.getRequestURI();

        // If provider redirected to root (e.g., redirect_uri=http://localhost:8080)
        // with code/state, forward to our REST callback endpoint
        String codeParam = req.getParameter("code");
        if ((path.equals("/") || path.isEmpty()) && (codeParam != null || req.getParameter("error") != null)) {
            StringBuilder target = new StringBuilder(req.getContextPath())
                    .append("/api/auth/gubuy/callback");
            String query = req.getQueryString();
            if (query != null && !query.isEmpty()) {
                target.append("?").append(query);
            }
            res.sendRedirect(target.toString());
            return;
        }

        // Allow static resources, JSF resources, API endpoints and auth pages
        if (isPublicPath(path)) {
            chain.doFilter(request, response);
            return;
        }

        // Enforce authentication: if no valid session or flag, redirect to login
        HttpSession session = req.getSession(false);
        boolean authenticated = session != null && Boolean.TRUE.equals(session.getAttribute("authenticated"));
        if (!authenticated) {
            res.sendRedirect(req.getContextPath() + "/auth/login.xhtml");
            return;
        }

        chain.doFilter(request, response);
    }

    private boolean isPublicPath(String path) {
        if (path == null)
            return true;
        String p = path.toLowerCase();
        // API and callback endpoints are public (callback will create session)
        if (p.startsWith("/api/") || p.contains("/auth/gubuy/"))
            return true;
        // JSF resources
        if (p.contains("/javax.faces.resource/") || p.contains("/jakarta.faces.resource/"))
            return true;
        // Auth pages and OIDC redirect landing
        if (p.endsWith("/auth/login.xhtml") || p.endsWith("/auth/callback.xhtml") || p.endsWith("/callback")
                || p.equals("/logout"))
            return true;
        // Root may receive OIDC code and is allowed; index pages now require
        // authentication
        if (p.equals("/"))
            return true;
        // Static files
        return p.endsWith(".css") || p.endsWith(".js") || p.endsWith(".png") || p.endsWith(".jpg")
                || p.endsWith(".jpeg") || p.endsWith(".svg") || p.endsWith(".ico");
    }
}
