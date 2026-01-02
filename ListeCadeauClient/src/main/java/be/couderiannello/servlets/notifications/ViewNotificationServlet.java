package be.couderiannello.servlets.notifications;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import be.couderiannello.dao.NotificationDAO;
import be.couderiannello.dao.PersonneDAO;
import be.couderiannello.models.Notification;
import be.couderiannello.models.Personne;

public class ViewNotificationServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        int userId = (Integer) session.getAttribute("userId");

        try {
            Personne user = Personne.findById(userId, PersonneDAO.getInstance(), true, false, false, false);

            List<Notification> notifications = user.getNotifications();
            req.setAttribute("notifications", notifications);

            req.getRequestDispatcher("/WEB-INF/view/notification/view.jsp")
               .forward(req, resp);

        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erreur chargement des notifications");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        int userId = (Integer) session.getAttribute("userId");

        try {
            Personne user = Personne.findById(userId,  PersonneDAO.getInstance(), true, false, false, false);
            
            NotificationDAO notifDao = NotificationDAO.getInstance();

            if (user.getNotifications() != null) {
                for (Notification n : user.getNotifications()) {
                    if (!n.isRead()) {
                        n.setRead(true);
                        n.update(notifDao);
                    }
                }
            }

            resp.sendRedirect(req.getContextPath() + "/notifications");

        } catch (Exception e) {
            resp.sendError(
                HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                "Erreur mise à jour notifications"
            );
        }
    }
}