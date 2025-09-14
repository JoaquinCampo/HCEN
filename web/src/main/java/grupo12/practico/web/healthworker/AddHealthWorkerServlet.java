package grupo12.practico.web.HealthWorker;

import grupo12.practico.models.Gender;
import grupo12.practico.models.HealthWorker;
import grupo12.practico.services.HealthWorker.HealthWorkerServiceLocal;
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

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("genders", Gender.values());
        req.getRequestDispatcher("/WEB-INF/jsp/healthworker/healthworker-form.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String firstName = req.getParameter("firstName");
        String lastName = req.getParameter("lastName");
        String dni = req.getParameter("dni");
        String genderParam = req.getParameter("gender");
        String specialty = req.getParameter("specialty");
        String licenseNumber = req.getParameter("licenseNumber");
        String hireDateParam = req.getParameter("hireDate");

        try {
            HealthWorker hw = new HealthWorker();
            hw.setFirstName(firstName);
            hw.setLastName(lastName);
            hw.setDni(dni);
            if (genderParam != null && !genderParam.isEmpty()) {
                hw.setGender(Gender.valueOf(genderParam));
            }
            hw.setSpecialty(specialty);
            hw.setLicenseNumber(licenseNumber);
            if (hireDateParam != null && !hireDateParam.isEmpty()) {
                hw.setHireDate(LocalDate.parse(hireDateParam));
            }

            healthWorkerService.addHealthWorker(hw);
            resp.sendRedirect(req.getContextPath() + "/healthworkers");
        } catch (ValidationException ex) {
            req.setAttribute("error", ex.getMessage());
            req.setAttribute("genders", Gender.values());
            req.getRequestDispatcher("/WEB-INF/jsp/healthworker/healthworker-form.jsp").forward(req, resp);
        } catch (Exception ex) {
            req.setAttribute("error", "Unexpected error: " + ex.getMessage());
            req.getRequestDispatcher("/WEB-INF/jsp/healthworker/healthworker-form.jsp").forward(req, resp);
        }
    }
}
