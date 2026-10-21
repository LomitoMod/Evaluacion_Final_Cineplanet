package controlador;

import dao.VentaDAO;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import modelo.Venta;

@WebServlet(name = "VentaServlet", urlPatterns = {"/VentaServlet"})
public class VentaServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
        String desde = req.getParameter("desde");
        String hasta = req.getParameter("hasta");

        if (desde == null || !desde.matches("\\d{4}-\\d{2}-\\d{2}")) {
            Calendar c = Calendar.getInstance();
            c.add(Calendar.DAY_OF_MONTH, -30);
            desde = formato.format(c.getTime());
        }
        if (hasta == null || !hasta.matches("\\d{4}-\\d{2}-\\d{2}")) {
            hasta = formato.format(Calendar.getInstance().getTime());
        }

        try {
            List<Venta> ventas = VentaDAO.listarReporte(desde, hasta);
            BigDecimal acumulado = BigDecimal.ZERO;
            for (Venta v : ventas) {
                if (v.getTotal() != null) {
                    acumulado = acumulado.add(v.getTotal());
                }
            }
            req.setAttribute("ventas", ventas);
            req.setAttribute("acumulado", acumulado);
            req.setAttribute("desdeSel", desde);
            req.setAttribute("hastaSel", hasta);
        } catch (SQLException e) {
            log("Ventas: error al consultar", e);
            req.setAttribute("mensaje", "No se pudo generar el reporte de ventas.");
        }
        req.getRequestDispatcher("/ventas.jsp").forward(req, resp);
    }
}
