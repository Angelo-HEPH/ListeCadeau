package be.couderiannello.servlets.listecadeau;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import be.couderiannello.dao.ListeCadeauDAO;
import be.couderiannello.models.ListeCadeau;

public class InvitationsListeCadeauServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

    	int userId = (int) req.getSession(false).getAttribute("userId");

        ListeCadeauDAO dao = ListeCadeauDAO.getInstance();
        List<ListeCadeau> invited = ListeCadeau.getInvitedByPersonneId(userId, dao, true, false, false);

        req.setAttribute("listesInvite", invited);
        req.getRequestDispatcher("/WEB-INF/view/listeCadeau/invitations.jsp").forward(req, resp);
    }

	protected void doPost(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		doGet(request, response);
	}
}
