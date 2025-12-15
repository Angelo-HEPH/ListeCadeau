package be.couderiannello.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import be.couderiannello.dao.PersonneDAO;
import be.couderiannello.models.Personne;

@WebServlet("/login")
public class loginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public loginServlet() {
        super();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.getRequestDispatcher("/WEB-INF/view/personne/login.jsp")
               .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        try {
            PersonneDAO dao = PersonneDAO.getInstance();
            Personne p = Personne.authenticate(email, password, dao);

            if (p == null) {
                request.setAttribute("error", "Email ou mot de passe incorrect.");
                request.getRequestDispatcher("/WEB-INF/view/personne/login.jsp")
                       .forward(request, response);
                return;
            }

            request.getSession().setAttribute("user", p);

            request.getRequestDispatcher("/WEB-INF/view/home/home.jsp")
                   .forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Erreur serveur lors de la connexion.");
            request.getRequestDispatcher("/WEB-INF/view/personne/login.jsp")
                   .forward(request, response);
        }
    }
}
