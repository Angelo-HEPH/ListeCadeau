package be.couderiannello.servlets.reservation;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import be.couderiannello.dao.CadeauDAO;
import be.couderiannello.dao.ReservationDAO;
import be.couderiannello.models.Cadeau;
import be.couderiannello.models.Reservation;

public class ReservationDeleteServlet extends HttpServlet {
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

        try {
            String reservationIdParam = req.getParameter("reservationId");
            if (reservationIdParam == null || reservationIdParam.isBlank()) {
                throw new IllegalArgumentException("Paramètre reservationId manquant.");
            }

            int reservationId;
            try {
                reservationId = Integer.parseInt(reservationIdParam);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Paramètre reservationId invalide.");
            }

            ReservationDAO reservationDao = ReservationDAO.getInstance();
            CadeauDAO cadeauDao = CadeauDAO.getInstance();

            Reservation r = Reservation.findById(reservationId, reservationDao, true, true);
            if (r == null) {
                throw new IllegalStateException("Réservation introuvable.");
            }

            boolean isOwner = r.getPersonnes() != null && r.getPersonnes().stream().anyMatch(p -> p.getId() == userId);

            if (!isOwner) {
                throw new IllegalStateException("Vous n'avez pas le droit de supprimer cette contribution.");
            }

            int cadeauId = (r.getCadeau() != null) ? r.getCadeau().getId() : 0;

            Reservation.delete(r, reservationDao);

            Cadeau cFull = Cadeau.findById(cadeauId, cadeauDao, false, true);
            if (cFull != null) {
                cFull.retirerContribution(r);
                cFull.update(cadeauDao);
            }


            resp.sendRedirect(req.getContextPath() + "/reservation/contributions");

        } catch (Exception e) {
            req.setAttribute("error", e.getMessage());
            req.getRequestDispatcher("/WEB-INF/view/reservation/viewContributions.jsp")
               .forward(req, resp);
        }
    }
}
