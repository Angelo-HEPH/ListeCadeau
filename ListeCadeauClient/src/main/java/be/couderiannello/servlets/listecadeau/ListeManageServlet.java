package be.couderiannello.servlets.listecadeau;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import be.couderiannello.dao.ListeCadeauDAO;
import be.couderiannello.dao.PersonneDAO;
import be.couderiannello.models.ListeCadeau;
import be.couderiannello.models.Personne;

public class ListeManageServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        try {
            String idParam = req.getParameter("id");
            if (idParam == null || idParam.isBlank()) {
                throw new IllegalArgumentException("Paramètre id manquant.");
            }

            int id;
            try {
                id = Integer.parseInt(idParam);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Paramètre id invalide.");
            }

            ListeCadeau l = ListeCadeauDAO.getInstance().find(id, false, true, true);
            if (l == null) {
                throw new IllegalStateException("Erreur : Liste introuvable.");
            }

            req.setAttribute("liste", l);
            req.getRequestDispatcher("/WEB-INF/view/listeCadeau/manage.jsp")
               .forward(req, resp);

        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("error", e.getMessage());
            req.getRequestDispatcher("/WEB-INF/view/listeCadeau/manage.jsp")
               .forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        try {
            String action = req.getParameter("action");
            if (action == null || action.isBlank()) {
                throw new IllegalArgumentException("Paramètre action manquant.");
            }

            String listeIdParam = req.getParameter("listeId");
            if (listeIdParam == null || listeIdParam.isBlank()) {
                throw new IllegalArgumentException("Paramètre listeId manquant.");
            }

            int listeId;
            try {
                listeId = Integer.parseInt(listeIdParam);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Paramètre listeId invalide.");
            }

            ListeCadeauDAO listeDao = ListeCadeauDAO.getInstance();
            ListeCadeau liste;

            switch (action) {

                case "invite": {
                    String email = req.getParameter("email");
                    if (email == null || email.isBlank()) {
                        throw new IllegalArgumentException("Email requis.");
                    }

                    liste = ListeCadeau.findById(listeId, listeDao);
                    if (liste == null) {
                        throw new IllegalStateException("Erreur : Liste introuvable.");
                    }

                    Personne p = Personne.findByEmail(email.trim().toLowerCase(), PersonneDAO.getInstance());

                    if (p == null) {
                        throw new IllegalArgumentException("Aucune personne trouvée avec cet email.");
                    }

                    liste.addInvite(p, listeDao);

                    resp.sendRedirect(req.getContextPath() + "/liste/manage?id=" + listeId);
                    return;
                }

                case "remove": {
                    String personneIdParam = req.getParameter("personneId");
                    if (personneIdParam == null || personneIdParam.isBlank()) {
                        throw new IllegalArgumentException("Identifiant invité manquant.");
                    }

                    int personneId;
                    try {
                        personneId = Integer.parseInt(personneIdParam);
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException("Identifiant invité invalide.");
                    }

                    liste = listeDao.find(listeId, false, true, false);
                    if (liste == null) {
                        throw new IllegalStateException("Erreur : Liste introuvable.");
                    }

                    liste.removeInvite(personneId, listeDao);

                    resp.sendRedirect(req.getContextPath() + "/liste/manage?id=" + listeId);
                    return;
                }

                default:
                    throw new IllegalArgumentException("Action invalide : " + action);
            }

        } catch (Exception e) {
            e.printStackTrace();

            try {
                String listeIdParam = req.getParameter("listeId");
                if (listeIdParam != null) {
                    int listeId = Integer.parseInt(listeIdParam);
                    ListeCadeau l = ListeCadeau.findById(listeId, ListeCadeauDAO.getInstance(), false, true, true);
                    req.setAttribute("liste", l);
                }
            } catch (Exception ignore) {}

            req.setAttribute("error", e.getMessage());
            req.getRequestDispatcher("/WEB-INF/view/listeCadeau/manage.jsp")
               .forward(req, resp);
        }
    }
}
