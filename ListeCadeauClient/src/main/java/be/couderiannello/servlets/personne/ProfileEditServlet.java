package be.couderiannello.servlets.personne;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import be.couderiannello.dao.PersonneDAO;
import be.couderiannello.models.Personne;

public class ProfileEditServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public ProfileEditServlet() {
        super();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        int userId = (Integer) session.getAttribute("userId");

        try {
            Personne user = Personne.findById(userId, PersonneDAO.getInstance(),
                    false, false, false, false);

            if (user == null) {
                session.invalidate();
                resp.sendRedirect(req.getContextPath() + "/login");
                return;
            }

            req.setAttribute("user", user);
            req.getRequestDispatcher("/WEB-INF/view/personne/editProfile.jsp")
               .forward(req, resp);

        } catch (RuntimeException e) {
            e.printStackTrace();
            req.setAttribute("error", "Erreur : Erreur serveur lors du chargement du profil.");
            req.getRequestDispatcher("/WEB-INF/view/personne/editProfile.jsp")
               .forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        int userId = (Integer) session.getAttribute("userId");

        PersonneDAO dao = PersonneDAO.getInstance();

        try {
            Personne p = Personne.findById(userId, dao);

            if (p == null) {
                session.invalidate();
                resp.sendRedirect(req.getContextPath() + "/login");
                return;
            }

            int age;
            int postalCode;

            try {
                age = Integer.parseInt(req.getParameter("age"));
            } catch (NumberFormatException ex) {
                req.setAttribute("error", "Erreur : L'âge doit être un nombre.");
                doGet(req, resp);
                return;
            }

            try {
                postalCode = Integer.parseInt(req.getParameter("postalCode"));
            } catch (NumberFormatException ex) {
                req.setAttribute("error", "Erreur : Le code postal doit être un nombre.");
                doGet(req, resp);
                return;
            }

            p.setFirstName(req.getParameter("firstName"));
            p.setName(req.getParameter("name"));
            p.setAge(age);
            p.setStreet(req.getParameter("street"));
            p.setStreetNumber(req.getParameter("streetNumber"));
            p.setCity(req.getParameter("city"));
            p.setPostalCode(postalCode);
            p.setEmail(req.getParameter("email"));

            boolean ok = p.update(dao);
            if (!ok) {
                session.invalidate();
                resp.sendRedirect(req.getContextPath() + "/login");
                return;
            }

            session.setAttribute("firstName", p.getFirstName());
            resp.sendRedirect(req.getContextPath() + "/profile");

        } catch (IllegalArgumentException e) {
            req.setAttribute("error", e.getMessage());
            doGet(req, resp);

        } catch (RuntimeException e) {
            e.printStackTrace();
            req.setAttribute("error", "Erreur : Erreur serveur lors de la mise à jour du profil.");
            doGet(req, resp);
        }
    }
}
