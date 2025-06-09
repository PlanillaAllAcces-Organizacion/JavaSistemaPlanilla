package Grupo05.Formularios;

import Grupo05.Persistencia.EmpleadoDAO;

import javax.swing.*; // Importa el paquete Swing, que proporciona clases para crear interfaces gráficas de usuario.
import javax.swing.table.DefaultTableModel; // Importa la clase DefaultTableModel, utilizada para crear y manipular modelos de datos para JTable.
import Grupo05.dominio.Empleado; // Importa la clase User, que representa la entidad de usuario en el dominio de la aplicación.
import Grupo05.Utils.CUD; // Importa el enum  CUD (Create, Update, Delete).

import java.awt.event.KeyAdapter; // Importa la clase KeyAdapter, una clase adaptadora para recibir eventos de teclado.
import java.awt.event.KeyEvent; // Importa la clase KeyEvent, que representa un evento de teclado.
import java.util.ArrayList; // Importa la clase ArrayList, una implementación de la interfaz List que permite almacenar colecciones dinámicas de objetos.


public class EmpleadoReadingForm extends JDialog {
    private JTextField txtName;
    private JButton btnCrear;
    private JButton btnModificar;
    private JButton btnEliminar;
    private JTable tableEmpleados;
    private JPanel mainPanel;

    private EmpleadoDAO empleadoDAO;
    private MainForm mainForm;

    public EmpleadoReadingForm(MainForm mainForm) {
        this.mainForm = mainForm;
        empleadoDAO = new EmpleadoDAO();
        setContentPane(mainPanel);
        setModal(true);
        setTitle("Buscar Empleados"); // Título actualizado
        pack();
        setLocationRelativeTo(mainForm);

        // Listener para el campo de búsqueda por nombre/DUI
        txtName.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (!txtName.getText().trim().isEmpty()) {
                    search(txtName.getText());
                } else {
                    // Si el campo está vacío, limpiar la tabla
                    DefaultTableModel emptyModel = new DefaultTableModel();
                    tableEmpleados.setModel(emptyModel);
                }
            }
        });

        // ActionListeners para los botones CRUD
        btnCrear.addActionListener(s -> {
            // Abre EmpleadoForm para crear un nuevo empleado
            EmpleadosForm empleadoForm = new EmpleadosForm(this.mainForm, CUD.CREATE, new Empleado());
            empleadoForm.setVisible(true);
            // Limpiar la tabla después de cerrar el formulario para forzar una recarga si es necesario
            DefaultTableModel emptyModel = new DefaultTableModel();
            tableEmpleados.setModel(emptyModel);
            // Opcional: Si el campo de búsqueda tiene texto, hacer la búsqueda nuevamente
            if (!txtName.getText().trim().isEmpty()) {
                search(txtName.getText());
            }
        });

        btnModificar.addActionListener(s -> {
            // Obtiene el empleado seleccionado y abre EmpleadoForm para actualizar
            Empleado empleado = getEmpleadoFromTableRow();
            if (empleado != null) {
                EmpleadosForm empleadoForm = new EmpleadosForm(this.mainForm, CUD.UPDATE, empleado);
                empleadoForm.setVisible(true);
                DefaultTableModel emptyModel = new DefaultTableModel();
                tableEmpleados.setModel(emptyModel);
                if (!txtName.getText().trim().isEmpty()) {
                    search(txtName.getText());
                }
            }
        });

        btnEliminar.addActionListener(s -> {
            // Obtiene el empleado seleccionado y abre EmpleadoForm para eliminar
            Empleado empleado = getEmpleadoFromTableRow();
            if (empleado != null) {
                EmpleadosForm empleadoForm = new EmpleadosForm(this.mainForm, CUD.DELETE, empleado);
                empleadoForm.setVisible(true);
                DefaultTableModel emptyModel = new DefaultTableModel();
                tableEmpleados.setModel(emptyModel);
                if (!txtName.getText().trim().isEmpty()) {
                    search(txtName.getText());
                }
            }
        });
    }

    private void search(String query) {
        try {
            ArrayList<Empleado> empleados = empleadoDAO.search(query); // Usa el método search de EmpleadoDAO
            createTable(empleados);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null,
                    "Error al buscar empleados: " + ex.getMessage(),
                    "ERROR", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void createTable(ArrayList<Empleado> empleados) {
        DefaultTableModel model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // No editar celdas
            }
        };

        // Definir columnas para la tabla de Empleados
        model.addColumn("ID");
        model.addColumn("Nombre");
        model.addColumn("Apellido");
        model.addColumn("DUI");
        model.addColumn("Teléfono");
        model.addColumn("Correo");
        model.addColumn("Usuario");
        model.addColumn("Estado");
        // Puedes añadir más columnas si lo deseas, como Puesto de Trabajo, Horario, etc.

        this.tableEmpleados.setModel(model);

        // Llenar la tabla con los datos de los empleados
        for (Empleado empleado : empleados) {
            model.addRow(new Object[]{
                    empleado.getId(),
                    empleado.getNombre(),
                    empleado.getApellido(),
                    empleado.getDui(),
                    empleado.getTelefono(),
                    empleado.getCorreo(),
                    empleado.getUsuario(),
                    empleado.getEstado() // Asumiendo que Empleado tiene un getStrEstado() similar a PuestoTrabajo
            });
        }

        hideCol(0); // Ocultar la columna del ID
    }

    private void hideCol(int pColumna) {
        this.tableEmpleados.getColumnModel().getColumn(pColumna).setMaxWidth(0);
        this.tableEmpleados.getColumnModel().getColumn(pColumna).setMinWidth(0);
        this.tableEmpleados.getTableHeader().getColumnModel().getColumn(pColumna).setMaxWidth(0);
        this.tableEmpleados.getTableHeader().getColumnModel().getColumn(pColumna).setMinWidth(0);
    }

    private Empleado getEmpleadoFromTableRow() {
        try {
            int filaSelect = this.tableEmpleados.getSelectedRow();

            if (filaSelect == -1) {
                JOptionPane.showMessageDialog(null,
                        "Seleccionar una fila de la tabla.",
                        "Validación", JOptionPane.WARNING_MESSAGE);
                return null;
            }

            // Obtener el ID desde la tabla
            Object idObj = this.tableEmpleados.getValueAt(filaSelect, 0);
            if (!(idObj instanceof Integer)) {
                JOptionPane.showMessageDialog(null,
                        "El ID del empleado no es válido.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return null;
            }

            int id = (int) idObj;
            System.out.println("ID seleccionado: " + id);

            Empleado empleado = empleadoDAO.getById(id);

            if (empleado == null) {
                JOptionPane.showMessageDialog(null,
                        "No se encontró ningún empleado con el ID seleccionado.",
                        "Validación", JOptionPane.WARNING_MESSAGE);
                return null;
            }

            return empleado;
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null,
                    "Error al obtener el empleado seleccionado: " + ex.getMessage(),
                    "ERROR", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

}
