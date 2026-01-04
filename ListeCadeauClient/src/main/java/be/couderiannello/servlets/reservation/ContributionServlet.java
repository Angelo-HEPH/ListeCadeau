package be.couderiannello.servlets.reservation;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.*;

import be.couderiannello.dao.CadeauDAO;
import be.couderiannello.dao.ListeCadeauDAO;
import be.couderiannello.dao.NotificationDAO;
import be.couderiannello.dao.ReservationDAO;
import be.couderiannello.enumeration.StatutCadeau;
import be.couderiannello.models.Cadeau;
import be.couderiannello.models.ListeCadeau;
import be.couderiannello.models.Notification;
import be.couderiannello.models.Personne;
import be.couderiannello.models.Reservation;

public class ContributionServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        int userId = (Integer) session.getAttribute("userId");

        String cadeauIdParam = req.getParameter("cadeauId");
        String amountParam   = req.getParameter("amount");
        String listeIdParam  = req.getParameter("listeId");

        String errorMsg = null;
        Integer listeIdToReload = null;

        try {
            if (cadeauIdParam == null || amountParam == null || listeIdParam == null) {
                throw new IllegalArgumentException("Paramètres manquants.");
            }

            int cadeauId;
            double amount;
            int listeId;

            try {
                cadeauId = Integer.parseInt(cadeauIdParam);
                amount   = Double.parseDouble(amountParam);
                listeId  = Integer.parseInt(listeIdParam);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Paramètres invalides.");
            }

            listeIdToReload = listeId;

            CadeauDAO cadeauDao = CadeauDAO.getInstance();
            ReservationDAO reservationDao = ReservationDAO.getInstance();

            Cadeau c = Cadeau.findById(cadeauId, cadeauDao, true, true);
            if (c == null) {
                throw new IllegalStateException("Cadeau introuvable.");
            }

            if (c.getListeCadeau() == null) {
                throw new IllegalStateException("Liste du cadeau introuvable.");
            }

            if (c.getListeCadeau().getId() != listeId) {
                throw new IllegalArgumentException("Incohérence liste/cadeau.");
            }

            StatutCadeau statutAvant = c.getStatutCadeau();

            if (c.getListeCadeau().getCreator().getId() == userId) {
                throw new IllegalArgumentException("Le créateur ne peut pas contribuer.");
            }

            Reservation r = Reservation.creerContribution(cadeauId, userId, amount);
            r.create(reservationDao);

            c.addContribution(r);
            StatutCadeau statutApres = c.getStatutCadeau();
            c.update(cadeauDao);

            if (statutAvant != StatutCadeau.PARTICIPATION && statutApres == StatutCadeau.PARTICIPATION) {

                ListeCadeau liste = ListeCadeau.findById(c.getListeCadeau().getId(), ListeCadeauDAO.getInstance(),true, true, false);

                if (liste != null && liste.getInvites() != null && !liste.getInvites().isEmpty()) {

                    NotificationDAO notifDao = NotificationDAO.getInstance();

                    String msg = "Participation ouverte pour le cadeau '" + c.getName()
                               + "' dans la liste '" + liste.getTitle() + "' de "
                               + liste.getCreator().getName() + " " + liste.getCreator().getFirstName() + ".";

                    for (Personne invite : liste.getInvites()) {
                        if (invite.getId() == userId) {
                        	continue;
                        }
                        if (liste.getCreator() != null && invite.getId() == liste.getCreator().getId()) {
                        	continue;
                        }

                        Notification n = new Notification();
                        n.setMessage(msg);

                        Personne ref = new Personne();
                        ref.setId(invite.getId());
                        n.setPersonne(ref);

                        n.create(notifDao);
                    }
                }
            }

            resp.sendRedirect(req.getContextPath() + "/liste/view?id=" + listeId);
            return;

        } catch (IllegalArgumentException e) {
            errorMsg = e.getMessage();
        } catch (IllegalStateException e) {
            errorMsg = e.getMessage();
        } catch (Exception e) {
            e.printStackTrace();
            errorMsg = "Erreur serveur lors de la contribution.";
        }

        if (listeIdToReload != null) {
            ListeCadeau l = ListeCadeau.findById(listeIdToReload, ListeCadeauDAO.getInstance(), true, false, true);

            req.setAttribute("liste", l);

            String creatorLabel = "Créateur inconnu";
            if (l != null && l.getCreator() != null) {
                creatorLabel = l.getCreator().getFirstName() + " " + l.getCreator().getName();
            }
            req.setAttribute("creatorLabel", creatorLabel);

            req.setAttribute("error", errorMsg);
            req.getRequestDispatcher("/WEB-INF/view/listeCadeau/view.jsp").forward(req, resp);
            return;
        }

        req.setAttribute("error", errorMsg);
        req.getRequestDispatcher("/WEB-INF/view/home.jsp").forward(req, resp);
    }
}