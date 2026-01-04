package be.couderiannello.servlets.reservation;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import be.couderiannello.dao.CadeauDAO;
import be.couderiannello.dao.ListeCadeauDAO;
import be.couderiannello.dao.ReservationDAO;
import be.couderiannello.models.Cadeau;
import be.couderiannello.models.ListeCadeau;
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

        try {
            String cadeauIdParam = req.getParameter("cadeauId");
            String listeIdParam  = req.getParameter("listeId");

            if (cadeauIdParam == null || listeIdParam == null) {
                throw new IllegalArgumentException("Paramètres manquants.");
            }

            int cadeauId;
            int listeId;
            try {
                cadeauId = Integer.parseInt(cadeauIdParam);
                listeId  = Integer.parseInt(listeIdParam);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Paramètres invalides.");
            }

            CadeauDAO cadeauDao = CadeauDAO.getInstance();
            ReservationDAO reservationDao = ReservationDAO.getInstance();

            Cadeau c = Cadeau.findById(cadeauId, cadeauDao, false, true);
            if (c == null) {
                throw new IllegalStateException("Cadeau introuvable.");
            }

            if (c.getListeCadeau().getCreator().getId() == userId) {
                throw new IllegalArgumentException("Le créateur ne peut pas réserver.");
            }

            Reservation r = Reservation.creerReservationComplete(cadeauId, userId, c.getPrice());
            r.create(reservationDao);

            c.addContribution(r);
            c.update(cadeauDao);

            resp.sendRedirect(req.getContextPath() + "/liste/view?id=" + listeId);

        } catch (Exception e) {
            e.printStackTrace();

                String listeIdParam = req.getParameter("listeId");
                if (listeIdParam != null && !listeIdParam.isBlank()) {
                    int listeId = Integer.parseInt(listeIdParam);

                    ListeCadeau l = ListeCadeau.findById(listeId, ListeCadeauDAO.getInstance(), true, false, true);

                    if (l != null && l.getCadeaux() != null && !l.getCadeaux().isEmpty()) {
                        CadeauDAO cadeauDao = CadeauDAO.getInstance();
                        List<Cadeau> cadeaux = l.getCadeaux();

                        for (int i = 0; i < cadeaux.size(); i++) {
                            Cadeau cLight = cadeaux.get(i);
                            Cadeau cFull = Cadeau.findById(cLight.getId(), cadeauDao, false, true);

                            if (cFull != null) {
                                cFull.setListeCadeau(l);
                                cadeaux.set(i, cFull);
                            }
                        }
                    }

                    String creatorLabel = "Créateur inconnu";
                    if (l != null && l.getCreator() != null) {
                        creatorLabel = l.getCreator().getFirstName() + " " + l.getCreator().getName();
                    }

                    req.setAttribute("creatorLabel", creatorLabel);
                    req.setAttribute("liste", l);
                }

            req.setAttribute("error", e.getMessage());
            req.getRequestDispatcher("/WEB-INF/view/listeCadeau/view.jsp").forward(req, resp);
        }
    }
}
