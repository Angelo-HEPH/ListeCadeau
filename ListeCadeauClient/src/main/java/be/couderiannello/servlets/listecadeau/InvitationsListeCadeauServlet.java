package be.couderiannello.servlets.listecadeau;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import be.couderiannello.dao.ListeCadeauDAO;
import be.couderiannello.models.ListeCadeau;

public class InvitationsListeCadeauServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        int userId = (Integer) session.getAttribute("userId");

        try {
            ListeCadeauDAO dao = ListeCadeauDAO.getInstance();

            List<ListeCadeau> invited = ListeCadeau.getInvitedByPersonneId(userId, dao, true, false, false);

            req.setAttribute("listesInvite", invited);
            req.getRequestDispatcher("/WEB-INF/view/listeCadeau/invitations.jsp").forward(req, resp);

        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("error", e.getMessage());
            req.getRequestDispatcher("/WEB-INF/view/listeCadeau/invitations.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}