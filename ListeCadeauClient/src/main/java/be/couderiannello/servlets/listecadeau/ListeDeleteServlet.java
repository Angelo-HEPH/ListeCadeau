package be.couderiannello.servlets.listecadeau;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import be.couderiannello.dao.CadeauDAO;
import be.couderiannello.dao.ListeCadeauDAO;
import be.couderiannello.dao.PersonneDAO;
import be.couderiannello.models.Cadeau;
import be.couderiannello.models.ListeCadeau;
import be.couderiannello.models.Personne;

public class ListeDeleteServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public ListeDeleteServlet() {
        super();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        int userId = (Integer) session.getAttribute("userId");

        try {
            String idParam = req.getParameter("id");
            if (idParam == null || idParam.isBlank()) {
                throw new IllegalArgumentException("Paramètre id manquant.");
            }

            int listeId;
            try {
                listeId = Integer.parseInt(idParam);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Paramètre id invalide.");
            }

            Personne fullUser = Personne.findById(userId, PersonneDAO.getInstance(), false, true, false, false);

            if (fullUser == null) {
                session.invalidate();
                resp.sendRedirect(req.getContextPath() + "/login");
                return;
            }

            ListeCadeauDAO dao = ListeCadeauDAO.getInstance();
            ListeCadeau liste = ListeCadeau.findById(listeId, dao, true, true, true);

            if (liste == null) {
                throw new IllegalStateException("Erreur : Liste introuvable.");
            }

            CadeauDAO cadeauDao = CadeauDAO.getInstance();

            for (int i = 0; i < liste.getCadeaux().size(); i++) {
                Cadeau light = liste.getCadeaux().get(i);

                    Cadeau full = Cadeau.findById(light.getId(), cadeauDao, false, true);

             full.setListeCadeau(liste);
             liste.getCadeaux().set(i, full);
         }

            if (liste.getCreator() == null || liste.getCreator().getId() != userId) {
                throw new IllegalStateException("Erreur : Accès interdit.");
            }

            liste.ensureCanBeDeleted();

            boolean ok = ListeCadeau.delete(liste, dao);
            if (!ok) {
                throw new IllegalStateException("Erreur : Impossible de supprimer cette liste.");
            }
            
            resp.sendRedirect(req.getContextPath() + "/liste/all");

        } catch (Exception e) {
                Personne fullUser = Personne.findById(userId, PersonneDAO.getInstance(), false, true, false, false);
                if (fullUser == null) {
                    session.invalidate();
                    resp.sendRedirect(req.getContextPath() + "/login");
                    return;
                }

                List<ListeCadeau> listes = fullUser.getListeCadeauCreator();
                req.setAttribute("listes", listes);

            req.setAttribute("error", e.getMessage());
            req.getRequestDispatcher("/WEB-INF/view/listeCadeau/all.jsp").forward(req, resp);
        }
    }
}
