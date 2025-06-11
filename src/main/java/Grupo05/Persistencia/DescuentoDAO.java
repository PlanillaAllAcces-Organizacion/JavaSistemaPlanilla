package Grupo05.Persistencia;

import Grupo05.dominio.Descuentos;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class DescuentoDAO {
    private ConnectionManager conn;
    private PreparedStatement ps;
    private ResultSet rs;

    public DescuentoDAO() {
        conn = ConnectionManager.getInstance();
    }

    /**
     * Crea un nuevo descuento en la base de datos
     * @param descuento Objeto Descuento con los datos a insertar
     * @return Descuento creado con su ID generado
     * @throws SQLException Si ocurre un error de base de datos
     */
    public Descuentos create(Descuentos descuento) throws SQLException {
        Descuentos res = null;
        try {
            ps = conn.connect().prepareStatement(
                    "INSERT INTO Descuento (Nombre, Valor, Estado, Operacion) VALUES (?, ?, ?, ? )",
                    PreparedStatement.RETURN_GENERATED_KEYS
            );

            ps.setString(1, descuento.getNombre());
            ps.setDouble(2, descuento.getValor());
            ps.setByte(3, descuento.getEstado());
            ps.setByte(4, descuento.getOperacion());

            int affectedRows = ps.executeUpdate();

            if (affectedRows != 0) {
                rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    int idGenerado = rs.getInt(1);
                    res = getById(idGenerado);
                }
            }
        } catch (SQLException ex) {
            throw new SQLException("Error al crear el descuento: " + ex.getMessage(), ex);
        } finally {
            closeResources();
        }
        return res;
    }

    /**
     * Actualiza un descuento existente
     * @param descuento Objeto Descuento con los datos actualizados
     * @return true si la actualización fue exitosa
     * @throws SQLException Si ocurre un error de base de datos
     */
    public boolean update(Descuentos descuento) throws SQLException {
        boolean res = false;
        try {
            ps = conn.connect().prepareStatement(
                    "UPDATE Descuento SET Nombre = ?, Valor = ?, Estado = ?, Operacion = ? WHERE Id = ?"
            );

            ps.setString(1, descuento.getNombre());
            ps.setDouble(2, descuento.getValor());
            ps.setByte(3, descuento.getEstado());
            ps.setByte(4, descuento.getOperacion());
            ps.setInt(5, descuento.getId());

            res = ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            throw new SQLException("Error al actualizar el descuento: " + ex.getMessage(), ex);
        } finally {
            closeResources();
        }
        return res;
    }

    /**
     * Elimina un descuento de la base de datos
     * @param id ID del descuento a eliminar
     * @return true si la eliminación fue exitosa
     * @throws SQLException Si ocurre un error de base de datos
     */
    public boolean delete(int id) throws SQLException {
        boolean res = false;
        try {
            // Primero verificar si el descuento está asignado a algún empleado
            if (tieneAsignaciones(id)) {
                throw new SQLException("No se puede eliminar el descuento porque está asignado a uno o más empleados");
            }

            ps = conn.connect().prepareStatement(
                    "DELETE FROM Descuento WHERE Id = ?"
            );

            ps.setInt(1, id);

            res = ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            throw new SQLException("Error al eliminar el descuento: " + ex.getMessage(), ex);
        } finally {
            closeResources();
        }
        return res;
    }

    /**
     * Verifica si un descuento tiene asignaciones a empleados
     * @param descuentoId ID del descuento a verificar
     * @return true si tiene asignaciones, false en caso contrario
     * @throws SQLException Si ocurre un error de base de datos
     */
    private boolean tieneAsignaciones(int descuentoId) throws SQLException {
        boolean tieneAsignaciones = false;
        try {
            ps = conn.connect().prepareStatement(
                    "SELECT COUNT(*) FROM AsignacionDescuento WHERE DescuentosId = ?"
            );
            ps.setInt(1, descuentoId);
            rs = ps.executeQuery();

            if (rs.next()) {
                tieneAsignaciones = rs.getInt(1) > 0;
            }
        } finally {
            closeResources();
        }
        return tieneAsignaciones;
    }

    /**
     * Obtiene un descuento por su ID
     * @param id ID del descuento a buscar
     * @return Objeto Descuento encontrado o null si no existe
     * @throws SQLException Si ocurre un error de base de datos
     */
    public Descuentos getById(int id) throws SQLException {
        Descuentos descuento = null;
        try {
            ps = conn.connect().prepareStatement(
                    "SELECT Id, Nombre, Valor, Estado, Operacion FROM Descuento WHERE Id = ?"
            );

            ps.setInt(1, id);
            rs = ps.executeQuery();

            if (rs.next()) {
                descuento = new Descuentos();
                descuento.setId(rs.getInt("Id"));
                descuento.setNombre(rs.getString("Nombre"));
                descuento.setValor(rs.getDouble("Valor"));
                descuento.setEstado(rs.getByte("Estado"));
                descuento.setOperacion(rs.getByte("Operacion"));
            }
        } catch (SQLException ex) {
            throw new SQLException("Error al obtener descuento por ID: " + ex.getMessage(), ex);
        } finally {
            closeResources();
        }
        return descuento;
    }

    /**
     * Busca descuentos por nombre (búsqueda parcial)
     * @param nombre Nombre o parte del nombre a buscar
     * @return Lista de descuentos que coinciden con la búsqueda
     * @throws SQLException Si ocurre un error de base de datos
     */
    public ArrayList<Descuentos> search(String nombre) throws SQLException {
        ArrayList<Descuentos> descuentos = new ArrayList<>();
        try {
            ps = conn.connect().prepareStatement(
                    "SELECT Id, Nombre, Valor, Estado, Operacion FROM Descuento WHERE Nombre LIKE ?"
            );

            ps.setString(1, "%" + nombre + "%");
            rs = ps.executeQuery();

            while (rs.next()) {
                Descuentos descuento = new Descuentos();
                descuento.setId(rs.getInt("Id"));
                descuento.setNombre(rs.getString("Nombre"));
                descuento.setValor(rs.getDouble("Valor"));
                descuento.setEstado(rs.getByte("Estado"));
                descuento.setOperacion(rs.getByte("Operacion"));
                descuentos.add(descuento);
            }
        } catch (SQLException ex) {
            throw new SQLException("Error al buscar descuentos: " + ex.getMessage(), ex);
        } finally {
            closeResources();
        }
        return descuentos;
    }

    /**
     * Obtiene todos los descuentos
     * @return Lista de todos los descuentos
     * @throws SQLException Si ocurre un error de base de datos
     */
    public ArrayList<Descuentos> getAll() throws SQLException {
        ArrayList<Descuentos> descuentos = new ArrayList<>();
        try {
            ps = conn.connect().prepareStatement(
                    "SELECT Id, Nombre, Valor, Estado, Operacion FROM Descuento"
            );

            rs = ps.executeQuery();

            while (rs.next()) {
                Descuentos descuento = new Descuentos();
                descuento.setId(rs.getInt("Id"));
                descuento.setNombre(rs.getString("Nombre"));
                descuento.setValor(rs.getDouble("Valor"));
                descuento.setEstado(rs.getByte("Estado"));
                descuento.setOperacion(rs.getByte("Operacion"));
                descuentos.add(descuento);
            }
        } catch (SQLException ex) {
            throw new SQLException("Error al obtener todos los descuentos: " + ex.getMessage(), ex);
        } finally {
            closeResources();
        }
        return descuentos;
    }

    /**
     * Obtiene los descuentos activos
     * @return Lista de descuentos activos
     * @throws SQLException Si ocurre un error de base de datos
     */
    public ArrayList<Descuentos> getActive() throws SQLException {
        ArrayList<Descuentos> descuentos = new ArrayList<>();
        try {
            ps = conn.connect().prepareStatement(
                    "SELECT Id, Nombre, Valor, Estado, Operacion FROM Descuento WHERE Estado = 1"
            );

            rs = ps.executeQuery();

            while (rs.next()) {
                Descuentos descuento = new Descuentos();
                descuento.setId(rs.getInt("Id"));
                descuento.setNombre(rs.getString("Nombre"));
                descuento.setValor(rs.getDouble("Valor"));
                descuento.setEstado(rs.getByte("Estado"));
                descuento.setOperacion(rs.getByte("Operacion"));
                descuentos.add(descuento);
            }
        } catch (SQLException ex) {
            throw new SQLException("Error al obtener descuentos activos: " + ex.getMessage(), ex);
        } finally {
            closeResources();
        }
        return descuentos;
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