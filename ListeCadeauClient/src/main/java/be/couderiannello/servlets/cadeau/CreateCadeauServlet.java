package be.couderiannello.servlets.cadeau;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import be.couderiannello.dao.CadeauDAO;
import be.couderiannello.dao.ListeCadeauDAO;
import be.couderiannello.enumeration.StatutPriorite;
import be.couderiannello.models.Cadeau;
import be.couderiannello.models.ListeCadeau;

public class CreateCadeauServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        try {
            String listeIdParam = req.getParameter("listeId");
            if (listeIdParam == null || listeIdParam.isBlank()) {
                throw new IllegalArgumentException("Erreur : Paramètre listeId manquant.");
            }

            int listeId;
            try {
                listeId = Integer.parseInt(listeIdParam);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Erreur : Paramètre listeId invalide.");
            }

            ListeCadeau l = ListeCadeau.findById(listeId, ListeCadeauDAO.getInstance(), false, true, false);

            if (l == null) {
                throw new IllegalStateException("Liste introuvable.");
            }

            l.ensureCanBeModified();

            req.setAttribute("liste", l);
            req.setAttribute("listeId", listeId);

            req.getRequestDispatcher("/WEB-INF/view/cadeau/addCadeau.jsp")
               .forward(req, resp);

        } catch (Exception e) {
            req.setAttribute("error", e.getMessage());

            String listeIdParam = req.getParameter("listeId");
            if (listeIdParam != null) {
                req.setAttribute("listeId", listeIdParam);
            }

            req.getRequestDispatcher("/WEB-INF/view/cadeau/addCadeau.jsp")
               .forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String listeIdStr = request.getParameter("listeId");
        request.setAttribute("listeId", listeIdStr);

        request.setAttribute("name", request.getParameter("name"));
        request.setAttribute("description", request.getParameter("description"));
        request.setAttribute("price", request.getParameter("price"));
        request.setAttribute("photo", request.getParameter("photo"));
        request.setAttribute("linkSite", request.getParameter("linkSite"));
        request.setAttribute("priorite", request.getParameter("priorite"));

        int listeId;
        try {
            listeId = Integer.parseInt(listeIdStr);
        } catch (NumberFormatException ex) {
            request.setAttribute("error", "Erreur : Paramètre listeId invalide.");
            request.getRequestDispatcher("/WEB-INF/view/cadeau/addCadeau.jsp")
                   .forward(request, response);
            return;
        }

        double price;
        try {
            price = Double.parseDouble(request.getParameter("price"));
        } catch (NumberFormatException ex) {
            request.setAttribute("error", "Erreur : Le prix doit être un nombre.");
            request.getRequestDispatcher("/WEB-INF/view/cadeau/addCadeau.jsp")
                   .forward(request, response);
            return;
        }

        try {
            Cadeau c = new Cadeau();
            c.setName(request.getParameter("name"));
            c.setDescription(request.getParameter("description"));
            c.setPrice(price);
            c.setPhoto(request.getParameter("photo"));
            c.setLinkSite(request.getParameter("linkSite"));
            c.setPriorite(StatutPriorite.valueOf(request.getParameter("priorite")));

            ListeCadeau l = ListeCadeau.findById(listeId, ListeCadeauDAO.getInstance(), false, true, false);

            if (l == null) {
                throw new IllegalStateException("Liste introuvable.");
            }

            l.ensureCanBeModified();

            c.setListeCadeau(l);

            c.create(CadeauDAO.getInstance());

            response.sendRedirect(request.getContextPath() + "/liste/manage?id=" + listeId);

        } catch (IllegalArgumentException e) {
            request.setAttribute("error", e.getMessage());
            request.getRequestDispatcher("/WEB-INF/view/cadeau/addCadeau.jsp")
                   .forward(request, response);

        } catch (RuntimeException e) {
            request.setAttribute("error", "Erreur : Une erreur serveur est survenue lors de la création du cadeau.");
            request.getRequestDispatcher("/WEB-INF/view/cadeau/addCadeau.jsp")
                   .forward(request, response);
        }
    }
}
