package grupo12.practico.web.healthworker;

import grupo12.practico.models.DocumentType;
import grupo12.practico.models.Gender;
import grupo12.practico.models.HealthWorker;
import grupo12.practico.services.HealthWorker.HealthWorkerServiceLocal;
import grupo12.practico.services.Clinic.ClinicServiceLocal;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ValidationException;

import java.io.IOException;
import java.time.LocalDate;

@WebServlet(name = "AddHealthWorkerServlet", urlPatterns = "/healthworkers/add")
public class AddHealthWorkerServlet extends HttpServlet {

    @EJB
    private HealthWorkerServiceLocal healthWorkerService;

    @EJB
    private ClinicServiceLocal clinicService;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("genders", Gender.values());
        req.setAttribute("healthProviders", clinicService.findAll());
        req.getRequestDispatcher("/WEB-INF/jsp/health-worker/healthworker-form.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String firstName = req.getParameter("firstName");
        String lastName = req.getParameter("lastName");
        String document = req.getParameter("document");
        String genderParam = req.getParameter("gender");
        String documentTypeParam = req.getParameter("documentType");
        String licenseNumber = req.getParameter("licenseNumber");
        String hireDateParam = req.getParameter("hireDate");
        String[] healthProvidersParams = req.getParameterValues("healthProviders");

        try {
            HealthWorker hw = new HealthWorker();
            hw.setFirstName(firstName);
            hw.setLastName(lastName);
            hw.setDocument(document);
            if (documentTypeParam != null && !documentTypeParam.isBlank()) {
                hw.setDocumentType(DocumentType.valueOf(documentTypeParam));
            }
            if (genderParam != null && !genderParam.isEmpty()) {
                hw.setGender(Gender.valueOf(genderParam));
            }
            hw.setLicenseNumber(licenseNumber);
            if (hireDateParam != null && !hireDateParam.isEmpty()) {
                hw.setHireDate(LocalDate.parse(hireDateParam));
            }
            if (healthProvidersParams != null && healthProvidersParams.length > 0) {
                for (String healthProviderParam : healthProvidersParams) {
                    if (healthProviderParam != null && !healthProviderParam.trim().isEmpty()) {
                        hw.addHealthProvider(clinicService.findById(healthProviderParam));
                    }
                }
            }

            healthWorkerService.addHealthWorker(hw);
            resp.sendRedirect(req.getContextPath() + "/healthworkers");
        } catch (ValidationException ex) {
            req.setAttribute("error", ex.getMessage());
            req.setAttribute("genders", Gender.values());
            req.setAttribute("healthProviders", clinicService.findAll());
            req.getRequestDispatcher("/WEB-INF/jsp/health-worker/healthworker-form.jsp").forward(req, resp);
        } catch (Exception ex) {
            req.setAttribute("error", "Unexpected error: " + ex.getMessage());
            req.setAttribute("genders", Gender.values());
            req.setAttribute("healthProviders", clinicService.findAll());
            req.getRequestDispatcher("/WEB-INF/jsp/health-worker/healthworker-form.jsp").forward(req, resp);
        }
    }
}
