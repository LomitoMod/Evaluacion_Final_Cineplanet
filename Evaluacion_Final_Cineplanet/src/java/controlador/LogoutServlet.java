package controlador;

import dao.AuditoriaDAO;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import util.Seguridad;

@WebServlet(name = "LogoutServlet", urlPatterns = {"/LogoutServlet"})
public class LogoutServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.sendRedirect(req.getContextPath() + "/login.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession sesion = req.getSession(false);
        if (sesion == null) {
            resp.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }

        String usuario = (String) sesion.getAttribute(filtro.SesionFilter.ATR_USUARIO);

        if (!Seguridad.tokenValido(req)) {
            AuditoriaDAO.registrar(usuario, AuditoriaDAO.CSRF,
                    "Logout sin token CSRF", Seguridad.ipDe(req));
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        AuditoriaDAO.registrar(usuario, AuditoriaDAO.LOGOUT,
                "Cierre de sesion", Seguridad.ipDe(req));
        sesion.invalidate();
        HttpSession nueva = req.getSession(true);
        nueva.setAttribute("mensaje", "Sesion cerrada correctamente.");
        resp.sendRedirect(req.getContextPath() + "/login.jsp");
    }
}
