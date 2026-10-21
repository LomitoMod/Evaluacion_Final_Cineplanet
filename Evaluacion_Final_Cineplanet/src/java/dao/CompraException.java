package dao;

/** Error de negocio de una compra; el mensaje ya es apto para mostrarse al usuario. */
public class CompraException extends Exception {

    public CompraException(String mensaje) {
        super(mensaje);
    }

    public CompraException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
