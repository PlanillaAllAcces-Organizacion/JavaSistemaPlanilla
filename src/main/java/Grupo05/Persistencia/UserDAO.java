package Grupo05.Persistencia;

import java.sql.PreparedStatement; // Clase para ejecutar consultas SQL preparadas, previniendo inyecciones SQL.
import java.sql.ResultSet;        // Interfaz para representar el resultado de una consulta SQL.
import java.sql.SQLException;     // Clase para manejar errores relacionados con la base de datos SQL.
import java.time.LocalDateTime;
import java.util.ArrayList;       // Clase para crear listas dinámicas de objetos.

import Grupo05.dominio.User;        // Clase que representa la entidad de usuario en el dominio de la aplicación.
import Grupo05.Utils.PasswordHasher; // Clase utilitaria para el manejo seguro de contraseñas (hash, verificación).

public class UserDAO {
    private ConnectionManager conn; // Objeto para gestionar la conexión con la base de datos.
    private PreparedStatement ps;   // Objeto para ejecutar consultas SQL preparadas.
    private ResultSet rs;           // Objeto para almacenar el resultado de una consulta SQL.

    public UserDAO(){
        conn = ConnectionManager.getInstance();
    }


    public User create(User user) throws SQLException {
        User res = null; // Variable para almacenar el usuario creado que se retornará.
        try{
            // Preparar la sentencia SQL para la inserción de un nuevo usuario.
            // Se especifica que se retornen las claves generadas automáticamente.
            PreparedStatement ps = conn.connect().prepareStatement(
                    "INSERT INTO " +
                            "Usuarios (name, password, fechaCreacion, status)" +
                            "VALUES (?, ?, ?, ?)",
                    java.sql.Statement.RETURN_GENERATED_KEYS
            );
            // Establecer los valores de los parámetros en la sentencia preparada.
            ps.setString(1, user.getName()); // Asignar el nombre del usuario.
            ps.setString(2, PasswordHasher.hashPassword(user.getPasswordHash())); // Hashear la contraseña antes de guardarla.
            ps.setObject(3, user.getFechaCreado()); // Asignar el correo electrónico del usuario.
            ps.setByte(4, user.getStatus());   // Asignar el estado del usuario.

            // Ejecutar la sentencia de inserción y obtener el número de filas afectadas.
            int affectedRows = ps.executeUpdate();

            // Verificar si la inserción fue exitosa (al menos una fila afectada).
            if (affectedRows != 0) {
                // Obtener las claves generadas automáticamente por la base de datos (en este caso, el ID).
                ResultSet  generatedKeys = ps.getGeneratedKeys();
                // Mover el cursor al primer resultado (si existe).
                if (generatedKeys.next()) {
                    // Obtener el ID generado. Generalmente la primera columna contiene la clave primaria.
                    int idGenerado= generatedKeys.getInt(1);
                    // Recuperar el usuario completo utilizando el ID generado.
                    res = getById(idGenerado);
                } else {
                    // Lanzar una excepción si la creación del usuario falló y no se obtuvo un ID.
                    throw new SQLException("Creating user failed, no ID obtained.");
                }
            }
            ps.close(); // Cerrar la sentencia preparada para liberar recursos.
        }catch (SQLException ex){
            // Capturar cualquier excepción SQL que ocurra durante el proceso.
            throw new SQLException("Error al crear el usuario: " + ex.getMessage(), ex);
        } finally {
            // Bloque finally para asegurar que los recursos se liberen.
            ps = null;         // Establecer la sentencia preparada a null.
            conn.disconnect(); // Desconectar de la base de datos.
        }
        return res; // Retornar el usuario creado (con su ID asignado) o null si hubo un error.
    }


    public boolean update(User user) throws SQLException{
        boolean res = false; // Variable para indicar si la actualización fue exitosa.
        try{
            // Preparar la sentencia SQL para actualizar la información de un usuario.
            ps = conn.connect().prepareStatement(
                    "UPDATE Usuarios " +
                            "SET name = ?, fechaCreacion = ?, status = ? " +
                            "WHERE Id = ?"
            );

            // Establecer los valores de los parámetros en la sentencia preparada.
            ps.setString(1, user.getName());  // Asignar el nuevo nombre del usuario.
            ps.setObject(2, user.getFechaCreado()); // Asignar el nuevo correo electrónico del usuario.
            ps.setByte(3, user.getStatus());    // Asignar el nuevo estado del usuario.
            ps.setInt(4, user.getId());       // Establecer la condición WHERE para identificar el usuario a actualizar por su ID.

            // Ejecutar la sentencia de actualización y verificar si se afectó alguna fila.
            if(ps.executeUpdate() > 0){
                res = true; // Si executeUpdate() retorna un valor mayor que 0, significa que la actualización fue exitosa.
            }
            ps.close(); // Cerrar la sentencia preparada para liberar recursos.
        }catch (SQLException ex){
            // Capturar cualquier excepción SQL que ocurra durante el proceso.
            throw new SQLException("Error al modificar el usuario: " + ex.getMessage(), ex);
        } finally {
            // Bloque finally para asegurar que los recursos se liberen.
            ps = null;         // Establecer la sentencia preparada a null.
            conn.disconnect(); // Desconectar de la base de datos.
        }

        return res; // Retornar el resultado de la operación de actualización.
    }


