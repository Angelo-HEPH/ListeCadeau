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

        try {
            String title = request.getParameter("title");
            String evenement = request.getParameter("evenement");
            String expirationDate = request.getParameter("expirationDate");

            var session = request.getSession(false);
            if (session == null || session.getAttribute("userId") == null) {
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }
            int idCreator = (Integer) session.getAttribute("userId");
            PersonneDAO personneDAO = PersonneDAO.getInstance();
            Personne p = Personne.findById(idCreator, personneDAO);

            ListeCadeau l = new ListeCadeau();
            l.setTitle(title);
            l.setEvenement(evenement);
            l.setExpirationDate(LocalDate.parse(expirationDate));
            l.setStatut(true);
            l.setCreator(p);
            l.setShareLink("https://lien-default.com");

            ListeCadeauDAO dao = ListeCadeauDAO.getInstance();
            int idListe = l.create(dao);

            response.sendRedirect(request.getContextPath() + "/liste/all");

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", e.getMessage());
            request.getRequestDispatcher("/WEB-INF/view/listeCadeau/createListeCadeau.jsp")
                   .forward(request, response);
        }
    }
}
