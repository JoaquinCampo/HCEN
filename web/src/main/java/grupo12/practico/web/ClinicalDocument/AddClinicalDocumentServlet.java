package grupo12.practico.web.ClinicalDocument;

import grupo12.practico.models.ClinicalDocument;
import grupo12.practico.models.ClinicalHistory;
import grupo12.practico.models.HealthProvider;
import grupo12.practico.models.HealthWorker;
import grupo12.practico.services.ClinicalDocument.ClinicalDocumentServiceLocal;
import grupo12.practico.services.HealthProvider.HealthProviderServiceLocal;
import grupo12.practico.services.HealthWorker.HealthWorkerServiceLocal;
import grupo12.practico.services.User.UserServiceLocal;
import grupo12.practico.models.User;
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

    @EJB
    private ClinicalDocumentServiceLocal docService;
    @EJB
    private UserServiceLocal userService;
    @EJB
    private HealthWorkerServiceLocal hwService;
    @EJB
    private HealthProviderServiceLocal hpService;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("users", userService.getAllUsers());
        req.setAttribute("healthWorkers", hwService.getAllHealthWorkers());
        req.setAttribute("healthProviders", hpService.findAll());
        req.getRequestDispatcher("/WEB-INF/jsp/clinical-document/document-form.jsp").forward(req, resp);
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

            User patient = userService.findById(userId);
            HealthWorker author = (authorId != null && !authorId.trim().isEmpty()) ? hwService.findById(authorId)
                    : null;
            HealthProvider provider = (providerId != null && !providerId.trim().isEmpty())
                    ? hpService.findById(providerId)
                    : null;

            ClinicalHistory history = patient != null ? patient.getClinicalHistory() : null;
            if (history == null && patient != null) {
                history = new ClinicalHistory();
                history.setPatient(patient);
                patient.setClinicalHistory(history);
            }

            doc.setClinicalHistory(history);
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
