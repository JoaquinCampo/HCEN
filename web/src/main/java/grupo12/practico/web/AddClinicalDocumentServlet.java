package grupo12.practico.web;

import grupo12.practico.model.ClinicalDocument;
import grupo12.practico.model.HealthProvider;
import grupo12.practico.model.HealthWorker;
import grupo12.practico.model.User;
import grupo12.practico.service.document.ClinicalDocumentServiceLocal;
import grupo12.practico.service.healthprovider.HealthProviderServiceLocal;
import grupo12.practico.service.healthworker.HealthWorkerServiceLocal;
import grupo12.practico.service.user.UserServiceLocal;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ValidationException;

import java.io.IOException;

@WebServlet(name = "AddClinicalDocumentServlet", urlPatterns = "/documents/add")
public class AddClinicalDocumentServlet extends HttpServlet {

    @EJB private ClinicalDocumentServiceLocal docService;
    @EJB private UserServiceLocal userService;
    @EJB private HealthWorkerServiceLocal hwService;
    @EJB private HealthProviderServiceLocal hpService;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("users", userService.getAllUsers());
        req.setAttribute("healthWorkers", hwService.getAllHealthWorkers());
        req.setAttribute("healthProviders", hpService.findAll());
        req.getRequestDispatcher("/WEB-INF/jsp/document-form.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String title = req.getParameter("title");
        String content = req.getParameter("content");
        String userId = req.getParameter("userId");
        String authorId = req.getParameter("authorId");
        String providerId = req.getParameter("providerId");

        try {
            ClinicalDocument doc = new ClinicalDocument();
            doc.setTitle(title);
            doc.setContent(content);

            // Using simple searches by id via services
            User patient = userService.getAllUsers().stream()
                    .filter(u -> u.getId().equals(userId))
                    .findFirst().orElse(null);
            HealthWorker author = hwService.getAllHealthWorkers().stream()
                    .filter(h -> h.getId().equals(authorId))
                    .findFirst().orElse(null);
            HealthProvider provider = hpService.findAll().stream()
                    .filter(p -> p.getId().equals(providerId))
                    .findFirst().orElse(null);

            doc.setPatient(patient);
            doc.setAuthor(author);
            doc.setProvider(provider);

            docService.addClinicalDocument(doc);
            resp.sendRedirect(req.getContextPath() + "/documents");
        } catch (ValidationException ex) {
            req.setAttribute("error", ex.getMessage());
            doGet(req, resp);
        } catch (Exception ex) {
            req.setAttribute("error", "Unexpected error: " + ex.getMessage());
            doGet(req, resp);
        }
    }
}
