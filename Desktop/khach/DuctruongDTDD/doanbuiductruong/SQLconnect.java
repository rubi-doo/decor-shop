package chatapplication;
import java.sql.*;

public class SQLconnect {
    private static final String DB_URL = "jdbc:sqlserver://localhost:1433;databaseName=ChatAP;encrypt=true;trustServerCertificate=true";
    private static final String USER = "sa";
    private static final String PASS = "123456";
    
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, USER, PASS);
    }
}