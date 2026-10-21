package controlador;

import dao.FuncionDAO;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Set;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import modelo.Funcion;
import util.Seguridad;

@WebServlet(name = "ButacasServlet", urlPatterns = {"/ButacasServlet"})
public class ButacasServlet extends HttpServlet {

    static final java.util.List<String> FILAS = java.util.Arrays.asList(
            "A", "B", "C", "D", "E", "F", "G", "H", "I", "J");
    static final int COLUMNAS = 12;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        int idFuncion = Seguridad.entero(req.getParameter("idFuncion"));
        if (idFuncion <= 0) {
            mensajeSesion(req, "Selecciona una funcion valida.");
            resp.sendRedirect(req.getContextPath() + "/TiendaServlet");
            return;
        }
        try {
            Funcion funcion = FuncionDAO.buscarPorId(idFuncion);
            if (funcion == null) {
                mensajeSesion(req, "La funcion seleccionada no existe.");
                resp.sendRedirect(req.getContextPath() + "/TiendaServlet");
                return;
            }
            Set<String> ocupadas = FuncionDAO.butacasOcupadas(idFuncion);
            java.util.Map<String, String> estado = new java.util.HashMap<String, String>();
            for (String letra : FILAS) {
                for (int n = 1; n <= COLUMNAS; n++) {
                    String butaca = letra + n;
                    estado.put(butaca, ocupadas.contains(butaca) ? "ocupada" : "libre");
                }
            }
            req.setAttribute("funcion", funcion);
            req.setAttribute("filas", FILAS);
            req.setAttribute("estado", estado);
            req.getRequestDispatcher("/butacas.jsp").forward(req, resp);
        } catch (SQLException e) {
            log("Butacas: error al consultar", e);
            mensajeSesion(req, "No se pudo cargar el mapa de butacas.");
            resp.sendRedirect(req.getContextPath() + "/TiendaServlet");
        }
    }

    private void mensajeSesion(HttpServletRequest req, String texto) {
        req.getSession(true).setAttribute("mensaje", texto);
    }
}
