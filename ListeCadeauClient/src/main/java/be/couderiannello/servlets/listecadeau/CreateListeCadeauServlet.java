package be.couderiannello.servlets.listecadeau;

import java.io.IOException;
import java.time.LocalDate;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import be.couderiannello.dao.ListeCadeauDAO;
import be.couderiannello.dao.PersonneDAO;
import be.couderiannello.models.ListeCadeau;
import be.couderiannello.models.Personne;

public class CreateListeCadeauServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.getRequestDispatcher("/WEB-INF/view/listeCadeau/createListeCadeau.jsp")
               .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String title = request.getParameter("title");
        String evenement = request.getParameter("evenement");
        String expirationDateStr = request.getParameter("expirationDate");

        request.setAttribute("title", title);
        request.setAttribute("evenement", evenement);
        request.setAttribute("expirationDate", expirationDateStr);

        try {
            int idCreator = (Integer) request.getSession(false).getAttribute("userId");

            PersonneDAO personneDAO = PersonneDAO.getInstance();
            Personne p = Personne.findById(idCreator, personneDAO);

            if (p == null) {
                request.getSession(false).invalidate();
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }

            LocalDate expirationDate;
            try {
                expirationDate = LocalDate.parse(expirationDateStr);
            } catch (Exception ex) {
                throw new IllegalArgumentException("Date d'expiration invalide.");
            }

            ListeCadeau l = new ListeCadeau();
            l.setTitle(title);
            l.setEvenement(evenement);
            l.setExpirationDate(expirationDate);
            l.setStatut(true);
            l.setCreator(p);
            l.setShareLink("https://lien-default.com");

            ListeCadeauDAO dao = ListeCadeauDAO.getInstance();
            int idListe = l.create(dao);

            String shareLink = "http://localhost:8080/ListeCadeauClient/liste/view?id=" + idListe;

            l.setId(idListe);
            l.setShareLink(shareLink);
            dao.update(l);

            response.sendRedirect(request.getContextPath() + "/liste/view?id=" + idListe);

        } catch (IllegalArgumentException e) {
            request.setAttribute("error", e.getMessage());
            request.getRequestDispatcher("/WEB-INF/view/listeCadeau/createListeCadeau.jsp")
                   .forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", e.getMessage());
            request.getRequestDispatcher("/WEB-INF/view/listeCadeau/createListeCadeau.jsp")
                   .forward(request, response);
        }
    }
}
