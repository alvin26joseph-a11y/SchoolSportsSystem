package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Oracle JDBC Connection Class
 * Change USERNAME and PASSWORD to match your Oracle setup
 */
public class DBConnection {

    private static final String URL      = "jdbc:oracle:thin:@localhost:1521:xe";
    private static final String USERNAME = "C##sports_admin";  
    private static final String PASSWORD = "sports123";     

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            return DriverManager.getConnection(URL, USERNAME, PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new SQLException("Oracle JDBC Driver not found! Add ojdbc jar.\n" + e.getMessage());
        }
    }
}