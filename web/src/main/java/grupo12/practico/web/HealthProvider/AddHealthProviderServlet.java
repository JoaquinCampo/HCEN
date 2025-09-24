package grupo12.practico.web.HealthProvider;

import grupo12.practico.models.Clinic;
import grupo12.practico.models.ClinicType;
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

@WebServlet(name = "AddHealthProviderServlet", urlPatterns = "/healthproviders/add")
public class AddHealthProviderServlet extends HttpServlet {

    @EJB
    private ClinicServiceLocal clinicService;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/jsp/health-provider/healthprovider-form.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String name = req.getParameter("name");
        String address = req.getParameter("address");
        String phone = req.getParameter("phone");
        String email = req.getParameter("email");
        String registrationNumber = req.getParameter("registrationNumber");
        String typeParam = req.getParameter("type");
        String registrationDateParam = req.getParameter("registrationDate");
        String activeParam = req.getParameter("active");

        try {
            Clinic hp = new Clinic();
            hp.setName(name);
            hp.setAddress(address);
            hp.setPhone(phone);
            hp.setEmail(email);
            hp.setRegistrationNumber(registrationNumber);

            if (typeParam != null && !typeParam.isEmpty()) {
                hp.setType(ClinicType.valueOf(typeParam));
            }

            if (registrationDateParam != null && !registrationDateParam.isEmpty()) {
                hp.setRegistrationDate(LocalDate.parse(registrationDateParam));
            }

            if (activeParam != null) {
                hp.setActive("on".equals(activeParam) || "true".equalsIgnoreCase(activeParam));
            }

            clinicService.addClinic(hp);
            resp.sendRedirect(req.getContextPath() + "/healthproviders");
        } catch (ValidationException ex) {
            req.setAttribute("error", ex.getMessage());
            req.getRequestDispatcher("/WEB-INF/jsp/healthprovider-form.jsp").forward(req, resp);
        } catch (Exception ex) {
            req.setAttribute("error", "Unexpected error: " + ex.getMessage());
            req.getRequestDispatcher("/WEB-INF/jsp/healthprovider-form.jsp").forward(req, resp);
        }
    }
}
