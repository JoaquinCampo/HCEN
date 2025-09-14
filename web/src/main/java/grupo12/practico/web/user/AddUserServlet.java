package grupo12.practico.web.User;

import grupo12.practico.models.Gender;
import grupo12.practico.models.User;
import grupo12.practico.services.HealthWorker.HealthWorkerServiceLocal;
import grupo12.practico.services.User.UserServiceLocal;
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
    private UserServiceLocal userService;

    @EJB
    private HealthWorkerServiceLocal healthWorkerService;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("genders", Gender.values());
        req.setAttribute("healthWorkers", healthWorkerService.getAllHealthWorkers());
        req.getRequestDispatcher("/WEB-INF/jsp/user/user-form.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String firstName = req.getParameter("firstName");
        String lastName = req.getParameter("lastName");
        String dni = req.getParameter("dni");
        String email = req.getParameter("email");
        String phone = req.getParameter("phone");
        String address = req.getParameter("address");
        String genderParam = req.getParameter("gender");
        String dobParam = req.getParameter("dateOfBirth");
        String[] healthWorkersParams = req.getParameterValues("healthWorkers");

        try {
            User user = new User();
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setDni(dni);
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
                    user.addHealthWorker(healthWorkerService.findById(healthWorkerParam));
                }
            }

            userService.addUser(user);
            resp.sendRedirect(req.getContextPath() + "/users");
        } catch (ValidationException ex) {
            req.setAttribute("error", ex.getMessage());
            req.setAttribute("genders", Gender.values());
            req.getRequestDispatcher("/WEB-INF/jsp/user/user-form.jsp").forward(req, resp);
        } catch (Exception ex) {
            req.setAttribute("error", "Unexpected error: " + ex.getMessage());
            req.setAttribute("genders", Gender.values());
            req.getRequestDispatcher("/WEB-INF/jsp/user/user-form.jsp").forward(req, resp);
        }
    }
}
