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
import util.ConstantesValidacion;
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
        clave = clave == null ? "" : clave;

        String error = validar(usuario, clave);
        if (error != null) {
            responderError(req, resp, error, usuario);
            return;
        }

        if (Seguridad.estaBloqueado(usuario)) {
            AuditoriaDAO.registrar(usuario, AuditoriaDAO.LOGIN_FALLIDO,
                    "Intento sobre cuenta bloqueada", Seguridad.ipDe(req));
            responderError(req, resp,
                    "Usuario bloqueado: acceso suspendido 5 minutos. Intenta más tarde.", usuario);
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
            responderError(req, resp, Seguridad.MSJ_GENERICO, usuario);
            return;
        }

        if (u == null && c == null) {
            AuditoriaDAO.registrar(usuario, AuditoriaDAO.LOGIN_FALLIDO,
                    "Clave incorrecta", ip);
            boolean bloqueado = Seguridad.sumarFallo(usuario);
            if (bloqueado) {
                AuditoriaDAO.registrar(usuario, AuditoriaDAO.BLOQUEO,
                        "Bloqueo de 5 minutos tras 3 intentos fallidos", ip);
                responderError(req, resp,
                        "Usuario bloqueado por 5 minutos tras 3 intentos fallidos.", usuario);
            } else {
                responderError(req, resp, Seguridad.MSJ_GENERICO, usuario);
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

    private String validar(String usuario, String clave) {
        if (usuario.isEmpty()) {
            return "El usuario o DNI es obligatorio.";
        }
        if (usuario.length() > ConstantesValidacion.TEXTO_SIZE_30
                || !usuario.matches(ConstantesValidacion.USUARIO)) {
            return "El usuario o DNI solo permite caracteres alfanuméricos y (@ . _ % + -) hasta "
                    + ConstantesValidacion.TEXTO_SIZE_30 + " caracteres.";
        }
        if (clave.isEmpty()) {
            return "La clave es obligatoria.";
        }
        if (clave.length() < ConstantesValidacion.TEXTO_SIZE_6
                || clave.length() > ConstantesValidacion.TEXTO_SIZE_15) {
            return "La clave debe tener entre " + ConstantesValidacion.TEXTO_SIZE_6
                    + " y " + ConstantesValidacion.TEXTO_SIZE_15 + " caracteres.";
        }
        if (!clave.matches(ConstantesValidacion.CLAVE)) {
            return "La clave debe contener entre " + ConstantesValidacion.TEXTO_SIZE_6
                    + " y " + ConstantesValidacion.TEXTO_SIZE_15
                    + " caracteres, al menos 1 mayúscula, 1 minúscula, 1 número y 1 carácter especial (@$!%*?&#._-).";
        }
        return null;
    }

    private void responderError(HttpServletRequest req, HttpServletResponse resp,
            String mensaje, String usuario) throws ServletException, IOException {
        req.setAttribute("mensaje", mensaje);
        req.setAttribute("valUsuario", usuario);
        req.getRequestDispatcher("/login.jsp").forward(req, resp);
    }

    private void redirigirConMensaje(HttpServletRequest req, HttpServletResponse resp, String mensaje)
            throws IOException {
        HttpSession sesion = req.getSession(true);
        sesion.setAttribute("mensaje", mensaje);
        resp.sendRedirect(req.getContextPath() + "/login.jsp");
    }
}
