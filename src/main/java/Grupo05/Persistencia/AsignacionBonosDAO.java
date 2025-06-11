package Grupo05.Persistencia;

import Grupo05.dominio.AsignacionBonos;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AsignacionBonosDAO {
    private ConnectionManager conn;
    private PreparedStatement ps;
    private ResultSet rs;

    public AsignacionBonosDAO() {
        conn = ConnectionManager.getInstance();
    }

    public AsignacionBonos create(AsignacionBonos asignacion) throws SQLException {
        try {
            ps = conn.connect().prepareStatement(
                    "INSERT INTO AsignacionBono (EmpleadosId, BonosId, Estado) VALUES (?, ?, ?)",
                    PreparedStatement.RETURN_GENERATED_KEYS
            );
            ps.setInt(1, asignacion.getEmpleadoId());
            ps.setInt(2, asignacion.getBonoId());
            ps.setByte(3, asignacion.getEstado());

            if (ps.executeUpdate() > 0) {
                rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    asignacion.setId(rs.getInt(1));
                    return asignacion;
                }
            }
            return null;
        } finally {
            closeResources();
        }
    }

    public List<AsignacionBonos> getByEmpleadoId(int empleadoId) throws SQLException {
        List<AsignacionBonos> asignaciones = new ArrayList<>();
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            connection = conn.connect();
            ps = connection.prepareStatement(
                    "SELECT Id, EmpleadosId, BonosId, Estado FROM AsignacionBono WHERE EmpleadosId = ? AND Estado = 1"
            );
            ps.setInt(1, empleadoId);
            rs = ps.executeQuery();

            while (rs.next()) {
                AsignacionBonos asignacion = new AsignacionBonos();
                asignacion.setId(rs.getInt("Id"));
                asignacion.setEmpleadoId(rs.getInt("EmpleadosId"));
                asignacion.setBonoId(rs.getInt("BonosId"));
                asignacion.setEstado(rs.getByte("Estado"));
                asignaciones.add(asignacion);
            }
        } finally {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
            conn.disconnect();
        }

        return asignaciones;
    }
    public boolean update(AsignacionBonos asignacion) throws SQLException {
        try {
            ps = conn.connect().prepareStatement(
                    "UPDATE AsignacionBono SET Estado = ? WHERE Id = ?"
            );
            ps.setByte(1, asignacion.getEstado());
            ps.setInt(2, asignacion.getId());
            return ps.executeUpdate() > 0;
        } finally {
            closeResources();
        }
    }

    private void closeResources() {
        try {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
            conn.disconnect();
        } catch (SQLException e) {
            System.err.println("Error al cerrar recursos: " + e.getMessage());
        }
    }

    public boolean exists(int empleadoId, int bonoId) throws SQLException {
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        boolean existe = false;

        try {
            connection = conn.connect();
            ps = connection.prepareStatement(
                    "SELECT COUNT(*) FROM AsignacionBono WHERE EmpleadosId = ? AND BonosId = ? AND Estado = 1"
            );
            ps.setInt(1, empleadoId);
            ps.setInt(2, bonoId);
            rs = ps.executeQuery();

            if (rs.next()) {
                existe = rs.getInt(1) > 0;
            }
        } finally {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
            conn.disconnect();
        }
        return existe;
    }
}