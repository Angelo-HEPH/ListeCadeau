package be.couderiannello.servlets.listecadeau;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import be.couderiannello.dao.ListeCadeauDAO;
import be.couderiannello.models.ListeCadeau;

public class ListeViewServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String idParam = req.getParameter("id");
        if (idParam == null || idParam.isBlank()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Paramètre id manquant");
            return;
        }

        int id;
        try {
            id = Integer.parseInt(idParam);
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Paramètre id invalide");
            return;
        }

        ListeCadeauDAO dao = ListeCadeauDAO.getInstance();
        ListeCadeau l = ListeCadeau.findById(id, dao, true, false, true);
        
        if (l == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Liste introuvable");
            return;
        }
        
        String creatorLabel = "Créateur inconnu";
        if (l.getCreator() != null) {
            creatorLabel = l.getCreator().getFirstName() + " " + l.getCreator().getName();
        }
        req.setAttribute("creatorLabel", creatorLabel);


        req.setAttribute("liste", l);
        req.getRequestDispatcher("/WEB-INF/view/listeCadeau/view.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        doGet(req, resp);
    }
}
