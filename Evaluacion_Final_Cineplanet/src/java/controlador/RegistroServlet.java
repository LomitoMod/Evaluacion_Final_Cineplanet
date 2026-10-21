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
            req.setAttribute("mensaje", error);
            preservarCampos(req, dni, nombres, apellidos, correo, telefono);
            req.getRequestDispatcher("/registro.jsp").forward(req, resp);
            return;
        }

        try {
            if (ClienteDAO.existeDni(dni)) {
                req.setAttribute("mensaje", "El DNI ingresado ya se encuentra registrado en el sistema.");
                preservarCampos(req, dni, nombres, apellidos, correo, telefono);
                req.getRequestDispatcher("/registro.jsp").forward(req, resp);
                return;
            }
            int id = ClienteDAO.insertar(dni, nombres, apellidos, correo,
                    telefono.isEmpty() ? null : telefono, Seguridad.sha256(clave));
            if (id > 0) {
                HttpSession sesion = req.getSession(true);
                sesion.setAttribute("mensaje",
                        "Registro exitoso. Ahora puedes iniciar sesión con tu DNI.");
                resp.sendRedirect(req.getContextPath() + "/login.jsp");
            } else {
                req.setAttribute("mensaje", "No se pudo completar el registro. Intenta nuevamente.");
                preservarCampos(req, dni, nombres, apellidos, correo, telefono);
                req.getRequestDispatcher("/registro.jsp").forward(req, resp);
            }
        } catch (SQLException e) {
            log("Registro: error al guardar", e);
            req.setAttribute("mensaje", Seguridad.MSJ_GENERICO);
            preservarCampos(req, dni, nombres, apellidos, correo, telefono);
            req.getRequestDispatcher("/registro.jsp").forward(req, resp);
        }
    }

    private String validar(String dni, String nombres, String apellidos,
            String correo, String telefono, String clave) {
        // 1) DNI: 8 digitos numéricos y obligatorio
        if (dni.isEmpty()) {
            return "El DNI es obligatorio.";
        }
        if (!dni.matches(util.ConstantesValidacion.DNI)) {
            return "El DNI debe contener exactamente " + util.ConstantesValidacion.TEXTO_SIZE_8 + " dígitos numéricos.";
        }

        // 2) Nombres: obligatorio, solo alfanumérico y vocales con tildes, máximo 60 caracteres
        if (nombres.isEmpty()) {
            return "Los nombres son obligatorios.";
        }
        if (nombres.length() > util.ConstantesValidacion.TEXTO_SIZE_60
                || !nombres.matches(util.ConstantesValidacion.TEXT_ALFANUMERICO_60)) {
            return "Los nombres solo deben permitir caracteres alfanuméricos, vocales con tildes y máximo "
                    + util.ConstantesValidacion.TEXTO_SIZE_60 + " caracteres.";
        }

        // 3) Apellidos: obligatorio, solo alfanumérico y vocales con tildes, máximo 60 caracteres
        if (apellidos.isEmpty()) {
            return "Los apellidos son obligatorios.";
        }
        if (apellidos.length() > util.ConstantesValidacion.TEXTO_SIZE_60
                || !apellidos.matches(util.ConstantesValidacion.TEXT_ALFANUMERICO_60)) {
            return "Los apellidos solo deben permitir caracteres alfanuméricos, vocales con tildes y máximo "
                    + util.ConstantesValidacion.TEXTO_SIZE_60 + " caracteres.";
        }

        // 4) Correo: obligatorio, formato seguro (sin caracteres peligrosos), máximo 80 caracteres
        if (correo.isEmpty()) {
            return "El correo electrónico es obligatorio.";
        }
        if (correo.length() > util.ConstantesValidacion.TEXTO_SIZE_80
                || !correo.matches(util.ConstantesValidacion.CORREO)) {
            return "El correo electrónico no es válido o supera los "
                    + util.ConstantesValidacion.TEXTO_SIZE_80 + " caracteres.";
        }

        // 5) Teléfono: opcional, solo números, máximo 15 dígitos
        if (!telefono.isEmpty() && !telefono.matches(util.ConstantesValidacion.TELEFONO)) {
            return "El teléfono es opcional, pero de ingresarse solo debe contener números (máximo "
                    + util.ConstantesValidacion.TEXTO_SIZE_15 + " dígitos).";
        }

        // 6) Clave: obligatoria, 6 a 15 caracteres, alfanumérico + especiales seguros (@$!%*?&#._-),
        //    al menos 1 mayúscula, 1 minúscula, 1 número y 1 especial.
        if (clave.isEmpty()) {
            return "La clave es obligatoria.";
        }
        if (clave.length() < util.ConstantesValidacion.TEXTO_SIZE_6
                || clave.length() > util.ConstantesValidacion.TEXTO_SIZE_15) {
            return "La clave debe tener entre " + util.ConstantesValidacion.TEXTO_SIZE_6
                    + " y " + util.ConstantesValidacion.TEXTO_SIZE_15 + " caracteres.";
        }
        if (!clave.matches(util.ConstantesValidacion.CLAVE)) {
            return "La clave debe contener entre " + util.ConstantesValidacion.TEXTO_SIZE_6
                    + " y " + util.ConstantesValidacion.TEXTO_SIZE_15
                    + " caracteres, al menos 1 mayúscula, 1 minúscula, 1 número y 1 carácter especial (@$!%*?&#._-).";
        }

        return null;
    }

    private void preservarCampos(HttpServletRequest req, String dni, String nombres,
            String apellidos, String correo, String telefono) {
        req.setAttribute("valDni", dni);
        req.setAttribute("valNombres", nombres);
        req.setAttribute("valApellidos", apellidos);
        req.setAttribute("valCorreo", correo);
        req.setAttribute("valTelefono", telefono);
    }

    private String valor(String parametro) {
        return parametro == null ? "" : parametro.trim();
    }
}
