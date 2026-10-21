package util;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class Seguridad {

    public static final String MSJ_GENERICO = "Usuario o clave incorrectos";
    public static final int MAX_INTENTOS = 3;
    public static final long MILIS_BLOQUEO = 5L * 60L * 1000L;
    public static final String ROL_ADMIN = "ADMIN";
    public static final String ROL_CAJERO = "CAJERO";

    private static final Map<String, Integer> INTENTOS = new ConcurrentHashMap<String, Integer>();
    private static final Map<String, Long> BLOQUEOS = new ConcurrentHashMap<String, Long>();
    private static final SecureRandom ALEATORIO = new SecureRandom();

    public static String sha256(String texto) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(texto.getBytes(StandardCharsets.UTF_8));
            return String.format("%064x", new BigInteger(1, hash));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 no disponible", e);
        }
    }

    public static boolean esAdmin(String rol) {
        return ROL_ADMIN.equals(rol);
    }

    public static boolean estaBloqueado(String usuario) {
        if (usuario == null) {
            return false;
        }
        String clave = normalizar(usuario);
        Long hasta = BLOQUEOS.get(clave);
        if (hasta == null) {
            return false;
        }
        if (System.currentTimeMillis() > hasta.longValue()) {
            BLOQUEOS.remove(clave);
            INTENTOS.remove(clave);
            return false;
        }
        return true;
    }

    /** Registra un intento fallido. Devuelve true si con este fallo se activo el bloqueo de 5 minutos. */
    public static boolean sumarFallo(String usuario) {
        String clave = normalizar(usuario);
        Integer previo = INTENTOS.get(clave);
        int acumulado = (previo == null ? 0 : previo.intValue()) + 1;
        if (acumulado >= MAX_INTENTOS) {
            INTENTOS.remove(clave);
            BLOQUEOS.put(clave, Long.valueOf(System.currentTimeMillis() + MILIS_BLOQUEO));
            return true;
        }
        INTENTOS.put(clave, Integer.valueOf(acumulado));
        return false;
    }

    public static int intentosFallidos(String usuario) {
        Integer n = INTENTOS.get(normalizar(usuario));
        return n == null ? 0 : n.intValue();
    }

    public static void limpiarIntentos(String usuario) {
        String clave = normalizar(usuario);
        INTENTOS.remove(clave);
        BLOQUEOS.remove(clave);
    }

    public static String nuevoTokenCsrf() {
        byte[] datos = new byte[32];
        ALEATORIO.nextBytes(datos);
        StringBuilder sb = new StringBuilder(datos.length * 2);
        for (byte b : datos) {
            sb.append(Character.forDigit((b >> 4) & 0xF, 16)).append(Character.forDigit(b & 0xF, 16));
        }
        return sb.toString();
    }

    public static String tokenEnSesion(HttpSession sesion, boolean crear) {
        Object actual = sesion.getAttribute("csrf");
        if (actual == null && crear) {
            actual = UUID.randomUUID().toString();
            sesion.setAttribute("csrf", actual);
        }
        return actual == null ? null : actual.toString();
    }

    public static boolean tokenValido(HttpServletRequest request) {
        HttpSession sesion = request.getSession(false);
        if (sesion == null) {
            return false;
        }
        Object esperado = sesion.getAttribute("csrf");
        String recibido = request.getParameter("csrf");
        return esperado != null && recibido != null && esperado.toString().equals(recibido);
    }

    /** Convierte un parametro a entero de forma segura; devuelve 0 si no es un numero valido. */
    public static int entero(String valor) {
        if (valor == null) {
            return 0;
        }
        String v = valor.trim();
        if (!v.matches("\\d{1,9}")) {
            return 0;
        }
        try {
            return Integer.parseInt(v);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public static String ipDe(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip != null) {
            int coma = ip.indexOf(',');
            String primera = (coma > 0 ? ip.substring(0, coma) : ip).trim();
            if (primera.length() > 0 && !"unknown".equalsIgnoreCase(primera)) {
                return primera;
            }
        }
        return request.getRemoteAddr();
    }

    private static String normalizar(String usuario) {
        return usuario == null ? "" : usuario.trim().toLowerCase();
    }
}
