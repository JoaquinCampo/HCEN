package grupo12.practico.web.ClinicalDocument;

import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

import grupo12.practico.services.ClinicalDocument.ClinicalDocumentServiceLocal;

@WebServlet(name = "ListClinicalDocumentsServlet", urlPatterns = "/documents")
public class ListClinicalDocumentsServlet extends HttpServlet {

    @EJB
    private ClinicalDocumentServiceLocal docService;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("documents", docService.getAllDocuments());
        req.getRequestDispatcher("/WEB-INF/jsp/clinical-document/document-list.jsp").forward(req, resp);
    }
}
