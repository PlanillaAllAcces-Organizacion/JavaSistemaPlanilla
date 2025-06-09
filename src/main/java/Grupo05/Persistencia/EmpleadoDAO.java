package Grupo05.Persistencia;

import Grupo05.dominio.Empleado;

import java.sql.*;

import java.time.LocalDateTime;

public class EmpleadoDAO {
    private ConnectionManager cnn;
    private PreparedStatement ps;
    private ResultSet rs;

    public EmpleadoDAO(){
        cnn = ConnectionManager.getInstance();
    }

    public Empleado create(Empleado empleado) throws SQLException {
        Empleado empleres = null;
        Connection conn = null; // Declarado aquí para asegurar que se cierre en finally si la asignación falla
        PreparedStatement ps = null; // Declarado aquí para asegurar que se cierre en finally si la asignación falla

        try {
            conn = cnn.connect(); // Solo se conecta una vez
            ps = conn.prepareStatement(
                    "INSERT INTO Empleado (TipoDeHorarioId, PuestoTrabajoId, DUI, Nombre, Apellido, Telefono, Correo, Estado, SalarioBase, FechaContraInicial, Usuario, Password) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            );

            LocalDateTime fechaLocalDateTime = empleado.getFechacontra();
            Timestamp timestampSql = fechaLocalDateTime != null ? Timestamp.valueOf(fechaLocalDateTime) : null;

            ps.setInt(1, empleado.getTipoDeHorarioId());
            ps.setInt(2, empleado.getPuestoTrabajoId());
            ps.setString(3, empleado.getDui());
            ps.setString(4, empleado.getNombre());
            ps.setString(5, empleado.getApellido());
            ps.setInt(6, empleado.getTelefono());
            ps.setString(7, empleado.getCorreo());
            ps.setByte(8, empleado.getEstado());
            ps.setDouble(9, empleado.getSalario());
            ps.setTimestamp(10, timestampSql);
            ps.setString(11, empleado.getUsuario());
            ps.setString(12, empleado.getPasswordHash());

            int affectedRows = ps.executeUpdate();
            System.out.println("DAO - create: Filas afectadas por la inserción: " + affectedRows); // <-- AÑADIDO PARA DEPURACIÓN

            if (affectedRows > 0) { // Modificado para ser más estricto con éxito
                ResultSet generatedKeys = ps.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int idGenerado = generatedKeys.getInt(1);
                    System.out.println("DAO - create: ID generado por la BD: " + idGenerado); // <-- AÑADIDO PARA DEPURACIÓN
                    empleres = getById(idGenerado); // Ahora sí funcionará bien
                    System.out.println("DAO - create: Empleado recuperado por getById con ID: " + (empleres != null ? empleres.getId() : "null")); // <-- AÑADIDO PARA DEPURACIÓN
                } else {
                    System.out.println("DAO - create: No se obtuvieron claves generadas."); // <-- AÑADIDO PARA DEPURACIÓN
                    throw new SQLException("Creación fallida, no se obtuvo ID.");
                }
                generatedKeys.close();
            } else {
                System.out.println("DAO - create: La inserción no afectó ninguna fila."); // <-- AÑADIDO PARA DEPURACIÓN
                empleres = null; // Si no se afectaron filas, consideramos que la operación no fue exitosa
            }

        } catch (SQLException ex) {
            System.err.println("DAO - create: ERROR: " + ex.getMessage()); // <-- AÑADIDO PARA DEPURACIÓN
            ex.printStackTrace(); // <-- AÑADIDO PARA DEPURACIÓN
            throw new SQLException("Error al crear el usuario: " + ex.getMessage(), ex);
        } finally {
            // Se asume que este 'ps' es local a este método y no el de la clase
            if (ps != null) try { ps.close(); } catch (SQLException ignore) {
                System.err.println("DAO - create: Error al cerrar PreparedStatement: " + ignore.getMessage()); // <-- AÑADIDO PARA DEPURACIÓN
            }
            if (cnn != null) cnn.disconnect(); // Asumiendo que ConnectionManager.disconnect puede tomar la conexión o la maneja internamente
            // Nota: Si ConnectionManager es un Singleton y maneja la única conexión,
            // asegúrate de que 'disconnect()' no cierre la conexión si aún se necesita en otros lugares.
            // Para pruebas, asumiremos que cerrar la conexión aquí es seguro.
        }

        return empleres;
    }


