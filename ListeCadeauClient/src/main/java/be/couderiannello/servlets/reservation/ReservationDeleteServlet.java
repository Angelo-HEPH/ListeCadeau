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
       

    public ReservationDeleteServlet() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	@Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        int userId = (Integer) session.getAttribute("userId");
        int reservationId = Integer.parseInt(req.getParameter("reservationId"));

        ReservationDAO reservationDao = ReservationDAO.getInstance();
        CadeauDAO cadeauDao = CadeauDAO.getInstance();

        Reservation r = reservationDao.find(reservationId, true, true);

        if (r == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        boolean isOwner = r.getPersonnes()
                           .stream()
                           .anyMatch(p -> p.getId() == userId);

        if (!isOwner) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        Cadeau c = r.getCadeau();

        reservationDao.delete(r);

        if (c != null) {
            c.recalculerStatutApresContribution();
            cadeauDao.update(c);
        }

        resp.sendRedirect(req.getContextPath() + "/reservation/contributions");
    }

}
