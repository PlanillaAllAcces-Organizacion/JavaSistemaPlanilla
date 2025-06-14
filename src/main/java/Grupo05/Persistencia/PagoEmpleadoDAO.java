package Grupo05.Persistencia;

import Grupo05.dominio.PagoEmpleado;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PagoEmpleadoDAO {
    private ConnectionManager conn;
    private PreparedStatement ps;
    private ResultSet rs;

    public PagoEmpleadoDAO() {
        conn = ConnectionManager.getInstance();
    }

    /**
     * Inserta un nuevo pago de empleado en la base de datos.
     * La tabla PagoEmpleado ahora usa TotalBonosAplicados y TotalDescuentosAplicados.
     * @param pago El objeto PagoEmpleado a insertar.
     * @return El objeto PagoEmpleado con el ID generado, o null si falla.
     * @throws SQLException Si ocurre un error de base de datos.
     */
    public PagoEmpleado create(PagoEmpleado pago) throws SQLException {
        PagoEmpleado res = null;
        try {

            ps = conn.connect().prepareStatement(
                    "INSERT INTO PagoEmpleado (EmpleadoId, FechaPago, HorasTrabajadas, ValorHora, TotalPago, TotalBonosAplicados, TotalDescuentosAplicados) VALUES (?, ?, ?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            );

            ps.setInt(1, pago.getEmpleadoId());
            ps.setTimestamp(2, Timestamp.valueOf(pago.getFechaPago()));
            ps.setInt(3, pago.getHorasTrabajadas());
            ps.setBigDecimal(4, BigDecimal.valueOf(pago.getValorHora())); // ValorHora es DECIMAL(8,2) en la DB
            ps.setBigDecimal(5, BigDecimal.valueOf(pago.getTotalPago())); // TotalPago es DECIMAL(10,2) en la DB (modificado por ti)

            // ¡MAPEO CORREGIDO! Usa los nuevos campos de totales
            ps.setBigDecimal(6, pago.getTotalBonosAplicados());
            ps.setBigDecimal(7, pago.getTotalDescuentosAplicados());

            int affectedRows = ps.executeUpdate();

            if (affectedRows > 0) {
                rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    int idGenerado = rs.getInt(1);
                    res = getPagoById(idGenerado); // Llama a getPagoById (nombre corregido)
                } else {
                    throw new SQLException("La creación del pago de empleado falló, no se obtuvo ID generado.");
                }
            }
        } catch (SQLException ex) {
            throw new SQLException("Error al crear el pago de empleado: " + ex.getMessage(), ex);
        } finally {
            closeResources();
        }
        return res;
    }

    /**
     * Actualiza un pago de empleado existente en la base de datos.
     * La tabla PagoEmpleado ahora usa TotalBonosAplicados y TotalDescuentosAplicados.
     * @param pago El objeto PagoEmpleado con los datos actualizados.
     * @return true si la actualización fue exitosa, false de lo contrario.
     * @throws SQLException Si ocurre un error de base de datos.
     */
    public boolean update(PagoEmpleado pago) throws SQLException {
        boolean res = false;
        try {
            // ¡CONSULTA SQL CORREGIDA! Utiliza TotalBonosAplicados y TotalDescuentosAplicados
            ps = conn.connect().prepareStatement(
                    "UPDATE PagoEmpleado " +
                            "SET EmpleadoId = ?, FechaPago = ?, HorasTrabajadas = ?, ValorHora = ?, TotalPago = ?, TotalBonosAplicados = ?, TotalDescuentosAplicados = ? " +
                            "WHERE Id = ?"
            );

            ps.setInt(1, pago.getEmpleadoId());
            ps.setTimestamp(2, Timestamp.valueOf(pago.getFechaPago()));
            ps.setInt(3, pago.getHorasTrabajadas());
            ps.setBigDecimal(4, BigDecimal.valueOf(pago.getValorHora()));
            ps.setBigDecimal(5, BigDecimal.valueOf(pago.getTotalPago()));
            // ¡MAPEO CORREGIDO! Usa los nuevos campos de totales
            ps.setBigDecimal(6, pago.getTotalBonosAplicados());
            ps.setBigDecimal(7, pago.getTotalDescuentosAplicados());
            ps.setInt(8, pago.getId());

            if (ps.executeUpdate() > 0) {
                res = true;
            }
        } catch (SQLException ex) {
            throw new SQLException("Error al modificar el pago de empleado: " + ex.getMessage(), ex);
        } finally {
            closeResources();
        }
        return res;
    }

    /**
     * Elimina un pago de empleado de la base de datos por su ID.
     * @param id El ID del pago de empleado a eliminar.
     * @return true si la eliminación fue exitosa, false de lo contrario.
     * @throws SQLException Si ocurre un error de base de datos.
     */
    public boolean delete(int id) throws SQLException {
        boolean res = false;
        try {
            ps = conn.connect().prepareStatement(
                    "DELETE FROM PagoEmpleado WHERE Id = ?"
            );
            ps.setInt(1, id);

            if (ps.executeUpdate() > 0) {
                res = true;
            }
        } catch (SQLException ex) {
            throw new SQLException("Error al eliminar el pago de empleado: " + ex.getMessage(), ex);
        } finally {
            closeResources();
        }
        return res;
    }

    /**
     * Obtiene un pago de empleado por su ID.
     * Nombre del método corregido de getById a getPagoById.
     * @param id El ID del pago de empleado.
     * @return El objeto PagoEmpleado si se encuentra, o null si no existe.
     * @throws SQLException Si ocurre un error de base de datos.
     */
    public PagoEmpleado getPagoById(int id) throws SQLException { // ¡NOMBRE CORREGIDO!
        PagoEmpleado pago = null;
        try {
            // ¡CONSULTA SQL CORREGIDA! Selecciona TotalBonosAplicados y TotalDescuentosAplicados
            ps = conn.connect().prepareStatement(
                    "SELECT Id, EmpleadoId, FechaPago, HorasTrabajadas, ValorHora, TotalPago, TotalBonosAplicados, TotalDescuentosAplicados FROM PagoEmpleado WHERE Id = ?"
            );
            ps.setInt(1, id);
            rs = ps.executeQuery();

            if (rs.next()) {
                pago = new PagoEmpleado();
                pago.setId(rs.getInt("Id"));
                pago.setEmpleadoId(rs.getInt("EmpleadoId"));
                pago.setFechaPago(rs.getObject("FechaPago", LocalDateTime.class));
                pago.setHorasTrabajadas(rs.getInt("HorasTrabajadas"));
                pago.setValorHora(rs.getDouble("ValorHora"));
                pago.setTotalPago(rs.getDouble("TotalPago"));
                // ¡MAPEO CORREGIDO! Obtiene los nuevos campos de totales
                pago.setTotalBonosAplicados(rs.getBigDecimal("TotalBonosAplicados"));
                pago.setTotalDescuentosAplicados(rs.getBigDecimal("TotalDescuentosAplicados"));
            }
        } catch (SQLException ex) {
            throw new SQLException("Error al obtener pago de empleado por ID: " + ex.getMessage(), ex);
        } finally {
            closeResources();
        }
        return pago;
    }

    /**
     * Obtiene una lista de todos los pagos de empleados.
     * Nombre del método corregido de getAll a getAllPagos.
     * @return Una lista de objetos PagoEmpleado.
     * @throws SQLException Si ocurre un error de base de datos.
     */
    public List<PagoEmpleado> getAllPagos() throws SQLException { // ¡NOMBRE CORREGIDO!
        List<PagoEmpleado> pagos = new ArrayList<>();
        try {
            // ¡CONSULTA SQL CORREGIDA! Selecciona TotalBonosAplicados y TotalDescuentosAplicados
            Statement stmt = conn.connect().createStatement();
            rs = stmt.executeQuery("SELECT Id, EmpleadoId, FechaPago, HorasTrabajadas, ValorHora, TotalPago, TotalBonosAplicados, TotalDescuentosAplicados FROM PagoEmpleado");

            while (rs.next()) {
                PagoEmpleado pago = new PagoEmpleado();
                pago.setId(rs.getInt("Id"));
                pago.setEmpleadoId(rs.getInt("EmpleadoId"));
                pago.setFechaPago(rs.getObject("FechaPago", LocalDateTime.class));
                pago.setHorasTrabajadas(rs.getInt("HorasTrabajadas"));
                pago.setValorHora(rs.getDouble("ValorHora"));
                pago.setTotalPago(rs.getDouble("TotalPago"));
                // ¡MAPEO CORREGIDO! Obtiene los nuevos campos de totales
                pago.setTotalBonosAplicados(rs.getBigDecimal("TotalBonosAplicados"));
                pago.setTotalDescuentosAplicados(rs.getBigDecimal("TotalDescuentosAplicados"));
                pagos.add(pago);
            }
            stmt.close();
        } catch (SQLException ex) {
            throw new SQLException("Error al obtener todos los pagos de empleado: " + ex.getMessage(), ex);
        } finally {
            closeResources();
        }
        return pagos;
    }



    /**
     * Calcula la suma total de los valores de bonos activos asignados a un empleado.
     * @param empleadoId El ID del empleado.
     * @return La suma total de bonos.
     * @throws SQLException Si ocurre un error de base de datos.
     */
    public double calcularBonosParaEmpleado(int empleadoId) throws SQLException {
        double totalBonos = 0.0;
        try {
            ps = conn.connect().prepareStatement(
                    "SELECT SUM(B.Valor) AS TotalBonos " +
                            "FROM AsignacionBono AB " +
                            "INNER JOIN Bono B ON AB.BonosId = B.Id " +
                            "WHERE AB.EmpleadosId = ? AND B.Estado = 1"
            );
            ps.setInt(1, empleadoId);
            rs = ps.executeQuery();
            if (rs.next()) {
                totalBonos = rs.getDouble("TotalBonos");
            }
        } catch (SQLException ex) {
            throw new SQLException("Error al calcular bonos para el empleado " + empleadoId + ": " + ex.getMessage(), ex);
        } finally {
            closeResources();
        }
        return totalBonos;
    }

    /**
     * Calcula la suma total de los valores de descuentos activos asignados a un empleado.
     * @param empleadoId El ID del empleado.
     * @return La suma total de descuentos.
     * @throws SQLException Si ocurre un error de base de datos.
     */
    public double calcularDescuentosParaEmpleado(int empleadoId) throws SQLException {
        double totalDescuentos = 0.0;
        try {
            ps = conn.connect().prepareStatement(
                    "SELECT SUM(D.Valor) AS TotalDescuentos " +
                            "FROM AsignacionDescuento AD " +
                            "INNER JOIN Descuento D ON AD.DescuentosId = D.Id " +
                            "WHERE AD.EmpleadosId = ? AND D.Estado = 1"
            );
            ps.setInt(1, empleadoId);
            rs = ps.executeQuery();
            if (rs.next()) {
                totalDescuentos = rs.getDouble("TotalDescuentos");
            }
        } catch (SQLException ex) {
            throw new SQLException("Error al calcular descuentos para el empleado " + empleadoId + ": " + ex.getMessage(), ex);
        } finally {
            closeResources();
        }
        return totalDescuentos;
    }

    /**
     * Busca pagos de empleado en un rango de fechas.
     * @param fechaInicio Fecha de inicio del rango (inclusive).
     * @param fechaFin Fecha de fin del rango (inclusive).
     * @return Lista de objetos PagoEmpleado.
     * @throws SQLException Si ocurre un error de base de datos.
     */
    public List<PagoEmpleado> searchPagosByFechaRango(LocalDate fechaInicio, LocalDate fechaFin) throws SQLException {
        List<PagoEmpleado> pagos = new ArrayList<>();
        try {
            // Consulta SQL para buscar por rango de fechas
            String sql = "SELECT Id, EmpleadoId, FechaPago, HorasTrabajadas, ValorHora, TotalPago, TotalBonosAplicados, TotalDescuentosAplicados " +
                    "FROM PagoEmpleado " +
                    "WHERE FechaPago BETWEEN ? AND ?";

            ps = conn.connect().prepareStatement(sql);

            // Convertir LocalDate a Timestamp para la base de datos
            ps.setTimestamp(1, Timestamp.valueOf(fechaInicio.atStartOfDay()));
            ps.setTimestamp(2, Timestamp.valueOf(fechaFin.atTime(23, 59, 59, 999_999_999)));

            rs = ps.executeQuery();
            while (rs.next()) {
                PagoEmpleado pago = new PagoEmpleado();
                pago.setId(rs.getInt("Id"));
                pago.setEmpleadoId(rs.getInt("EmpleadoId"));
                pago.setFechaPago(rs.getTimestamp("FechaPago").toLocalDateTime());
                pago.setHorasTrabajadas(rs.getInt("HorasTrabajadas"));
                pago.setValorHora(rs.getDouble("ValorHora"));
                pago.setTotalPago(rs.getDouble("TotalPago"));
                pago.setTotalBonosAplicados(rs.getBigDecimal("TotalBonosAplicados"));
                pago.setTotalDescuentosAplicados(rs.getBigDecimal("TotalDescuentosAplicados"));
                pagos.add(pago);
            }
        } catch (SQLException ex) {
            throw new SQLException("Error al buscar pagos por rango de fechas: " + ex.getMessage(), ex);
        } finally {
            closeResources();
        }
        return pagos;
    }


    /**
     * Método auxiliar para cerrar de forma segura los recursos (ResultSet, PreparedStatement, Connection).
     */
    private void closeResources() {
        try {
            if (rs != null) {
                rs.close();
                rs = null;
            }
        } catch (SQLException e) {
            System.err.println("Error al cerrar ResultSet: " + e.getMessage());
        }
        try {
            if (ps != null) {
                ps.close();
                ps = null;
            }
        } catch (SQLException e) {
            System.err.println("Error al cerrar PreparedStatement: " + e.getMessage());
        }
        try {
            if (conn != null) {
                conn.disconnect();
            }
        } catch (SQLException e) {
            System.err.println("Error al desconectar la conexión en ConnectionManager: " + e.getMessage());
        }
    }
}
