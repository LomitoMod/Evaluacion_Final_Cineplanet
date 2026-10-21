package util;

/**
 * Constantes genéricas de validación para todo el sistema (Java, HTML y JavaScript).
 * Patrones genéricos y tamaños TEXTO_SIZE_N reutilizables para mínimos, máximos o fijos.
 */
public final class ConstantesValidacion {

    private ConstantesValidacion() {
    }

    // ==========================================
    // PATRONES REGEX GENÉRICOS (Java, HTML pattern y JS)
    // ==========================================
    public static final String DNI = "\\d{8}";
    public static final String TEXT_ALFANUMERICO_60 = "[a-zA-Z0-9áéíóúÁÉÍÓÚñÑüÜ\\s]{1,60}";
    public static final String CORREO = "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}";
    public static final String TELEFONO = "\\d{1,15}";
    public static final String USUARIO = "[a-zA-Z0-9@._%+-]{1,30}";
    public static final String CLAVE = "(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&#._-])[A-Za-z\\d@$!%*?&#._-]{6,15}";

    // ==========================================
    // TAMAÑOS GENÉRICOS (mínimos, máximos y longitudes fijas)
    // ==========================================
    public static final int TEXTO_SIZE_6 = 6;
    public static final int TEXTO_SIZE_8 = 8;
    public static final int TEXTO_SIZE_15 = 15;
    public static final int TEXTO_SIZE_30 = 30;
    public static final int TEXTO_SIZE_60 = 60;
    public static final int TEXTO_SIZE_80 = 80;
}
