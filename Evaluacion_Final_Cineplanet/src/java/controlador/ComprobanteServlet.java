package controlador;

import dao.VentaDAO;
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
import modelo.DetalleVenta;
import modelo.Entrada;
import modelo.Venta;
import util.Seguridad;

@WebServlet(name = "ComprobanteServlet", urlPatterns = {"/ComprobanteServlet"})
public class ComprobanteServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession sesion = req.getSession(false);
        Object idClienteAttr = sesion == null ? null : sesion.getAttribute(SesionFilter.ATR_ID_CLIENTE);
        int idVenta = Seguridad.entero(req.getParameter("id"));

        if (idVenta <= 0 || !(idClienteAttr instanceof Integer)) {
            noEncontrada(req, resp);
            return;
        }

        try {
            Venta venta = VentaDAO.buscarPropia(idVenta, ((Integer) idClienteAttr).intValue());
            if (venta == null) {
                noEncontrada(req, resp);
                return;
            }
            List<Entrada> entradas = VentaDAO.entradasDeVenta(idVenta);
            List<DetalleVenta> productos = VentaDAO.productosDeVenta(idVenta);
            req.setAttribute("venta", venta);
            req.setAttribute("entradas", entradas);
            req.setAttribute("productos", productos);
            req.getRequestDispatcher("/comprobante.jsp").forward(req, resp);
        } catch (SQLException e) {
            log("Comprobante: error al consultar", e);
            req.setAttribute("mensaje", "No se pudo cargar la compra. Intenta nuevamente.");
            req.getRequestDispatcher("/comprobante.jsp").forward(req, resp);
        }
    }

    private void noEncontrada(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("/comprobante.jsp").forward(req, resp);
    }
}
