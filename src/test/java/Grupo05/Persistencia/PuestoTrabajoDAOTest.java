package Grupo05.Persistencia;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import Grupo05.dominio.PuestoTrabajo;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;
import static org.junit.jupiter.api.Assertions.*;

class PuestoTrabajoDAOTest {
    private PuestoTrabajoDAO puestoTrabajoDAO;

    @BeforeEach
    void setUp() {
        puestoTrabajoDAO = new PuestoTrabajoDAO();
    }

    private PuestoTrabajo create(PuestoTrabajo puesto) throws SQLException {
        PuestoTrabajo res = puestoTrabajoDAO.create(puesto);

        assertNotNull(res, "El puesto creado no debería ser nulo.");
        assertEquals(puesto.getNombrePuesto(), res.getNombrePuesto(),
                "El nombre del puesto creado debe ser igual al original.");
        assertEquals(0, puesto.getSalarioBase().compareTo(res.getSalarioBase()),
                "El salario base del puesto creado debe ser igual al original.");
        assertEquals(0, puesto.getValorxHora().compareTo(res.getValorxHora()),
                "El valor por hora del puesto creado debe ser igual al original.");
        assertEquals(0, puesto.getValorExtra().compareTo(res.getValorExtra()),
                "El valor extra del puesto creado debe ser igual al original.");
        assertEquals(puesto.getEstado(), res.getEstado(),
                "El estado del puesto creado debe ser igual al original.");

        return res;
    }

    private void update(PuestoTrabajo puesto) throws SQLException {
        // Modificamos los valores del puesto
        puesto.setNombrePuesto(puesto.getNombrePuesto() + "_mod");
        puesto.setSalarioBase(puesto.getSalarioBase().add(new BigDecimal("100.00")));
        puesto.setValorxHora(puesto.getValorxHora().add(new BigDecimal("5.00")));
        puesto.setValorExtra(puesto.getValorExtra().add(new BigDecimal("2.50")));
        puesto.setEstado((byte)1);

        boolean res = puestoTrabajoDAO.update(puesto);
        assertTrue(res, "La actualización del puesto debería ser exitosa.");

        getById(puesto);
    }

    private void getById(PuestoTrabajo puesto) throws SQLException {
        PuestoTrabajo res = puestoTrabajoDAO.getById(puesto.getId());

        assertNotNull(res, "El puesto obtenido por ID no debería ser nulo.");
        assertEquals(puesto.getId(), res.getId(),
                "El ID del puesto obtenido debe ser igual al original.");
        assertEquals(puesto.getNombrePuesto(), res.getNombrePuesto(),
                "El nombre del puesto obtenido debe ser igual al esperado.");
        assertEquals(0, puesto.getSalarioBase().compareTo(res.getSalarioBase()),
                "El salario base del puesto obtenido debe ser igual al esperado.");
        assertEquals(0, puesto.getValorxHora().compareTo(res.getValorxHora()),
                "El valor por hora del puesto obtenido debe ser igual al esperado.");
        assertEquals(0, puesto.getValorExtra().compareTo(res.getValorExtra()),
                "El valor extra del puesto obtenido debe ser igual al esperado.");
        assertEquals(puesto.getEstado(), res.getEstado(),
                "El estado del puesto obtenido debe ser igual al esperado.");
    }

    private void search(PuestoTrabajo puesto) throws SQLException {
        ArrayList<PuestoTrabajo> puestos = puestoTrabajoDAO.search(puesto.getNombrePuesto());
        boolean encontrado = false;

        for (PuestoTrabajo p : puestos) {
            if (p.getNombrePuesto().contains(puesto.getNombrePuesto())) {
                encontrado = true;
            } else {
                encontrado = false;
                break;
            }
        }

        assertTrue(encontrado, "El nombre del puesto buscado no fue encontrado: " + puesto.getNombrePuesto());
    }

    private void delete(PuestoTrabajo puesto) throws SQLException {
        boolean res = puestoTrabajoDAO.delete(puesto.getId());
        assertTrue(res, "La eliminación del puesto debería ser exitosa.");

        PuestoTrabajo res2 = puestoTrabajoDAO.getById(puesto.getId());
        assertNull(res2, "El puesto debería haber sido eliminado y no encontrado por ID.");
    }

    @Test
    void testPuestoTrabajoDAO() throws SQLException {
        Random random = new Random();
        int num = random.nextInt(1000) + 1;
        String nombrePuesto = "Puesto Test " + num;

        PuestoTrabajo puesto = new PuestoTrabajo(
                0,
                nombrePuesto,
                new BigDecimal("1000.00"),
                new BigDecimal("10.50"),
                new BigDecimal("15.75"),
                (byte)2
        );

        // Prueba de creación
        PuestoTrabajo testPuesto = create(puesto);

        // Prueba de actualización
        update(testPuesto);

        // Prueba de búsqueda
        search(testPuesto);

        // Prueba de eliminación
        delete(testPuesto);
    }

    @Test
    void createPuestoTrabajo() throws SQLException {
        PuestoTrabajo puesto = new PuestoTrabajo(
                0,
                "Gerente",
                new BigDecimal("2500.00"),
                new BigDecimal("25.00"),
                new BigDecimal("30.00"),
                (byte)1
        );

        PuestoTrabajo res = puestoTrabajoDAO.create(puesto);
        assertNotNull(res);
    }

    @Test
    void getAllActiveTest() throws SQLException {
        ArrayList<PuestoTrabajo> puestosActivos = puestoTrabajoDAO.getAllActive();

        assertNotNull(puestosActivos, "La lista de puestos activos no debería ser nula");

        // Verificamos que todos los puestos devueltos estén activos (estado = 1)
        for (PuestoTrabajo puesto : puestosActivos) {
            assertEquals(1, puesto.getEstado(),
                    "Todos los puestos devueltos deberían estar activos");
        }
    }
}