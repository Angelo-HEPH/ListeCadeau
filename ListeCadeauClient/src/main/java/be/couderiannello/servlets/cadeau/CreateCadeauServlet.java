package be.couderiannello.servlets.cadeau;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import be.couderiannello.dao.CadeauDAO;
import be.couderiannello.models.Cadeau;
import be.couderiannello.models.ListeCadeau;
import be.couderiannello.enumeration.StatutPriorite;


public class CreateCadeauServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
	        throws ServletException, IOException {

	    String listeIdParam = req.getParameter("listeId");
	    if (listeIdParam == null) {
	        resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Paramètre listeId manquant");
	        return;
	    }

	    try {
	        int listeId = Integer.parseInt(listeIdParam);
	        req.setAttribute("listeId", listeId);
	        req.getRequestDispatcher("/WEB-INF/view/cadeau/addCadeau.jsp").forward(req, resp);
	    } catch (NumberFormatException ex) {
	        resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Paramètre listeId invalide");
	    }
	}


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            int listeId = Integer.parseInt(request.getParameter("listeId"));

            Cadeau c = new Cadeau();
            c.setName(request.getParameter("name"));
            c.setDescription(request.getParameter("description"));
            c.setPrice(Double.parseDouble(request.getParameter("price")));
            c.setPhoto(request.getParameter("photo"));
            c.setLinkSite(request.getParameter("linkSite"));
            c.setPriorite(StatutPriorite.valueOf(request.getParameter("priorite")));

            ListeCadeau l = new ListeCadeau();
            l.setId(listeId);
            c.setListeCadeau(l);

            CadeauDAO dao = CadeauDAO.getInstance();
            c.create(dao);

            response.sendRedirect(request.getContextPath() + "/liste/detail?id=" + listeId);

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", e.getMessage());

            request.setAttribute("listeId", request.getParameter("listeId"));

            request.getRequestDispatcher("/WEB-INF/view/cadeau/addCadeau.jsp")
                   .forward(request, response);
        }
    }
}
