package be.couderiannello.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import be.couderiannello.dao.PersonneDAO;
import be.couderiannello.models.ListeCadeau;
import be.couderiannello.models.Personne;

@WebServlet("/liste/all")
public class ListeAllServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        Personne user = (Personne) req.getSession().getAttribute("user");

        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        Personne fullUser = PersonneDAO.getInstance()
                .find(user.getId(), false, true, true, false);

        List<ListeCadeau> listes = new ArrayList<>();
        listes.addAll(fullUser.getListeCadeauCreator());
        listes.addAll(fullUser.getListeCadeauInvitations());

        req.setAttribute("listes", listes);
        req.getRequestDispatcher("/WEB-INF/view/listeCadeau/all.jsp").forward(req, resp);
    }
}
