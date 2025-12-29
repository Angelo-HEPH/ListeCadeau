package be.couderiannello.servlets.cadeau;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.*;

import be.couderiannello.dao.CadeauDAO;
import be.couderiannello.dao.ListeCadeauDAO;
import be.couderiannello.enumeration.StatutPriorite;
import be.couderiannello.models.Cadeau;
import be.couderiannello.models.ListeCadeau;

public class CadeauEditServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        int userId = (Integer) session.getAttribute("userId");

        int cadeauId = Integer.parseInt(req.getParameter("id"));
        int listeId  = Integer.parseInt(req.getParameter("listeId"));
        
        ListeCadeau liste = ListeCadeau.findById(listeId, ListeCadeauDAO.getInstance(), true, false, false);

        if (liste == null || liste.getCreator() == null || liste.getCreator().getId() != userId) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        Cadeau cadeau = CadeauDAO.getInstance().find(cadeauId);
        if (cadeau == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        req.setAttribute("cadeau", cadeau);
        req.setAttribute("listeId", listeId);
        req.getRequestDispatcher("/WEB-INF/view/cadeau/edit.jsp")
           .forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        int userId = (Integer) session.getAttribute("userId");

        int cadeauId = Integer.parseInt(req.getParameter("id"));
        int listeId  = Integer.parseInt(req.getParameter("listeId"));

        ListeCadeau liste = ListeCadeauDAO.getInstance()
                .find(listeId, true, false, false);

        if (liste == null || liste.getCreator() == null || liste.getCreator().getId() != userId) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        Cadeau c = CadeauDAO.getInstance().find(cadeauId);
        if (c == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        c.setName(req.getParameter("name"));
        c.setDescription(req.getParameter("description"));
        c.setPrice(Double.parseDouble(req.getParameter("price")));
        c.setLinkSite(req.getParameter("linkSite"));
        c.setPhoto(req.getParameter("photo"));
        c.setPriorite(StatutPriorite.valueOf(req.getParameter("priorite")));

        CadeauDAO.getInstance().update(c);

        resp.sendRedirect(req.getContextPath() + "/liste/manage?id=" + listeId);
    }
}
