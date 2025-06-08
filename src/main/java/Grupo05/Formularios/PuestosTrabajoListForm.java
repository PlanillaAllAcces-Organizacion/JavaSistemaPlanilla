package Grupo05.Formularios;
import Grupo05.Persistencia.PuestoTrabajoDAO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;
import java.util.List;

public class PuestosTrabajoListForm extends JFrame {
    private JTable tblPuestos;
    private JButton btnNuevo;
    private JButton btnEditar;
    private JButton btnEliminar;
    private JButton btnActualizar;
    private JPanel mainPanel;
    private PuestoTrabajoDAO puestoTrabajoDAO;

    public PuestosTrabajoListForm() {
        super("Gestión de Puestos de Trabajo");

        puestoTrabajoDAO = new PuestoTrabajoDAO();

        initializeComponents();
        setupMainPanel();
        setupListeners();

        loadPuestosData();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
    }

    private void initializeComponents() {
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Tabla
        tblPuestos = new JTable();
        JScrollPane scrollPane = new JScrollPane(tblPuestos);

        // Botones
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        btnNuevo = new JButton("Nuevo");
        btnEditar = new JButton("Editar");
        btnEliminar = new JButton("Eliminar");
        btnActualizar = new JButton("Actualizar");

        buttonPanel.add(btnNuevo);
        buttonPanel.add(btnEditar);
        buttonPanel.add(btnEliminar);
        buttonPanel.add(btnActualizar);

        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
    }

    private void setupMainPanel() {
        add(mainPanel);
    }

    private void setupListeners() {
        btnNuevo.addActionListener(e -> abrirFormularioCreacion());
        btnEditar.addActionListener(e -> editarPuestoSeleccionado());
        btnEliminar.addActionListener(e -> eliminarPuestoSeleccionado());
        btnActualizar.addActionListener(e -> loadPuestosData());
    }

    private void loadPuestosData() {
        try {
            List<Grupo05.dominio.PuestoTrabajo> puestos = puestoTrabajoDAO.getAllActive();

            String[] columnNames = {"ID", "Nombre", "Salario Base", "Valor x Hora", "Valor Extra", "Estado"};
            DefaultTableModel model = new DefaultTableModel(columnNames, 0);

            for (Grupo05.dominio.PuestoTrabajo puesto : puestos) {
                Object[] row = {
                        puesto.getId(),
                        puesto.getNombrePuesto(),
                        puesto.getSalarioBase(),
                        puesto.getValorxHora(),
                        puesto.getValorExtra(),
                        puesto.getStrEstado()
                };
                model.addRow(row);
            }

            tblPuestos.setModel(model);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error al cargar puestos: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void abrirFormularioCreacion() {
        // CORRECCIÓN: Instancia la clase JDialog PuestoTrabajo (Grupo05.Formularios.PuestoTrabajo)
        // en lugar de la clase de dominio.
        // Como PuestosTrabajoListForm está en el mismo paquete que PuestoTrabajo (el JDialog),
        // no necesitas calificarlo completamente aquí, pero es una buena práctica.
        Grupo05.Formularios.PuestoTrabajo formularioDialogo = new Grupo05.Formularios.PuestoTrabajo(this);

        formularioDialogo.setVisible(true);

        // Agregar el WindowListener para actualizar la lista al cerrar
        formularioDialogo.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                loadPuestosData(); // Actualizar la lista después de cerrar el formulario
            }
        });
    }

    private void editarPuestoSeleccionado() {
        int selectedRow = tblPuestos.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Por favor seleccione un puesto para editar",
                    "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int idPuesto = (int) tblPuestos.getValueAt(selectedRow, 0);
            Grupo05.dominio.PuestoTrabajo puestoDominio = puestoTrabajoDAO.getById(idPuesto);

            if (puestoDominio != null) {
                Grupo05.Formularios.PuestoTrabajo formularioDialogo = new Grupo05.Formularios.PuestoTrabajo(this, puestoDominio);
                formularioDialogo.setVisible(true);
                formularioDialogo.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosed(WindowEvent windowEvent) {
                        loadPuestosData();
                    }
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error al obtener el puesto: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarPuestoSeleccionado() {
        int selectedRow = tblPuestos.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Por favor seleccione un puesto para eliminar",
                    "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirmacion = JOptionPane.showConfirmDialog(this,
                "¿Está seguro que desea eliminar este puesto?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION);

        if (confirmacion == JOptionPane.YES_OPTION) {
            try {
                int idPuesto = (int) tblPuestos.getValueAt(selectedRow, 0);
                boolean eliminado = puestoTrabajoDAO.delete(idPuesto);

                if (eliminado) {
                    JOptionPane.showMessageDialog(this,
                            "Puesto eliminado exitosamente",
                            "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    loadPuestosData();
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this,
                        "Error al eliminar el puesto: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            PuestosTrabajoListForm form = new PuestosTrabajoListForm();
            form.setVisible(true);
        });
    }
}