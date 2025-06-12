package Grupo05.Persistencia;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionManager {

   /* private static final String  STR_CONNECTION = "jdbc:sqlserver://LAPTOP-B8J343I7:1433 "+
            "encrypt=true; " +
            "database=Paysheet2; " +
            "trustServerCertificate=true;" +
            "user=java2025;" +
            "password=12345"; */

    /*private static final String  STR_CONNECTION = "jdbc:sqlserver://JC39012\\SQLEXPRESS; "+
            "encrypt=true; " +
            "database=Paysheet2; " +
            "trustServerCertificate=true;" +
            "user=java2025;" +
            "password=12345";*/

    private static final String  STR_CONNECTION = "jdbc:sqlserver://JC39012\\SQLEXPRESS; "+
            "encrypt=true; " +
            "database=Paysheet2; " +
            "trustServerCertificate=true;" +
            "user=java2025;" +
            "password=12345";


    private Connection connection;

    private static ConnectionManager instance;


    private ConnectionManager() {
        this.connection = null;
        try {

            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException e) {
            // Si el driver no se encuentra, se lanza una excepción indicando el error.
            throw new RuntimeException("Error al cargar el driver JDBC de SQL Server", e);
        }
    }


    public synchronized Connection connect() throws SQLException {
        if (this.connection == null || this.connection.isClosed()) {
            try {
                this.connection = DriverManager.getConnection(STR_CONNECTION);
            } catch (SQLException exception) {

                throw new SQLException("Error al conectar a la base de datos: " + exception.getMessage(), exception);
            }
        }
        // Retorna la conexión (ya sea la existente o la recién creada).
        return this.connection;
    }


    public void disconnect() throws SQLException {
        if (this.connection != null) {
            try {
                this.connection.close();
            } catch (SQLException exception) {

                throw new SQLException("Error al cerrar la conexión: " + exception.getMessage(), exception);
            } finally {

                this.connection = null;
            }
        }
    }

    public static synchronized ConnectionManager getInstance() {
        if (instance == null) {
            instance = new ConnectionManager();
        }
        return instance;
    }
}