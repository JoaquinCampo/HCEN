package grupo12.practico.web.user;

import grupo12.practico.models.DocumentType;
import grupo12.practico.models.Gender;
import grupo12.practico.models.HealthUser;
import grupo12.practico.services.HealthWorker.HealthWorkerServiceLocal;
import grupo12.practico.services.Clinic.ClinicServiceLocal;
import grupo12.practico.services.HealthUser.HealthUserServiceLocal;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ValidationException;

import java.io.IOException;
import java.time.LocalDate;

@WebServlet(name = "AddUserServlet", urlPatterns = "/users/add")
public class AddUserServlet extends HttpServlet {

    @EJB
    private HealthUserServiceLocal userService;

    @EJB
    private HealthWorkerServiceLocal healthWorkerService;

    @EJB
    private ClinicServiceLocal clinicService;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("genders", Gender.values());
        req.setAttribute("healthWorkers", healthWorkerService.getAllHealthWorkers());
        req.setAttribute("healthProviders", clinicService.findAll());
        req.getRequestDispatcher("/WEB-INF/jsp/user/user-form.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String firstName = req.getParameter("firstName");
        String lastName = req.getParameter("lastName");
        String document = req.getParameter("document");
        String documentTypeParam = req.getParameter("documentType");
        String password = req.getParameter("password");
        String email = req.getParameter("email");
        String phone = req.getParameter("phone");
        String address = req.getParameter("address");
        String genderParam = req.getParameter("gender");
        String dobParam = req.getParameter("dateOfBirth");
        String[] healthWorkersParams = req.getParameterValues("healthWorkers");
        String[] affiliatedHealthProvidersParams = req.getParameterValues("affiliatedHealthProviders");

        try {
            HealthUser user = new HealthUser();
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setDocument(document);
            if (documentTypeParam != null && !documentTypeParam.isBlank()) {
                user.setDocumentType(DocumentType.valueOf(documentTypeParam));
            }
            if (password != null && !password.trim().isEmpty()) {
                user.setPassword(password);
            }
            user.setEmail(email);
            user.setPhone(phone);
            user.setAddress(address);
            if (genderParam != null && !genderParam.isEmpty()) {
                user.setGender(Gender.valueOf(genderParam));
            }
            if (dobParam != null && !dobParam.isEmpty()) {
                user.setDateOfBirth(LocalDate.parse(dobParam));
            }
            if (healthWorkersParams != null && healthWorkersParams.length > 0) {
                for (String healthWorkerParam : healthWorkersParams) {
                    if (healthWorkerParam != null && !healthWorkerParam.trim().isEmpty()) {
                        user.addHealthWorker(healthWorkerService.findById(healthWorkerParam));
                    }
                }
            }
            if (affiliatedHealthProvidersParams != null && affiliatedHealthProvidersParams.length > 0) {
                for (String healthProviderParam : affiliatedHealthProvidersParams) {
                    if (healthProviderParam != null && !healthProviderParam.trim().isEmpty()) {
                        user.addAffiliatedHealthProvider(clinicService.findById(healthProviderParam));
                    }
                }
            }

            userService.addUser(user);
            resp.sendRedirect(req.getContextPath() + "/users");
        } catch (ValidationException ex) {
            req.setAttribute("error", ex.getMessage());
            req.setAttribute("genders", Gender.values());
            req.setAttribute("healthWorkers", healthWorkerService.getAllHealthWorkers());
            req.setAttribute("healthProviders", clinicService.findAll());
            req.getRequestDispatcher("/WEB-INF/jsp/user/user-form.jsp").forward(req, resp);
        } catch (Exception ex) {
            req.setAttribute("error", "Unexpected error: " + ex.getMessage());
            req.setAttribute("genders", Gender.values());
            req.setAttribute("healthWorkers", healthWorkerService.getAllHealthWorkers());
            req.setAttribute("healthProviders", clinicService.findAll());
            req.getRequestDispatcher("/WEB-INF/jsp/user/user-form.jsp").forward(req, resp);
        }
    }
}
