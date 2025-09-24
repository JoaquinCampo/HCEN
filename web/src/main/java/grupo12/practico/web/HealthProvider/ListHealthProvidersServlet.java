package grupo12.practico.web.HealthProvider;

import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import grupo12.practico.services.Clinic.ClinicServiceLocal;

@WebServlet(name = "ListHealthProvidersServlet", urlPatterns = "/healthproviders")
public class ListHealthProvidersServlet extends HttpServlet {

    @EJB
    private ClinicServiceLocal clinicService;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("healthProviders", clinicService.findAll());
        req.getRequestDispatcher("/WEB-INF/jsp/health-provider/healthprovider-list.jsp").forward(req, resp);
    }
}
