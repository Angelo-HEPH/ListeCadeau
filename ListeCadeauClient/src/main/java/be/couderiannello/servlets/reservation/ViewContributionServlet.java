package be.couderiannello.servlets.reservation;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import be.couderiannello.dao.PersonneDAO;
import be.couderiannello.dao.ReservationDAO;
import be.couderiannello.models.Personne;
import be.couderiannello.models.Reservation;

public class ViewContributionServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        int userId = (Integer) session.getAttribute("userId");

        try {
            PersonneDAO personneDao = PersonneDAO.getInstance();
            Personne user = Personne.findById(userId, personneDao, false, false, false, true);

            if (user == null) {
                session.invalidate();
                resp.sendRedirect(req.getContextPath() + "/login");
                return;
            }

            List<Reservation> reservations = user.getReservations();

            if (reservations != null && !reservations.isEmpty()) {
                ReservationDAO reservationDao = ReservationDAO.getInstance();

                for (int i = 0; i < reservations.size(); i++) {
                    Reservation r = reservations.get(i);

                    Reservation rFull = Reservation.findById(r.getId(), reservationDao, true, false);
                    if (rFull != null) {
                        reservations.set(i, rFull);
                    }
                }
            }

            req.setAttribute("contributions", reservations);
            req.getRequestDispatcher("/WEB-INF/view/reservation/viewContributions.jsp")
               .forward(req, resp);

        } catch (Exception e) {
            req.setAttribute("error", e.getMessage());
            req.getRequestDispatcher("/WEB-INF/view/reservation/viewContributions.jsp")
               .forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        doGet(req, resp);
    }
}
