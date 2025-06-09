package Grupo05.Persistencia;

import Grupo05.dominio.PagoEmpleado;
import java.sql.*;
import java.util.ArrayList;

public class PagoEmpleadoDAO {
    private ConnectionManager conn;
    private PreparedStatement ps;
    private ResultSet rs;

    public PagoEmpleadoDAO() {
        conn = ConnectionManager.getInstance();
    }

    /**
     * Crea un nuevo registro de pago de empleado
     * @param pago Objeto PagoEmpleado con los datos a insertar
     * @return PagoEmpleado creado con su ID generado
     * @throws SQLException Si ocurre un error de base de datos
     */
    public PagoEmpleado create(PagoEmpleado pago) throws SQLException {
        PagoEmpleado res = null;
        try {
            ps = conn.connect().prepareStatement(
                    "INSERT INTO PagoEmpleado (EmpleadoId, FechaPago, HorasTrabajadas, ValorHora, TotalPago) VALUES (?, ?, ?, ?, ?)",
                    PreparedStatement.RETURN_GENERATED_KEYS
            );

            ps.setInt(1, pago.getEmpleadoId());
            ps.setDate(2, new java.sql.Date(pago.getFechaPago().getTime()));
            ps.setInt(3, pago.getHorasTrabajadas());
            ps.setDouble(4, pago.getValorHora());
            ps.setDouble(5, pago.getTotalPago());

            int affectedRows = ps.executeUpdate();

            if (affectedRows != 0) {
                rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    int idGenerado = rs.getInt(1);
                    res = getById(idGenerado);
                }
            }
        } catch (SQLException ex) {
            throw new SQLException("Error al crear el pago: " + ex.getMessage(), ex);
        } finally {
            closeResources();
        }
        return res;
    }

    /**
     * Obtiene un pago por su ID
     * @param id ID del pago a buscar
     * @return Objeto PagoEmpleado encontrado o null si no existe
     * @throws SQLException Si ocurre un error de base de datos
     */
    public PagoEmpleado getById(int id) throws SQLException {
        PagoEmpleado pago = null;
        try {
            ps = conn.connect().prepareStatement(
                    "SELECT Id, EmpleadoId, FechaPago, HorasTrabajadas, ValorHora, TotalPago FROM PagoEmpleado WHERE Id = ?"
            );

            ps.setInt(1, id);
            rs = ps.executeQuery();

            if (rs.next()) {
                pago = new PagoEmpleado();
                pago.setId(rs.getInt("Id"));
                pago.setEmpleadoId(rs.getInt("EmpleadoId"));
                pago.setFechaPago(rs.getDate("FechaPago"));
                pago.setHorasTrabajadas(rs.getInt("HorasTrabajadas"));
                pago.setValorHora(rs.getDouble("ValorHora"));
                pago.setTotalPago(rs.getDouble("TotalPago"));
            }
        } catch (SQLException ex) {
            throw new SQLException("Error al obtener pago por ID: " + ex.getMessage(), ex);
        } finally {
            closeResources();
        }
        return pago;
    }

    /**
     * Obtiene todos los pagos de un empleado específico
     * @param empleadoId ID del empleado
     * @return Lista de pagos del empleado
     * @throws SQLException Si ocurre un error de base de datos
     */
    public ArrayList<PagoEmpleado> getByEmpleado(int empleadoId) throws SQLException {
        ArrayList<PagoEmpleado> pagos = new ArrayList<>();
        try {
            ps = conn.connect().prepareStatement(
                    "SELECT Id, EmpleadoId, FechaPago, HorasTrabajadas, ValorHora, TotalPago FROM PagoEmpleado WHERE EmpleadoId = ? ORDER BY FechaPago DESC"
            );

            ps.setInt(1, empleadoId);
            rs = ps.executeQuery();

            while (rs.next()) {
                PagoEmpleado pago = new PagoEmpleado();
                pago.setId(rs.getInt("Id"));
                pago.setEmpleadoId(rs.getInt("EmpleadoId"));
                pago.setFechaPago(rs.getDate("FechaPago"));
                pago.setHorasTrabajadas(rs.getInt("HorasTrabajadas"));
                pago.setValorHora(rs.getDouble("ValorHora"));
                pago.setTotalPago(rs.getDouble("TotalPago"));
                pagos.add(pago);
            }
        } catch (SQLException ex) {
            throw new SQLException("Error al obtener pagos del empleado: " + ex.getMessage(), ex);
        } finally {
            closeResources();
        }
        return pagos;
    }

    /**
     * Obtiene todos los pagos en un rango de fechas
     * @param fechaInicio Fecha de inicio del rango
     * @param fechaFin Fecha de fin del rango
     * @return Lista de pagos en el rango especificado
     * @throws SQLException Si ocurre un error de base de datos
     */
    public ArrayList<PagoEmpleado> getByFecha(Date fechaInicio, Date fechaFin) throws SQLException {
        ArrayList<PagoEmpleado> pagos = new ArrayList<>();
        try {
            ps = conn.connect().prepareStatement(
                    "SELECT Id, EmpleadoId, FechaPago, HorasTrabajadas, ValorHora, TotalPago FROM PagoEmpleado WHERE FechaPago BETWEEN ? AND ? ORDER BY FechaPago"
            );

            ps.setDate(1, new java.sql.Date(fechaInicio.getTime()));
            ps.setDate(2, new java.sql.Date(fechaFin.getTime()));
            rs = ps.executeQuery();

            while (rs.next()) {
                PagoEmpleado pago = new PagoEmpleado();
                pago.setId(rs.getInt("Id"));
                pago.setEmpleadoId(rs.getInt("EmpleadoId"));
                pago.setFechaPago(rs.getDate("FechaPago"));
                pago.setHorasTrabajadas(rs.getInt("HorasTrabajadas"));
                pago.setValorHora(rs.getDouble("ValorHora"));
                pago.setTotalPago(rs.getDouble("TotalPago"));
                pagos.add(pago);
            }
        } catch (SQLException ex) {
            throw new SQLException("Error al obtener pagos por fecha: " + ex.getMessage(), ex);
        } finally {
            closeResources();
        }
        return pagos;
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