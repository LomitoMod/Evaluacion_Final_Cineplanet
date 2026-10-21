package controlador;

import dao.AuditoriaDAO;
import dao.EntradaDAO;
import filtro.SesionFilter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import modelo.Entrada;
import util.Seguridad;

@WebServlet(name = "AnularServlet", urlPatterns = {"/AnularServlet"})
public class AnularServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String dni = req.getParameter("dni");
        dni = dni != null && dni.matches("\\d{8}") ? dni : null;
        List<Entrada> entradas = Collections.emptyList();
        try {
            if (dni != null) {
                entradas = EntradaDAO.listarPorDni(dni);
            }
        } catch (SQLException e) {
            log("Anular: error al buscar entradas", e);
            req.setAttribute("mensaje", "No se pudo buscar el cliente.");
        }
        req.setAttribute("entradas", entradas);
        if (dni != null) {
            req.setAttribute("dniSel", dni);
        }
        req.getRequestDispatcher("/anular.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (!Seguridad.tokenValido(req)) {
            rechazarCsrf(req, resp);
            return;
        }

        HttpSession sesion = req.getSession(false);
        String usuario = sesion == null ? null
                : (String) sesion.getAttribute(SesionFilter.ATR_USUARIO);
        int idEntrada = Seguridad.entero(req.getParameter("idEntrada"));
        String mensaje;

        if (idEntrada <= 0) {
            mensaje = "Debe indicar una entrada valida.";
        } else {
            try {
                Entrada entrada = EntradaDAO.buscarPorId(idEntrada);
                if (entrada == null) {
                    mensaje = "Entrada no encontrada.";
                } else if (EntradaDAO.anular(idEntrada)) {
                    AuditoriaDAO.registrar(usuario, AuditoriaDAO.ANULACION,
                            "Entrada " + idEntrada + " | butaca " + entrada.getButaca()
                            + " | " + entrada.getTituloPelicula(), Seguridad.ipDe(req));
                    mensaje = "Entrada " + idEntrada + " anulada correctamente.";
                } else {
                    mensaje = "La entrada " + idEntrada + " ya estaba anulada.";
                }
            } catch (SQLException e) {
                log("Anular: error al anular", e);
                mensaje = "No se pudo anular la entrada. Intenta nuevamente.";
            }
        }

        req.getSession(true).setAttribute("mensaje", mensaje);
        resp.sendRedirect(req.getContextPath() + "/AnularServlet");
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
