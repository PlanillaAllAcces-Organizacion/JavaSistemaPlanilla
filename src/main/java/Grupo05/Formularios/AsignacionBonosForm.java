package Grupo05.Formularios;

import Grupo05.Persistencia.AsignacionBonosDAO;
import Grupo05.Persistencia.BonoDAO;
import Grupo05.Persistencia.EmpleadoDAO;
import Grupo05.dominio.AsignacionBonos;
import Grupo05.dominio.Bonos;
import Grupo05.dominio.Empleado;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AsignacionBonosForm extends JFrame {
    private JTable empleadosTable;
    private JTable bonosTable;
    private JButton btnAsignar;
    private JButton btnDesasignar;
    private JButton btnRefresh;
    private JButton btnVerBonosAsignados;
    private EmpleadoDAO empleadoDAO;
    private BonoDAO bonoDAO;
    private AsignacionBonosDAO asignacionBonoDAO;
    private JPanel mainPanel;
    private JTabbedPane tabbedPane;


    public AsignacionBonosForm() {
        empleadoDAO = new EmpleadoDAO();
        bonoDAO = new BonoDAO();
        asignacionBonoDAO = new AsignacionBonosDAO();

        initComponents();
        setTitle("Asignación de Bonos a Empleados");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        //loadEmpleados();
        loadBonosDisponibles();
    }

    private void initComponents() {
        mainPanel = new JPanel(new BorderLayout());

        // Pestañas para organizar la información
        tabbedPane = new JTabbedPane();

        // Panel para empleados
        JPanel empleadosPanel = new JPanel(new BorderLayout());

        // Tabla de empleados
        empleadosTable = new JTable();
        empleadosTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane empleadosScrollPane = new JScrollPane(empleadosTable);
        empleadosPanel.add(empleadosScrollPane, BorderLayout.CENTER);

        JPanel empleadosButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnVerBonosAsignados = new JButton("Ver Bonos Asignados");
        btnVerBonosAsignados.addActionListener(e -> mostrarBonosAsignados());
        btnRefresh = new JButton("Actualizar");
        btnRefresh.addActionListener(e -> {
            //loadEmpleados();
            loadBonosDisponibles();
        });

        empleadosButtonPanel.add(btnVerBonosAsignados);
        empleadosButtonPanel.add(btnRefresh);
        empleadosPanel.add(empleadosButtonPanel, BorderLayout.SOUTH);

        // Panel para bonos
        JPanel bonosPanel = new JPanel(new BorderLayout());

        // Tabla de bonos disponibles
        bonosTable = new JTable();
        bonosTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane bonosScrollPane = new JScrollPane(bonosTable);
        bonosPanel.add(bonosScrollPane, BorderLayout.CENTER);

        // Panel de botones para bonos
        JPanel bonosButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnAsignar = new JButton("Asignar Bono");
        btnAsignar.addActionListener(e -> asignarBono());
        btnDesasignar = new JButton("Desasignar Bono");
        btnDesasignar.addActionListener(e -> desasignarBono());

        bonosButtonPanel.add(btnAsignar);
        bonosButtonPanel.add(btnDesasignar);
        bonosPanel.add(bonosButtonPanel, BorderLayout.SOUTH);

        // Agregar pestañas al panel principal
        tabbedPane.addTab("Empleados", empleadosPanel);
        tabbedPane.addTab("Bonos Disponibles", bonosPanel);

        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        add(mainPanel);
    }
//
//    private void loadEmpleados() {
//        try {
//            List<Empleado> empleados = empleadoDAO.getAll();
//            updateEmpleadosTableModel(empleados);
//        } catch (SQLException ex) {
//            showError("Error al cargar empleados: " + ex.getMessage());
//            ex.printStackTrace();
//        }
//    }

    private void loadBonosDisponibles() {
        try {
            List<Bonos> bonos = bonoDAO.getAll();
            updateBonosTableModel(bonos);
        } catch (SQLException ex) {
            showError("Error al cargar bonos: " + ex.getMessage());
        }
    }

    private void updateEmpleadosTableModel(List<Empleado> empleados) {
        DefaultTableModel model = new DefaultTableModel(
                new Object[]{"ID", "Nombre", "Apellido", "Puesto", "Bonos Asignados"},
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        for (Empleado empleado : empleados) {
            try {
                int bonosAsignados = asignacionBonoDAO.getByEmpleadoId(empleado.getId()).size();
                model.addRow(new Object[]{
                        empleado.getId(),
                        empleado.getNombre(),
                        empleado.getApellido(),
                        empleado.getPuestoTrabajoId(),
                        bonosAsignados
                });
            } catch (SQLException ex) {
                showError("Error al obtener bonos asignados: " + ex.getMessage());
            }
        }
        empleadosTable.setModel(model);
    }

    private void updateBonosTableModel(List<Bonos> bonos) {
        DefaultTableModel model = new DefaultTableModel(
                new Object[]{"ID", "Nombre", "Valor", "Tipo"},
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        for (Bonos bono : bonos) {
            model.addRow(new Object[]{
                    bono.getId(),
                    bono.getNombreBono(),
                    bono.getValor(),
                    bono.getOperacion() == 1 ? "Fijo" : "Variable"
            });
        }
        bonosTable.setModel(model);
    }

    private void mostrarBonosAsignados() {
        int selectedRow = empleadosTable.getSelectedRow();
        if (selectedRow >= 0) {
            int idEmpleado = (int) empleadosTable.getModel().getValueAt(selectedRow, 0);

            try {
                List<AsignacionBonos> asignaciones = asignacionBonoDAO.getByEmpleadoId(idEmpleado);
                List<Bonos> bonosAsignados = new ArrayList<>();

                for (AsignacionBonos asignacion : asignaciones) {
                    Bonos bono = bonoDAO.getById(asignacion.getBonoId());
                    if (bono != null) {
                        bonosAsignados.add(bono);
                    }
                }

                if (!bonosAsignados.isEmpty()) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("Bonos asignados al empleado:\n\n");

                    for (Bonos bono : bonosAsignados) {
                        sb.append("- ").append(bono.getNombreBono())
                                .append(" ($").append(bono.getValor()).append(")\n");
                    }

                    JOptionPane.showMessageDialog(this, sb.toString(),
                            "Bonos Asignados", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this,
                            "El empleado no tiene bonos asignados",
                            "Información", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (SQLException ex) {
                showError("Error al obtener bonos asignados: " + ex.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(this,
                    "Seleccione un empleado para ver sus bonos asignados",
                    "Advertencia",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    private void asignarBono() {
        int empleadoSelectedRow = empleadosTable.getSelectedRow();
        int bonoSelectedRow = bonosTable.getSelectedRow();

        if (empleadoSelectedRow >= 0 && bonoSelectedRow >= 0) {
            try {
                int idEmpleado = (int) empleadosTable.getModel().getValueAt(empleadoSelectedRow, 0);
                int idBono = (int) bonosTable.getModel().getValueAt(bonoSelectedRow, 0);

                // Verificar si ya está asignado
                boolean yaAsignado = false;
                List<AsignacionBonos> asignaciones = asignacionBonoDAO.getByEmpleadoId(idEmpleado);
                for (AsignacionBonos asignacion : asignaciones) {
                    if (asignacion.getBonoId() == idBono && asignacion.getEstado() == 1) {
                        yaAsignado = true;
                        break;
                    }
                }

                if (!yaAsignado) {
                    AsignacionBonos nuevaAsignacion = new AsignacionBonos(0, idEmpleado, idBono, (byte)1);
                    AsignacionBonos asignacionCreada = asignacionBonoDAO.create(nuevaAsignacion);

                    if (asignacionCreada != null) {
                        JOptionPane.showMessageDialog(this,
                                "Bono asignado exitosamente",
                                "Éxito",
                                JOptionPane.INFORMATION_MESSAGE);
                        //loadEmpleados();
                    }
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Este bono ya está asignado al empleado",
                            "Advertencia",
                            JOptionPane.WARNING_MESSAGE);
                }
            } catch (SQLException ex) {
                showError("Error al asignar bono: " + ex.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(this,
                    "Seleccione un empleado y un bono para asignar",
                    "Advertencia",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    private void desasignarBono() {
        int empleadoSelectedRow = empleadosTable.getSelectedRow();

        if (empleadoSelectedRow >= 0) {
            try {
                int idEmpleado = (int) empleadosTable.getModel().getValueAt(empleadoSelectedRow, 0);
                List<AsignacionBonos> asignaciones = asignacionBonoDAO.getByEmpleadoId(idEmpleado);

                if (!asignaciones.isEmpty()) {
                    // Crear lista de nombres de bonos para el diálogo de selección
                    String[] opcionesBonos = new String[asignaciones.size()];
                    for (int i = 0; i < asignaciones.size(); i++) {
                        Bonos bono = bonoDAO.getById(asignaciones.get(i).getBonoId());
                        opcionesBonos[i] = bono.getNombreBono() + " ($" + bono.getValor() + ")";
                    }

                    String bonoSeleccionado = (String) JOptionPane.showInputDialog(
                            this,
                            "Seleccione el bono a desasignar:",
                            "Desasignar Bono",
                            JOptionPane.QUESTION_MESSAGE,
                            null,
                            opcionesBonos,
                            opcionesBonos[0]);

                    if (bonoSeleccionado != null) {
                        int index = -1;
                        for (int i = 0; i < opcionesBonos.length; i++) {
                            if (opcionesBonos[i].equals(bonoSeleccionado)) {
                                index = i;
                                break;
                            }
                        }

                        if (index >= 0) {
                            AsignacionBonos asignacion = asignaciones.get(index);
                            asignacion.setEstado((byte)0); // Desactivar la asignación

                            boolean actualizado = asignacionBonoDAO.update(asignacion);
                            if (actualizado) {
                                JOptionPane.showMessageDialog(this,
                                        "Bono desasignado exitosamente",
                                        "Éxito",
                                        JOptionPane.INFORMATION_MESSAGE);
                                //loadEmpleados();
                            }
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(this,
                            "El empleado no tiene bonos asignados",
                            "Información",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (SQLException ex) {
                showError("Error al desasignar bono: " + ex.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(this,
                    "Seleccione un empleado para desasignar bonos",
                    "Advertencia",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this,
                message,
                "Error",
                JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AsignacionBonosForm().setVisible(true));
    }


}