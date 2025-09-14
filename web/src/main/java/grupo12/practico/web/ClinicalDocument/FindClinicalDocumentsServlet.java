package grupo12.practico.web.ClinicalDocument;

import grupo12.practico.models.ClinicalDocument;
import grupo12.practico.services.ClinicalDocument.ClinicalDocumentServiceLocal;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "FindClinicalDocumentsServlet", urlPatterns = "/documents/search")
public class FindClinicalDocumentsServlet extends HttpServlet {

    @EJB
    private ClinicalDocumentServiceLocal docService;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");

        String q = req.getParameter("q");
        String scopeParam = req.getParameter("scope");
        final String scope = (scopeParam == null || scopeParam.isBlank()) ? "patient" : scopeParam; // default similar
                                                                                                    // to other pages

        // Expose current query params back to the JSP
        req.setAttribute("q", q);
        req.setAttribute("scope", scope);

        if (q != null && !q.isBlank()) {
            List<ClinicalDocument> results;
            switch (scope) {
                case "patient":
                    results = docService.searchByPatientName(q);
                    break;
                case "author":
                    results = docService.searchByAuthorName(q);
                    break;
                case "provider":
                    results = docService.searchByProviderName(q);
                    break;
                case "all":
                default:
                    results = docService.searchByAnyName(q);
            }
            req.setAttribute("documents", results);
        }

        req.getRequestDispatcher("/WEB-INF/jsp/clinical-documents/document-find.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Backward compatibility: if someone POSTs, treat as GET-based search
        doGet(req, resp);
    }

    // No need for in-web filtering helpers anymore; repository/service handle it
}
