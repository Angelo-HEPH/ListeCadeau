package be.couderiannello.servlets.auth;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import be.couderiannello.dao.PersonneDAO;
import be.couderiannello.models.Personne;

public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public LoginServlet() {
        super();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        var session = request.getSession(false);
        if (session != null && session.getAttribute("userId") != null) {
            response.sendRedirect(request.getContextPath() + "/liste/all");
            return;
        }
        request.getRequestDispatcher("/WEB-INF/view/personne/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        request.setAttribute("email", email);

        try {
            PersonneDAO dao = PersonneDAO.getInstance();
            Personne p = Personne.authenticate(email, password, dao);

            if (p == null) {
                request.setAttribute("error", "Email ou mot de passe incorrect.");
                request.getRequestDispatcher("/WEB-INF/view/personne/login.jsp")
                    .forward(request, response);
                return;
            }

            var session = request.getSession(true);
            session.setAttribute("userId", p.getId());
            session.setAttribute("firstName", p.getFirstName());

            response.sendRedirect(request.getContextPath() + "/home");

        } catch (RuntimeException e) {
            request.setAttribute("error", e.getMessage());
            request.getRequestDispatcher("/WEB-INF/view/personne/login.jsp")
                   .forward(request, response);
        }
    }
}