    public Empleado getById(int id) throws SQLException {
        Empleado empleres = null;
        Connection conn = null; // Declarado aquí para asegurar que se cierre en finally
        PreparedStatement ps = null; // Declarado aquí para asegurar que se cierre en finally
        ResultSet rs = null; // Declarado aquí para asegurar que se cierre en finally

        try {
            conn = cnn.connect(); // Obtener la conexión aquí también
            System.out.println("DAO - getById: Conectado para buscar ID: " + id); // <-- AÑADIDO PARA DEPURACIÓN
            // Consulta corregida con espacio entre SELECT y FROM
            ps = conn.prepareStatement( // Usar 'conn' local
                    "SELECT Id, TipoDeHorarioId, PuestoTrabajoId, DUI, Nombre, Apellido, Telefono, Correo, Estado, SalarioBase, FechaContraInicial, Usuario, Password " +
                            "FROM Empleado " +
                            "WHERE Id = ?"
            );

            ps.setInt(1, id);
            rs = ps.executeQuery();

            if (rs.next()) {
                empleres = new Empleado();

                empleres.setId(rs.getInt("Id"));
                System.out.println("DAO - getById: Encontrado empleado con ID: " + empleres.getId()); // <-- AÑADIDO PARA DEPURACIÓN
                empleres.setTipoDeHorarioId(rs.getInt("TipoDeHorarioId"));
                empleres.setPuestoTrabajoId(rs.getInt("PuestoTrabajoId"));
                empleres.setDui(rs.getString("DUI"));
                empleres.setNombre(rs.getString("Nombre"));
                empleres.setApellido(rs.getString("Apellido"));
                empleres.setTelefono(rs.getInt("Telefono"));
                empleres.setCorreo(rs.getString("Correo"));
                empleres.setEstado(rs.getByte("Estado"));
                empleres.setSalario(rs.getDouble("SalarioBase"));

                Timestamp fechaTimestamp = rs.getTimestamp("FechaContraInicial");
                if (fechaTimestamp != null) {
                    empleres.setFechacontra(fechaTimestamp.toLocalDateTime());
                }

                empleres.setUsuario(rs.getString("Usuario"));
                empleres.setPasswordHash(rs.getString("Password"));
            } else {
                System.out.println("DAO - getById: No se encontró empleado con ID: " + id); // <-- AÑADIDO PARA DEPURACIÓN
            }

        } catch (SQLException ex) {
            System.err.println("DAO - getById: ERROR: " + ex.getMessage()); // <-- AÑADIDO PARA DEPURACIÓN
            ex.printStackTrace(); // <-- AÑADIDO PARA DEPURACIÓN
            throw new SQLException("Error al obtener un usuario por id: " + ex.getMessage(), ex);
        } finally {
            if (rs != null) try { rs.close(); } catch (SQLException ignore) {
                System.err.println("DAO - getById: Error al cerrar ResultSet: " + ignore.getMessage()); // <-- AÑADIDO PARA DEPURACIÓN
            }
            if (ps != null) try { ps.close(); } catch (SQLException ignore) {
                System.err.println("DAO - getById: Error al cerrar PreparedStatement: " + ignore.getMessage()); // <-- AÑADIDO PARA DEPURACIÓN
            }
            if (cnn != null) cnn.disconnect(); // Asumiendo que ConnectionManager.disconnect puede tomar la conexión
        }

        return empleres;
    }

}