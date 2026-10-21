package controlador;

import dao.AuditoriaDAO;
import dao.CompraException;
import dao.SedeDAO;
import dao.VentaDAO;
import filtro.SesionFilter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import modelo.Carrito;
import modelo.DetalleVenta;
import modelo.Entrada;
import util.Seguridad;

@WebServlet(name = "PagoServlet", urlPatterns = {"/PagoServlet"})
public class PagoServlet extends HttpServlet {

    private static final Set<String> MEDIOS = new HashSet<String>();
    static {
        MEDIOS.add("TARJETA");
        MEDIOS.add("YAPE");
        MEDIOS.add("PLIN");
    }
    private static final String PATRON_BUTACA = "^[A-J](1[0-2]|[1-9])$";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.sendRedirect(req.getContextPath() + "/CarritoServlet");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (!Seguridad.tokenValido(req)) {
            rechazarCsrf(req, resp);
            return;
        }

        HttpSession sesion = req.getSession(false);
        Object idClienteAttr = sesion == null ? null : sesion.getAttribute(SesionFilter.ATR_ID_CLIENTE);
        if (!(idClienteAttr instanceof Integer)) {
            resp.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }
        int idCliente = ((Integer) idClienteAttr).intValue();
        String usuario = (String) sesion.getAttribute(SesionFilter.ATR_USUARIO);

        Object carritoAttr = sesion.getAttribute("carrito");
        if (!(carritoAttr instanceof Carrito) || ((Carrito) carritoAttr).estaVacio()) {
            volver(req, resp, "Tu carrito esta vacio.");
            return;
        }
        Carrito carrito = (Carrito) carritoAttr;

        String medioPago = req.getParameter("medioPago");
        if (medioPago == null || !MEDIOS.contains(medioPago)) {
            volver(req, resp, "Selecciona un medio de pago valido: TARJETA, YAPE o PLIN.");
            return;
        }

        if (carrito.getIdSede() <= 0) {
            volver(req, resp, "Tu carrito no tiene un cine valido asociado.");
            return;
        }

        for (Entrada e : carrito.getEntradas()) {
            if (e.getButaca() == null || !e.getButaca().matches(PATRON_BUTACA)) {
                volver(req, resp, "Hay una butaca con formato no valido en el carrito.");
                return;
            }
        }
        for (DetalleVenta d : carrito.getProductos()) {
            if (d.getCantidad() < 1 || d.getCantidad() > VentaDAO.MAX_CANTIDAD_PRODUCTO) {
                volver(req, resp, "Hay una cantidad de producto no valida en el carrito.");
                return;
            }
        }

        try {
            if (SedeDAO.buscarPorId(carrito.getIdSede()) == null) {
                volver(req, resp, "El cine del carrito ya no existe.");
                return;
            }
            int nEntradas = carrito.totalEntradas();
            int idVenta = VentaDAO.registrar(idCliente, carrito.getIdSede(), medioPago,
                    carrito.getEntradas(), carrito.getProductos());
            carrito.vaciar();
            AuditoriaDAO.registrar(usuario, AuditoriaDAO.COMPRA,
                    "Compra " + idVenta + " | " + nEntradas + " entradas | medio "
                    + medioPago, Seguridad.ipDe(req));
            resp.sendRedirect(req.getContextPath() + "/ComprobanteServlet?id=" + idVenta);
        } catch (CompraException e) {
            log("Pago rechazado: " + e.getMessage());
            AuditoriaDAO.registrar(usuario, AuditoriaDAO.COMPRA,
                    "Compra rechazada: " + e.getMessage(), Seguridad.ipDe(req));
            volver(req, resp, e.getMessage());
        } catch (SQLException e) {
            log("Pago: error de base de datos", e);
            AuditoriaDAO.registrar(usuario, AuditoriaDAO.COMPRA,
                    "Fallo tecnico al pagar", Seguridad.ipDe(req));
            volver(req, resp, "No se pudo procesar el pago. Tu compra NO fue registrada, "
                    + "intenta nuevamente.");
        }
    }

    private void volver(HttpServletRequest req, HttpServletResponse resp, String mensaje)
            throws IOException {
        req.getSession(true).setAttribute("mensaje", mensaje);
        resp.sendRedirect(req.getContextPath() + "/CarritoServlet");
    }

    private void rechazarCsrf(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession sesion = req.getSession(false);
        String usuario = sesion == null ? null
                : (String) sesion.getAttribute(SesionFilter.ATR_USUARIO);
        AuditoriaDAO.registrar(usuario, AuditoriaDAO.CSRF,
                "POST sin token valido en " + req.getServletPath(), Seguridad.ipDe(req));
        resp.sendError(HttpServletResponse.SC_FORBIDDEN);
    }
}
