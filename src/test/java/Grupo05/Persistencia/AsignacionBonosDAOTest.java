package Grupo05.Persistencia;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import Grupo05.dominio.AsignacionBonos;
import java.sql.SQLException;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@Disabled
class AsignacionBonosDAOTest {
    private AsignacionBonosDAO asignacionDAO;

    // IDs de prueba (deben existir en tu base de datos)
    private final int EMPLEADO_EXISTENTE_ID = 1;
    private final int BONO_EXISTENTE_ID = 1;
    private final int BONO_ALTERNATIVO_ID = 2;

    @BeforeEach
    void setUp() {
        asignacionDAO = new AsignacionBonosDAO();
    }

    @Test
    void testCreateWithExistingData() throws SQLException {
        // Precondición: Verificar que no existe ya esta asignación
        boolean existsBefore = asignacionDAO.exists(EMPLEADO_EXISTENTE_ID, BONO_EXISTENTE_ID);
        if (existsBefore) {
            // Si ya existe, eliminarla para empezar limpio
            List<AsignacionBonos> existentes = asignacionDAO.getByEmpleadoId(EMPLEADO_EXISTENTE_ID);
            existentes.stream()
                    .filter(a -> a.getBonoId() == BONO_EXISTENTE_ID)
                    .findFirst()
                    .ifPresent(a -> {
                        try {
                            asignacionDAO.delete(a.getId());
                        } catch (SQLException e) {
                            fail("Error al limpiar datos existentes");
                        }
                    });
        }

        // Crear nueva asignación
        AsignacionBonos nueva = new AsignacionBonos();
        nueva.setEmpleadoId(EMPLEADO_EXISTENTE_ID);
        nueva.setBonoId(BONO_EXISTENTE_ID);

        AsignacionBonos resultado = asignacionDAO.create(nueva);

        // Verificaciones
        assertNotNull(resultado, "La asignación creada no debería ser nula");
        assertTrue(resultado.getId() > 0, "Debería tener un ID asignado");
        assertEquals(EMPLEADO_EXISTENTE_ID, resultado.getEmpleadoId());
        assertEquals(BONO_EXISTENTE_ID, resultado.getBonoId());

        // Limpieza
        asignacionDAO.delete(resultado.getId());
    }

    @Test
    void testGetByEmpleadoId() throws SQLException {
        // Obtener asignaciones existentes (asumiendo que hay datos en la BD)
        List<AsignacionBonos> resultados = asignacionDAO.getByEmpleadoId(EMPLEADO_EXISTENTE_ID);

        assertNotNull(resultados, "La lista no debería ser nula");
        // Podría estar vacía si no hay asignaciones para este empleado
    }

    @Test
    void testDeleteExistingAssignment() throws SQLException {
        // 1. Crear una asignación temporal para probar
        AsignacionBonos temp = new AsignacionBonos();
        temp.setEmpleadoId(EMPLEADO_EXISTENTE_ID);
        temp.setBonoId(BONO_EXISTENTE_ID);
        AsignacionBonos creada = asignacionDAO.create(temp);

        // 2. Eliminar la asignación
        boolean deleteResult = asignacionDAO.delete(creada.getId());

        // 3. Verificaciones
        assertTrue(deleteResult, "La eliminación debería ser exitosa");

        // 4. Verificar que ya no existe
        boolean existeDespues = asignacionDAO.exists(EMPLEADO_EXISTENTE_ID, BONO_EXISTENTE_ID);
        assertFalse(existeDespues, "La asignación ya no debería existir");
    }

    @Test
    void testDeleteNonExistentAssignment() throws SQLException {
        // Intentar eliminar una asignación que no existe
        boolean resultado = asignacionDAO.delete(-1);
        assertFalse(resultado, "Debería fallar al intentar eliminar ID inexistente");
    }

    @Test
    void testExistsForNonExistentPair() throws SQLException {
        // Probar con combinación que probablemente no exista
        boolean existe = asignacionDAO.exists(EMPLEADO_EXISTENTE_ID, -1);
        assertFalse(existe, "No debería existir esta asignación");
    }

    @Test
    void testExistsForExistingPair() throws SQLException {
        // 1. Crear asignación temporal
        AsignacionBonos temp = new AsignacionBonos();
        temp.setEmpleadoId(EMPLEADO_EXISTENTE_ID);
        temp.setBonoId(BONO_EXISTENTE_ID);
        AsignacionBonos creada = asignacionDAO.create(temp);

        // 2. Verificar existencia
        boolean existe = asignacionDAO.exists(EMPLEADO_EXISTENTE_ID, BONO_EXISTENTE_ID);
        assertTrue(existe, "Debería existir esta asignación");

        // 3. Limpieza
        asignacionDAO.delete(creada.getId());
    }
}