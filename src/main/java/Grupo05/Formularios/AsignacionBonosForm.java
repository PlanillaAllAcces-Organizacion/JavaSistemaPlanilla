package Grupo05.Formularios;

import Grupo05.Persistencia.AsignacionBonosDAO;
import Grupo05.Persistencia.BonoDAO;
import Grupo05.Persistencia.EmpleadoDAO;
import Grupo05.Persistencia.PuestoTrabajoDAO;
import Grupo05.dominio.AsignacionBonos;
import Grupo05.dominio.Bonos;
import Grupo05.dominio.Empleado;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class AsignacionBonosForm extends JPanel {
    private JTable tableEmpleados;
    private JTable tableBonosAsignados;
    private JComboBox<Bonos> comboBoxBonos;
    private JButton asignarBonoButton;
    private JButton eliminarAsignacionButton;

    private EmpleadoDAO empleadoDAO;
    private BonoDAO bonoDAO;
    private AsignacionBonosDAO asignacionBonosDAO;
    private PuestoTrabajoDAO puestoTrabajoDAO;
    private Empleado empleadoSeleccionado;

    public AsignacionBonosForm() {
        // Inicializar DAOs
        empleadoDAO = new EmpleadoDAO();
        bonoDAO = new BonoDAO();
        asignacionBonosDAO = new AsignacionBonosDAO();
        puestoTrabajoDAO = new PuestoTrabajoDAO();

        // Configurar el layout
        setLayout(new BorderLayout(10, 10));

        // Panel superior con título
        JPanel panelTitulo = new JPanel();
        JLabel tituloLabel = new JLabel("Asignación de Bonos a Empleados");
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
        JPanel panelBonos = new JPanel(new BorderLayout());
        panelBonos.add(new JLabel("Bonos Asignados:"), BorderLayout.NORTH);
        tableBonosAsignados = new JTable();
        panelBonos.add(new JScrollPane(tableBonosAsignados), BorderLayout.CENTER);
        panelCentral.add(panelBonos);

        add(panelCentral, BorderLayout.CENTER);

        // Panel inferior con controles
        JPanel panelInferior = new JPanel(new BorderLayout(10, 10));

        // Panel para selección de bonos
        JPanel panelSeleccionBono = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelSeleccionBono.add(new JLabel("Bono a asignar:"));
        comboBoxBonos = new JComboBox<>();
        panelSeleccionBono.add(comboBoxBonos);
        panelInferior.add(panelSeleccionBono, BorderLayout.CENTER);

        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        asignarBonoButton = new JButton("Asignar Bono");
        eliminarAsignacionButton = new JButton("Eliminar Asignación");
        panelBotones.add(asignarBonoButton);
        panelBotones.add(eliminarAsignacionButton);
        panelInferior.add(panelBotones, BorderLayout.SOUTH);

        add(panelInferior, BorderLayout.SOUTH);

        // Cargar datos iniciales
        cargarEmpleados();
        cargarBonosDisponibles();

        // Configurar eventos
        tableEmpleados.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = tableEmpleados.getSelectedRow();
                if (selectedRow >= 0) {
                    int empleadoId = (int) tableEmpleados.getValueAt(selectedRow, 0);
                    try {
                        empleadoSeleccionado = empleadoDAO.getById(empleadoId);
                        cargarBonosAsignados();
                    } catch (SQLException ex) {
                        mostrarError("Error al cargar empleado: " + ex.getMessage());
                    }
                }
            }
        });

        asignarBonoButton.addActionListener(e -> asignarBono());
        eliminarAsignacionButton.addActionListener(e -> eliminarAsignacion());
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

    private void cargarBonosDisponibles() {
        try {
            List<Bonos> bonos = bonoDAO.getAll();
            comboBoxBonos.removeAllItems();

            // Configurar renderer para mostrar solo el nombre
            comboBoxBonos.setRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(JList<?> list, Object value,
                                                              int index, boolean isSelected, boolean cellHasFocus) {
                    super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    if (value instanceof Bonos) {
                        setText(((Bonos) value).getNombreBono());
                    }
                    return this;
                }
            });

            for (Bonos bono : bonos) {
                comboBoxBonos.addItem(bono);
            }
        } catch (SQLException ex) {
            mostrarError("Error al cargar bonos disponibles: " + ex.getMessage());
        }
    }

    private void cargarBonosAsignados() {
        if (empleadoSeleccionado == null) return;

        try {
            List<AsignacionBonos> asignaciones = asignacionBonosDAO.getByEmpleadoId(empleadoSeleccionado.getId());
            DefaultTableModel model = new DefaultTableModel() {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

            model.addColumn("Nombre Bono");
            model.addColumn("Valor");
            model.addColumn("Estado");

            for (AsignacionBonos asignacion : asignaciones) {
                Bonos bono = bonoDAO.getById(asignacion.getBonoId());
                if (bono != null) {
                    model.addRow(new Object[]{
                            bono.getNombreBono(),  // Solo mostramos el nombre
                            bono.getValor(),
                            asignacion.getEstado() == 1 ? "Activo" : "Inactivo"
                    });
                }
            }

            tableBonosAsignados.setModel(model);
        } catch (SQLException ex) {
            mostrarError("Error al cargar bonos asignados: " + ex.getMessage());
        }
    }

    private void asignarBono() {
        if (empleadoSeleccionado == null) {
            mostrarAdvertencia("Seleccione un empleado primero");
            return;
        }

        Bonos bonoSeleccionado = (Bonos) comboBoxBonos.getSelectedItem();
        if (bonoSeleccionado == null) {
            mostrarAdvertencia("Seleccione un bono para asignar");
            return;
        }

        try {
            if (asignacionBonosDAO.exists(empleadoSeleccionado.getId(), bonoSeleccionado.getId())) {
                mostrarAdvertencia("Este bono ya está asignado al empleado");
                return;
            }

            AsignacionBonos asignacion = new AsignacionBonos();
            asignacion.setEmpleadoId(empleadoSeleccionado.getId());
            asignacion.setBonoId(bonoSeleccionado.getId());
            asignacion.setEstado((byte) 1);

            if (asignacionBonosDAO.create(asignacion) != null) {
                cargarBonosAsignados();
                mostrarMensaje("Bono asignado correctamente");
            } else {
                mostrarError("No se pudo asignar el bono");
            }
        } catch (SQLException ex) {
            mostrarError("Error al asignar bono: " + ex.getMessage());
        }
    }

    private void eliminarAsignacion() {
        if (empleadoSeleccionado == null) {
            mostrarAdvertencia("Seleccione un empleado primero");
            return;
        }

        int selectedRow = tableBonosAsignados.getSelectedRow();
        if (selectedRow == -1) {
            mostrarAdvertencia("Seleccione un bono asignado para eliminar");
            return;
        }

        int asignacionId = (int) tableBonosAsignados.getValueAt(selectedRow, 0);

        try {
            AsignacionBonos asignacion = new AsignacionBonos();
            asignacion.setId(asignacionId);
            asignacion.setEstado((byte) 0);

            if (asignacionBonosDAO.update(asignacion)) {
                cargarBonosAsignados();
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Asignación de Bonos");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(900, 600);
            frame.setLocationRelativeTo(null);
            frame.add(new AsignacionBonosForm());
            frame.setVisible(true);
        });
    }
}