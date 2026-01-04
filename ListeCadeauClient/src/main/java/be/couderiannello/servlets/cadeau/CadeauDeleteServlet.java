package be.couderiannello.servlets.cadeau;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.*;

import be.couderiannello.dao.CadeauDAO;
import be.couderiannello.dao.ListeCadeauDAO;
import be.couderiannello.models.Cadeau;
import be.couderiannello.models.ListeCadeau;

public class CadeauDeleteServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        int userId = (Integer) session.getAttribute("userId");

        Integer cadeauId;
        Integer listeId;

        try {
            cadeauId = Integer.parseInt(req.getParameter("cadeauId"));
            listeId  = Integer.parseInt(req.getParameter("listeId"));
        } catch (NumberFormatException ex) {
            req.setAttribute("error", "Erreur : Paramètres invalides.");
            req.getRequestDispatcher("/WEB-INF/view/listeCadeau/manage.jsp")
               .forward(req, resp);
            return;
        }

        try {
            ListeCadeau liste = ListeCadeau.findById(listeId, ListeCadeauDAO.getInstance(), true, true, true);

            if (liste == null || liste.getCreator() == null || liste.getCreator().getId() != userId) {
                req.setAttribute("error", "Erreur : Accès interdit.");
                req.setAttribute("liste", liste);
                req.getRequestDispatcher("/WEB-INF/view/listeCadeau/manage.jsp")
                   .forward(req, resp);
                return;
            }

            Cadeau c = Cadeau.findById(cadeauId, CadeauDAO.getInstance());
            Cadeau.delete(c, CadeauDAO.getInstance());

            resp.sendRedirect(req.getContextPath() + "/liste/manage?id=" + listeId);

        } catch (IllegalArgumentException | IllegalStateException e) {
            req.setAttribute("error", e.getMessage());

            ListeCadeau refreshed = ListeCadeau.findById(listeId, ListeCadeauDAO.getInstance(), true, true, true);
            req.setAttribute("liste", refreshed);

            req.getRequestDispatcher("/WEB-INF/view/listeCadeau/manage.jsp")
               .forward(req, resp);

        } catch (RuntimeException e) {
            e.printStackTrace();
            req.setAttribute("error", "Erreur : Erreur serveur lors de la suppression du cadeau.");

            ListeCadeau refreshed = ListeCadeau.findById(listeId, ListeCadeauDAO.getInstance(), true, true, true);
            req.setAttribute("liste", refreshed);

            req.getRequestDispatcher("/WEB-INF/view/listeCadeau/manage.jsp")
               .forward(req, resp);
        }
    }
}
