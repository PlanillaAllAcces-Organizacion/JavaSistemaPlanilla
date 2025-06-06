package Grupo05.Persistencia;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import static org.junit.jupiter.api.Assertions.*;

class ConnectionManagerTest {
    ConnectionManager connectionManager;
    @BeforeEach
    void setUp() throws SQLException {

        connectionManager = ConnectionManager.getInstance();
    }

    @AfterEach
    void tearDown() throws SQLException {

        if (connectionManager != null) {
            connectionManager.disconnect();
            connectionManager = null;
        }
    }
    @Test
    void connect() throws SQLException {

        Connection conn = connectionManager.connect();

        assertNotNull(conn, "La conexion no debe ser nula");

        assertFalse(conn.isClosed(), "La conexion debe esta abierta");
        if (conn != null) {
            conn.close();
        }
    }
}