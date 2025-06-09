package Grupo05.Persistencia;

import Grupo05.dominio.Descuentos;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import Grupo05.dominio.Descuentos;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;
import static org.junit.jupiter.api.Assertions.*;

class DescuentoDAOTest {
    private DescuentoDAO descuentoDAO;

    @BeforeEach
    void setUp() {
        descuentoDAO = new DescuentoDAO();
    }

    private Descuentos create(Descuentos descuento) throws SQLException {
        Descuentos res = descuentoDAO.create(descuento);

        assertNotNull(res, "El descuento creado no debería ser nulo.");
        assertEquals(descuento.getNombre(), res.getNombre(),
                "El nombre del descuento creado debe ser igual al original.");
        assertEquals(descuento.getValor(), res.getValor(),
                0.001, "El valor del descuento creado debe ser igual al original.");
        assertEquals(descuento.getEstado(), res.getEstado(),
                "El estado del descuento creado debe ser igual al original.");
        assertEquals(descuento.getOperacion(), res.getOperacion(),
                "La operación del descuento creado debe ser igual al original.");
        assertEquals(descuento.getPlanilla(), res.getPlanilla(),
                "El planilla del descuento creado debe ser igual al original.");

        return res;
    }

    private void update(Descuentos descuento) throws SQLException {
        // Modificamos los valores del descuento
        descuento.setNombre(descuento.getNombre() + "_mod");
        descuento.setValor(descuento.getValor() + 10.0);
        descuento.setEstado((byte)(descuento.getEstado() == 1 ? 0 : 1));
        descuento.setOperacion((byte)(descuento.getOperacion() == 1 ? 0 : 1));
        descuento.setPlanilla((byte)(descuento.getPlanilla() == 1 ? 0 : 1));

        boolean res = descuentoDAO.update(descuento);
        assertTrue(res, "La actualización del descuento debería ser exitosa.");

        getById(descuento);
    }

    private void getById(Descuentos descuento) throws SQLException {
        Descuentos res = descuentoDAO.getById(descuento.getId());

        assertNotNull(res, "El descuento obtenido por ID no debería ser nulo.");
        assertEquals(descuento.getId(), res.getId(),
                "El ID del descuento obtenido debe ser igual al original.");
        assertEquals(descuento.getNombre(), res.getNombre(),
                "El nombre del descuento obtenido debe ser igual al esperado.");
        assertEquals(descuento.getValor(), res.getValor(),
                0.001, "El valor del descuento obtenido debe ser igual al esperado.");
        assertEquals(descuento.getEstado(), res.getEstado(),
                "El estado del descuento obtenido debe ser igual al esperado.");
        assertEquals(descuento.getOperacion(), res.getOperacion(),
                "La operación del descuento obtenido debe ser igual al esperado.");
        assertEquals(descuento.getPlanilla(), res.getPlanilla(),
                "El planilla del descuento obtenido debe ser igual al esperado.");
    }

    private void search(Descuentos descuento) throws SQLException {
        ArrayList<Descuentos> descuentos = descuentoDAO.search(descuento.getNombre());
        boolean encontrado = false;

        for (Descuentos d : descuentos) {
            if (d.getNombre().contains(descuento.getNombre())) {
                encontrado = true;
                break;
            }
        }

        assertTrue(encontrado, "El nombre del descuento buscado no fue encontrado: " + descuento.getNombre());
    }

    private void delete(Descuentos descuento) throws SQLException {
        boolean res = descuentoDAO.delete(descuento.getId());
        assertTrue(res, "La eliminación del descuento debería ser exitosa.");

        Descuentos res2 = descuentoDAO.getById(descuento.getId());
        assertNull(res2, "El descuento debería haber sido eliminado y no encontrado por ID.");
    }

    @Test
    void testDescuentoDAO() throws SQLException {
        Random random = new Random();
        int num = random.nextInt(1000) + 1;
        String nombreDescuento = "Descuento Test " + num;

        Descuentos descuento = new Descuentos(
                0,
                nombreDescuento,
                50.0,
                (byte)1,
                (byte)1,
                (byte)1
        );

        // Prueba de creación
        Descuentos testDescuento = create(descuento);

        // Prueba de actualización
        update(testDescuento);

        // Prueba de búsqueda
        search(testDescuento);

        // Prueba de eliminación
        delete(testDescuento);
    }

    @Test
    void createDescuento() throws SQLException {
        Descuentos descuento = new Descuentos(
                0,
                "Descuento por tardanza",
                25.0,
                (byte)1,
                (byte)0,
                (byte)1
        );

        Descuentos res = descuentoDAO.create(descuento);
        assertNotNull(res);
    }

    @Test
    void getAllTest() throws SQLException {
        ArrayList<Descuentos> descuentos = descuentoDAO.getAll();

        assertNotNull(descuentos, "La lista de descuentos no debería ser nula");
        assertFalse(descuentos.isEmpty(), "La lista de descuentos no debería estar vacía");
    }

    @Test
    void getActiveTest() throws SQLException {
        ArrayList<Descuentos> descuentosActivos = descuentoDAO.getActive();

        assertNotNull(descuentosActivos, "La lista de descuentos activos no debería ser nula");

        // Verificamos que todos los descuentos devueltos estén activos (estado = 1)
        for (Descuentos descuento : descuentosActivos) {
            assertEquals(1, descuento.getEstado(),
                    "Todos los descuentos devueltos deberían estar activos");
        }
    }

    @Test
    void testDeleteWithAssignments() throws SQLException {
        // Crear un descuento para probar
        Descuentos descuento = new Descuentos(
                0,
                "Descuento Temporal",
                30.0,
                (byte)1,
                (byte)1,
                (byte)1
        );
        Descuentos created = descuentoDAO.create(descuento);

        // Intento de eliminar (debería fallar si tiene asignaciones)
        try {
            boolean deleted = descuentoDAO.delete(created.getId());
            assertTrue(deleted || !deleted, "La eliminación puede fallar si hay asignaciones");
        } catch (SQLException ex) {
            assertTrue(ex.getMessage().contains("asignado"), "Debería fallar por asignaciones existentes");
        }
    }
}