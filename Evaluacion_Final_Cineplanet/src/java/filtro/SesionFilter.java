package filtro;

import dao.AuditoriaDAO;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.annotation.WebFilter;
import util.Seguridad;

/**
 * Control de acceso por rol sobre cada recurso. Sin sesion valida -> login;
 * rol incorrecto -> 403 + evento ACCESO_DENEGADO en la auditoria.
 */
@WebFilter(urlPatterns = "/*")
public class SesionFilter implements Filter {

    public static final String ATR_USUARIO = "usuarioSesion";
    public static final String ATR_ROL = "rolSesion";
    public static final String ATR_NOMBRE = "nombreSesion";
    public static final String ATR_ID_CLIENTE = "idClienteSesion";

    public static final String ROL_ADMIN = "ADMIN";
    public static final String ROL_CAJERO = "CAJERO";
    public static final String ROL_CLIENTE = "CLIENTE";

    private static final Set<String> PUBLICAS = new HashSet<String>(Arrays.asList(
            "/login.jsp", "/registro.jsp", "/error.jsp", "/LoginServlet", "/RegistroServlet"));

    private static final Set<String> CLIENTE = new HashSet<String>(Arrays.asList(
            "/TiendaServlet", "/ButacasServlet", "/CarritoServlet", "/PagoServlet",
            "/ComprobanteServlet", "/MisEntradasServlet",
            "/tienda.jsp", "/butacas.jsp", "/carrito.jsp", "/comprobante.jsp",
            "/misentradas.jsp"));

    private static final Set<String> PERSONAL = new HashSet<String>(Arrays.asList(
            "/AnularServlet", "/anular.jsp", "/panel.jsp"));

    private static final Set<String> ADMIN = new HashSet<String>(Arrays.asList(
            "/VentaServlet", "/AuditoriaServlet", "/ventas.jsp", "/auditoria.jsp"));

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        String ruta = req.getServletPath();

        if (PUBLICAS.contains(ruta) || ruta.startsWith("/css/") || ruta.isEmpty()) {
            chain.doFilter(request, response);
            return;
        }

        HttpSession sesion = req.getSession(false);
        String rol = sesion == null ? null : (String) sesion.getAttribute(ATR_ROL);
        String usuario = sesion == null ? null : (String) sesion.getAttribute(ATR_USUARIO);

        if (rol == null) {
            HttpSession nueva = req.getSession(true);
            nueva.setAttribute("mensaje", "Tu sesion expiro o debes iniciar sesion para continuar.");
            res.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }

        boolean permitido;
        if (CLIENTE.contains(ruta)) {
            permitido = ROL_CLIENTE.equals(rol);
        } else if (PERSONAL.contains(ruta)) {
            permitido = ROL_ADMIN.equals(rol) || ROL_CAJERO.equals(rol);
        } else if (ADMIN.contains(ruta)) {
            permitido = ROL_ADMIN.equals(rol);
        } else if (ruta.startsWith("/fragmentos/")) {
            permitido = false;
        } else {
            permitido = true;
        }

        if (!permitido) {
            AuditoriaDAO.registrar(usuario, AuditoriaDAO.ACCESO_DENEGADO,
                    "Intento de acceso a " + ruta + " con rol " + rol, Seguridad.ipDe(req));
            res.setStatus(HttpServletResponse.SC_FORBIDDEN);
            req.setAttribute("codigo", "403");
            req.setAttribute("mensaje", "No tienes permiso para acceder a este recurso.");
            req.getRequestDispatcher("/error.jsp").forward(req, res);
            return;
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }
}
