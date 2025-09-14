package grupo12.practico.web;

import grupo12.practico.service.healthworker.HealthWorkerServiceLocal;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(name = "ListHealthWorkersServlet", urlPatterns = "/healthworkers")
public class ListHealthWorkersServlet extends HttpServlet {

    @EJB
    private HealthWorkerServiceLocal healthWorkerService;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("healthWorkers", healthWorkerService.getAllHealthWorkers());
        req.getRequestDispatcher("/WEB-INF/jsp/healthworker-list.jsp").forward(req, resp);
    }
}


