package be.couderiannello.servlets;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import be.couderiannello.dao.ListeCadeauDAO;
import be.couderiannello.models.ListeCadeau;

@WebServlet("/liste/detail")
public class ListeDetailServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        int id = Integer.parseInt(req.getParameter("id"));

        ListeCadeauDAO dao = ListeCadeauDAO.getInstance();
        ListeCadeau l = dao.find(id, true, true, true);

        req.setAttribute("liste", l);

        req.getRequestDispatcher("/WEB-INF/view/listeCadeau/detail.jsp").forward(req, resp);
    }
}
