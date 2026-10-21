package conexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionSQL {

    public static final String URL = "jdbc:sqlserver://localhost:1433;databaseName=BD_CineplanetFinal;user=sa;password=123;encrypt=false";
    private static final String DRIVER = "com.microsoft.sqlserver.jdbc.SQLServerDriver";

    public static Connection getConexion() throws SQLException {
        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver JDBC de SQL Server no encontrado", e);
        }
        return DriverManager.getConnection(URL);
    }
}
