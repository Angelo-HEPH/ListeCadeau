package be.couderiannello.servlets.listecadeau;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import be.couderiannello.dao.CadeauDAO;
import be.couderiannello.dao.ListeCadeauDAO;
import be.couderiannello.models.Cadeau;
import be.couderiannello.models.ListeCadeau;

public class ListeViewServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        int userId = (Integer) session.getAttribute("userId");

        try {
            String idParam = req.getParameter("id");
            if (idParam == null || idParam.isBlank()) {
                throw new IllegalArgumentException("Paramètre id manquant.");
            }

            int id;
            try {
                id = Integer.parseInt(idParam);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Paramètre id invalide.");
            }

            ListeCadeauDAO dao = ListeCadeauDAO.getInstance();
            ListeCadeau l = ListeCadeau.findById(id, dao, true, false, true);

            if (l == null) {
                throw new IllegalStateException("Erreur : Liste introuvable.");
            }

            boolean isCreator = (l.getCreator() != null && l.getCreator().getId() == userId);

            if (!l.isStatut() && !isCreator) {
                throw new IllegalStateException("Erreur : Liste pas encore disponible.");
            }

            if (l.getCadeaux() != null && !l.getCadeaux().isEmpty()) {

                CadeauDAO cadeauDao = CadeauDAO.getInstance();
                List<Cadeau> cadeaux = l.getCadeaux();

                for (int i = 0; i < cadeaux.size(); i++) {
                    Cadeau cadeau = cadeaux.get(i);

                    Cadeau cFull = Cadeau.findById(cadeau.getId(), cadeauDao, false, true);

                    if (cFull != null) {
                        cFull.setListeCadeau(l);
                        cadeaux.set(i, cFull);
                    }
                }
                cadeaux.sort(Comparator.comparing(Cadeau::getPriorite));
            }

            String creatorLabel = "Créateur inconnu";
            if (l.getCreator() != null) {
                creatorLabel = l.getCreator().getFirstName() + " " + l.getCreator().getName();
            }

            req.setAttribute("creatorLabel", creatorLabel);
            req.setAttribute("liste", l);
            req.getRequestDispatcher("/WEB-INF/view/listeCadeau/view.jsp").forward(req, resp);

        } catch (Exception e) {
            try {
                String idParam = req.getParameter("id");
                if (idParam != null && !idParam.isBlank()) {
                    int id = Integer.parseInt(idParam);
                    ListeCadeau l = ListeCadeauDAO.getInstance().find(id, true, false, true);
                    req.setAttribute("liste", l);

                    String creatorLabel = "Créateur inconnu";
                    if (l != null && l.getCreator() != null) {
                        creatorLabel = l.getCreator().getFirstName() + " " + l.getCreator().getName();
                    }
                    req.setAttribute("creatorLabel", creatorLabel);
                }
            } catch (Exception ignore) {}

            req.setAttribute("error", e.getMessage());
            req.getRequestDispatcher("/WEB-INF/view/listeCadeau/view.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        doGet(req, resp);
    }
}
