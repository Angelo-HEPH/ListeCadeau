package be.couderiannello.servlets.listecadeau;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import be.couderiannello.dao.PersonneDAO;
import be.couderiannello.models.Personne;

public class ListeAllServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        int id = (Integer) session.getAttribute("userId");

        try {
            Personne fullUser = Personne.findById(id, PersonneDAO.getInstance(), false, true, false, false);

            if (fullUser == null) {
                session.invalidate();
                resp.sendRedirect(req.getContextPath() + "/login");
                return;
            }

            req.setAttribute("listes", fullUser.getListeCadeauCreator());
            req.getRequestDispatcher("/WEB-INF/view/listeCadeau/all.jsp").forward(req, resp);

        } catch (Exception e) {
            req.setAttribute("error", e.getMessage());
            req.getRequestDispatcher("/WEB-INF/view/listeCadeau/all.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}