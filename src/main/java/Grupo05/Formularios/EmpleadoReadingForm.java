package Grupo05.Formularios;

import Grupo05.Persistencia.EmpleadoDAO;
import Grupo05.dominio.Empleado;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import Grupo05.Utils.CUD;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class EmpleadoReadingForm extends JDialog {
    private JTextField txtName;
    private JButton btnCrear;
    private JButton btnModificar;
    private JButton btnEliminar;
    private JTable tableEmpleados;
    private JPanel mainPanel;
    private JButton button1;

    private EmpleadoDAO empleadoDAO; // Instancia de UserDAO para realizar operaciones de base de datos de usuarios.
    private MainForm mainForm; //

    // Constructor de la clase UserReadingForm. Recibe una instancia de MainForm como parámetro.
    public EmpleadoReadingForm(MainForm mainForm) {
        super(mainForm, true);
        this.mainForm = mainForm;
        empleadoDAO = new EmpleadoDAO();
        setContentPane(mainPanel);
        setModal(true);
        setTitle("Buscar Empleados"); // Título actualizado
        pack();
        setLocationRelativeTo(mainForm);
        setContentPane(mainPanel);

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
            ArrayList<Empleado> empleados = empleadoDAO.search(query);
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
        model.addColumn("Estado");

        this.tableEmpleados.setModel(model);

        for (Empleado empleado : empleados) {
            model.addRow(new Object[]{
                    empleado.getId(),
                    empleado.getNombre(),
                    empleado.getApellido(),
                    empleado.getDui(),
                    empleado.getTelefono(),
                    empleado.getCorreo(),
                    empleado.getStrEstatus()
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

    // Método para obtener el Empleado seleccionado de la fila de la tabla.
    private Empleado getEmpleadoFromTableRow() {
        Empleado empleado = null;
        try {
            int filaSelect = this.tableEmpleados.getSelectedRow();
            if (filaSelect == -1) {
                JOptionPane.showMessageDialog(null,
                        "Selecciona una fila de la tabla.",
                        "Validación", JOptionPane.WARNING_MESSAGE);
                return null;
            }

            int id = (int) this.tableEmpleados.getValueAt(filaSelect, 0); // Obtener ID de la primera columna
            empleado = empleadoDAO.getById(id); // Buscar el empleado por ID

            if (empleado == null) { // Si getById devuelve null, no se encontró
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

