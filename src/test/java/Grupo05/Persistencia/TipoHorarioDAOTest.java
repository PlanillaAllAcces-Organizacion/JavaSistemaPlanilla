package Grupo05.Persistencia;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import Grupo05.dominio.Horario;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;
import static org.junit.jupiter.api.Assertions.*;

class TipoHorarioDAOTest {
    private TipoHorarioDAO tipoHorarioDAO;

    @BeforeEach
    void setUp() {
        tipoHorarioDAO = new TipoHorarioDAO();
    }

    private Horario create(Horario horario) throws SQLException {
        Horario res = tipoHorarioDAO.create(horario);

        assertNotNull(res, "El horario creado no debería ser nulo");
        assertEquals(horario.getNombreHorario(), res.getNombreHorario(),
                "El nombre del horario creado debe ser igual al original");

        return res;
    }

    private void update(Horario horario) throws SQLException {
        String nuevoNombre = horario.getNombreHorario() + "_actualizado";
        horario.setNombreHorario(nuevoNombre);

        boolean res = tipoHorarioDAO.update(horario);
        assertTrue(res, "La actualización del horario debería ser exitosa");

        getById(horario);
    }

    private void getById(Horario horario) throws SQLException {
        Horario res = tipoHorarioDAO.getById(horario.getId());

        assertNotNull(res, "El horario obtenido por ID no debería ser nulo");
        assertEquals(horario.getId(), res.getId(),
                "El ID del horario obtenido debe ser igual al original");
        assertEquals(horario.getNombreHorario(), res.getNombreHorario(),
                "El nombre del horario obtenido debe ser igual al esperado");
    }

    private void search(Horario horario) throws SQLException {
        ArrayList<Horario> horarios = tipoHorarioDAO.search(horario.getNombreHorario());
        boolean encontrado = false;

        for (Horario h : horarios) {
            if (h.getNombreHorario().contains(horario.getNombreHorario())) {
                encontrado = true;
                break;
            }
        }

        assertTrue(encontrado, "El horario buscado no fue encontrado: " + horario.getNombreHorario());
    }

    private void delete(Horario horario) throws SQLException {
        boolean res = tipoHorarioDAO.delete(horario.getId());
        assertTrue(res, "La eliminación del horario debería ser exitosa");

        Horario res2 = tipoHorarioDAO.getById(horario.getId());
        assertNull(res2, "El horario debería haber sido eliminado y no encontrado por ID");
    }

    @Test
    void testTipoHorarioDAO() throws SQLException {
        Random random = new Random();
        int num = random.nextInt(1000) + 1;
        String nombreHorario = "Horario_" + num;

        Horario horario = new Horario();
        horario.setNombreHorario(nombreHorario);

        // Prueba de creación
        Horario testHorario = create(horario);

        // Prueba de actualización
        update(testHorario);

        // Prueba de búsqueda
        search(testHorario);

        // Prueba de eliminación
        delete(testHorario);
    }

    @Test
    void createHorario() throws SQLException {
        Horario horario = new Horario();
        horario.setNombreHorario("Horario_Admin");

        Horario res = tipoHorarioDAO.create(horario);
        assertNotNull(res);

        // Limpieza después de la prueba
        tipoHorarioDAO.delete(res.getId());
    }

    @Test
    void getAllHorarios() throws SQLException {
        ArrayList<Horario> horarios = tipoHorarioDAO.getAll();
        assertNotNull(horarios, "La lista de horarios no debería ser nula");
        assertFalse(horarios.isEmpty(), "Debería haber al menos un horario en la lista");
    }
}