    public boolean delete(User user) throws SQLException{
        boolean res = false; // Variable para indicar si la eliminación fue exitosa.
        try{
            // Preparar la sentencia SQL para eliminar un usuario por su ID.
            ps = conn.connect().prepareStatement(
                    "DELETE FROM Usuarios WHERE Id = ?"
            );
            // Establecer el valor del parámetro en la sentencia preparada (el ID del usuario a eliminar).
            ps.setInt(1, user.getId());

            // Ejecutar la sentencia de eliminación y verificar si se afectó alguna fila.
            if(ps.executeUpdate() > 0){
                res = true; // Si executeUpdate() retorna un valor mayor que 0, significa que la eliminación fue exitosa.
            }
            ps.close(); // Cerrar la sentencia preparada para liberar recursos.
        }catch (SQLException ex){
            // Capturar cualquier excepción SQL que ocurra durante el proceso.
            throw new SQLException("Error al eliminar el usuario: " + ex.getMessage(), ex);
        } finally {
            // Bloque finally para asegurar que los recursos se liberen.
            ps = null;         // Establecer la sentencia preparada a null.
            conn.disconnect(); // Desconectar de la base de datos.
        }

        return res; // Retornar el resultado de la operación de eliminación.
    }


    public ArrayList<User> search(String name) throws SQLException{
        ArrayList<User> records  = new ArrayList<>(); // Lista para almacenar los usuarios encontrados.

        try {
            // Preparar la sentencia SQL para buscar usuarios por nombre (usando LIKE para búsqueda parcial).
            ps = conn.connect().prepareStatement("SELECT Id, name, fechaCreacion, status " +
                    "FROM Usuarios " +
                    "WHERE name LIKE ?");

            // Establecer el valor del parámetro en la sentencia preparada.
            // El '%' al inicio y al final permiten la búsqueda de la cadena 'name' en cualquier parte del nombre del usuario.
            ps.setString(1, "%" + name + "%");

            // Ejecutar la consulta SQL y obtener el resultado.
            rs = ps.executeQuery();

            // Iterar a través de cada fila del resultado.
            while (rs.next()){
                // Crear un nuevo objeto User para cada registro encontrado.
                User user = new User();
                // Asignar los valores de las columnas a los atributos del objeto User.
                user.setId(rs.getInt(1));       // Obtener el ID del usuario.
                user.setName(rs.getString(2));   // Obtener el nombre del usuario.
                user.setFechaCreado(rs.getObject(3, LocalDateTime.class));  // Obtener el correo electrónico del usuario.
                user.setStatus(rs.getByte(4));    // Obtener el estado del usuario.
                // Agregar el objeto User a la lista de resultados.
                records.add(user);
            }
            ps.close(); // Cerrar la sentencia preparada para liberar recursos.
            rs.close(); // Cerrar el conjunto de resultados para liberar recursos.
        } catch (SQLException ex){
            // Capturar cualquier excepción SQL que ocurra durante el proceso.
            throw new SQLException("Error al buscar usuarios: " + ex.getMessage(), ex);
        } finally {
            // Bloque finally para asegurar que los recursos se liberen.
            ps = null;         // Establecer la sentencia preparada a null.
            rs = null;         // Establecer el conjunto de resultados a null.
            conn.disconnect(); // Desconectar de la base de datos.
        }
        return records; // Retornar la lista de usuarios encontrados.
    }


    public User getById(int id) throws SQLException{
        User user  = new User(); // Inicializar un objeto User que se retornará.

        try {
            // Preparar la sentencia SQL para seleccionar un usuario por su ID.
            ps = conn.connect().prepareStatement("SELECT Id, name, fechaCreacion, status " +
                    "FROM Usuarios " +
                    "WHERE Id = ?");

            // Establecer el valor del parámetro en la sentencia preparada (el ID a buscar).
            ps.setInt(1, id);

            // Ejecutar la consulta SQL y obtener el resultado.
            rs = ps.executeQuery();

            // Verificar si se encontró algún registro.
            if (rs.next()) {
                // Si se encontró un usuario, asignar los valores de las columnas al objeto User.
                user.setId(rs.getInt(1));       // Obtener el ID del usuario.
                user.setName(rs.getString(2));   // Obtener el nombre del usuario.
                user.setFechaCreado(rs.getObject(3, LocalDateTime.class));  // Obtener el correo electrónico del usuario.
                user.setStatus(rs.getByte(4));    // Obtener el estado del usuario.
            } else {
                // Si no se encontró ningún usuario con el ID especificado, establecer el objeto User a null.
                user = null;
            }
            ps.close(); // Cerrar la sentencia preparada para liberar recursos.
            rs.close(); // Cerrar el conjunto de resultados para liberar recursos.
        } catch (SQLException ex){
            // Capturar cualquier excepción SQL que ocurra durante el proceso.
            throw new SQLException("Error al obtener un usuario por id: " + ex.getMessage(), ex);
        } finally {
            // Bloque finally para asegurar que los recursos se liberen.
            ps = null;         // Establecer la sentencia preparada a null.
            rs = null;         // Establecer el conjunto de resultados a null.
            conn.disconnect(); // Desconectar de la base de datos.
        }
        return user; // Retornar el objeto User encontrado o null si no existe.
    }


