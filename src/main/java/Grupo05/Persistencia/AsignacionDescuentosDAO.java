package Grupo05.Persistencia;

import Grupo05.dominio.AsignacionDescuento;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AsignacionDescuentosDAO {
    private ConnectionManager conn;
    private PreparedStatement ps;
    private ResultSet rs;

    public AsignacionDescuentosDAO() {
        conn = ConnectionManager.getInstance();
    }

    public AsignacionDescuento create(AsignacionDescuento asignacion) throws SQLException {
        try {
            ps = conn.connect().prepareStatement(
                    "INSERT INTO AsignacionDescuento (EmpleadosId, DescuentosId) VALUES (?, ?)",
                    PreparedStatement.RETURN_GENERATED_KEYS
            );
            ps.setInt(1, asignacion.getEmpleadoId());
            ps.setInt(2, asignacion.getDescuentos());

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

    public List<AsignacionDescuento> getByEmpleadoId(int empleadoId) throws SQLException {
        List<AsignacionDescuento> asignaciones = new ArrayList<>();
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            connection = conn.connect();
            ps = connection.prepareStatement(
                    "SELECT Id, EmpleadosId, DescuentosId FROM AsignacionDescuento WHERE EmpleadosId = ?"
            );
            ps.setInt(1, empleadoId);
            rs = ps.executeQuery();

            while (rs.next()) {
                AsignacionDescuento asignacion = new AsignacionDescuento();
                asignacion.setId(rs.getInt("Id"));
                asignacion.setEmpleadoId(rs.getInt("EmpleadosId"));
                asignacion.setDescuentos(rs.getInt("DescuentosId"));
                asignaciones.add(asignacion);
            }
        } finally {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
            conn.disconnect();
        }

        return asignaciones;
    }

    public boolean update(AsignacionDescuento asignacion) throws SQLException {
        try {
            ps = conn.connect().prepareStatement(
                    "UPDATE AsignacionDescuento SET EmpleadosId = ?, DescuentosId = ? WHERE Id = ?"
            );
            ps.setInt(1, asignacion.getEmpleadoId());
            ps.setInt(2, asignacion.getDescuentos());
            ps.setInt(3, asignacion.getId());
            return ps.executeUpdate() > 0;
        } finally {
            closeResources();
        }
    }

    public boolean exists(int empleadoId, int descuentoId) throws SQLException {
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        boolean existe = false;

        try {
            connection = conn.connect();
            ps = connection.prepareStatement(
                    "SELECT COUNT(*) FROM AsignacionDescuento WHERE EmpleadosId = ? AND DescuentosId = ?"
            );
            ps.setInt(1, empleadoId);
            ps.setInt(2, descuentoId);
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

    private void closeResources() {
        try {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
            conn.disconnect();
        } catch (SQLException e) {
            System.err.println("Error al cerrar recursos: " + e.getMessage());
        }
    }
}
