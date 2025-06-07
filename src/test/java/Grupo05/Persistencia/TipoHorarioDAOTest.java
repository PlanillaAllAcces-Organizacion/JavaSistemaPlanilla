package Grupo05.Persistencia;

import Grupo05.dominio.Horario;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class TipoHorarioDAOTest {
    private ConnectionManager conn;
    private PreparedStatement ps;
    private ResultSet rs;

    public TipoHorarioDAOTest() {
        conn = ConnectionManager.getInstance();
    }

    /**
     * Crea un nuevo tipo de horario en la base de datos
     * @param horario Objeto Horario con los datos a insertar
     * @return Horario creado con su ID generado
     * @throws SQLException Si ocurre un error de base de datos
     */
    public Horario create(Horario horario) throws SQLException {
        Horario res = null;
        try {
            ps = conn.connect().prepareStatement(
                    "INSERT INTO TipoDeHorario (NombreHorario) VALUES (?)",
                    PreparedStatement.RETURN_GENERATED_KEYS
            );

            ps.setString(1, horario.getNombreHorario());

            int affectedRows = ps.executeUpdate();

            if (affectedRows != 0) {
                rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    int idGenerado = rs.getInt(1);
                    res = getById(idGenerado);
                }
            }
        } catch (SQLException ex) {
            throw new SQLException("Error al crear el horario: " + ex.getMessage(), ex);
        } finally {
            closeResources();
        }
        return res;
    }

    /**
     * Actualiza un tipo de horario existente
     * @param horario Objeto Horario con los datos actualizados
     * @return true si la actualización fue exitosa
     * @throws SQLException Si ocurre un error de base de datos
     */
    public boolean update(Horario horario) throws SQLException {
        boolean res = false;
        try {
            ps = conn.connect().prepareStatement(
                    "UPDATE TipoDeHorario SET NombreHorario = ? WHERE Id = ?"
            );

            ps.setString(1, horario.getNombreHorario());
            ps.setInt(2, horario.getId());

            res = ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            throw new SQLException("Error al actualizar el horario: " + ex.getMessage(), ex);
        } finally {
            closeResources();
        }
        return res;
    }

    /**
     * Elimina un tipo de horario de la base de datos
     * @param id ID del horario a eliminar
     * @return true si la eliminación fue exitosa
     * @throws SQLException Si ocurre un error de base de datos
     */
    public boolean delete(int id) throws SQLException {
        boolean res = false;
        try {
            ps = conn.connect().prepareStatement(
                    "DELETE FROM TipoDeHorario WHERE Id = ?"
            );

            ps.setInt(1, id);

            res = ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            throw new SQLException("Error al eliminar el horario: " + ex.getMessage(), ex);
        } finally {
            closeResources();
        }
        return res;
    }

    /**
     * Obtiene un tipo de horario por su ID
     * @param id ID del horario a buscar
     * @return Objeto Horario encontrado o null si no existe
     * @throws SQLException Si ocurre un error de base de datos
     */
    public Horario getById(int id) throws SQLException {
        Horario horario = null;
        try {
            ps = conn.connect().prepareStatement(
                    "SELECT Id, NombreHorario FROM TipoDeHorario WHERE Id = ?"
            );

            ps.setInt(1, id);
            rs = ps.executeQuery();

            if (rs.next()) {
                horario = new Horario();
                horario.setId(rs.getInt("Id"));
                horario.setNombreHorario(rs.getString("NombreHorario"));
            }
        } catch (SQLException ex) {
            throw new SQLException("Error al obtener horario por ID: " + ex.getMessage(), ex);
        } finally {
            closeResources();
        }
        return horario;
    }

    /**
     * Busca tipos de horario por nombre (búsqueda parcial)
     * @param nombre Nombre o parte del nombre a buscar
     * @return Lista de horarios que coinciden con la búsqueda
     * @throws SQLException Si ocurre un error de base de datos
     */
    public ArrayList<Horario> search(String nombre) throws SQLException {
        ArrayList<Horario> horarios = new ArrayList<>();
        try {
            ps = conn.connect().prepareStatement(
                    "SELECT Id, NombreHorario FROM TipoDeHorario WHERE NombreHorario LIKE ?"
            );

            ps.setString(1, "%" + nombre + "%");
            rs = ps.executeQuery();

            while (rs.next()) {
                Horario horario = new Horario();
                horario.setId(rs.getInt("Id"));
                horario.setNombreHorario(rs.getString("NombreHorario"));
                horarios.add(horario);
            }
        } catch (SQLException ex) {
            throw new SQLException("Error al buscar horarios: " + ex.getMessage(), ex);
        } finally {
            closeResources();
        }
        return horarios;
    }

    /**
     * Obtiene todos los tipos de horario activos
     * @return Lista de todos los horarios
     * @throws SQLException Si ocurre un error de base de datos
     */
    public ArrayList<Horario> getAll() throws SQLException {
        ArrayList<Horario> horarios = new ArrayList<>();
        try {
            ps = conn.connect().prepareStatement(
                    "SELECT Id, NombreHorario FROM TipoDeHorario"
            );

            rs = ps.executeQuery();

            while (rs.next()) {
                Horario horario = new Horario();
                horario.setId(rs.getInt("Id"));
                horario.setNombreHorario(rs.getString("NombreHorario"));
                horarios.add(horario);
            }
        } catch (SQLException ex) {
            throw new SQLException("Error al obtener todos los horarios: " + ex.getMessage(), ex);
        } finally {
            closeResources();
        }
        return horarios;
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