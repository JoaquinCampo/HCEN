package grupo12.practico.web.User;

import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import grupo12.practico.services.HealthUser.HealthUserServiceLocal;

@WebServlet(name = "SearchUsersServlet", urlPatterns = "/users/search")
public class SearchUsersServlet extends HttpServlet {

    @EJB
    private HealthUserServiceLocal userService;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String name = req.getParameter("q");
        if (name != null && !name.trim().isEmpty()) {
            req.setAttribute("users", userService.findUsersByName(name.trim()));
            req.setAttribute("q", name.trim());
        }
        req.getRequestDispatcher("/WEB-INF/jsp/user/user-search.jsp").forward(req, resp);
    }
}
