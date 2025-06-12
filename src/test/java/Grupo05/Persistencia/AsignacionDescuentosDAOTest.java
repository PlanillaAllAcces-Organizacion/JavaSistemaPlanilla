package Grupo05.Persistencia;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import Grupo05.dominio.AsignacionDescuento;
import java.sql.SQLException;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class AsignacionDescuentosDAOTest {
    private AsignacionDescuentosDAO asignacionDAO;

    // IDs de prueba (deben existir en tu base de datos)
    private final int EMPLEADO_EXISTENTE_ID = 1;
    private final int DESCUENTO_EXISTENTE_ID = 1;
    private final int DESCUENTO_ALTERNATIVO_ID = 2;

    @BeforeEach
    void setUp() {
        asignacionDAO = new AsignacionDescuentosDAO();
    }

    @Test
    void testCreateWithExistingData() throws SQLException {
        // Precondición: Verificar que no existe ya esta asignación
        boolean existsBefore = asignacionDAO.exists(EMPLEADO_EXISTENTE_ID, DESCUENTO_EXISTENTE_ID);
        if (existsBefore) {
            // Si ya existe, eliminarla para empezar limpio
            List<AsignacionDescuento> existentes = asignacionDAO.getByEmpleadoId(EMPLEADO_EXISTENTE_ID);
            existentes.stream()
                    .filter(a -> a.getDescuentos() == DESCUENTO_EXISTENTE_ID)
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
        AsignacionDescuento nueva = new AsignacionDescuento();
        nueva.setEmpleadoId(EMPLEADO_EXISTENTE_ID);
        nueva.setDescuentos(DESCUENTO_EXISTENTE_ID);

        AsignacionDescuento resultado = asignacionDAO.create(nueva);

        // Verificaciones
        assertNotNull(resultado, "La asignación creada no debería ser nula");
        assertTrue(resultado.getId() > 0, "Debería tener un ID asignado");
        assertEquals(EMPLEADO_EXISTENTE_ID, resultado.getEmpleadoId());
        assertEquals(DESCUENTO_EXISTENTE_ID, resultado.getDescuentos());

        // Limpieza
        asignacionDAO.delete(resultado.getId());
    }

    @Test
    void testGetByEmpleadoId() throws SQLException {
        // Obtener asignaciones existentes (asumiendo que hay datos en la BD)
        List<AsignacionDescuento> resultados = asignacionDAO.getByEmpleadoId(EMPLEADO_EXISTENTE_ID);

        assertNotNull(resultados, "La lista no debería ser nula");
        // Podría estar vacía si no hay asignaciones para este empleado
    }

    @Test
    void testUpdateExistingAssignment() throws SQLException {
        // 1. Crear una asignación temporal para probar
        AsignacionDescuento temp = new AsignacionDescuento();
        temp.setEmpleadoId(EMPLEADO_EXISTENTE_ID);
        temp.setDescuentos(DESCUENTO_EXISTENTE_ID);
        AsignacionDescuento creada = asignacionDAO.create(temp);

        // 2. Modificar la asignación
        creada.setDescuentos(DESCUENTO_ALTERNATIVO_ID);
        boolean updateResult = asignacionDAO.update(creada);

        // 3. Verificaciones
        assertTrue(updateResult, "La actualización debería ser exitosa");

        // 4. Verificar el cambio
        List<AsignacionDescuento> actualizadas = asignacionDAO.getByEmpleadoId(EMPLEADO_EXISTENTE_ID);
        boolean encontrada = actualizadas.stream()
                .anyMatch(a -> a.getId() == creada.getId() &&
                        a.getDescuentos() == DESCUENTO_ALTERNATIVO_ID);
        assertTrue(encontrada, "El cambio no se reflejó correctamente");

        // 5. Limpieza
        asignacionDAO.delete(creada.getId());
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
}