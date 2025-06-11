package Grupo05.Persistencia;

import Grupo05.dominio.Empleado;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class EmpleadoDAOTest {

    private EmpleadoDAO empleadoDAO;

    @BeforeEach
    void setUp() {
        empleadoDAO = new EmpleadoDAO();
    }

    private Empleado create(Empleado empleado) throws SQLException {
        Empleado Emple = empleadoDAO.create(empleado);
        assertNotNull(Emple, "El empleado creado no debería ser nulo.");
        assertEquals(empleado.getTipoDeHorarioId(), Emple.getTipoDeHorarioId(), "El horarioId del empleado creado debe ser igual al original.");
        assertEquals(empleado.getPuestoTrabajoId(), Emple.getPuestoTrabajoId(), "El puestoId del empleado creado debe ser igual al original.");
        assertEquals(empleado.getDui(), Emple.getDui(), "El dui del empleado creado debe ser igual al original.");
        assertEquals(empleado.getNombre(), Emple.getNombre(), "El nombre del empleado creado debe ser igual al original.");
        assertEquals(empleado.getApellido(), Emple.getApellido(), "El apellido del empleado creado debe ser igual al original.");
        assertEquals(empleado.getTelefono(), Emple.getTelefono(), "El telefono del empleado creado debe ser igual al original.");
        assertEquals(empleado.getCorreo(), Emple.getCorreo(), "El correo del empleado creado debe ser igual al original.");
        assertEquals(empleado.getEstado(), Emple.getEstado(), "El estado del empleado creado debe ser igual al original.");
        assertEquals(empleado.getSalario(), Emple.getSalario(), "El salario del empleado creado debe ser igual al original.");
        assertEquals(empleado.getFechacontra(), Emple.getFechacontra(), "El fecha del empleado creado debe ser igual al original.");
        return Emple;
    }

    private void update(Empleado empleado) throws SQLException {
        // Modifica los atributos del objeto User para simular una actualización.
        empleado.setTipoDeHorarioId(2); // Establece el tipoHorarioId a 2.
        empleado.setPuestoTrabajoId(1); // Establece el puestoTrabajoId a 1.
        empleado.setDui(empleado.getDui() + "-3"); // Añade "-3" al final del dui.
        empleado.setNombre(empleado.getNombre() + "_u"); // Añade "_u" al final del nombre.
        empleado.setApellido(empleado.getApellido() + "_P"); // Añade "_P" al final del apellido.
        empleado.setTelefono(empleado.getTelefono() + 4); // Añade "4" al final del telefono.
        empleado.setCorreo(empleado.getCorreo() + "_u"); // Añade "_u" al final del correo.
        empleado.setEstado((byte) 2); // Establece el status a 2.
        empleado.setSalario(empleado.getSalario() + 32); // Añade 32 al final del salario.

        // Llama al método 'update' del UserDAO para actualizar el usuario en la base de datos (simulada).
        boolean emple = empleadoDAO.update(empleado);

        // Realiza una aserción para verificar que la actualización fue exitosa.
        assertTrue(emple, "La actualización del empleado debería ser exitosa.");

        // Llama al método 'getById' para verificar que los cambios se persistieron correctamente.
        getById(empleado);
    }

    private void getById(Empleado empleado) throws SQLException {
        // Llama al método 'getById' del EmpleadoDAO para obtener un usuario por su ID.
        Empleado emple = empleadoDAO.getById(empleado.getId());

        // Realiza aserciones para verificar que el usuario obtenido coincide
        // con el usuario original (o el usuario modificado en pruebas de actualización).
        assertNotNull(emple, "El empleado creado no debería ser nulo.");
        assertEquals(empleado.getTipoDeHorarioId(), emple.getTipoDeHorarioId(), "");
        assertEquals(empleado.getPuestoTrabajoId(), emple.getPuestoTrabajoId(), "");
        assertEquals(empleado.getDui(), emple.getDui(), "");
        assertEquals(empleado.getNombre(), emple.getNombre(), "");
        assertEquals(empleado.getApellido(), emple.getApellido(), "");
        assertEquals(empleado.getTelefono(), emple.getTelefono(), "El status del usuario creado debe ser igual al original.");
        assertEquals(empleado.getCorreo(), emple.getCorreo(), "");
        assertEquals(empleado.getEstado(), emple.getEstado(), "El status del usuario creado debe ser igual al original.");
        assertEquals(empleado.getSalario(), emple.getSalario(), "");
        assertEquals(empleado.getFechacontra(), emple.getFechacontra(), "");
    }

    private void search(Empleado empleado) throws SQLException {
        // Llama al método 'search' del EmpleadoDAO para buscar empleados por nombre.
        ArrayList<Empleado> empleados = empleadoDAO.search(empleado.getNombre());
        boolean find = false;

        // Itera sobre la lista de usuarios devuelta por la búsqueda.
        // Se asume que la búsqueda devuelve todos los empleados que *contienen* la cadena de búsqueda.
        // La lógica actual solo verifica que *al menos uno* contenga la cadena.
        // Para una verificación más robusta, se debería asegurar que *todos* los resultados contengan la cadena
        // y que el empleado original se encuentre entre ellos.
        if (!empleados.isEmpty()) {
            for (Empleado empleadoItem : empleados) {
                if (empleadoItem.getNombre().contains(empleado.getNombre())) {
                    find = true;
                    break;
                }
            }
        }

        // Realiza una aserción para verificar que al menos un empleado con el nombre buscado fue encontrado.
        assertTrue(find, "El nombre buscado no fue encontrado: " + empleado.getNombre());
    }

    private void delete(Empleado empleado) throws SQLException {
        // Llama al método 'delete' del EmpleadoDAO para eliminar un empleado por su ID.
        boolean emple = empleadoDAO.delete(empleado);

        // Realiza una aserción para verificar que la eliminación fue exitosa.
        assertTrue(emple, "La eliminación del empleado debería ser exitosa.");

        // Intenta obtener el empleado por su ID después de la eliminación.
        Empleado emple2 = empleadoDAO.getById(empleado.getId());

        // Realiza una aserción para verificar que el empleado ya no existe en la base de datos
        // después de la eliminación (el método 'getById' debería retornar null).
        assertNull(emple2, "El empleado debería haber sido eliminado y no encontrado por ID.");
    }

    @Test
    void testEmpleadoDAO() throws SQLException {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        LocalDateTime localDateTime = LocalDateTime.now();
        String strFecha = localDateTime.format(formatter);

        Empleado empleado = new Empleado(0, 1, 2, "123456", "nuevoEmpleado", "ApellidoEmpleado", 12345,"jeff@gmail.com", (byte) 2, 500.0, strFecha);

        // Llama al método 'create' para persistir el empleado de prueba en la base de datos (simulada) y verifica su creación.
        Empleado testEmpleado = create(empleado);

        // Llama al método 'update' para modificar los datos del empleado de prueba y verifica la actualización.
        update(testEmpleado);

        // Llama al método 'search' para buscar empleados por el nombre del empleado de prueba y verifica que se encuentre.
        search(testEmpleado);

        // Llama al método 'delete' para eliminar el empleado de prueba.
        delete(testEmpleado);
    }


    @Test
    void createEmpleado() throws SQLException {

        //numero de empleado para el nombre random
        Random randomEmpleNum = new Random();
        int empleNum = randomEmpleNum.nextInt(1000) + 1;
        String strEmple = "Manuel" + empleNum;

        //formato correcto para la fecha de creacion
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime localDateTime = LocalDateTime.now();
        String strFecha = localDateTime.format(formatter);

        Empleado empleado = new Empleado(0, 1, 2, "123456", strEmple, "gonza", 12345,"jeff@gmail.com", (byte) 2, 500.0, strFecha);
        Empleado emple = empleadoDAO.create(empleado);
        assertNotEquals(emple, null);
    }

}
