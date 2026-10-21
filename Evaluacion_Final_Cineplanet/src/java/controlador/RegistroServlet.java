package controlador;

import dao.AuditoriaDAO;
import dao.ClienteDAO;
import java.io.IOException;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import util.Seguridad;

@WebServlet(name = "RegistroServlet", urlPatterns = {"/RegistroServlet"})
public class RegistroServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.sendRedirect(req.getContextPath() + "/registro.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String dni = valor(req.getParameter("dni"));
        String nombres = valor(req.getParameter("nombres"));
        String apellidos = valor(req.getParameter("apellidos"));
        String correo = valor(req.getParameter("correo"));
        String telefono = valor(req.getParameter("telefono"));
        String clave = req.getParameter("clave") == null ? "" : req.getParameter("clave");

        String error = validar(dni, nombres, apellidos, correo, telefono, clave);
        if (error != null) {
            HttpSession sesion = req.getSession(true);
            sesion.setAttribute("mensaje", error);
            resp.sendRedirect(req.getContextPath() + "/registro.jsp");
            return;
        }

        try {
            if (ClienteDAO.existeDni(dni)) {
                HttpSession sesion = req.getSession(true);
                sesion.setAttribute("mensaje", "Ese DNI ya esta registrado.");
                resp.sendRedirect(req.getContextPath() + "/registro.jsp");
                return;
            }
            int id = ClienteDAO.insertar(dni, nombres, apellidos, correo,
                    telefono.isEmpty() ? null : telefono, Seguridad.sha256(clave));
            HttpSession sesion = req.getSession(true);
            if (id > 0) {
                sesion.setAttribute("mensaje",
                        "Cliente registrado correctamente. Ahora inicia sesion con tu DNI.");
            } else {
                sesion.setAttribute("mensaje", "No se pudo completar el registro.");
            }
        } catch (SQLException e) {
            log("Registro: error al guardar", e);
            HttpSession sesion = req.getSession(true);
            sesion.setAttribute("mensaje", Seguridad.MSJ_GENERICO);
        }
        resp.sendRedirect(req.getContextPath() + "/registro.jsp");
    }

    private String validar(String dni, String nombres, String apellidos,
            String correo, String telefono, String clave) {
        if (!dni.matches("\\d{8}")) {
            return "El DNI debe tener 8 digitos.";
        }
        if (nombres.length() < 2 || nombres.length() > 60) {
            return "El nombre debe tener entre 2 y 60 caracteres.";
        }
        if (apellidos.length() < 2 || apellidos.length() > 60) {
            return "El apellido debe tener entre 2 y 60 caracteres.";
        }
        if (correo.length() > 60 || !correo.matches("[^@\\s]+@[^@\\s]+\\.[^@\\s]{2,}")) {
            return "El correo electronico no es valido.";
        }
        if (!telefono.isEmpty() && !telefono.matches("\\d{7,15}")) {
            return "El telefono debe tener de 7 a 15 digitos.";
        }
        if (clave.length() < 8) {
            return "La clave debe tener al menos 8 caracteres.";
        }
        return null;
    }

    private String valor(String parametro) {
        return parametro == null ? "" : parametro.trim();
    }
}
