package grupo12.practico.web.clinicaldocuments;

import grupo12.practico.service.document.ClinicalDocumentServiceLocal;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "FindClinicalDocumentsServlet", urlPatterns = "/documents/search")
public class FindClinicalDocumentsServlet extends HttpServlet {

    @EJB
    private ClinicalDocumentServiceLocal docService;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/jsp/ClinicalDocuments/document-find.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String patientId = req.getParameter("patientId");
        String authorId = req.getParameter("authorId");
        String providerId = req.getParameter("providerId");

        // Simple precedence: patient > author > provider; default all
        if (patientId != null && !patientId.isBlank()) {
            req.setAttribute("documents", docService.getDocumentsByPatient(patientId));
        } else if (authorId != null && !authorId.isBlank()) {
            req.setAttribute("documents", docService.getDocumentsByAuthor(authorId));
        } else if (providerId != null && !providerId.isBlank()) {
            req.setAttribute("documents", docService.getDocumentsByProvider(providerId));
        } else {
            req.setAttribute("documents", docService.getAllDocuments());
        }

        req.getRequestDispatcher("/WEB-INF/jsp/ClinicalDocuments/document-list.jsp").forward(req, resp);
    }
}
