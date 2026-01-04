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

        Integer cadeauId = null;
        Integer listeId = null;

        try {
            cadeauId = Integer.parseInt(req.getParameter("id"));
            listeId  = Integer.parseInt(req.getParameter("listeId"));
        } catch (NumberFormatException ex) {
            req.setAttribute("error", "Erreur : Paramètres invalides.");
            req.getRequestDispatcher("/WEB-INF/view/cadeau/edit.jsp").forward(req, resp);
            return;
        }

        try {
            ListeCadeau liste = ListeCadeau.findById(listeId, ListeCadeauDAO.getInstance(), true, false, false);

            if (liste == null || liste.getCreator() == null || liste.getCreator().getId() != userId) {
                req.setAttribute("error", "Erreur : Accès interdit.");
                req.setAttribute("listeId", listeId);
                req.getRequestDispatcher("/WEB-INF/view/cadeau/edit.jsp").forward(req, resp);
                return;
            }

            Cadeau cadeau = Cadeau.findById(cadeauId, CadeauDAO.getInstance());
            if (cadeau == null) {
                req.setAttribute("error", "Erreur : Cadeau introuvable.");
                req.setAttribute("listeId", listeId);
                req.getRequestDispatcher("/WEB-INF/view/cadeau/edit.jsp").forward(req, resp);
                return;
            }

            req.setAttribute("cadeau", cadeau);
            req.setAttribute("listeId", listeId);
            req.getRequestDispatcher("/WEB-INF/view/cadeau/edit.jsp").forward(req, resp);

        } catch (RuntimeException e) {
            req.setAttribute("error", "Erreur : Erreur serveur lors du chargement du cadeau.");
            req.setAttribute("listeId", listeId);
            req.getRequestDispatcher("/WEB-INF/view/cadeau/edit.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        int userId = (Integer) session.getAttribute("userId");

        Integer cadeauId = null;
        Integer listeId = null;

        try {
            cadeauId = Integer.parseInt(req.getParameter("id"));
            listeId  = Integer.parseInt(req.getParameter("listeId"));
        } catch (NumberFormatException ex) {
            req.setAttribute("error", "Erreur : Paramètres invalides.");
            req.getRequestDispatcher("/WEB-INF/view/cadeau/edit.jsp").forward(req, resp);
            return;
        }

        try {
            ListeCadeau liste = ListeCadeau.findById(listeId, ListeCadeauDAO.getInstance(), true, false, false);

            if (liste == null || liste.getCreator() == null || liste.getCreator().getId() != userId) {
                req.setAttribute("error", "Erreur : Accès interdit.");
                req.setAttribute("listeId", listeId);
                req.getRequestDispatcher("/WEB-INF/view/cadeau/edit.jsp").forward(req, resp);
                return;
            }
            
            liste.ensureCanManageCadeaux();

            Cadeau c = Cadeau.findById(cadeauId, CadeauDAO.getInstance(), false, true);
            if (c == null) {
                throw new IllegalStateException("Cadeau introuvable.");
            }
            
            liste.ensureCanManageCadeaux();
            c.ensureCanBeModifiedOrDeleted();

            double price;
            try {
                price = Double.parseDouble(req.getParameter("price"));
            } catch (NumberFormatException ex) {
                req.setAttribute("error", "Erreur : Le prix doit être un nombre.");
                req.setAttribute("cadeau", c);
                req.setAttribute("listeId", listeId);
                req.getRequestDispatcher("/WEB-INF/view/cadeau/edit.jsp").forward(req, resp);
                return;
            }

            c.setName(req.getParameter("name"));
            c.setDescription(req.getParameter("description"));
            c.setPrice(price);
            c.setLinkSite(req.getParameter("linkSite"));
            c.setPhoto(req.getParameter("photo"));
            c.setPriorite(StatutPriorite.valueOf(req.getParameter("priorite")));

            boolean ok = c.update(CadeauDAO.getInstance());
            if (!ok) {
                req.setAttribute("error", "Erreur : Cadeau introuvable.");
                req.setAttribute("listeId", listeId);
                req.getRequestDispatcher("/WEB-INF/view/cadeau/edit.jsp").forward(req, resp);
                return;
            }

            resp.sendRedirect(req.getContextPath() + "/liste/manage?id=" + listeId);
            return;

        } catch (IllegalArgumentException e) {
            req.setAttribute("error", e.getMessage());
            req.setAttribute("listeId", listeId);
            req.setAttribute("cadeau", Cadeau.findById(cadeauId, CadeauDAO.getInstance()));
            req.getRequestDispatcher("/WEB-INF/view/cadeau/edit.jsp").forward(req, resp);
            return;

        } catch (IllegalStateException e) {
            req.setAttribute("error", e.getMessage());
            req.setAttribute("listeId", listeId);
            req.setAttribute("cadeau", Cadeau.findById(cadeauId, CadeauDAO.getInstance()));
            req.getRequestDispatcher("/WEB-INF/view/cadeau/edit.jsp").forward(req, resp);
            return;

        } catch (RuntimeException e) {
            req.setAttribute("error", "Erreur : Erreur serveur lors de la mise à jour du cadeau.");
            req.setAttribute("listeId", listeId);
            req.setAttribute("cadeau", Cadeau.findById(cadeauId, CadeauDAO.getInstance()));
            req.getRequestDispatcher("/WEB-INF/view/cadeau/edit.jsp").forward(req, resp);
            return;
        }
    }
}
