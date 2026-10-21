package controlador;

import dao.AuditoriaDAO;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "AuditoriaServlet", urlPatterns = {"/AuditoriaServlet"})
public class AuditoriaServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            List<AuditoriaDAO.Evento> eventos = AuditoriaDAO.listar(100);
            req.setAttribute("eventos", eventos);
        } catch (SQLException e) {
            log("Auditoria: error al consultar", e);
            req.setAttribute("mensaje", "No se pudo leer la bitacora de auditoria.");
        }
        req.getRequestDispatcher("/auditoria.jsp").forward(req, resp);
    }
}
