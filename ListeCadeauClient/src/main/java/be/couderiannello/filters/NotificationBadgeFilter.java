package be.couderiannello.filters;

import java.io.IOException;
import java.util.List;

import javax.servlet.*;
import javax.servlet.http.*;

import be.couderiannello.dao.PersonneDAO;
import be.couderiannello.models.Notification;
import be.couderiannello.models.Personne;

public class NotificationBadgeFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpSession session = req.getSession(false);

        int unreadNotifCount = 0;

        if (session != null && session.getAttribute("userId") != null) {
            int userId = (Integer) session.getAttribute("userId");

            try {
                Personne user = Personne.findById(
                        userId, PersonneDAO.getInstance(), true, false, false, false);

                if (user != null) {
                    List<Notification> notifs = user.getNotifications();
                    if (notifs != null) {
                        for (Notification n : notifs) {
                            if (!n.isRead()) unreadNotifCount++;
                        }
                    }
                }
            } catch (Exception ignored) {
                unreadNotifCount = 0;
            }
        }

        req.setAttribute("unreadNotifCount", unreadNotifCount);
        chain.doFilter(request, response);
    }
}
