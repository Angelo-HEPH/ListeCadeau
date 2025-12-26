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

        if (session == null || session.getAttribute("userId") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        int userId = (Integer) session.getAttribute("userId");

        Personne user = PersonneDAO.getInstance()
                .find(userId, false, false, false, false);

        if (user == null) {
            session.invalidate();
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        req.setAttribute("user", user);
        req.getRequestDispatcher("/WEB-INF/view/personne/editProfile.jsp")
           .forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);

        if (session == null || session.getAttribute("userId") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        int userId = (Integer) session.getAttribute("userId");

        Personne p = PersonneDAO.getInstance().find(userId);

        if (p == null) {
            session.invalidate();
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        p.setFirstName(req.getParameter("firstName"));
        p.setName(req.getParameter("name"));
        p.setAge(Integer.parseInt(req.getParameter("age")));
        p.setStreet(req.getParameter("street"));
        p.setStreetNumber(req.getParameter("streetNumber"));
        p.setCity(req.getParameter("city"));
        p.setPostalCode(Integer.parseInt(req.getParameter("postalCode")));
        p.setEmail(req.getParameter("email"));

        PersonneDAO.getInstance().update(p);

        session.setAttribute("firstName", p.getFirstName());

        resp.sendRedirect(req.getContextPath() + "/profile");
    }
}
