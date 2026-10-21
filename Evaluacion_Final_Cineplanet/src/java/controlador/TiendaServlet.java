package controlador;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import dao.FuncionDAO;
import dao.ProductoDAO;
import dao.SedeDAO;
import modelo.Funcion;
import modelo.Producto;
import modelo.Sede;

@WebServlet(name = "TiendaServlet", urlPatterns = {"/TiendaServlet"})
public class TiendaServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        int idSede = util.Seguridad.entero(req.getParameter("idSede"));
        try {
            List<Sede> sedes = SedeDAO.listar();
            req.setAttribute("sedes", sedes);
            if (idSede > 0) {
                Sede sede = SedeDAO.buscarPorId(idSede);
                if (sede == null) {
                    req.setAttribute("mensaje", "El cine seleccionado no existe.");
                } else {
                    Timestamp desde = new Timestamp(System.currentTimeMillis());
                    Calendar c = Calendar.getInstance();
                    c.add(Calendar.DAY_OF_MONTH, 7);
                    Timestamp hasta = new Timestamp(c.getTimeInMillis());
                    List<Funcion> funciones = FuncionDAO.cartelera(idSede, desde, hasta);
                    List<Producto> productos = ProductoDAO.listarActivos();
                    req.setAttribute("sedeSel", sede);
                    req.setAttribute("funciones", funciones);
                    req.setAttribute("productos", productos);
                }
            }
        } catch (SQLException e) {
            log("Tienda: error al consultar", e);
            req.setAttribute("mensaje", "No se pudo cargar la cartelera. Intenta nuevamente.");
        }
        req.getRequestDispatcher("/tienda.jsp").forward(req, resp);
    }
}
