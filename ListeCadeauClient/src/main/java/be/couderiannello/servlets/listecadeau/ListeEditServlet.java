package be.couderiannello.servlets.listecadeau;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import be.couderiannello.dao.CadeauDAO;
import be.couderiannello.dao.ListeCadeauDAO;
import be.couderiannello.models.Cadeau;
import be.couderiannello.models.ListeCadeau;

public class ListeEditServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        int userId = (Integer) session.getAttribute("userId");

        try {
            String idParam = req.getParameter("id");
            if (idParam == null || idParam.isBlank()) {
                throw new IllegalArgumentException("Paramètre id manquant.");
            }

            int listeId;
            try {
                listeId = Integer.parseInt(idParam);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Paramètre id invalide.");
            }

            ListeCadeau liste = ListeCadeau.findById(listeId, ListeCadeauDAO.getInstance(), true, false, true);

            if (liste == null) {
                throw new IllegalStateException("Erreur : Liste introuvable.");
            }

            if (liste.getCreator() == null || liste.getCreator().getId() != userId) {
                throw new IllegalStateException("Erreur : Accès interdit.");
            }
            
            if (liste.getCadeaux() != null) {
                List<Cadeau> fullCadeaux = new ArrayList<>();

                for (Cadeau c : liste.getCadeaux()) {
                    Cadeau full = Cadeau.findById(c.getId(), CadeauDAO.getInstance(), false, true);
                    fullCadeaux.add(full);
                }

                liste.setCadeaux(fullCadeaux);
            }

            req.setAttribute("liste", liste);
            req.getRequestDispatcher("/WEB-INF/view/listeCadeau/edit.jsp").forward(req, resp);

        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("error", e.getMessage());
            req.getRequestDispatcher("/WEB-INF/view/listeCadeau/edit.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        int userId = (Integer) session.getAttribute("userId");

        try {
            String idParam = req.getParameter("id");
            if (idParam == null || idParam.isBlank()) {
                throw new IllegalArgumentException("Paramètre id manquant.");
            }

            int listeId;
            try {
                listeId = Integer.parseInt(idParam);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Paramètre id invalide.");
            }

            ListeCadeau liste = ListeCadeau.findById(listeId, ListeCadeauDAO.getInstance(), true, false, true);

            if (liste == null) {
                throw new IllegalStateException("Erreur : Liste introuvable.");
            }

            if (liste.getCreator() == null || liste.getCreator().getId() != userId) {
                throw new IllegalStateException("Erreur : Accès interdit.");
            }
            
            if (liste.getCadeaux() != null) {
                List<Cadeau> fullCadeaux = new ArrayList<>();

                for (Cadeau c : liste.getCadeaux()) {
                    Cadeau full = Cadeau.findById(c.getId(), CadeauDAO.getInstance(), false, true);
                    fullCadeaux.add(full);
                }

                liste.setCadeaux(fullCadeaux);
            }
            
            liste.ensureCanManageCadeaux();


            liste.setTitle(req.getParameter("title"));
            liste.setEvenement(req.getParameter("evenement"));
            liste.setStatut(req.getParameter("statut") != null);

            String date = req.getParameter("expirationDate");
            liste.setExpirationDate(LocalDate.parse(date));

            boolean ok = liste.update(ListeCadeauDAO.getInstance());
            if (!ok) {
                throw new IllegalStateException("Erreur : La mise à jour a échoué.");
            }

            resp.sendRedirect(req.getContextPath() + "/liste/manage?id=" + listeId);

        } catch (Exception e) {
            e.printStackTrace();

                String idParam = req.getParameter("id");
                if (idParam != null && !idParam.isBlank()) {
                    int listeId = Integer.parseInt(idParam);
                    ListeCadeau liste = ListeCadeau.findById(listeId, ListeCadeauDAO.getInstance(), true, false, false);
                    req.setAttribute("liste", liste);
                }

            req.setAttribute("error", e.getMessage());
            req.getRequestDispatcher("/WEB-INF/view/listeCadeau/edit.jsp").forward(req, resp);
        }
    }
}
