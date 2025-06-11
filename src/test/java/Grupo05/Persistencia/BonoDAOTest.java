package Grupo05.Persistencia;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import Grupo05.dominio.Bonos;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;
import static org.junit.jupiter.api.Assertions.*;

class BonoDAOTest {
    private BonoDAO bonoDAO;

    @BeforeEach
    void setUp() {
        bonoDAO = new BonoDAO();
    }

    private Bonos create(Bonos bono) throws SQLException {
        Bonos res = bonoDAO.create(bono);

        assertNotNull(res, "El bono creado no debería ser nulo.");
        assertEquals(bono.getNombreBono(), res.getNombreBono(),
                "El nombre del bono creado debe ser igual al original.");
        assertEquals(bono.getValor(), res.getValor(),
                0.001, "El valor del bono creado debe ser igual al original.");
        assertEquals(bono.getEstado(), res.getEstado(),
                "El estado del bono creado debe ser igual al original.");
        assertEquals(bono.getOperacion(), res.getOperacion(),
                "La operación del bono creado debe ser igual al original.");

        return res;
    }

    private void update(Bonos bono) throws SQLException {
        // Modificamos los valores del bono
        bono.setNombreBono(bono.getNombreBono() + "_mod");
        bono.setValor(bono.getValor() + 50.0);
        bono.setEstado((byte)(bono.getEstado() == 1 ? 0 : 1));
        bono.setOperacion((byte)(bono.getOperacion() == 1 ? 0 : 1));

        boolean res = bonoDAO.update(bono);
        assertTrue(res, "La actualización del bono debería ser exitosa.");

        getById(bono);
    }

    private void getById(Bonos bono) throws SQLException {
        Bonos res = bonoDAO.getById(bono.getId());

        assertNotNull(res, "El bono obtenido por ID no debería ser nulo.");
        assertEquals(bono.getId(), res.getId(),
                "El ID del bono obtenido debe ser igual al original.");
        assertEquals(bono.getNombreBono(), res.getNombreBono(),
                "El nombre del bono obtenido debe ser igual al esperado.");
        assertEquals(bono.getValor(), res.getValor(),
                0.001, "El valor del bono obtenido debe ser igual al esperado.");
        assertEquals(bono.getEstado(), res.getEstado(),
                "El estado del bono obtenido debe ser igual al esperado.");
        assertEquals(bono.getOperacion(), res.getOperacion(),
                "La operación del bono obtenido debe ser igual al esperado.");

    }

    private void search(Bonos bono) throws SQLException {
        ArrayList<Bonos> bonos = bonoDAO.search(bono.getNombreBono());
        boolean encontrado = false;

        for (Bonos b : bonos) {
            if (b.getNombreBono().contains(bono.getNombreBono())) {
                encontrado = true;
                break;
            }
        }

        assertTrue(encontrado, "El nombre del bono buscado no fue encontrado: " + bono.getNombreBono());
    }

    private void delete(Bonos bono) throws SQLException {
        boolean res = bonoDAO.delete(bono.getId());
        assertTrue(res, "La eliminación del bono debería ser exitosa.");

        Bonos res2 = bonoDAO.getById(bono.getId());
        assertNull(res2, "El bono debería haber sido eliminado y no encontrado por ID.");
    }

    @Test
    void testBonoDAO() throws SQLException {
        Random random = new Random();
        int num = random.nextInt(1000) + 1;
        String nombreBono = "Bono Test " + num;

        Bonos bono = new Bonos(
                0,
                nombreBono,
                100.0,
                (byte)1,
                (byte)1

        );

        // Prueba de creación
        Bonos testBono = create(bono);

        // Prueba de actualización
        update(testBono);

        // Prueba de búsqueda
        search(testBono);

        // Prueba de eliminación
        delete(testBono);
    }

    @Test
    void createBono() throws SQLException {
        Bonos bono = new Bonos(
                0,
                "Bono de Productividad",
                150.0,
                (byte)1,
                (byte)0
        );

        Bonos res = bonoDAO.create(bono);
        assertNotNull(res);
    }

    @Test
    void getAllTest() throws SQLException {
        ArrayList<Bonos> bonos = bonoDAO.getAll();

        assertNotNull(bonos, "La lista de bonos no debería ser nula");
        assertFalse(bonos.isEmpty(), "La lista de bonos no debería estar vacía");
    }
}