package Grupo05.Formularios;

import Grupo05.Persistencia.TipoHorarioDAO;
import Grupo05.Utils.CUD;
import Grupo05.dominio.Horario;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.util.List;

public class TipoDeHorarioListForm extends JFrame {
    private JTable tblHorarios;
    private JButton btnNuevo;
    private JButton btnEditar;
    private JButton btnEliminar;
    private JButton btnActualizar;
    private JButton btnBuscar;
    private JTextField txtBusqueda;
    private JPanel mainPanel;
    private JPanel panelBusqueda;
    private TipoHorarioDAO horarioDAO;

    public TipoDeHorarioListForm() {
        super("Gestión de Tipos de Horario");
        initialize();
    }

    private void initialize() {
        horarioDAO = new TipoHorarioDAO();
        setupUIComponents();
        setupLayout();
        setupEventHandlers();
        loadHorariosData();

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void setupUIComponents() {
        tblHorarios = new JTable();
        tblHorarios.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        btnActualizar = createStyledButton("Nuevo", new Color(0, 102, 204));
        btnNuevo = createStyledButton("Editar", new Color(0, 153, 51));
        btnEliminar = createStyledButton("Eliminar", new Color(204, 0, 0));
        btnEditar = createStyledButton("Actualizar", new Color(153, 102, 255));
        btnBuscar = createStyledButton("Buscar", new Color(255, 153, 0));

        txtBusqueda = new JTextField(20);

        mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panelBusqueda = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panelBusqueda.add(new JLabel("Buscar:"));
        panelBusqueda.add(txtBusqueda);
        panelBusqueda.add(btnBuscar);
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        return button;
    }

    private void setupLayout() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.add(btnNuevo);
        buttonPanel.add(btnEditar);
        buttonPanel.add(btnEliminar);
        buttonPanel.add(btnActualizar);

        mainPanel.add(panelBusqueda, BorderLayout.NORTH);
        mainPanel.add(new JScrollPane(tblHorarios), BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
    }

    private void setupEventHandlers() {
        // Uso explícito del enum CUD
        btnNuevo.addActionListener(e -> abrirFormularioHorario(CUD.CREATE, null));
        btnEditar.addActionListener(e -> abrirFormularioHorario(CUD.UPDATE, obtenerHorarioSeleccionado()));
        btnEliminar.addActionListener(e -> eliminarHorario(CUD.DELETE, obtenerHorarioSeleccionado()));
        btnActualizar.addActionListener(e -> loadHorariosData());
        btnBuscar.addActionListener(e -> searchHorarios());

        tblHorarios.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    abrirFormularioHorario(CUD.UPDATE, obtenerHorarioSeleccionado());
                }
            }
        });
    }

    private Horario obtenerHorarioSeleccionado() {
        int selectedRow = tblHorarios.getSelectedRow();
        if (selectedRow == -1) {
            showWarning("Por favor seleccione un horario");
            return null;
        }

        try {
            int id = (int) tblHorarios.getValueAt(selectedRow, 0);
            return horarioDAO.getById(id);
        } catch (SQLException ex) {
            showError("Error al obtener horario: " + ex.getMessage());
            return null;
        }
    }

    private void abrirFormularioHorario(CUD operacion, Horario horario) {
        if (operacion != CUD.CREATE && horario == null) {
            return; // Validación adicional
        }

        SwingUtilities.invokeLater(() -> {
            CrearHorario form = new CrearHorario(this, horario, operacion);
            form.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    loadHorariosData();
                }
            });
            form.setVisible(true);
        });
    }

    private void eliminarHorario(CUD operacion, Horario horario) {
        if (horario == null) return;
        if (operacion != CUD.DELETE) return;

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "¿Está seguro de eliminar el horario: " + horario.getNombreHorario() + "?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                boolean success = horarioDAO.delete(horario.getId());
                if (success) {
                    showInfo("Horario eliminado exitosamente");
                    loadHorariosData();
                } else {
                    showError("No se pudo eliminar el horario");
                }
            } catch (SQLException ex) {
                showError("Error al eliminar horario: " + ex.getMessage());
            }
        }
    }

    private void loadHorariosData() {
        try {
            updateTable(horarioDAO.getAll());
        } catch (SQLException ex) {
            showError("Error al cargar horarios: " + ex.getMessage());
        }
    }

    private void searchHorarios() {
        try {
            String searchText = txtBusqueda.getText().trim();
            List<Horario> horarios = searchText.isEmpty() ?
                    horarioDAO.getAll() :
                    horarioDAO.search(searchText);
            updateTable(horarios);
        } catch (SQLException ex) {
            showError("Error al buscar horarios: " + ex.getMessage());
        }
    }

    private void updateTable(List<Horario> horarios) {
        DefaultTableModel model = new DefaultTableModel(
                new String[]{"ID", "Nombre del Horario"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        for (Horario horario : horarios) {
            model.addRow(new Object[]{horario.getId(), horario.getNombreHorario()});
        }

        tblHorarios.setModel(model);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showWarning(String message) {
        JOptionPane.showMessageDialog(this, message, "Advertencia", JOptionPane.WARNING_MESSAGE);
    }

    private void showInfo(String message) {
        JOptionPane.showMessageDialog(this, message, "Información", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TipoDeHorarioListForm());
    }
}