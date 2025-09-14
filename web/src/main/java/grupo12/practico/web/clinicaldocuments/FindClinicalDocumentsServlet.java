package grupo12.practico.web.clinicaldocuments;

import grupo12.practico.service.document.ClinicalDocumentServiceLocal;
import grupo12.practico.model.ClinicalDocument;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet(name = "FindClinicalDocumentsServlet", urlPatterns = "/documents/search")
public class FindClinicalDocumentsServlet extends HttpServlet {

    @EJB
    private ClinicalDocumentServiceLocal docService;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");

    String q = req.getParameter("q");
    String scopeParam = req.getParameter("scope");
    final String scope = (scopeParam == null || scopeParam.isBlank()) ? "patient" : scopeParam; // default similar to other pages

        // Expose current query params back to the JSP
        req.setAttribute("q", q);
    req.setAttribute("scope", scope);

        if (q != null && !q.isBlank()) {
            String needle = q.trim().toLowerCase();
            List<ClinicalDocument> filtered = docService.getAllDocuments().stream()
                .filter(d -> matchesScope(d, scope, needle))
                .collect(Collectors.toList());
            req.setAttribute("documents", filtered);
        }

        req.getRequestDispatcher("/WEB-INF/jsp/ClinicalDocuments/document-find.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Backward compatibility: if someone POSTs, treat as GET-based search
        doGet(req, resp);
    }

    private static boolean matchesScope(ClinicalDocument d, String scope, String needle) {
        switch (scope) {
            case "patient":
                return d.getPatient() != null && nameContains(
                        d.getPatient().getFirstName(), d.getPatient().getLastName(), needle);
            case "author":
                return d.getAuthor() != null && nameContains(
                        d.getAuthor().getFirstName(), d.getAuthor().getLastName(), needle);
            case "provider":
                return d.getProvider() != null && containsIgnoreCase(d.getProvider().getName(), needle);
            case "all":
                return (d.getPatient() != null && nameContains(d.getPatient().getFirstName(), d.getPatient().getLastName(), needle))
                    || (d.getAuthor() != null && nameContains(d.getAuthor().getFirstName(), d.getAuthor().getLastName(), needle))
                    || (d.getProvider() != null && containsIgnoreCase(d.getProvider().getName(), needle));
            default:
                return false;
        }
    }

    private static boolean nameContains(String first, String last, String needle) {
        String f = safeLower(first);
        String l = safeLower(last);
        // check individual and common full-name variants
        return (f.contains(needle) || l.contains(needle)
                || (l + ", " + f).contains(needle)
                || (f + " " + l).contains(needle));
    }

    private static boolean containsIgnoreCase(String value, String needle) {
        return safeLower(value).contains(needle);
    }

    private static String safeLower(String s) {
        return s == null ? "" : s.toLowerCase();
    }
}
