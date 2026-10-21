package dao;

import conexion.ConexionSQL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import modelo.Cliente;

public class ClienteDAO {

    public static Cliente validar(String dni, String hashClave) throws SQLException {
        String sql = "SELECT idCliente, dni, nombres, apellidos, correo, telefono "
                + "FROM Cliente WHERE dni = ? AND clave = ?";
        Connection cn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            cn = ConexionSQL.getConexion();
            ps = cn.prepareStatement(sql);
            ps.setString(1, dni);
            ps.setString(2, hashClave);
            rs = ps.executeQuery();
            if (rs.next()) {
                return extraer(rs);
            }
            return null;
        } finally {
            cerrar(rs);
            cerrar(ps);
            cerrar(cn);
        }
    }

    public static Cliente buscarPorId(int idCliente) throws SQLException {
        String sql = "SELECT idCliente, dni, nombres, apellidos, correo, telefono "
                + "FROM Cliente WHERE idCliente = ?";
        Connection cn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            cn = ConexionSQL.getConexion();
            ps = cn.prepareStatement(sql);
            ps.setInt(1, idCliente);
            rs = ps.executeQuery();
            return rs.next() ? extraer(rs) : null;
        } finally {
            cerrar(rs);
            cerrar(ps);
            cerrar(cn);
        }
    }

    public static Cliente buscarPorDni(String dni) throws SQLException {
        String sql = "SELECT idCliente, dni, nombres, apellidos, correo, telefono "
                + "FROM Cliente WHERE dni = ?";
        Connection cn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            cn = ConexionSQL.getConexion();
            ps = cn.prepareStatement(sql);
            ps.setString(1, dni);
            rs = ps.executeQuery();
            return rs.next() ? extraer(rs) : null;
        } finally {
            cerrar(rs);
            cerrar(ps);
            cerrar(cn);
        }
    }

    public static boolean existeDni(String dni) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Cliente WHERE dni = ?";
        Connection cn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            cn = ConexionSQL.getConexion();
            ps = cn.prepareStatement(sql);
            ps.setString(1, dni);
            rs = ps.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        } finally {
            cerrar(rs);
            cerrar(ps);
            cerrar(cn);
        }
    }

    public static int insertar(String dni, String nombres, String apellidos,
            String correo, String telefono, String hashClave) throws SQLException {
        String sql = "INSERT INTO Cliente (dni, nombres, apellidos, correo, telefono, clave) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        Connection cn = null;
        PreparedStatement ps = null;
        try {
            cn = ConexionSQL.getConexion();
            ps = cn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setString(1, dni);
            ps.setString(2, nombres);
            ps.setString(3, apellidos);
            ps.setString(4, correo);
            ps.setString(5, telefono);
            ps.setString(6, hashClave);
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            try {
                if (rs.next()) {
                    return rs.getInt(1);
                }
                return 0;
            } finally {
                cerrar(rs);
            }
        } finally {
            cerrar(ps);
            cerrar(cn);
        }
    }

    private static Cliente extraer(ResultSet rs) throws SQLException {
        Cliente c = new Cliente();
        c.setIdCliente(rs.getInt("idCliente"));
        c.setDni(rs.getString("dni"));
        c.setNombres(rs.getString("nombres"));
        c.setApellidos(rs.getString("apellidos"));
        c.setCorreo(rs.getString("correo"));
        c.setTelefono(rs.getString("telefono"));
        return c;
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
