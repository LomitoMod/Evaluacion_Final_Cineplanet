package dao;

import conexion.ConexionSQL;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import modelo.DetalleVenta;
import modelo.Entrada;
import modelo.Venta;

public class VentaDAO {

    public static final int MAX_ENTRADAS = 10;
    public static final int MAX_CANTIDAD_PRODUCTO = 10;

    /**
     * Registra la compra completa en UNA transaccion: cabecera, entradas,
     * descuento de butacas y detalle de dulceria. Los precios se leen de la
     * BD dentro de la misma transaccion: jamas llegan del navegador.
     */
    public static int registrar(int idCliente, int idSede, String medioPago,
            List<Entrada> entradas, List<DetalleVenta> productos) throws SQLException, CompraException {

        if (entradas == null) {
            entradas = new ArrayList<Entrada>();
        }
        boolean conEntradas = !entradas.isEmpty();
        boolean conProductos = productos != null && !productos.isEmpty();
        if (!conEntradas && !conProductos) {
            throw new CompraException("El carrito esta vacio.");
        }
        if (entradas.size() > MAX_ENTRADAS) {
            throw new CompraException("No puedes comprar mas de " + MAX_ENTRADAS + " entradas por compra.");
        }

        Connection cn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        boolean ok = false;
        try {
            cn = ConexionSQL.getConexion();
            cn.setAutoCommit(false);

            BigDecimal precioFuncion = null;
            int idFuncion = 0;

            if (conEntradas) {
                idFuncion = entradas.get(0).getIdFuncion();
                precioFuncion = precioFuncion(cn, idFuncion);
                for (Entrada e : entradas) {
                    if (e.getIdFuncion() != idFuncion) {
                        throw new CompraException("Todas las butacas deben pertenecer a la misma funcion.");
                    }
                }
                Set<String> ocupadas = butacasOcupadas(cn, idFuncion);
                for (Entrada e : entradas) {
                    if (ocupadas.contains(e.getButaca())) {
                        throw new CompraException("La butaca " + e.getButaca() + " ya fue vendida para esa funcion.");
                    }
                }
            }

            ps = cn.prepareStatement(
                    "INSERT INTO Venta (idCliente, idSede, canal, medioPago, total, estado) "
                    + "VALUES (?, ?, 'WEB', ?, 0, 'PAGADA')",
                    PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setInt(1, idCliente);
            ps.setInt(2, idSede);
            ps.setString(3, medioPago);
            ps.executeUpdate();
            rs = ps.getGeneratedKeys();
            if (!rs.next()) {
                throw new SQLException("No se pudo crear la venta.");
            }
            int idVenta = rs.getInt(1);
            cerrar(rs);
            rs = null;
            cerrar(ps);
            ps = null;

            if (conEntradas) {
                ps = cn.prepareStatement(
                        "INSERT INTO Entrada (idCliente, idFuncion, butaca, precioPagado, estado, idVenta) "
                        + "VALUES (?, ?, ?, ?, 'ACTIVA', ?)");
                for (Entrada e : entradas) {
                    ps.setInt(1, idCliente);
                    ps.setInt(2, idFuncion);
                    ps.setString(3, e.getButaca());
                    ps.setBigDecimal(4, precioFuncion);
                    ps.setInt(5, idVenta);
                    ps.addBatch();
                }
                ps.executeBatch();
                cerrar(ps);
                ps = null;

                ps = cn.prepareStatement(
                        "UPDATE Funcion SET butacasDisponibles = butacasDisponibles - ? "
                        + "WHERE idFuncion = ? AND butacasDisponibles >= ?");
                ps.setInt(1, entradas.size());
                ps.setInt(2, idFuncion);
                ps.setInt(3, entradas.size());
                if (ps.executeUpdate() == 0) {
                    throw new CompraException("No hay butacas suficientes disponibles para esa funcion.");
                }
                cerrar(ps);
                ps = null;
            }

            BigDecimal total = conEntradas
                    ? precioFuncion.multiply(new BigDecimal(entradas.size())) : BigDecimal.ZERO;

            if (productos != null && !productos.isEmpty()) {
                ps = cn.prepareStatement(
                        "INSERT INTO DetalleVenta (idVenta, idProducto, cantidad, precioUnit) "
                        + "VALUES (?, ?, ?, ?)");
                for (DetalleVenta d : productos) {
                    if (d.getCantidad() < 1 || d.getCantidad() > MAX_CANTIDAD_PRODUCTO) {
                        throw new CompraException("Cantidad de producto no valida (1 a "
                                + MAX_CANTIDAD_PRODUCTO + ").");
                    }
                    BigDecimal precioProducto = precioProducto(cn, d.getIdProducto());
                    ps.setInt(1, idVenta);
                    ps.setInt(2, d.getIdProducto());
                    ps.setInt(3, d.getCantidad());
                    ps.setBigDecimal(4, precioProducto);
                    ps.addBatch();
                    total = total.add(precioProducto.multiply(new BigDecimal(d.getCantidad())));
                }
                ps.executeBatch();
                cerrar(ps);
                ps = null;
            }

            ps = cn.prepareStatement("UPDATE Venta SET total = ? WHERE idVenta = ?");
            ps.setBigDecimal(1, total);
            ps.setInt(2, idVenta);
            ps.executeUpdate();
            cerrar(ps);
            ps = null;

            cn.commit();
            ok = true;
            return idVenta;
        } catch (SQLException e) {
            int codigo = e.getErrorCode();
            if (codigo == 2601 || codigo == 2627) {
                throw new CompraException("La butaca ya fue vendida (otro usuario la compro al mismo tiempo).", e);
            }
            throw e;
        } finally {
            cerrar(rs);
            cerrar(ps);
            if (cn != null) {
                try {
                    if (!ok) {
                        cn.rollback();
                    }
                    cn.setAutoCommit(true);
                } catch (SQLException ignorada) {
                }
                cerrar(cn);
            }
        }
    }

    /** Solo el dueno de la compra puede ver su comprobante (evita IDOR). */
    public static Venta buscarPropia(int idVenta, int idCliente) throws SQLException {
        String sql = "SELECT v.idVenta, v.idCliente, v.idSede, v.fechaVenta, v.canal, v.medioPago, "
                + "v.total, v.estado, c.nombres + ' ' + c.apellidos AS nombreCliente, c.dni AS dniCliente, "
                + "s.nombre AS nombreSede "
                + "FROM Venta v JOIN Cliente c ON c.idCliente = v.idCliente "
                + "JOIN Sede s ON s.idSede = v.idSede "
                + "WHERE v.idVenta = ? AND v.idCliente = ?";
        Connection cn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            cn = ConexionSQL.getConexion();
            ps = cn.prepareStatement(sql);
            ps.setInt(1, idVenta);
            ps.setInt(2, idCliente);
            rs = ps.executeQuery();
            return rs.next() ? extraer(rs) : null;
        } finally {
            cerrar(rs);
            cerrar(ps);
            cerrar(cn);
        }
    }

