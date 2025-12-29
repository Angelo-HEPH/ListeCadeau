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

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req,resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        int userId = (Integer) session.getAttribute("userId");

        int cadeauId = Integer.parseInt(req.getParameter("cadeauId"));
        int listeId  = Integer.parseInt(req.getParameter("listeId"));
        
        ListeCadeau liste = ListeCadeau.findById(listeId, ListeCadeauDAO.getInstance(), true, false, false);

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
