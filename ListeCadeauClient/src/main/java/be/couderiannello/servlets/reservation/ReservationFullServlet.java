package be.couderiannello.servlets.reservation;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.*;

import be.couderiannello.dao.CadeauDAO;
import be.couderiannello.dao.ReservationDAO;
import be.couderiannello.models.Cadeau;
import be.couderiannello.models.Reservation;

public class ReservationFullServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        int userId = (Integer) session.getAttribute("userId");

        String cadeauIdParam = req.getParameter("cadeauId");
        String listeIdParam  = req.getParameter("listeId");

        if (cadeauIdParam == null || listeIdParam == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Paramètres manquants.");
            return;
        }

        int cadeauId;
        int listeId;
        try {
            cadeauId = Integer.parseInt(cadeauIdParam);
            listeId  = Integer.parseInt(listeIdParam);
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Paramètres invalides.");
            return;
        }

        CadeauDAO cadeauDao = CadeauDAO.getInstance();
        ReservationDAO reservationDao = ReservationDAO.getInstance();

        try {
            Cadeau c = cadeauDao.find(cadeauId);
            if (c == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Cadeau introuvable.");
                return;
            }

            c.reserverCompletement();

            Reservation r = Reservation.creerReservationComplete(cadeauId, userId, c.getPrice());
            r.create(reservationDao);
            c.addReservation(r);

            c.update(cadeauDao);

            resp.sendRedirect(req.getContextPath() + "/liste/view?id=" + listeId);

        } catch (IllegalStateException e) {
            resp.sendError(HttpServletResponse.SC_CONFLICT, e.getMessage());
        } catch (IllegalArgumentException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erreur réservation complète.");
        }
    }
}
