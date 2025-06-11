package Grupo05.Persistencia;

import Grupo05.dominio.Bonos;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BonoDAO {
    private ConnectionManager conn;
    private PreparedStatement ps;
    private ResultSet rs;

    public BonoDAO() {
        conn = ConnectionManager.getInstance();
    }

    /**
     * Crea un nuevo bono en la base de datos
     * @param bono Objeto Bono con los datos a insertar
     * @return Bono creado con su ID generado
     * @throws SQLException Si ocurre un error de base de datos
     */
    public Bonos create(Bonos bono) throws SQLException {
        Bonos res = null;
        try {
            ps = conn.connect().prepareStatement(
                    "INSERT INTO Bono (NombreBono, Valor, Estado, Operacion) VALUES (?, ?, ?, ?)",
                    PreparedStatement.RETURN_GENERATED_KEYS
            );

            ps.setString(1, bono.getNombreBono());
            ps.setDouble(2, bono.getValor());
            ps.setByte(3, bono.getEstado());
            ps.setByte(4, bono.getOperacion());

            int affectedRows = ps.executeUpdate();

            if (affectedRows != 0) {
                rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    int idGenerado = rs.getInt(1);
                    res = getById(idGenerado);
                }
            }
        } catch (SQLException ex) {
            throw new SQLException("Error al crear el bono: " + ex.getMessage(), ex);
        } finally {
            closeResources();
        }
        return res;
    }

    /**
     * Actualiza un bono existente
     * @param bono Objeto Bono con los datos actualizados
     * @return true si la actualización fue exitosa
     * @throws SQLException Si ocurre un error de base de datos
     */
    public boolean update(Bonos bono) throws SQLException {
        boolean res = false;
        try {
            ps = conn.connect().prepareStatement(
                    "UPDATE Bono SET NombreBono = ?, Valor = ?, Estado = ?, Operacion = ? WHERE Id = ?"
            );

            ps.setString(1, bono.getNombreBono());
            ps.setDouble(2, bono.getValor());
            ps.setByte(3, bono.getEstado());
            ps.setByte(4, bono.getOperacion());
            ps.setInt(5, bono.getId());

            res = ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            throw new SQLException("Error al actualizar el bono: " + ex.getMessage(), ex);
        } finally {
            closeResources();
        }
        return res;
    }

    /**
     * Elimina un bono de la base de datos
     * @param id ID del bono a eliminar
     * @return true si la eliminación fue exitosa
     * @throws SQLException Si ocurre un error de base de datos
     */
    public boolean delete(int id) throws SQLException {
        boolean res = false;
        try {
            ps = conn.connect().prepareStatement(
                    "DELETE FROM Bono WHERE Id = ?"
            );

            ps.setInt(1, id);

            res = ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            throw new SQLException("Error al eliminar el bono: " + ex.getMessage(), ex);
        } finally {
            closeResources();
        }
        return res;
    }

    /**
     * Obtiene un bono por su ID
     * @param id ID del bono a buscar
     * @return Objeto Bono encontrado o null si no existe
     * @throws SQLException Si ocurre un error de base de datos
     */
    public Bonos getById(int id) throws SQLException {
        Bonos bono = null;
        try {
            ps = conn.connect().prepareStatement(
                    "SELECT Id, NombreBono, Valor, Estado, Operacion FROM Bono WHERE Id = ?"
            );

            ps.setInt(1, id);
            rs = ps.executeQuery();

            if (rs.next()) {
                bono = new Bonos();
                bono.setId(rs.getInt("Id"));
                bono.setNombreBono(rs.getString("NombreBono"));
                bono.setValor(rs.getDouble("Valor"));
                bono.setEstado(rs.getByte("Estado"));
                bono.setOperacion(rs.getByte("Operacion"));
            }
        } catch (SQLException ex) {
            throw new SQLException("Error al obtener bono por ID: " + ex.getMessage(), ex);
        } finally {
            closeResources();
        }
        return bono;
    }

    /**
     * Busca bonos por nombre (búsqueda parcial)
     * @param nombre Nombre o parte del nombre a buscar
     * @return Lista de bonos que coinciden con la búsqueda
     * @throws SQLException Si ocurre un error de base de datos
     */
    public ArrayList<Bonos> search(String nombre) throws SQLException {
        ArrayList<Bonos> bonos = new ArrayList<>();
        try {
            ps = conn.connect().prepareStatement(
                    "SELECT Id, NombreBono, Valor, Estado, Operacion FROM Bono WHERE NombreBono LIKE ?"
            );

            ps.setString(1, "%" + nombre + "%");
            rs = ps.executeQuery();

            while (rs.next()) {
                Bonos bono = new Bonos();
                bono.setId(rs.getInt("Id"));
                bono.setNombreBono(rs.getString("NombreBono"));
                bono.setValor(rs.getDouble("Valor"));
                bono.setEstado(rs.getByte("Estado"));
                bono.setOperacion(rs.getByte("Operacion"));
                bonos.add(bono);
            }
        } catch (SQLException ex) {
            throw new SQLException("Error al buscar bonos: " + ex.getMessage(), ex);
        } finally {
            closeResources();
        }
        return bonos;
    }

    /**
     * Obtiene todos los bonos
     * @return Lista de todos los bonos
     * @throws SQLException Si ocurre un error de base de datos
     */
    public ArrayList<Bonos> getAll() throws SQLException {
        ArrayList<Bonos> bonos = new ArrayList<>();
        try {
            ps = conn.connect().prepareStatement(
                    "SELECT Id, NombreBono, Valor, Estado, Operacion FROM Bono"
            );

            rs = ps.executeQuery();

            while (rs.next()) {
                Bonos bono = new Bonos();
                bono.setId(rs.getInt("Id"));
                bono.setNombreBono(rs.getString("NombreBono"));
                bono.setValor(rs.getDouble("Valor"));
                bono.setEstado(rs.getByte("Estado"));
                bono.setOperacion(rs.getByte("Operacion"));
                bonos.add(bono);
            }
        } catch (SQLException ex) {
            throw new SQLException("Error al obtener todos los bonos: " + ex.getMessage(), ex);
        } finally {
            closeResources();
        }
        return bonos;
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