    public static List<Venta> listarReporte(String fechaDesde, String fechaHasta) throws SQLException {
        StringBuilder sql = new StringBuilder(
                "SELECT v.idVenta, v.idCliente, v.idSede, v.fechaVenta, v.canal, v.medioPago, "
                + "v.total, v.estado, c.nombres + ' ' + c.apellidos AS nombreCliente, "
                + "c.dni AS dniCliente, s.nombre AS nombreSede "
                + "FROM Venta v JOIN Cliente c ON c.idCliente = v.idCliente "
                + "JOIN Sede s ON s.idSede = v.idSede "
                + "WHERE v.fechaVenta >= ? AND v.fechaVenta < DATEADD(DAY, 1, ?) ");
        List<Venta> ventas = new ArrayList<Venta>();
        Connection cn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            cn = ConexionSQL.getConexion();
            ps = cn.prepareStatement(sql.toString());
            ps.setString(1, fechaDesde);
            ps.setString(2, fechaHasta);
            rs = ps.executeQuery();
            while (rs.next()) {
                ventas.add(extraer(rs));
            }
            return ventas;
        } finally {
            cerrar(rs);
            cerrar(ps);
            cerrar(cn);
        }
    }

    public static List<Entrada> entradasDeVenta(int idVenta) throws SQLException {
        String sql = "SELECT e.idEntrada, e.idCliente, e.idFuncion, e.idVenta, e.butaca, e.fechaVenta, "
                + "e.precioPagado, e.estado, p.titulo AS tituloPelicula, "
                + "CONVERT(VARCHAR(16), f.fechaHora, 120) AS fechaFuncion, "
                + "CAST(f.sala AS VARCHAR(5)) AS sala, s.nombre AS nombreSede "
                + "FROM Entrada e JOIN Funcion f ON f.idFuncion = e.idFuncion "
                + "JOIN Pelicula p ON p.idPelicula = f.idPelicula "
                + "JOIN Sede s ON s.idSede = f.idSede "
                + "WHERE e.idVenta = ? ORDER BY e.butaca";
        List<Entrada> entradas = new ArrayList<Entrada>();
        Connection cn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            cn = ConexionSQL.getConexion();
            ps = cn.prepareStatement(sql);
            ps.setInt(1, idVenta);
            rs = ps.executeQuery();
            while (rs.next()) {
                Entrada e = new Entrada();
                e.setIdEntrada(rs.getInt("idEntrada"));
                e.setIdCliente(rs.getInt("idCliente"));
                e.setIdFuncion(rs.getInt("idFuncion"));
                e.setIdVenta(rs.getInt("idVenta"));
                e.setButaca(rs.getString("butaca"));
                e.setFechaVenta(rs.getTimestamp("fechaVenta"));
                e.setPrecioPagado(rs.getBigDecimal("precioPagado"));
                e.setEstado(rs.getString("estado"));
                e.setTituloPelicula(rs.getString("tituloPelicula"));
                e.setFechaFuncion(rs.getString("fechaFuncion"));
                e.setSala(rs.getString("sala"));
                e.setNombreSede(rs.getString("nombreSede"));
                entradas.add(e);
            }
            return entradas;
        } finally {
            cerrar(rs);
            cerrar(ps);
            cerrar(cn);
        }
    }

    public static List<DetalleVenta> productosDeVenta(int idVenta) throws SQLException {
        String sql = "SELECT d.idDetalle, d.idVenta, d.idProducto, d.cantidad, d.precioUnit, d.subtotal, "
                + "p.nombre AS nombreProducto, p.categoria "
                + "FROM DetalleVenta d JOIN Producto p ON p.idProducto = d.idProducto "
                + "WHERE d.idVenta = ? ORDER BY p.nombre";
        List<DetalleVenta> detalles = new ArrayList<DetalleVenta>();
        Connection cn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            cn = ConexionSQL.getConexion();
            ps = cn.prepareStatement(sql);
            ps.setInt(1, idVenta);
            rs = ps.executeQuery();
            while (rs.next()) {
                DetalleVenta d = new DetalleVenta();
                d.setIdDetalle(rs.getInt("idDetalle"));
                d.setIdVenta(rs.getInt("idVenta"));
                d.setIdProducto(rs.getInt("idProducto"));
                d.setCantidad(rs.getInt("cantidad"));
                d.setPrecioUnit(rs.getBigDecimal("precioUnit"));
                d.setSubtotal(rs.getBigDecimal("subtotal"));
                d.setNombreProducto(rs.getString("nombreProducto"));
                d.setCategoria(rs.getString("categoria"));
                detalles.add(d);
            }
            return detalles;
        } finally {
            cerrar(rs);
            cerrar(ps);
            cerrar(cn);
        }
    }

    private static BigDecimal precioFuncion(Connection cn, int idFuncion) throws SQLException, CompraException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = cn.prepareStatement("SELECT precio FROM Funcion WHERE idFuncion = ?");
            ps.setInt(1, idFuncion);
            rs = ps.executeQuery();
            if (!rs.next()) {
                throw new CompraException("La funcion seleccionada no existe.");
            }
            return rs.getBigDecimal(1);
        } finally {
            cerrar(rs);
            cerrar(ps);
        }
    }

    private static BigDecimal precioProducto(Connection cn, int idProducto) throws SQLException, CompraException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = cn.prepareStatement("SELECT precio FROM Producto WHERE idProducto = ? AND activo = 1");
            ps.setInt(1, idProducto);
            rs = ps.executeQuery();
            if (!rs.next()) {
                throw new CompraException("El producto seleccionado no existe o no esta activo.");
            }
            return rs.getBigDecimal(1);
        } finally {
            cerrar(rs);
            cerrar(ps);
        }
    }

    private static Set<String> butacasOcupadas(Connection cn, int idFuncion) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        Set<String> ocupadas = new HashSet<String>();
        try {
            ps = cn.prepareStatement(
                    "SELECT butaca FROM Entrada WHERE idFuncion = ? AND estado = 'ACTIVA'");
            ps.setInt(1, idFuncion);
            rs = ps.executeQuery();
            while (rs.next()) {
                ocupadas.add(rs.getString(1));
            }
            return ocupadas;
        } finally {
            cerrar(rs);
            cerrar(ps);
        }
    }

    private static Venta extraer(ResultSet rs) throws SQLException {
        Venta v = new Venta();
        v.setIdVenta(rs.getInt("idVenta"));
        v.setIdCliente(rs.getInt("idCliente"));
        v.setIdSede(rs.getInt("idSede"));
        v.setFechaVenta(rs.getTimestamp("fechaVenta"));
        v.setCanal(rs.getString("canal"));
        v.setMedioPago(rs.getString("medioPago"));
        v.setTotal(rs.getBigDecimal("total"));
        v.setEstado(rs.getString("estado"));
        v.setNombreCliente(rs.getString("nombreCliente"));
        v.setDniCliente(rs.getString("dniCliente"));
        v.setNombreSede(rs.getString("nombreSede"));
        return v;
    }

    private static void cerrar(AutoCloseable c) {
        if (c != null) {
            try {
                c.close();
            } catch (Exception ignored) {
            }
        }
    }
}
