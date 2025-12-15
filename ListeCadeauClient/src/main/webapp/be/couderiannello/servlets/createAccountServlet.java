package be.couderiannello.servlets;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import be.couderiannello.dao.PersonneDAO;
import be.couderiannello.models.Personne;

@WebServlet("/createAccount")
public class createAccountServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public createAccountServlet() {
        super();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.getRequestDispatcher("/WEB-INF/view/personne/createAccount.jsp")
               .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            String name = request.getParameter("name");
            String firstName = request.getParameter("firstName");
            int age = Integer.parseInt(request.getParameter("age"));
            String street = request.getParameter("street");
            String city = request.getParameter("city");
            String streetNumber = request.getParameter("streetNumber");
            int postalCode = Integer.parseInt(request.getParameter("postalCode"));
            String email = request.getParameter("email");
            String password = request.getParameter("password");

            Personne p = new Personne();
            p.setName(name);
            p.setFirstName(firstName);
            p.setAge(age);
            p.setStreet(street);
            p.setCity(city);
            p.setStreetNumber(streetNumber);
            p.setPostalCode(postalCode);
            p.setEmail(email);
            p.setPassword(password);

            PersonneDAO dao = PersonneDAO.getInstance();
            int id = p.create(dao);

            p.setId(id);

            request.setAttribute("personne", p);
            request.setAttribute("message", "Compte créé avec succès !");

            request.getRequestDispatcher("/WEB-INF/view/accountSuccess.jsp")
                   .forward(request, response);

        } catch (Exception e) {
        	e.printStackTrace();
            request.setAttribute("error", "Erreur lors de la création : " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/view/personne/createAccount.jsp")
                   .forward(request, response);
        }
    }
}
