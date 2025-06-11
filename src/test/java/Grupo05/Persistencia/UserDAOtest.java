package Grupo05.Persistencia;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter; // Importar para formatear LocalDateTime
import java.util.ArrayList;
import java.util.Random;

import Grupo05.dominio.User;

import static org.junit.jupiter.api.Assertions.*;

public class UserDAOtest {

    private UserDAO userDAO;

    @BeforeEach
    void setUp() {
        userDAO = new UserDAO();
    }

    /**
     * Crea un nuevo usuario en la base de datos (simulada) y verifica que la operación sea exitosa.
     *
     * @param user El objeto User a crear.
     * @return El objeto User creado, con el ID asignado por la base de datos.
     * @throws SQLException Si ocurre un error de SQL.
     */
    private User create(User user) throws SQLException {
        User res = userDAO.create(user);
        assertNotNull(res, "El usuario creado no debería ser nulo.");
        assertEquals(user.getName(), res.getName(), "El nombre del usuario creado debe ser igual al original.");
        assertEquals(user.getFechaCreado(), res.getFechaCreado(), "La Fecha del usuario creado debe ser igual al original.");
        assertEquals(user.getStatus(), res.getStatus(), "El status del usuario creado debe ser igual al original.");

        return res;
    }

    /**
     * Actualiza un usuario existente en la base de datos (simulada) y verifica que la operación sea exitosa.
     *
     * @param user El objeto User a actualizar.
     * @throws SQLException Si ocurre un error de SQL.
     */
    private void update(User user) throws SQLException {
        // Modifica los atributos del objeto User para simular una actualización.
        user.setName(user.getName() + "_u"); // Añade "_u" al final del nombre.
        user.setStatus((byte) 1);             // Establece el status a 1.

        // Llama al método 'update' del UserDAO para actualizar el usuario en la base de datos (simulada).
        boolean res = userDAO.update(user);

        // Realiza una aserción para verificar que la actualización fue exitosa.
        assertTrue(res, "La actualización del usuario debería ser exitosa.");

        // Llama al método 'getById' para verificar que los cambios se persistieron correctamente.
        getById(user);
    }

    /**
     * Obtiene un usuario por su ID de la base de datos (simulada) y verifica que los datos sean correctos.
     *
     * @param user El objeto User con el ID a buscar.
     * @throws SQLException Si ocurre un error de SQL.
     */
    private void getById(User user) throws SQLException {
        // Llama al método 'getById' del UserDAO para obtener un usuario por su ID.
        User res = userDAO.getById(user.getId());

        // Realiza aserciones para verificar que el usuario obtenido coincide
        // con el usuario original (o el usuario modificado en pruebas de actualización).
        assertNotNull(res, "El usuario obtenido por ID no debería ser nulo.");
        assertEquals(user.getId(), res.getId(), "El ID del usuario obtenido debe ser igual al original.");
        assertEquals(user.getName(), res.getName(), "El nombre del usuario obtenido debe ser igual al esperado.");
        assertEquals(user.getFechaCreado(), res.getFechaCreado(), "La fecha del usuario obtenido debe ser igual al esperado.");
        assertEquals(user.getStatus(), res.getStatus(), "El status del usuario obtenido debe ser igual al esperado.");
    }

    /**
     * Busca usuarios por nombre en la base de datos (simulada) y verifica que se encuentren los resultados esperados.
     *
     * @param user El objeto User con el nombre a buscar.
     * @throws SQLException Si ocurre un error de SQL.
     */
    private void search(User user) throws SQLException {
        // Llama al método 'search' del UserDAO para buscar usuarios por nombre.
        ArrayList<User> users = userDAO.search(user.getName());
        boolean find = false;

        // Itera sobre la lista de usuarios devuelta por la búsqueda.
        // Se asume que la búsqueda devuelve todos los usuarios que *contienen* la cadena de búsqueda.
        // La lógica actual solo verifica que *al menos uno* contenga la cadena.
        // Para una verificación más robusta, se debería asegurar que *todos* los resultados contengan la cadena
        // y que el usuario original se encuentre entre ellos.
        if (!users.isEmpty()) {
            for (User userItem : users) {
                if (userItem.getName().contains(user.getName())) {
                    find = true;
                    break;
                }
            }
        }

        // Realiza una aserción para verificar que al menos un usuario con el nombre buscado fue encontrado.
        assertTrue(find, "El nombre buscado no fue encontrado: " + user.getName());
    }

    /**
     * Elimina un usuario de la base de datos (simulada) y verifica que la operación sea exitosa
     * y que el usuario ya no pueda ser encontrado.
     *
     * @param user El objeto User a eliminar.
     * @throws SQLException Si ocurre un error de SQL.
     */
    private void delete(User user) throws SQLException {
        // Llama al método 'delete' del UserDAO para eliminar un usuario por su ID.
        boolean res = userDAO.delete(user);

        // Realiza una aserción para verificar que la eliminación fue exitosa.
        assertTrue(res, "La eliminación del usuario debería ser exitosa.");

        // Intenta obtener el usuario por su ID después de la eliminación.
        User res2 = userDAO.getById(user.getId());

        // Realiza una aserción para verificar que el usuario ya no existe en la base de datos
        // después de la eliminación (el método 'getById' debería retornar null).
        assertNull(res2, "El usuario debería haber sido eliminado y no encontrado por ID.");
    }


    @Test
    void testUserDAO() throws SQLException {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        LocalDateTime localDateTime = LocalDateTime.now();
        String strFecha = localDateTime.format(formatter);

        User user = new User(0, "Test User", "password", strFecha, (byte) 2);

        // Llama al método 'create' para persistir el usuario de prueba en la base de datos (simulada) y verifica su creación.
        User testUser = create(user);

        // Llama al método 'update' para modificar los datos del usuario de prueba y verifica la actualización.
        update(testUser);

        // Llama al método 'search' para buscar usuarios por el nombre del usuario de prueba y verifica que se encuentre.
        search(testUser);

        // Llama al método 'delete' para eliminar el usuario de prueba.
        delete(testUser);
    }

    @Test
    void createUser() throws SQLException {

        //numero de admin random
        Random randomUsuaNum = new Random();
        int usuaNum = randomUsuaNum.nextInt(1000) + 1;
        String strUsua = "admin" + usuaNum;

        //formato correcto para la fecha de creacion
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime localDateTime = LocalDateTime.now();
        String strFecha = localDateTime.format(formatter);

        User user = new User(0, strUsua, "12345", strFecha, (byte) 1);
        User res = userDAO.create(user);
        assertNotEquals(res, null);
    }
}



