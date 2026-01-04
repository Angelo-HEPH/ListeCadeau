package be.couderiannello.servlets.auth;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import be.couderiannello.dao.PersonneDAO;
import be.couderiannello.models.Personne;


public class CreateAccountServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public CreateAccountServlet() {
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

        String name = request.getParameter("name");
        String firstName = request.getParameter("firstName");
        String ageStr = request.getParameter("age");
        String street = request.getParameter("street");
        String city = request.getParameter("city");
        String streetNumber = request.getParameter("streetNumber");
        String postalCodeStr = request.getParameter("postalCode");
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        request.setAttribute("name", name);
        request.setAttribute("firstName", firstName);
        request.setAttribute("age", ageStr);
        request.setAttribute("street", street);
        request.setAttribute("city", city);
        request.setAttribute("streetNumber", streetNumber);
        request.setAttribute("postalCode", postalCodeStr);
        request.setAttribute("email", email);

        int age;
        int postalCode;

        try {
            age = Integer.parseInt(ageStr);
        } catch (NumberFormatException ex) {
            request.setAttribute("error", "Erreur : L'âge doit être un nombre.");
            request.getRequestDispatcher("/WEB-INF/view/personne/createAccount.jsp")
                   .forward(request, response);
            return;
        }

        try {
            postalCode = Integer.parseInt(postalCodeStr);
        } catch (NumberFormatException ex) {
            request.setAttribute("error", "Erreur : Le code postal doit être un nombre.");
            request.getRequestDispatcher("/WEB-INF/view/personne/createAccount.jsp")
                   .forward(request, response);
            return;
        }

        try {
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
            p.create(dao);

            response.sendRedirect(request.getContextPath() + "/login");

        } catch (IllegalArgumentException e) {
            request.setAttribute("error", e.getMessage());
            request.getRequestDispatcher("/WEB-INF/view/personne/createAccount.jsp")
                   .forward(request, response);

        } catch (RuntimeException e) {
            request.setAttribute("error",
                "Erreur : Une erreur serveur est survenue. Veuillez réessayer.");
            request.getRequestDispatcher("/WEB-INF/view/personne/createAccount.jsp")
                   .forward(request, response);
        }
    }
}
