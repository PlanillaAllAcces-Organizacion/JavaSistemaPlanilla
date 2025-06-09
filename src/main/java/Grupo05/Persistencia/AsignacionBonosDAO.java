package Grupo05.Persistencia;

import Grupo05.dominio.AsignacionBonos;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class AsignacionBonosDAO {
    private ConnectionManager conn;
    private PreparedStatement ps;
    private ResultSet rs;

    public AsignacionBonosDAO() {
        conn = ConnectionManager.getInstance();
    }

    /**
     * Asigna un bono a un empleado
     * @param asignacion Objeto AsignacionBono con los datos a insertar
     * @return AsignacionBono creada con su ID generado
     * @throws SQLException Si ocurre un error de base de datos
     */
    public AsignacionBonos create(AsignacionBonos asignacion) throws SQLException {
        AsignacionBonos res = null;
        try {
            // Verificar si ya existe la asignación
            if (!existeAsignacion(asignacion.getEmpleadoId(), asignacion.getBonoId())) {
                ps = conn.connect().prepareStatement(
                        "INSERT INTO AsignacionBono (EmpleadosId, BonosId, Estado) VALUES (?, ?, ?)",
                        PreparedStatement.RETURN_GENERATED_KEYS
                );

                ps.setInt(1, asignacion.getEmpleadoId());
                ps.setInt(2, asignacion.getBonoId());
                ps.setByte(3, asignacion.getEstado());

                int affectedRows = ps.executeUpdate();

                if (affectedRows != 0) {
                    rs = ps.getGeneratedKeys();
                    if (rs.next()) {
                        int idGenerado = rs.getInt(1);
                        res = getById(idGenerado);
                    }
                }
            } else {
                throw new SQLException("El bono ya está asignado a este empleado");
            }
        } catch (SQLException ex) {
            throw new SQLException("Error al asignar el bono: " + ex.getMessage(), ex);
        } finally {
            closeResources();
        }
        return res;
    }

    /**
     * Actualiza el estado de una asignación de bono
     * @param asignacion Objeto AsignacionBono con los datos actualizados
     * @return true si la actualización fue exitosa
     * @throws SQLException Si ocurre un error de base de datos
     */
    public boolean update(AsignacionBonos asignacion) throws SQLException {
        boolean res = false;
        try {
            ps = conn.connect().prepareStatement(
                    "UPDATE AsignacionBono SET Estado = ? WHERE Id = ?"
            );

            ps.setByte(1, asignacion.getEstado());
            ps.setInt(2, asignacion.getId());

            res = ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            throw new SQLException("Error al actualizar la asignación de bono: " + ex.getMessage(), ex);
        } finally {
            closeResources();
        }
        return res;
    }

    /**
     * Obtiene una asignación por su ID
     * @param id ID de la asignación a buscar
     * @return Objeto AsignacionBono encontrado o null si no existe
     * @throws SQLException Si ocurre un error de base de datos
     */
    public AsignacionBonos getById(int id) throws SQLException {
        AsignacionBonos asignacion = null;
        try {
            ps = conn.connect().prepareStatement(
                    "SELECT Id, EmpleadosId, BonosId, Estado FROM AsignacionBono WHERE Id = ?"
            );

            ps.setInt(1, id);
            rs = ps.executeQuery();

            if (rs.next()) {
                asignacion = new AsignacionBonos();
                asignacion.setId(rs.getInt("Id"));
                asignacion.setEmpleadoId(rs.getInt("EmpleadosId"));
                asignacion.setBonoId(rs.getInt("BonosId"));
                asignacion.setEstado(rs.getByte("Estado"));
            }
        } catch (SQLException ex) {
            throw new SQLException("Error al obtener asignación por ID: " + ex.getMessage(), ex);
        } finally {
            closeResources();
        }
        return asignacion;
    }

    /**
     * Obtiene todas las asignaciones de bonos de un empleado
     * @param empleadoId ID del empleado
     * @return Lista de asignaciones de bonos del empleado
     * @throws SQLException Si ocurre un error de base de datos
     */
    public ArrayList<AsignacionBonos> getByEmpleadoId(int empleadoId) throws SQLException {
        ArrayList<AsignacionBonos> asignaciones = new ArrayList<>();
        try {
            ps = conn.connect().prepareStatement(
                    "SELECT Id, EmpleadosId, BonosId, Estado FROM AsignacionBono WHERE EmpleadosId = ?"
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
        } catch (SQLException ex) {
            throw new SQLException("Error al obtener asignaciones por empleado: " + ex.getMessage(), ex);
        } finally {
            closeResources();
        }
        return asignaciones;
    }

    /**
     * Verifica si ya existe una asignación para un empleado y bono específicos
     * @param empleadoId ID del empleado
     * @param bonoId ID del bono
     * @return true si ya existe la asignación
     * @throws SQLException Si ocurre un error de base de datos
     */
    private boolean existeAsignacion(int empleadoId, int bonoId) throws SQLException {
        boolean existe = false;
        try {
            ps = conn.connect().prepareStatement(
                    "SELECT 1 FROM AsignacionBono WHERE EmpleadosId = ? AND BonosId = ?"
            );

            ps.setInt(1, empleadoId);
            ps.setInt(2, bonoId);
            rs = ps.executeQuery();

            existe = rs.next();
        } catch (SQLException ex) {
            throw new SQLException("Error al verificar asignación existente: " + ex.getMessage(), ex);
        } finally {
            closeResources();
        }
        return existe;
    }

    /**
     * Método auxiliar para cerrar recursos de base de datos
     */
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