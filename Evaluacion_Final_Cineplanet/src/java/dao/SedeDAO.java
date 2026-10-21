package dao;

import conexion.ConexionSQL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import modelo.Sede;

public class SedeDAO {

    public static List<Sede> listar() throws SQLException {
        String sql = "SELECT idSede, nombre, ciudad, region, salas FROM Sede ORDER BY nombre";
        List<Sede> sedes = new ArrayList<Sede>();
        Connection cn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            cn = ConexionSQL.getConexion();
            ps = cn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                Sede s = new Sede();
                s.setIdSede(rs.getInt("idSede"));
                s.setNombre(rs.getString("nombre"));
                s.setCiudad(rs.getString("ciudad"));
                s.setRegion(rs.getString("region"));
                s.setSalas(rs.getInt("salas"));
                sedes.add(s);
            }
            return sedes;
        } finally {
            cerrar(rs);
            cerrar(ps);
            cerrar(cn);
        }
    }

    public static Sede buscarPorId(int idSede) throws SQLException {
        String sql = "SELECT idSede, nombre, ciudad, region, salas FROM Sede WHERE idSede = ?";
        Connection cn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            cn = ConexionSQL.getConexion();
            ps = cn.prepareStatement(sql);
            ps.setInt(1, idSede);
            rs = ps.executeQuery();
            if (rs.next()) {
                Sede s = new Sede();
                s.setIdSede(rs.getInt("idSede"));
                s.setNombre(rs.getString("nombre"));
                s.setCiudad(rs.getString("ciudad"));
                s.setRegion(rs.getString("region"));
                s.setSalas(rs.getInt("salas"));
                return s;
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
