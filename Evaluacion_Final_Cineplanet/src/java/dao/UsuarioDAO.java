package dao;

import conexion.ConexionSQL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import modelo.Usuario;

public class UsuarioDAO {

    public static Usuario validar(String usuario, String hashClave) throws SQLException {
        String sql = "SELECT idUsuario, usuario, nombre, rol FROM Usuario WHERE usuario = ? AND clave = ?";
        Connection cn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            cn = ConexionSQL.getConexion();
            ps = cn.prepareStatement(sql);
            ps.setString(1, usuario);
            ps.setString(2, hashClave);
            rs = ps.executeQuery();
            if (rs.next()) {
                return new Usuario(rs.getInt("idUsuario"), rs.getString("usuario"),
                        rs.getString("nombre"), rs.getString("rol"));
            }
            return null;
        } finally {
            cerrar(rs);
            cerrar(ps);
            cerrar(cn);
        }
    }

    public static Usuario buscarPorUsuario(String usuario) throws SQLException {
        String sql = "SELECT idUsuario, usuario, nombre, rol FROM Usuario WHERE usuario = ?";
        Connection cn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            cn = ConexionSQL.getConexion();
            ps = cn.prepareStatement(sql);
            ps.setString(1, usuario);
            rs = ps.executeQuery();
            if (rs.next()) {
                return new Usuario(rs.getInt("idUsuario"), rs.getString("usuario"),
                        rs.getString("nombre"), rs.getString("rol"));
            }
            return null;
        } finally {
            cerrar(rs);
            cerrar(ps);
            cerrar(cn);
        }
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