    public User authenticate(User user) throws SQLException{

        User userAutenticate = new User(); // Inicializar un objeto User para almacenar el usuario autenticado.

        try {
            // Preparar la sentencia SQL para seleccionar un usuario por su correo electrónico,
            // contraseña hasheada y estado activo (status = 1).
            ps = conn.connect().prepareStatement("SELECT Id, name, fechaCreacion, status " +
                    "FROM Usuarios " +
                    "WHERE name = ? AND password = ? AND status = 1");

            // Establecer los valores de los parámetros en la sentencia preparada.
            ps.setString(1, user.getName()); // Asignar el correo electrónico del usuario a autenticar.
            ps.setString(2, PasswordHasher.hashPassword(user.getPasswordHash())); // Hashear la contraseña proporcionada para compararla con la almacenada.
            rs = ps.executeQuery(); // Ejecutar la consulta SQL y obtener el resultado.

            // Verificar si se encontró un registro que coincida con las credenciales y el estado.
            if (rs.next()) {
                // Si se encontró un usuario, asignar los valores de las columnas al objeto userAutenticate.
                userAutenticate.setId(rs.getInt(1));       // Obtener el ID del usuario autenticado.
                userAutenticate.setName(rs.getString(2));   // Obtener el nombre del usuario autenticado.
                userAutenticate.setFechaCreado(rs.getObject(3, LocalDateTime.class));  // Obtener el correo electrónico del usuario autenticado.
                userAutenticate.setStatus(rs.getByte(4));    // Obtener el estado del usuario autenticado.
            } else {
                // Si no se encontraron coincidencias, la autenticación falla y se establece userAutenticate a null.
                userAutenticate = null;
            }
            ps.close(); // Cerrar la sentencia preparada para liberar recursos.
            rs.close(); // Cerrar el conjunto de resultados para liberar recursos.
        } catch (SQLException ex){
            // Capturar cualquier excepción SQL que ocurra durante el proceso de autenticación.
            throw new SQLException("Error al autenticar un usuario por id: " + ex.getMessage(), ex);
        } finally {
            // Bloque finally para asegurar que los recursos se liberen.
            ps = null;         // Establecer la sentencia preparada a null.
            rs = null;         // Establecer el conjunto de resultados a null.
            conn.disconnect(); // Desconectar de la base de datos.
        }
        return userAutenticate; // Retornar el objeto User autenticado o null si la autenticación falló.
    }


    public boolean updatePassword(User user) throws SQLException{
        boolean res = false; // Variable para indicar si la actualización de la contraseña fue exitosa.
        try{
            // Preparar la sentencia SQL para actualizar solo la columna 'passwordHash' de un usuario.
            ps = conn.connect().prepareStatement(
                    "UPDATE Usuarios " +
                            "SET password = ? " +
                            "WHERE Id = ?"
            );
            // Hashear la nueva contraseña proporcionada antes de establecerla en la consulta.
            ps.setString(1, PasswordHasher.hashPassword(user.getPasswordHash()));
            // Establecer el ID del usuario cuya contraseña se va a actualizar en la cláusula WHERE.
            ps.setInt(2, user.getId());

            // Ejecutar la sentencia de actualización y verificar si se afectó alguna fila.
            if(ps.executeUpdate() > 0){
                res = true; // Si executeUpdate() retorna un valor mayor que 0, la actualización fue exitosa.
            }
            ps.close(); // Cerrar la sentencia preparada para liberar recursos.
        }catch (SQLException ex){
            // Capturar cualquier excepción SQL que ocurra durante el proceso.
            throw new SQLException("Error al modificar el password del usuario: " + ex.getMessage(), ex);
        } finally {
            // Bloque finally para asegurar que los recursos se liberen.
            ps = null;         // Establecer la sentencia preparada a null.
            conn.disconnect(); // Desconectar de la base de datos.
        }

        return res; // Retornar el resultado de la operación de actualización de la contraseña.
    }
}

