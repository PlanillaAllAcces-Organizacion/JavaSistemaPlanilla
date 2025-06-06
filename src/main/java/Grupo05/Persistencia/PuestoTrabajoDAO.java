package Grupo05.Persistencia;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import Grupo05.dominio.PuestoTrabajo;

public class PuestoTrabajoDAO {
    private ConnectionManager conn;
    private PreparedStatement ps;
    private ResultSet rs;

    public PuestoTrabajoDAO() {
        conn = ConnectionManager.getInstance();
    }

    /**
     * Crea un nuevo puesto de trabajo en la base de datos.
     * @param puesto El objeto PuestoTrabajo con los datos a insertar
     * @return El objeto PuestoTrabajo con el ID generado
     * @throws SQLException Si ocurre un error de base de datos
     */
    public PuestoTrabajo create(PuestoTrabajo puesto) throws SQLException {
        PuestoTrabajo res = null;
        try {
            ps = conn.connect().prepareStatement(
                    "INSERT INTO PuestoTrabajo (NombrePuesto, SalarioBase, ValorxHora, ValorExtra, Estado) " +
                            "VALUES (?, ?, ?, ?, ?)",
                    java.sql.Statement.RETURN_GENERATED_KEYS);

            ps.setString(1, puesto.getNombrePuesto());
            ps.setBigDecimal(2, puesto.getSalarioBase());
            ps.setBigDecimal(3, puesto.getValorxHora());
            ps.setBigDecimal(4, puesto.getValorExtra());
            ps.setByte(5, puesto.getEstado());

            int affectedRows = ps.executeUpdate();

            if (affectedRows != 0) {
                ResultSet generatedKeys = ps.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int idGenerado = generatedKeys.getInt(1);
                    res = getById(idGenerado);
                }
            }
        } finally {
            closeResources();
        }
        return res;
    }

    /**
     * Actualiza un puesto de trabajo existente.
     * @param puesto El objeto PuestoTrabajo con los datos actualizados
     * @return true si la actualización fue exitosa
     * @throws SQLException Si ocurre un error de base de datos
     */
    public boolean update(PuestoTrabajo puesto) throws SQLException {
        boolean res = false;
        try {
            ps = conn.connect().prepareStatement(
                    "UPDATE PuestoTrabajo SET NombrePuesto=?, SalarioBase=?, " +
                            "ValorxHora=?, ValorExtra=?, Estado=? WHERE Id=?");

            ps.setString(1, puesto.getNombrePuesto());
            ps.setBigDecimal(2, puesto.getSalarioBase());
            ps.setBigDecimal(3, puesto.getValorxHora());
            ps.setBigDecimal(4, puesto.getValorExtra());
            ps.setByte(5, puesto.getEstado());
            ps.setInt(6, puesto.getId());

            res = ps.executeUpdate() > 0;
        } finally {
            closeResources();
        }
        return res;
    }

    /**
     * Elimina un puesto de trabajo por su ID.
     * @param id El ID del puesto a eliminar
     * @return true si la eliminación fue exitosa
     * @throws SQLException Si ocurre un error de base de datos
     */
    public boolean delete(int id) throws SQLException {
        boolean res = false;
        try {
            ps = conn.connect().prepareStatement(
                    "DELETE FROM PuestoTrabajo WHERE Id=?");

            ps.setInt(1, id);
            res = ps.executeUpdate() > 0;
        } finally {
            closeResources();
        }
        return res;
    }

    /**
     * Busca puestos de trabajo por nombre (búsqueda parcial).
     * @param nombre El nombre o parte del nombre a buscar
     * @return Lista de puestos que coinciden con la búsqueda
     * @throws SQLException Si ocurre un error de base de datos
     */
    public ArrayList<PuestoTrabajo> search(String nombre) throws SQLException {
        ArrayList<PuestoTrabajo> resultados = new ArrayList<>();
        try {
            ps = conn.connect().prepareStatement(
                    "SELECT Id, NombrePuesto, SalarioBase, ValorxHora, ValorExtra, Estado " +
                            "FROM PuestoTrabajo WHERE NombrePuesto LIKE ?");

            ps.setString(1, "%" + nombre + "%");
            rs = ps.executeQuery();

            while (rs.next()) {
                resultados.add(mapResultSetToPuestoTrabajo(rs));
            }
        } finally {
            closeResources();
        }
        return resultados;
    }

    /**
     * Obtiene un puesto de trabajo por su ID.
     * @param id El ID del puesto a buscar
     * @return El puesto encontrado o null si no existe
     * @throws SQLException Si ocurre un error de base de datos
     */
    public PuestoTrabajo getById(int id) throws SQLException {
        PuestoTrabajo puesto = null;
        try {
            ps = conn.connect().prepareStatement(
                    "SELECT Id, NombrePuesto, SalarioBase, ValorxHora, ValorExtra, Estado " +
                            "FROM PuestoTrabajo WHERE Id=?");

            ps.setInt(1, id);
            rs = ps.executeQuery();

            if (rs.next()) {
                puesto = mapResultSetToPuestoTrabajo(rs);
            }
        } finally {
            closeResources();
        }
        return puesto;
    }

    /**
     * Obtiene todos los puestos de trabajo activos.
     * @return Lista de puestos activos
     * @throws SQLException Si ocurre un error de base de datos
     */
    public ArrayList<PuestoTrabajo> getAllActive() throws SQLException {
        ArrayList<PuestoTrabajo> resultados = new ArrayList<>();
        try {
            ps = conn.connect().prepareStatement(
                    "SELECT Id, NombrePuesto, SalarioBase, ValorxHora, ValorExtra, Estado " +
                            "FROM PuestoTrabajo WHERE Estado = 1");

            rs = ps.executeQuery();

            while (rs.next()) {
                resultados.add(mapResultSetToPuestoTrabajo(rs));
            }
        } finally {
            closeResources();
        }
        return resultados;
    }

    /**
     * Mapea un ResultSet a un objeto PuestoTrabajo.
     * @param rs El ResultSet con los datos
     * @return Un objeto PuestoTrabajo
     * @throws SQLException Si ocurre un error al acceder a los datos
     */
    private PuestoTrabajo mapResultSetToPuestoTrabajo(ResultSet rs) throws SQLException {
        PuestoTrabajo puesto = new PuestoTrabajo();
        puesto.setId(rs.getInt("Id"));
        puesto.setNombrePuesto(rs.getString("NombrePuesto"));
        puesto.setSalarioBase(rs.getBigDecimal("SalarioBase"));
        puesto.setValorxHora(rs.getBigDecimal("ValorxHora"));
        puesto.setValorExtra(rs.getBigDecimal("ValorExtra"));
        puesto.setEstado(rs.getByte("Estado"));
        return puesto;
    }

    /**
     * Cierra los recursos abiertos.
     */
    private void closeResources() {
        try {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
            conn.disconnect();
        } catch (SQLException e) {
            // Loggear el error si es necesario
        }
        rs = null;
        ps = null;
    }
}