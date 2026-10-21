package dao;

import conexion.ConexionSQL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import modelo.Producto;

public class ProductoDAO {

    public static List<Producto> listarActivos() throws SQLException {
        String sql = "SELECT idProducto, nombre, categoria, precio, activo "
                + "FROM Producto WHERE activo = 1 ORDER BY categoria, nombre";
        List<Producto> productos = new ArrayList<Producto>();
        Connection cn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            cn = ConexionSQL.getConexion();
            ps = cn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                productos.add(extraer(rs));
            }
            return productos;
        } finally {
            cerrar(rs);
            cerrar(ps);
            cerrar(cn);
        }
    }

    public static Producto buscarPorId(int idProducto) throws SQLException {
        String sql = "SELECT idProducto, nombre, categoria, precio, activo "
                + "FROM Producto WHERE idProducto = ? AND activo = 1";
        Connection cn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            cn = ConexionSQL.getConexion();
            ps = cn.prepareStatement(sql);
            ps.setInt(1, idProducto);
            rs = ps.executeQuery();
            return rs.next() ? extraer(rs) : null;
        } finally {
            cerrar(rs);
            cerrar(ps);
            cerrar(cn);
        }
    }

    private static Producto extraer(ResultSet rs) throws SQLException {
        Producto p = new Producto();
        p.setIdProducto(rs.getInt("idProducto"));
        p.setNombre(rs.getString("nombre"));
        p.setCategoria(rs.getString("categoria"));
        p.setPrecio(rs.getBigDecimal("precio"));
        p.setActivo(rs.getBoolean("activo"));
        return p;
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
