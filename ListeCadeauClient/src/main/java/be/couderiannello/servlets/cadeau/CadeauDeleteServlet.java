package be.couderiannello.servlets.cadeau;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import be.couderiannello.dao.CadeauDAO;
import be.couderiannello.dao.ListeCadeauDAO;
import be.couderiannello.models.Cadeau;
import be.couderiannello.models.ListeCadeau;


public class CadeauDeleteServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public CadeauDeleteServlet() {
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
	        int cadeauId = Integer.parseInt(req.getParameter("cadeauId"));
	        int listeId  = Integer.parseInt(req.getParameter("listeId"));

	        ListeCadeau liste = ListeCadeauDAO.getInstance()
	                .find(listeId, true, false, false);

	        if (liste == null || liste.getCreator() == null
	                || liste.getCreator().getId() != userId) {
	            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
	            return;
	        }

	        Cadeau c = CadeauDAO.getInstance().find(cadeauId);
	        if (c != null) {
	            CadeauDAO.getInstance().delete(c);
	        }

	        resp.sendRedirect(req.getContextPath() + "/liste/manage?id=" + listeId);
	    }

}
