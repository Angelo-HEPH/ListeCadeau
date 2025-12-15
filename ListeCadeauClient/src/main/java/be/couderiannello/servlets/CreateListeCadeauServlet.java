package be.couderiannello.servlets;

import java.io.IOException;
import java.time.LocalDate;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import be.couderiannello.dao.ListeCadeauDAO;
import be.couderiannello.models.ListeCadeau;
import be.couderiannello.models.Personne;

@WebServlet("/liste/create")
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
            String shareLink = request.getParameter("shareLink");

            Personne user = (Personne) request.getSession().getAttribute("user");

            ListeCadeau l = new ListeCadeau();
            l.setTitle(title);
            l.setEvenement(evenement);
            l.setExpirationDate(LocalDate.parse(expirationDate));
            l.setStatut(true);
            l.setShareLink(shareLink);
            l.setCreator(user);

            ListeCadeauDAO dao = ListeCadeauDAO.getInstance();
            int id = l.create(dao);

            response.sendRedirect(request.getContextPath() + "/liste/" + id);

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", e.getMessage());
            request.getRequestDispatcher("/WEB-INF/view/listeCadeau/createListeCadeau.jsp")
                   .forward(request, response);
        }
    }
}
