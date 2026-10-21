package dao;

import conexion.ConexionSQL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import modelo.Entrada;

public class EntradaDAO {

    public static final String SQL_LISTA = "SELECT TOP 100 e.idEntrada, e.idCliente, e.idFuncion, e.idVenta, "
            + "e.butaca, e.fechaVenta, e.precioPagado, e.estado, p.titulo AS tituloPelicula, "
            + "CONVERT(VARCHAR(16), f.fechaHora, 120) AS fechaFuncion, CAST(f.sala AS VARCHAR(5)) AS sala, "
            + "s.nombre AS nombreSede, c.nombres + ' ' + c.apellidos AS nombreCliente "
            + "FROM Entrada e "
            + "JOIN Funcion f ON f.idFuncion = e.idFuncion "
            + "JOIN Pelicula p ON p.idPelicula = f.idPelicula "
            + "JOIN Sede s ON s.idSede = f.idSede "
            + "JOIN Cliente c ON c.idCliente = e.idCliente ";

    public static List<Entrada> listarPorCliente(int idCliente) throws SQLException {
        String sql = SQL_LISTA + "WHERE e.idCliente = ? ORDER BY e.fechaVenta DESC";
        return consultar(sql, idCliente);
    }

    public static List<Entrada> listarPorDni(String dni) throws SQLException {
        String sql = SQL_LISTA + "WHERE c.dni = ? ORDER BY e.fechaVenta DESC";
        return consultar(sql, dni);
    }

    /**
     * Anula una entrada y devuelve la butaca a la funcion, dentro de una
     * transaccion. Devuelve false si la entrada ya estaba anulada.
     */
    public static boolean anular(int idEntrada) throws SQLException {
        Connection cn = null;
        PreparedStatement ps = null;
        boolean ok = false;
        try {
            cn = ConexionSQL.getConexion();
            cn.setAutoCommit(false);

            ps = cn.prepareStatement(
                    "UPDATE Entrada SET estado = 'ANULADA' WHERE idEntrada = ? AND estado = 'ACTIVA'");
            ps.setInt(1, idEntrada);
            if (ps.executeUpdate() == 0) {
                cn.rollback();
                return false;
            }
            cerrar(ps);
            ps = null;

            ps = cn.prepareStatement(
                    "UPDATE Funcion SET butacasDisponibles = butacasDisponibles + 1 "
                    + "WHERE idFuncion = (SELECT idFuncion FROM Entrada WHERE idEntrada = ?)");
            ps.setInt(1, idEntrada);
            ps.executeUpdate();
            cerrar(ps);
            ps = null;

            cn.commit();
            ok = true;
            return true;
        } finally {
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

    public static Entrada buscarPorId(int idEntrada) throws SQLException {
        String sql = SQL_LISTA + "WHERE e.idEntrada = ?";
        Connection cn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            cn = ConexionSQL.getConexion();
            ps = cn.prepareStatement(sql);
            ps.setInt(1, idEntrada);
            rs = ps.executeQuery();
            return rs.next() ? extraer(rs) : null;
        } finally {
            cerrar(rs);
            cerrar(ps);
            cerrar(cn);
        }
    }

    private static List<Entrada> consultar(String sql, String parametro) throws SQLException {
        List<Entrada> entradas = new ArrayList<Entrada>();
        Connection cn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            cn = ConexionSQL.getConexion();
            ps = cn.prepareStatement(sql);
            ps.setString(1, parametro);
            rs = ps.executeQuery();
            while (rs.next()) {
                entradas.add(extraer(rs));
            }
            return entradas;
        } finally {
            cerrar(rs);
            cerrar(ps);
            cerrar(cn);
        }
    }

    private static List<Entrada> consultar(String sql, int parametro) throws SQLException {
        List<Entrada> entradas = new ArrayList<Entrada>();
        Connection cn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            cn = ConexionSQL.getConexion();
            ps = cn.prepareStatement(sql);
            ps.setInt(1, parametro);
            rs = ps.executeQuery();
            while (rs.next()) {
                entradas.add(extraer(rs));
            }
            return entradas;
        } finally {
            cerrar(rs);
            cerrar(ps);
            cerrar(cn);
        }
    }

    private static Entrada extraer(ResultSet rs) throws SQLException {
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
        e.setNombreCliente(rs.getString("nombreCliente"));
        return e;
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
