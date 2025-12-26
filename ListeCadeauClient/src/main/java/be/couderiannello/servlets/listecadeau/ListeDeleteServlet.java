package be.couderiannello.servlets.listecadeau;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import be.couderiannello.dao.ListeCadeauDAO;
import be.couderiannello.dao.PersonneDAO;
import be.couderiannello.models.ListeCadeau;
import be.couderiannello.models.Personne;


public class ListeDeleteServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public ListeDeleteServlet() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.getWriter().append("Served at: ").append(request.getContextPath());
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
	        int listeId = Integer.parseInt(req.getParameter("id"));

	        Personne fullUser = PersonneDAO.getInstance()
	                .find(userId, false, true, false, false);

	        if (fullUser == null) {
	            session.invalidate();
	            resp.sendRedirect(req.getContextPath() + "/login");
	            return;
	        }

	        ListeCadeauDAO dao = ListeCadeauDAO.getInstance();
	        ListeCadeau liste = dao.find(listeId, true, false, false);

	        if (liste == null || liste.getCreator() == null 
	                || liste.getCreator().getId() != userId) {
	            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
	            return;
	        }

	        dao.delete(liste);

	        resp.sendRedirect(req.getContextPath() + "/liste/all");
	    }

}
