package be.couderiannello.servlets.reservation;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.*;

import be.couderiannello.dao.CadeauDAO;
import be.couderiannello.dao.ReservationDAO;
import be.couderiannello.models.Cadeau;
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

        if (cadeauIdParam == null || amountParam == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Paramètres manquants");
            return;
        }

        int cadeauId;
        double amount;

        try {
            cadeauId = Integer.parseInt(cadeauIdParam);
            amount   = Double.parseDouble(amountParam);
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Paramètres invalides");
            return;
        }

        CadeauDAO cadeauDao = CadeauDAO.getInstance();
        ReservationDAO reservationDao = ReservationDAO.getInstance();

        try {
            Cadeau c = Cadeau.findById(cadeauId, cadeauDao, false, true);
            if (c == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Cadeau introuvable");
                return;
            }

            c.verifierContribution(amount);

            Reservation r = Reservation.creerContribution(cadeauId, userId, amount);
            r.create(reservationDao);
            c.addReservation(r);

            c.recalculerStatutApresContribution();

            c.update(cadeauDao);

            int listeIdRedirect = c.getListeCadeau().getId();
            resp.sendRedirect(req.getContextPath() + "/liste/view?id=" + listeIdRedirect);

        } catch (IllegalArgumentException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (IllegalStateException e) {
            resp.sendError(HttpServletResponse.SC_CONFLICT, e.getMessage());
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erreur contribution");
        }
    }
}
