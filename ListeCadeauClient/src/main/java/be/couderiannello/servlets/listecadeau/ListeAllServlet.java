package be.couderiannello.servlets.listecadeau;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import be.couderiannello.dao.PersonneDAO;
import be.couderiannello.models.Personne;


public class ListeAllServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
	        throws ServletException, IOException {

	    var session = req.getSession(false);
	    if (session == null || session.getAttribute("userId") == null) {
	        resp.sendRedirect(req.getContextPath() + "/login");
	        return;
	    }

	    int id = (Integer) session.getAttribute("userId");

	    Personne fullUser = PersonneDAO.getInstance().find(id, false, true, false, false);
	    if (fullUser == null) {
	        session.invalidate();
	        resp.sendRedirect(req.getContextPath() + "/login");
	        return;
	    }

	    req.setAttribute("listes", fullUser.getListeCadeauCreator());
	    req.getRequestDispatcher("/WEB-INF/view/listeCadeau/all.jsp").forward(req, resp);
	}

}
