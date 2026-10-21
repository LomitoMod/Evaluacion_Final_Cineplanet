package dao;

import conexion.ConexionSQL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AuditoriaDAO {

    public static final String LOGIN_OK = "LOGIN_OK";
    public static final String LOGIN_FALLIDO = "LOGIN_FALLIDO";
    public static final String BLOQUEO = "BLOQUEO";
    public static final String LOGOUT = "LOGOUT";
    public static final String COMPRA = "COMPRA";
    public static final String ANULACION = "ANULACION";
    public static final String ACCESO_DENEGADO = "ACCESO_DENEGADO";
    public static final String CSRF = "CSRF";

    /** Nunca lanza excepciones: la auditoria no debe romper la peticion en curso. */
    public static void registrar(String usuario, String evento, String detalle, String ip) {
        String sql = "INSERT INTO Auditoria (usuario, evento, detalle, ip) VALUES (?, ?, ?, ?)";
        Connection cn = null;
        PreparedStatement ps = null;
        try {
            cn = ConexionSQL.getConexion();
            ps = cn.prepareStatement(sql);
            ps.setString(1, cortar(usuario, 30));
            ps.setString(2, cortar(evento, 20));
            ps.setString(3, cortar(detalle, 200));
            ps.setString(4, cortar(ip, 45));
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("[AUDITORIA] No se pudo registrar " + evento + ": " + e.getMessage());
        } finally {
            cerrar(ps);
            cerrar(cn);
        }
    }

    public static class Evento {

        public int idAuditoria;
        public String fecha;
        public String usuario;
        public String evento;
        public String detalle;
        public String ip;

        public int getIdAuditoria() {
            return idAuditoria;
        }

        public String getFecha() {
            return fecha;
        }

        public String getUsuario() {
            return usuario;
        }

        public String getEvento() {
            return evento;
        }

        public String getDetalle() {
            return detalle;
        }

        public String getIp() {
            return ip;
        }
    }

    public static List<Evento> listar(int limite) throws SQLException {
        String sql = "SELECT TOP (?) idAuditoria, CONVERT(VARCHAR(19), fecha, 120) AS fecha, "
                + "usuario, evento, detalle, ip FROM Auditoria ORDER BY idAuditoria DESC";
        List<Evento> eventos = new ArrayList<Evento>();
        Connection cn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            cn = ConexionSQL.getConexion();
            ps = cn.prepareStatement(sql);
            ps.setInt(1, limite);
            rs = ps.executeQuery();
            while (rs.next()) {
                Evento ev = new Evento();
                ev.idAuditoria = rs.getInt("idAuditoria");
                ev.fecha = rs.getString("fecha");
                ev.usuario = rs.getString("usuario");
                ev.evento = rs.getString("evento");
                ev.detalle = rs.getString("detalle");
                ev.ip = rs.getString("ip");
                eventos.add(ev);
            }
            return eventos;
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ignorada) {
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException ignorada) {
                }
            }
            if (cn != null) {
                try {
                    cn.close();
                } catch (SQLException ignorada) {
                }
            }
        }
    }

    private static String cortar(String texto, int max) {
        if (texto == null) {
            return "-";
        }
        String t = texto.trim();
        if (t.length() == 0) {
            return "-";
        }
        return t.length() > max ? t.substring(0, max) : t;
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
