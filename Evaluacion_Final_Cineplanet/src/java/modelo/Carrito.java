package modelo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/** Carrito de compra guardado en la sesion del cliente. */
public class Carrito {

    private int idSede;
    private int idFuncion;
    private String detalleFuncion;
    private List<Entrada> entradas = new ArrayList<Entrada>();
    private List<DetalleVenta> productos = new ArrayList<DetalleVenta>();

    public int getIdSede() {
        return idSede;
    }

    public void setIdSede(int idSede) {
        this.idSede = idSede;
    }

    public int getIdFuncion() {
        return idFuncion;
    }

    public void setIdFuncion(int idFuncion) {
        this.idFuncion = idFuncion;
    }

    public String getDetalleFuncion() {
        return detalleFuncion;
    }

    public void setDetalleFuncion(String detalleFuncion) {
        this.detalleFuncion = detalleFuncion;
    }

    public List<Entrada> getEntradas() {
        return entradas;
    }

    public List<DetalleVenta> getProductos() {
        return productos;
    }

    public boolean estaVacio() {
        return entradas.isEmpty() && productos.isEmpty();
    }

    public void vaciar() {
        idSede = 0;
        idFuncion = 0;
        detalleFuncion = null;
        entradas.clear();
        productos.clear();
    }

    /** Devuelve null si la butaca quedo agregada o el motivo del error. */
    public String agregarEntrada(int idSedeNueva, int idFuncionNueva, String detalle, Entrada entrada) {
        if (idSede != 0 && idSede != idSedeNueva) {
            return "El carrito es de otro cine; vacialo para continuar.";
        }
        for (Entrada e : entradas) {
            if (e.getButaca().equals(entrada.getButaca()) && e.getIdFuncion() == idFuncionNueva) {
                return "La butaca " + entrada.getButaca() + " ya esta en tu carrito.";
            }
        }
        if (!entradas.isEmpty() && this.idFuncion != idFuncionNueva) {
            return "El carrito ya tiene butacas de otra funcion; vacialo para continuar.";
        }
        if (entradas.size() >= 10) {
            return "No puedes agregar mas de 10 entradas por compra.";
        }
        this.idSede = idSedeNueva;
        this.idFuncion = idFuncionNueva;
        this.detalleFuncion = detalle;
        entrada.setIdFuncion(idFuncionNueva);
        entradas.add(entrada);
        return null;
    }

    public String agregarProducto(int idSedeNueva, DetalleVenta detalle) {
        if (idSede != 0 && idSede != idSedeNueva) {
            return "El carrito es de otro cine; vacialo para continuar.";
        }
        if (productos.isEmpty() && entradas.isEmpty()) {
            this.idSede = idSedeNueva;
        }
        for (DetalleVenta d : productos) {
            if (d.getIdProducto() == detalle.getIdProducto()) {
                int nueva = d.getCantidad() + detalle.getCantidad();
                if (nueva > 10) {
                    return "La cantidad maxima por producto es 10.";
                }
                d.setCantidad(nueva);
                return null;
            }
        }
        if (detalle.getCantidad() < 1 || detalle.getCantidad() > 10) {
            return "Cantidad de producto no valida (1 a 10).";
        }
        productos.add(detalle);
        return null;
    }

    public int totalEntradas() {
        return entradas.size();
    }

    public BigDecimal totalProductos() {
        BigDecimal total = BigDecimal.ZERO;
        for (DetalleVenta d : productos) {
            if (d.getPrecioUnit() != null) {
                total = total.add(d.getPrecioUnit().multiply(new BigDecimal(d.getCantidad())));
            }
        }
        return total;
    }
}
