package be.couderiannello.servlets.listecadeau;

import java.io.IOException;
import java.time.LocalDate;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import be.couderiannello.dao.ListeCadeauDAO;
import be.couderiannello.models.ListeCadeau;

public class ListeEditServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        int userId  = (Integer) session.getAttribute("userId");
        int listeId = Integer.parseInt(req.getParameter("id"));

        ListeCadeau liste = ListeCadeauDAO.getInstance()
                .find(listeId, true, false, false);

        if (liste == null || liste.getCreator().getId() != userId) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        req.setAttribute("liste", liste);
        req.getRequestDispatcher("/WEB-INF/view/listeCadeau/edit.jsp")
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

        int userId  = (Integer) session.getAttribute("userId");
        int listeId = Integer.parseInt(req.getParameter("id"));

        ListeCadeau liste = ListeCadeauDAO.getInstance()
                .find(listeId, true, false, false);

        if (liste == null || liste.getCreator().getId() != userId) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        liste.setTitle(req.getParameter("title"));
        liste.setEvenement(req.getParameter("evenement"));
        liste.setStatut(req.getParameter("statut") != null);

        String date = req.getParameter("expirationDate");
        if (date != null && !date.isBlank()) {
            liste.setExpirationDate(LocalDate.parse(date));
        }

        ListeCadeauDAO.getInstance().update(liste);

        resp.sendRedirect(req.getContextPath()
                + "/liste/manage?id=" + listeId);
    }
}
