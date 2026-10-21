package dao;

import conexion.ConexionSQL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import modelo.Funcion;

public class FuncionDAO {

    public static final String SQL_BASICA = "SELECT f.idFuncion, f.idPelicula, f.idSede, f.sala, f.fechaHora, "
            + "f.precio, f.butacasTotales, f.butacasDisponibles, p.titulo, p.genero, s.nombre AS nombreSede "
            + "FROM Funcion f "
            + "JOIN Pelicula p ON p.idPelicula = f.idPelicula "
            + "JOIN Sede s ON s.idSede = f.idSede ";

    public static List<Funcion> cartelera(int idSede, Timestamp desde, Timestamp hasta) throws SQLException {
        String sql = SQL_BASICA
                + "WHERE f.idSede = ? AND f.fechaHora >= ? AND f.fechaHora < ? AND f.butacasDisponibles > 0 "
                + "ORDER BY f.fechaHora";
        List<Funcion> funciones = new ArrayList<Funcion>();
        Connection cn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            cn = ConexionSQL.getConexion();
            ps = cn.prepareStatement(sql);
            ps.setInt(1, idSede);
            ps.setTimestamp(2, desde);
            ps.setTimestamp(3, hasta);
            rs = ps.executeQuery();
            while (rs.next()) {
                funciones.add(extraer(rs));
            }
            return funciones;
        } finally {
            cerrar(rs);
            cerrar(ps);
            cerrar(cn);
        }
    }

    public static Funcion buscarPorId(int idFuncion) throws SQLException {
        String sql = SQL_BASICA + "WHERE f.idFuncion = ?";
        Connection cn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            cn = ConexionSQL.getConexion();
            ps = cn.prepareStatement(sql);
            ps.setInt(1, idFuncion);
            rs = ps.executeQuery();
            return rs.next() ? extraer(rs) : null;
        } finally {
            cerrar(rs);
            cerrar(ps);
            cerrar(cn);
        }
    }

    public static Set<String> butacasOcupadas(int idFuncion) throws SQLException {
        String sql = "SELECT butaca FROM Entrada WHERE idFuncion = ? AND estado = 'ACTIVA'";
        Set<String> ocupadas = new HashSet<String>();
        Connection cn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            cn = ConexionSQL.getConexion();
            ps = cn.prepareStatement(sql);
            ps.setInt(1, idFuncion);
            rs = ps.executeQuery();
            while (rs.next()) {
                ocupadas.add(rs.getString("butaca"));
            }
            return ocupadas;
        } finally {
            cerrar(rs);
            cerrar(ps);
            cerrar(cn);
        }
    }

    private static Funcion extraer(ResultSet rs) throws SQLException {
        Funcion f = new Funcion();
        f.setIdFuncion(rs.getInt("idFuncion"));
        f.setIdPelicula(rs.getInt("idPelicula"));
        f.setIdSede(rs.getInt("idSede"));
        f.setSala(rs.getInt("sala"));
        f.setFechaHora(rs.getTimestamp("fechaHora"));
        f.setPrecio(rs.getBigDecimal("precio"));
        f.setButacasTotales(rs.getInt("butacasTotales"));
        f.setButacasDisponibles(rs.getInt("butacasDisponibles"));
        f.setTituloPelicula(rs.getString("titulo"));
        f.setGeneroPelicula(rs.getString("genero"));
        f.setNombreSede(rs.getString("nombreSede"));
        return f;
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
