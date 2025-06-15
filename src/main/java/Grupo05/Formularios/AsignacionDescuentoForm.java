package Grupo05.Formularios;

import javax.swing.*;

import Grupo05.Persistencia.*;
import Grupo05.dominio.*;
import Grupo05.Persistencia.DescuentoDAO;
import Grupo05.Persistencia.EmpleadoDAO;
import Grupo05.Persistencia.PuestoTrabajoDAO;
import javax.swing.table.DefaultTableModel;
import Grupo05.dominio.AsignacionDescuento;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import Grupo05.dominio.Empleado;

public class AsignacionDescuentoForm extends JDialog{

    private JTable tableDescuentosAsignados;
    private JTable tableEmpleados;
    private JComboBox<Descuentos> comboBoxDescuentos;
    private JButton eliminarAsignaciónButton;
    private JButton asignarDescuentoButton;

    private EmpleadoDAO empleadoDAO;
    private DescuentoDAO descuentoDAO;
    private AsignacionDescuentosDAO asignacionDescuentoDAO;
    private PuestoTrabajoDAO puestoTrabajoDAO;
    private Empleado empleadoSeleccionado;

    public AsignacionDescuentoForm(JFrame parent){
        super(parent, "Asignación de Descuentos", true);

        setSize(900, 600);
        setLocationRelativeTo(parent);

        // Configurar el layout
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        empleadoDAO = new EmpleadoDAO();
        descuentoDAO = new DescuentoDAO();
        asignacionDescuentoDAO = new AsignacionDescuentosDAO();
        puestoTrabajoDAO = new PuestoTrabajoDAO();

        // Configurar el layout
        setLayout(new BorderLayout(10, 10));

        // Panel superior con título
        JPanel panelTitulo = new JPanel();
        JLabel tituloLabel = new JLabel("Asignación de Descuento a Empleados");
        tituloLabel.setFont(new Font("Arial", Font.BOLD, 16));
        panelTitulo.add(tituloLabel);
        add(panelTitulo, BorderLayout.NORTH);

        // Panel central con tablas
        JPanel panelCentral = new JPanel(new GridLayout(1, 2, 10, 10));

        // Tabla de empleados
        JPanel panelEmpleados = new JPanel(new BorderLayout());
        panelEmpleados.add(new JLabel("Empleados:"), BorderLayout.NORTH);
        tableEmpleados = new JTable();
        tableEmpleados.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        panelEmpleados.add(new JScrollPane(tableEmpleados), BorderLayout.CENTER);
        panelCentral.add(panelEmpleados);

        // Tabla de bonos asignados
        JPanel panelDescuentos = new JPanel(new BorderLayout());
        panelDescuentos.add(new JLabel("Descuentos Asignados:"), BorderLayout.NORTH);
        tableDescuentosAsignados = new JTable();
        panelDescuentos.add(new JScrollPane(tableDescuentosAsignados), BorderLayout.CENTER);
        panelCentral.add(panelDescuentos);

        add(panelCentral, BorderLayout.CENTER);

        // Panel inferior con controles
        JPanel panelInferior = new JPanel(new BorderLayout(10, 10));

        // Panel para selección de bonos
        JPanel panelSeleccionDescuento = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelSeleccionDescuento.add(new JLabel("Descuento a asignar:"));
        comboBoxDescuentos = new JComboBox<>();
        panelSeleccionDescuento.add(comboBoxDescuentos);
        panelInferior.add(panelSeleccionDescuento, BorderLayout.CENTER);

        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        asignarDescuentoButton = new JButton("Asignar Descuento");
        eliminarAsignaciónButton = new JButton("Eliminar Asignación");
        panelBotones.add(asignarDescuentoButton);
        panelBotones.add(eliminarAsignaciónButton);
        panelInferior.add(panelBotones, BorderLayout.SOUTH);

        asignarDescuentoButton.setBackground(new Color(34, 139, 34));
        asignarDescuentoButton.setForeground(Color.WHITE);
        eliminarAsignaciónButton.setBackground(new Color(178, 34, 34));
        eliminarAsignaciónButton.setForeground(Color.WHITE);

        add(panelInferior, BorderLayout.SOUTH);

        // Cargar datos iniciales
        cargarEmpleados();
        cargarDescuentosDisponibles();

        // Configurar eventos
        tableEmpleados.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = tableEmpleados.getSelectedRow();
                if (selectedRow >= 0) {
                    int empleadoId = (int) tableEmpleados.getValueAt(selectedRow, 0);
                    try {
                        empleadoSeleccionado = empleadoDAO.getById(empleadoId);
                        cargarDescuentosAsignados();
                    } catch (SQLException ex) {
                        mostrarError("Error al cargar empleado: " + ex.getMessage());
                    }
                }
            }
        });

        asignarDescuentoButton.addActionListener(e -> asignarDescuento());
        eliminarAsignaciónButton.addActionListener(e -> eliminarAsignacion());
    }

    private void cargarEmpleados() {
        try {
            List<Empleado> empleados = empleadoDAO.getAllActive();
            DefaultTableModel model = new DefaultTableModel() {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

            model.addColumn("ID");
            model.addColumn("Nombre");
            model.addColumn("Apellido");
            model.addColumn("DUI");
            model.addColumn("Cargo");

            for (Empleado empleado : empleados) {
                model.addRow(new Object[]{
                        empleado.getId(),
                        empleado.getNombre(),
                        empleado.getApellido(),
                        empleado.getDui(),
                        obtenerNombrePuesto(empleado.getPuestoTrabajoId())
                });
            }

            tableEmpleados.setModel(model);
        } catch (SQLException ex) {
            mostrarError("Error al cargar empleados: " + ex.getMessage());
        }
    }

    private String obtenerNombrePuesto(Integer puestoId) {
        if (puestoId == null) return "Sin asignar";

        try {
            return puestoTrabajoDAO.getNombrePuesto(puestoId);
        } catch (SQLException e) {
            return "Puesto " + puestoId;
        }
    }

    private void cargarDescuentosDisponibles() {
        try {
            List<Descuentos> descuentos = descuentoDAO.getAll();
            comboBoxDescuentos.removeAllItems();

            // Configurar renderer para mostrar solo el nombre
            comboBoxDescuentos.setRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(JList<?> list, Object value,
                                                              int index, boolean isSelected, boolean cellHasFocus) {
                    super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    if (value instanceof Descuentos) {
                        setText(((Descuentos) value).getNombre());
                    }
                    return this;
                }
            });

            for (Descuentos descuentos1 : descuentos) {
                comboBoxDescuentos.addItem(descuentos1);
            }
        } catch (SQLException ex) {
            mostrarError("Error al cargar descuentos disponibles: " + ex.getMessage());
        }
    }

    private void cargarDescuentosAsignados() {
        if (empleadoSeleccionado == null) return;

        try {
            List<AsignacionDescuento> asignaciones = asignacionDescuentoDAO.getByEmpleadoId(empleadoSeleccionado.getId());
            DefaultTableModel model = new DefaultTableModel() {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

            model.addColumn("ID Asignación");
            model.addColumn("Nombre Descuento");
            model.addColumn("Valor");

            for (AsignacionDescuento asignacion : asignaciones) {
                Descuentos descuento = descuentoDAO.getById(asignacion.getDescuentos());
                if (descuento != null) {
                    model.addRow(new Object[]{
                            asignacion.getId(),
                            descuento.getNombre(),
                            descuento.getValor()
                    });
                }
            }

            tableDescuentosAsignados.setModel(model);
        } catch (SQLException ex) {
            mostrarError("Error al cargar descuentos asignados: " + ex.getMessage());
        }
    }

    private void asignarDescuento() {
        if (empleadoSeleccionado == null) {
            mostrarAdvertencia("Seleccione un empleado primero");
            return;
        }

        Descuentos descuentoSelecionado = (Descuentos) comboBoxDescuentos.getSelectedItem();
        if (descuentoSelecionado == null) {
            mostrarAdvertencia("Seleccione un descuento para asignar");
            return;
        }

        try {
            if (asignacionDescuentoDAO.exists(empleadoSeleccionado.getId(), descuentoSelecionado.getId())) {
                mostrarAdvertencia("Este descuento ya está asignado al empleado");
                return;
            }

            AsignacionDescuento asignacion = new AsignacionDescuento();
            asignacion.setEmpleadoId(empleadoSeleccionado.getId());
            asignacion.setDescuentos(descuentoSelecionado.getId()); // Corregido: usar setDescuentos()

            if (asignacionDescuentoDAO.create(asignacion) != null) {
                cargarDescuentosAsignados();
                mostrarMensaje("Descuento asignado correctamente");
            } else {
                mostrarError("No se pudo asignar el descuento");
            }
        } catch (SQLException ex) {
            mostrarError("Error al asignar descuento: " + ex.getMessage());
        }
    }

    private void eliminarAsignacion() {
        if (empleadoSeleccionado == null) {
            mostrarAdvertencia("Seleccione un empleado primero");
            return;
        }

        int selectedRow = tableDescuentosAsignados.getSelectedRow();
        if (selectedRow == -1) {
            mostrarAdvertencia("Seleccione un descuento asignado para eliminar");
            return;
        }

        int asignacionId = (int) tableDescuentosAsignados.getValueAt(selectedRow, 0);

        try {
            AsignacionDescuento asignacion = new AsignacionDescuento();
            asignacion.setId(asignacionId);

            if (asignacionDescuentoDAO.delete(asignacionId)) {
                cargarDescuentosAsignados();
                mostrarMensaje("Asignación eliminada correctamente");
            } else {
                mostrarError("No se pudo eliminar la asignación");
            }
        } catch (SQLException ex) {
            mostrarError("Error al eliminar asignación: " + ex.getMessage());
        }
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void mostrarAdvertencia(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Advertencia", JOptionPane.WARNING_MESSAGE);
    }

    private void mostrarMensaje(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Información", JOptionPane.INFORMATION_MESSAGE);
    }

}