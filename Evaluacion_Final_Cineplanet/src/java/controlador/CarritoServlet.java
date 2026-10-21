package controlador;

import dao.AuditoriaDAO;
import dao.FuncionDAO;
import dao.ProductoDAO;
import dao.SedeDAO;
import filtro.SesionFilter;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
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
import modelo.Funcion;
import modelo.Producto;
import modelo.Sede;
import util.Seguridad;

@WebServlet(name = "CarritoServlet", urlPatterns = {"/CarritoServlet"})
public class CarritoServlet extends HttpServlet {

    private static final String PATRON_BUTACA = "^[A-J](1[0-2]|[1-9])$";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        Carrito carrito = carritoDe(req);
        BigDecimal total = BigDecimal.ZERO;
        try {
            if (carrito.totalEntradas() > 0) {
                Funcion funcion = FuncionDAO.buscarPorId(carrito.getIdFuncion());
                if (funcion != null) {
                    req.setAttribute("funcion", funcion);
                    total = funcion.getPrecio().multiply(new BigDecimal(carrito.totalEntradas()));
                }
            }
            for (DetalleVenta d : carrito.getProductos()) {
                Producto p = ProductoDAO.buscarPorId(d.getIdProducto());
                if (p != null) {
                    d.setPrecioUnit(p.getPrecio());
                    d.setNombreProducto(p.getNombre());
                    d.setCategoria(p.getCategoria());
                    total = total.add(p.getPrecio().multiply(new BigDecimal(d.getCantidad())));
                }
            }
        } catch (SQLException e) {
            log("Carrito: error al calcular totales", e);
            req.setAttribute("mensaje", "No se pudo calcular el total del carrito.");
        }
        req.setAttribute("total", total);
        req.setAttribute("carrito", carrito);
        req.setAttribute("vacio", Boolean.valueOf(carrito.estaVacio()));
        req.getRequestDispatcher("/carrito.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (!Seguridad.tokenValido(req)) {
            rechazarCsrf(req, resp);
            return;
        }

        String accion = req.getParameter("accion");
        Carrito carrito = carritoDe(req);
        String error = null;
        String destino;

        try {
            if ("entradas".equals(accion)) {
                error = agregarEntradas(req, carrito);
                destino = req.getContextPath() + "/ButacasServlet?idFuncion="
                        + Seguridad.entero(req.getParameter("idFuncion"));
            } else if ("producto".equals(accion)) {
                error = agregarProducto(req, carrito);
                destino = req.getContextPath() + "/TiendaServlet?idSede="
                        + Seguridad.entero(req.getParameter("idSede"));
            } else if ("vaciar".equals(accion)) {
                carrito.vaciar();
                error = null;
                destino = req.getContextPath() + "/CarritoServlet";
            } else {
                error = "Accion no reconocida.";
                destino = req.getContextPath() + "/CarritoServlet";
            }
        } catch (SQLException e) {
            log("Carrito: error al agregar item", e);
            error = "No se pudo actualizar el carrito. Intenta nuevamente.";
            destino = req.getContextPath() + "/CarritoServlet";
        }

        if (error != null) {
            req.getSession(true).setAttribute("mensaje", error);
        } else if ("vaciar".equals(accion)) {
            req.getSession(true).setAttribute("mensaje", "Carrito vaciado.");
        } else {
            req.getSession(true).setAttribute("mensaje", "Item agregado al carrito.");
        }
        resp.sendRedirect(destino);
    }

    private String agregarEntradas(HttpServletRequest req, Carrito carrito) throws SQLException {
        int idFuncion = Seguridad.entero(req.getParameter("idFuncion"));
        String[] butacas = req.getParameterValues("butaca");
        if (idFuncion <= 0) {
            return "Selecciona una funcion valida.";
        }
        if (butacas == null || butacas.length == 0) {
            return "Marca al menos una butaca.";
        }
        if (butacas.length > 10) {
            return "No puedes agregar mas de 10 entradas por compra.";
        }
        Funcion funcion = FuncionDAO.buscarPorId(idFuncion);
        if (funcion == null) {
            return "La funcion seleccionada ya no existe.";
        }
        Set<String> ocupadas = FuncionDAO.butacasOcupadas(idFuncion);
        String detalle = funcion.getTituloPelicula() + " | Sede " + funcion.getNombreSede()
                + " | Sala " + funcion.getSala() + " | "
                + new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm").format(funcion.getFechaHora());

        for (String butaca : butacas) {
            String b = butaca == null ? "" : butaca.trim().toUpperCase();
            if (!b.matches(PATRON_BUTACA)) {
                return "Formato de butaca no valido: " + b;
            }
            if (ocupadas.contains(b)) {
                return "La butaca " + b + " ya fue vendida.";
            }
            Entrada entrada = new Entrada(idFuncion, b);
            String resultado = carrito.agregarEntrada(funcion.getIdSede(), idFuncion, detalle, entrada);
            if (resultado != null) {
                return resultado;
            }
        }
        return null;
    }

    private String agregarProducto(HttpServletRequest req, Carrito carrito) throws SQLException {
        int idProducto = Seguridad.entero(req.getParameter("idProducto"));
        int idSede = Seguridad.entero(req.getParameter("idSede"));
        int cantidad = Seguridad.entero(req.getParameter("cantidad"));
        if (idProducto <= 0) {
            return "Producto no valido.";
        }
        if (idSede <= 0) {
            return "Selecciona primero un cine.";
        }
        if (cantidad < 1 || cantidad > 10) {
            return "La cantidad debe estar entre 1 y 10.";
        }
        Sede sede = SedeDAO.buscarPorId(idSede);
        if (sede == null) {
            return "El cine seleccionado no existe.";
        }
        Producto producto = ProductoDAO.buscarPorId(idProducto);
        if (producto == null) {
            return "El producto no existe o no esta activo.";
        }
        DetalleVenta detalle = new DetalleVenta(idProducto, cantidad);
        detalle.setPrecioUnit(producto.getPrecio());
        detalle.setNombreProducto(producto.getNombre());
        return carrito.agregarProducto(idSede, detalle);
    }

    private Carrito carritoDe(HttpServletRequest req) {
        HttpSession sesion = req.getSession(false);
        if (sesion != null) {
            Object actual = sesion.getAttribute("carrito");
            if (actual instanceof Carrito) {
                return (Carrito) actual;
            }
        }
        Carrito nuevo = new Carrito();
        if (sesion != null) {
            sesion.setAttribute("carrito", nuevo);
        }
        return nuevo;
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
