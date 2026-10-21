package controlador;

import dao.EntradaDAO;
import filtro.SesionFilter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import modelo.Entrada;

@WebServlet(name = "MisEntradasServlet", urlPatterns = {"/MisEntradasServlet"})
public class MisEntradasServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession sesion = req.getSession(false);
        Object idClienteAttr = sesion == null ? null : sesion.getAttribute(SesionFilter.ATR_ID_CLIENTE);
        if (!(idClienteAttr instanceof Integer)) {
            resp.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }
        try {
            List<Entrada> entradas = EntradaDAO.listarPorCliente(
                    ((Integer) idClienteAttr).intValue());
            req.setAttribute("entradas", entradas);
        } catch (SQLException e) {
            log("Mis entradas: error al consultar", e);
            req.setAttribute("mensaje", "No se pudieron cargar tus entradas.");
        }
        req.getRequestDispatcher("/misentradas.jsp").forward(req, resp);
    }
}
