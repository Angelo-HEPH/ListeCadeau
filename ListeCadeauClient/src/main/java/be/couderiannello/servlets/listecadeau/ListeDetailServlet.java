package be.couderiannello.servlets.listecadeau;

import java.io.IOException;
import java.util.NoSuchElementException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import be.couderiannello.dao.ListeCadeauDAO;
import be.couderiannello.dao.PersonneDAO;
import be.couderiannello.models.ListeCadeau;
import be.couderiannello.models.Personne;

public class ListeDetailServlet extends HttpServlet {

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
        ListeCadeau l = dao.find(id, false, true, true);

        if (l == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Liste introuvable");
            return;
        }

        req.setAttribute("liste", l);
        req.getRequestDispatcher("/WEB-INF/view/listeCadeau/detail.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String action = req.getParameter("action");
        if (action == null || action.isBlank()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Paramètre action manquant");
            return;
        }

        String listeIdParam = req.getParameter("listeId");
        if (listeIdParam == null || listeIdParam.isBlank()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Paramètre listeId manquant");
            return;
        }

        int listeId;
        try {
            listeId = Integer.parseInt(listeIdParam);
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Paramètre listeId invalide");
            return;
        }

        ListeCadeauDAO listeDao = ListeCadeauDAO.getInstance();

        switch (action) {

            case "invite": {
                String email = req.getParameter("email");
                email = email.trim().toLowerCase();

                ListeCadeau liste = listeDao.find(listeId, false, false, false);
                if (liste == null) {
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Liste introuvable");
                    return;
                }

                PersonneDAO personneDao = PersonneDAO.getInstance();
                Personne p = personneDao.findByEmail(email);
                if (p == null) {
                    reloadWithError(req, resp, listeId, "Aucune personne trouvée avec cet email.");
                    return;
                }

                try {
                    liste.addInvite(p, listeDao);

                } catch (IllegalArgumentException ex) {
                    reloadWithError(req, resp, listeId, ex.getMessage());
                    return;

                } catch (IllegalStateException ex) {
                    reloadWithError(req, resp, listeId, ex.getMessage());
                    return;

                } catch (Exception ex) {
                    reloadWithError(req, resp, listeId, "Erreur lors de l'invitation.");
                    return;
                }

                resp.sendRedirect(req.getContextPath() + "/liste/detail?id=" + listeId);
                return;
            }

            case "remove": {
                String personneIdParam = req.getParameter("personneId");
                if (personneIdParam == null || personneIdParam.isBlank()) {
                    reloadWithError(req, resp, listeId, "Identifiant invité manquant.");
                    return;
                }

                int personneId;
                try {
                    personneId = Integer.parseInt(personneIdParam);
                } catch (NumberFormatException e) {
                    reloadWithError(req, resp, listeId, "Identifiant invité invalide.");
                    return;
                }

                ListeCadeau liste = listeDao.find(listeId, false, true, false);
                if (liste == null) {
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Liste introuvable");
                    return;
                }

                try {
                    liste.removeInvite(personneId, listeDao);

                } catch (NoSuchElementException ex) {
                    reloadWithError(req, resp, listeId, "Invitation introuvable.");
                    return;

                } catch (IllegalArgumentException ex) {
                    reloadWithError(req, resp, listeId, ex.getMessage());
                    return;

                } catch (Exception ex) {
                    reloadWithError(req, resp, listeId, "Erreur lors de la suppression de l'invité.");
                    return;
                }

                resp.sendRedirect(req.getContextPath() + "/liste/detail?id=" + listeId);
                return;
            }

            default:
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Action invalide : " + action);
        }
    }

    private void reloadWithError(HttpServletRequest req, HttpServletResponse resp, int listeId, String errorMsg)
            throws ServletException, IOException {

        ListeCadeauDAO dao = ListeCadeauDAO.getInstance();

        ListeCadeau l = dao.find(listeId, false, true, true);
        if (l == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Liste introuvable");
            return;
        }

        req.setAttribute("error", errorMsg);
        req.setAttribute("liste", l);
        req.getRequestDispatcher("/WEB-INF/view/listeCadeau/detail.jsp").forward(req, resp);
    }
}
