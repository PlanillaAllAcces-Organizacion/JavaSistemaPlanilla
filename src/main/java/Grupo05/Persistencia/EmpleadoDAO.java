package Grupo05.Persistencia;

import Grupo05.dominio.Empleado;

import java.sql.*;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class EmpleadoDAO {
    private ConnectionManager conn;
    private PreparedStatement ps;
    private ResultSet rs;

    public EmpleadoDAO(){
        conn = ConnectionManager.getInstance();
    }

    public Empleado create(Empleado empleado) throws SQLException {
        Empleado res = null;
        try{
            ps = conn.connect().prepareStatement("INSERT INTO " +
                            "Empleado (TipoDeHorarioId, PuestoTrabajoId, DUI, Nombre, Apellido, Telefono, Correo, Estado, SalarioBase, FechaContraInicial)" +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                    java.sql.Statement.RETURN_GENERATED_KEYS
            );

            ps.setInt(1, empleado.getTipoDeHorarioId());
            ps.setInt(2, empleado.getPuestoTrabajoId());
            ps.setString(3, empleado.getDui());
            ps.setString(4, empleado.getNombre());
            ps.setString(5, empleado.getApellido());
            ps.setInt(6, empleado.getTelefono());
            ps.setString(7, empleado.getCorreo());
            ps.setByte(8, empleado.getEstado());
            ps.setDouble(9, empleado.getSalario());
            ps.setObject(10, empleado.getFechacontra());

            int affectedRows = ps.executeUpdate();

            if (affectedRows != 0) {
                // El ResultSet 'generatedKeys' también debe ser cerrado, pero getById ya maneja su propia conexión.
                // Si el ID generado no se usara con getById, deberíamos cerrar 'generatedKeys' aquí.
                ResultSet  generatedKeys = ps.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int idGenerado= generatedKeys.getInt(1);
                    res = getById(idGenerado);
                } else {
                    throw new SQLException("Creating empleado failed, no ID obtained.");
                }
                generatedKeys.close(); // Cerrar el ResultSet de las claves generadas
            }
            // ps.close(); // ¡ELIMINADO DE AQUÍ! Se moverá al finally.
        }catch (SQLException ex){
            throw new SQLException("Error al crear el empleado: " + ex.getMessage(), ex);
        } finally {
            // *** Cierre seguro de recursos ***
            try {
                if (ps != null) { // Solo cierra si ps no es null
                    ps.close();
                }
            } catch (SQLException e) {
                System.err.println("Error al cerrar PreparedStatement en create: " + e.getMessage());
            }
            conn.disconnect(); // Desconecta la conexión al final
        }
        return res;
    }


    public boolean update(Empleado empleado) throws SQLException{
        boolean res = false;
        try{
            ps = conn.connect().prepareStatement(
                    "UPDATE Empleado " +
                            "SET TipoDeHorarioId = ?, PuestoTrabajoId = ?, DUI = ?, Nombre = ?, Apellido = ?, Telefono = ?, Correo = ?, Estado = ?, SalarioBase = ?, FechaContraInicial = ? " +
                            "WHERE Id = ?"
            );

            ps.setInt(1, empleado.getTipoDeHorarioId());
            ps.setInt(2, empleado.getPuestoTrabajoId());
            ps.setString(3, empleado.getDui());
            ps.setString(4, empleado.getNombre());
            ps.setString(5, empleado.getApellido());
            ps.setInt(6, empleado.getTelefono());
            ps.setString(7, empleado.getCorreo());
            ps.setByte(8, empleado.getEstado());
            ps.setDouble(9, empleado.getSalario());
            ps.setObject(10, empleado.getFechacontra());
            ps.setInt(11, empleado.getId());

            if(ps.executeUpdate() > 0){
                res = true;
            }
            // ps.close(); // ¡ELIMINADO DE AQUÍ!
        }catch (SQLException ex){
            throw new SQLException("Error al modificar el empleado: " + ex.getMessage(), ex);
        } finally {
            // *** Cierre seguro de recursos ***
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException e) {
                System.err.println("Error al cerrar PreparedStatement en update: " + e.getMessage());
            }
            conn.disconnect();
        }
        return res;
    }


    public boolean delete(Empleado empleado) throws SQLException{
        boolean res = false;
        try{
            ps = conn.connect().prepareStatement(
                    "DELETE FROM Empleado WHERE Id = ?"
            );
            ps.setInt(1, empleado.getId());

            if(ps.executeUpdate() > 0){
                res = true;
            }
            // ps.close(); // ¡ELIMINADO DE AQUÍ!
        }catch (SQLException ex){
            throw new SQLException("Error al eliminar el empleado: " + ex.getMessage(), ex);
        } finally {
            // *** Cierre seguro de recursos ***
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException e) {
                System.err.println("Error al cerrar PreparedStatement en delete: " + e.getMessage());
            }
            conn.disconnect();
        }
        return res;
    }

    /**
     * Busca empleados en la base de datos cuyo nombre o DUI contenga la cadena de búsqueda proporcionada.
     * ...
     */
    public ArrayList<Empleado> search(String query) throws SQLException{
        ArrayList<Empleado> records  = new ArrayList<>();

        try {
            ps = conn.connect().prepareStatement("SELECT Id, TipoDeHorarioId, PuestoTrabajoId, DUI, Nombre, Apellido, Telefono, Correo, Estado, SalarioBase, FechaContraInicial " +
                    "FROM Empleado " +
                    "WHERE Nombre LIKE ? OR DUI LIKE ?");

            ps.setString(1, "%" + query + "%");
            ps.setString(2, "%" + query + "%");

            rs = ps.executeQuery(); // rs se inicializa aquí

            while (rs.next()){
                Empleado empleado = new Empleado();
                empleado.setId(rs.getInt(1));
                empleado.setTipoDeHorarioId(rs.getInt(2));
                empleado.setPuestoTrabajoId(rs.getInt(3));
                empleado.setDui(rs.getString(4));
                empleado.setNombre(rs.getString(5));
                empleado.setApellido(rs.getString(6));
                empleado.setTelefono(rs.getInt(7));
                empleado.setCorreo(rs.getString(8));
                empleado.setEstado(rs.getByte(9));
                empleado.setSalario(rs.getDouble(10));
                empleado.setFechacontra(rs.getObject(11, LocalDateTime.class));
                records.add(empleado);
            }
            // ps.close(); // ¡ELIMINADO DE AQUÍ!
            // rs.close(); // ¡ELIMINADO DE AQUÍ!
        } catch (SQLException ex){
            throw new SQLException("Error al buscar empleados: " + ex.getMessage(), ex);
        } finally {
            // *** Cierre seguro de recursos ***
            try {
                if (rs != null) { // Solo cierra si rs no es null
                    rs.close();
                }
            } catch (SQLException e) {
                System.err.println("Error al cerrar ResultSet en search: " + e.getMessage());
            }
            try {
                if (ps != null) { // Solo cierra si ps no es null
                    ps.close();
                }
            } catch (SQLException e) {
                System.err.println("Error al cerrar PreparedStatement en search: " + e.getMessage());
            }
            conn.disconnect();
        }
        return records;
    }


    public Empleado getById(int id) throws SQLException{
        Empleado empleado  = null;

        try {
            ps = conn.connect().prepareStatement("SELECT Id, TipoDeHorarioId, PuestoTrabajoId, DUI, Nombre, Apellido, Telefono, Correo, Estado, SalarioBase, FechaContraInicial " +
                    "FROM Empleado " +
                    "WHERE Id = ?");

            ps.setInt(1, id);

            rs = ps.executeQuery();

            if (rs.next()) {
                empleado = new Empleado();
                empleado.setId(rs.getInt(1));
                empleado.setTipoDeHorarioId(rs.getInt(2));
                empleado.setPuestoTrabajoId(rs.getInt(3));
                empleado.setDui(rs.getString(4));
                empleado.setNombre(rs.getString(5));
                empleado.setApellido(rs.getString(6));
                empleado.setTelefono(rs.getInt(7));
                empleado.setCorreo(rs.getString(8));
                empleado.setEstado(rs.getByte(9));
                empleado.setSalario(rs.getDouble(10));
                empleado.setFechacontra(rs.getObject(11, LocalDateTime.class));
            }
            // ps.close(); // ¡ELIMINADO DE AQUÍ!
            // rs.close(); // ¡ELIMINADO DE AQUÍ!
        } catch (SQLException ex){
            throw new SQLException("Error al obtener un empleado por id: " + ex.getMessage(), ex);
        } finally {
            // *** Cierre seguro de recursos ***
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {
                System.err.println("Error al cerrar ResultSet en getById: " + e.getMessage());
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException e) {
                System.err.println("Error al cerrar PreparedStatement en getById: " + e.getMessage());
            }
            conn.disconnect();
        }
        return empleado;
    }

}
