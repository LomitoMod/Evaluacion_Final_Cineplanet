package util;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * Publica las constantes de validación en applicationScope para JSP y HTML.
 */
@WebListener
public class AppContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext ctx = sce.getServletContext();

        // Patrones regex genéricos
        ctx.setAttribute("DNI", ConstantesValidacion.DNI);
        ctx.setAttribute("TEXT_ALFANUMERICO_60", ConstantesValidacion.TEXT_ALFANUMERICO_60);
        ctx.setAttribute("CORREO", ConstantesValidacion.CORREO);
        ctx.setAttribute("TELEFONO", ConstantesValidacion.TELEFONO);
        ctx.setAttribute("USUARIO", ConstantesValidacion.USUARIO);
        ctx.setAttribute("CLAVE", ConstantesValidacion.CLAVE);

        // Tamaños genéricos (mínimos, máximos y longitudes fijas)
        ctx.setAttribute("TEXTO_SIZE_6", Integer.valueOf(ConstantesValidacion.TEXTO_SIZE_6));
        ctx.setAttribute("TEXTO_SIZE_8", Integer.valueOf(ConstantesValidacion.TEXTO_SIZE_8));
        ctx.setAttribute("TEXTO_SIZE_15", Integer.valueOf(ConstantesValidacion.TEXTO_SIZE_15));
        ctx.setAttribute("TEXTO_SIZE_30", Integer.valueOf(ConstantesValidacion.TEXTO_SIZE_30));
        ctx.setAttribute("TEXTO_SIZE_60", Integer.valueOf(ConstantesValidacion.TEXTO_SIZE_60));
        ctx.setAttribute("TEXTO_SIZE_80", Integer.valueOf(ConstantesValidacion.TEXTO_SIZE_80));
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }
}
