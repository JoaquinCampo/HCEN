package grupo12.practico.web;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Simple landing servlet to receive redirect_uri at /callback and forward
 * the query parameters to the REST callback endpoint under /api.
 * This makes it easy to register http://localhost:8080/callback with IdUruguay.
 */
@WebServlet(urlPatterns = "/callback")
public class CallbackServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String code = req.getParameter("code");
        String state = req.getParameter("state");
        String error = req.getParameter("error");
        String errorDescription = req.getParameter("error_description");

        StringBuilder target = new StringBuilder(req.getContextPath())
                .append("/api/auth/gubuy/callback");

        boolean first = true;
        if (code != null) {
            target.append(first ? "?" : "&");
            first = false;
            target.append("code=").append(url(code));
        }
        if (state != null) {
            target.append(first ? "?" : "&");
            first = false;
            target.append("state=").append(url(state));
        }
        if (error != null) {
            target.append(first ? "?" : "&");
            first = false;
            target.append("error=").append(url(error));
        }
        if (errorDescription != null) {
            target.append(first ? "?" : "&");
            target.append("error_description=").append(url(errorDescription));
        }

        resp.sendRedirect(target.toString());
    }

    private String url(String v) {
        return URLEncoder.encode(v, StandardCharsets.UTF_8);
    }
}
