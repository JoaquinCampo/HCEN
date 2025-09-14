package grupo12.practico.web.HealthWorker;

import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import grupo12.practico.services.HealthWorker.HealthWorkerServiceLocal;

@WebServlet(name = "SearchHealthWorkersServlet", urlPatterns = "/healthworkers/search")
public class SearchHealthWorkersServlet extends HttpServlet {

    @EJB
    private HealthWorkerServiceLocal healthWorkerService;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String name = req.getParameter("q");
        if (name != null && !name.trim().isEmpty()) {
            req.setAttribute("healthWorkers", healthWorkerService.findHealthWorkersByName(name.trim()));
            req.setAttribute("q", name.trim());
        }
        req.getRequestDispatcher("/WEB-INF/jsp/health-worker/healthworker-search.jsp").forward(req, resp);
    }
}
