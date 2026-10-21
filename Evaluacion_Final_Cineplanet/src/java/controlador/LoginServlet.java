package controlador;

import dao.AuditoriaDAO;
import dao.ClienteDAO;
import dao.UsuarioDAO;
import java.io.IOException;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import modelo.Cliente;
import modelo.Usuario;
import util.Seguridad;

@WebServlet(name = "LoginServlet", urlPatterns = {"/LoginServlet"})
public class LoginServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.sendRedirect(req.getContextPath() + "/login.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String usuario = req.getParameter("usuario");
        String clave = req.getParameter("clave");
        usuario = usuario == null ? "" : usuario.trim();

        if (usuario.isEmpty() || clave == null || clave.isEmpty()) {
            redirigirConMensaje(req, resp, Seguridad.MSJ_GENERICO);
            return;
        }

        if (Seguridad.estaBloqueado(usuario)) {
            AuditoriaDAO.registrar(usuario, AuditoriaDAO.LOGIN_FALLIDO,
                    "Intento sobre cuenta bloqueada", Seguridad.ipDe(req));
            redirigirConMensaje(req, resp,
                    "Usuario bloqueado: acceso suspendido 5 minutos. Intenta mas tarde.");
            return;
        }

        String hash = Seguridad.sha256(clave);
        String ip = Seguridad.ipDe(req);
        Usuario u = null;
        Cliente c = null;
        try {
            u = UsuarioDAO.validar(usuario, hash);
            if (u == null && usuario.matches("\\d{8}")) {
                c = ClienteDAO.validar(usuario, hash);
            }
        } catch (SQLException e) {
            log("Login: error de consulta", e);
            redirigirConMensaje(req, resp, Seguridad.MSJ_GENERICO);
            return;
        }

        if (u == null && c == null) {
            AuditoriaDAO.registrar(usuario, AuditoriaDAO.LOGIN_FALLIDO,
                    "Clave incorrecta", ip);
            boolean bloqueado = Seguridad.sumarFallo(usuario);
            if (bloqueado) {
                AuditoriaDAO.registrar(usuario, AuditoriaDAO.BLOQUEO,
                        "Bloqueo de 5 minutos tras 3 intentos fallidos", ip);
                redirigirConMensaje(req, resp,
                        "Usuario bloqueado por 5 minutos tras 3 intentos fallidos.");
            } else {
                redirigirConMensaje(req, resp, Seguridad.MSJ_GENERICO);
            }
            return;
        }

        Seguridad.limpiarIntentos(usuario);

        HttpSession anterior = req.getSession(false);
        if (anterior != null) {
            anterior.invalidate();
        }
        HttpSession sesion = req.getSession(true);
        sesion.setMaxInactiveInterval(15 * 60);
        String rol;
        String destino;
        if (u != null) {
            rol = u.getRol();
            sesion.setAttribute(filtro.SesionFilter.ATR_USUARIO, u.getUsuario());
            sesion.setAttribute(filtro.SesionFilter.ATR_NOMBRE, u.getNombre());
            destino = req.getContextPath() + "/panel.jsp";
            AuditoriaDAO.registrar(u.getUsuario(), AuditoriaDAO.LOGIN_OK,
                    "Rol " + rol, ip);
        } else {
            rol = filtro.SesionFilter.ROL_CLIENTE;
            sesion.setAttribute(filtro.SesionFilter.ATR_USUARIO, c.getDni());
            sesion.setAttribute(filtro.SesionFilter.ATR_NOMBRE,
                    c.getNombres() + " " + c.getApellidos());
            sesion.setAttribute(filtro.SesionFilter.ATR_ID_CLIENTE,
                    Integer.valueOf(c.getIdCliente()));
            destino = req.getContextPath() + "/TiendaServlet";
            AuditoriaDAO.registrar(c.getDni(), AuditoriaDAO.LOGIN_OK,
                    "Rol CLIENTE", ip);
        }
        sesion.setAttribute(filtro.SesionFilter.ATR_ROL, rol);
        Seguridad.tokenEnSesion(sesion, true);
        resp.sendRedirect(destino);
    }

    private void redirigirConMensaje(HttpServletRequest req, HttpServletResponse resp, String mensaje)
            throws IOException {
        HttpSession sesion = req.getSession(true);
        sesion.setAttribute("mensaje", mensaje);
        resp.sendRedirect(req.getContextPath() + "/login.jsp");
    }
}
