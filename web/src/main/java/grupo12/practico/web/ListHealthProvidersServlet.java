package grupo12.practico.web;

import grupo12.practico.service.healthprovider.HealthProviderServiceLocal;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(name = "ListHealthProvidersServlet", urlPatterns = "/healthproviders")
public class ListHealthProvidersServlet extends HttpServlet {

    @EJB
    private HealthProviderServiceLocal healthProviderService;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("healthProviders", healthProviderService.findAll());
        req.getRequestDispatcher("/WEB-INF/jsp/healthprovider-list.jsp").forward(req, resp);
    }
}
