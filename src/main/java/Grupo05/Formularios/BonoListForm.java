package Grupo05.Formularios;

import Grupo05.Persistencia.BonoDAO;
import Grupo05.Utils.CUD;
import Grupo05.dominio.Bonos;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;

public class BonoListForm extends JFrame {
    private JTable table;
    private JButton btnRefresh;
    private JButton btnNew;
    private JButton btnEdit;
    private JButton btnDelete;
    private JButton btnSearch;
    private JTextField txtSearch;
    private BonoDAO bonoDAO;
    private JPanel mainPanel;

    public BonoListForm() {
        bonoDAO = new BonoDAO();
        initComponents();
        setTitle("Gestión de Bonos");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        loadBonos();
    }

    private void initComponents() {
        JPanel panel = new JPanel(new BorderLayout());

        // Panel de búsqueda
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        txtSearch = new JTextField(20);
        btnSearch = new JButton("Buscar");

        btnSearch.addActionListener(e -> searchBonos());
        txtSearch.addActionListener(e -> searchBonos());

        searchPanel.add(new JLabel("Buscar:"));
        searchPanel.add(txtSearch);
        searchPanel.add(btnSearch);
        panel.add(searchPanel, BorderLayout.NORTH);

        // Tabla
        table = new JTable();
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Panel de botones
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnNew = new JButton("Nuevo");
        btnEdit = new JButton("Editar");
        btnDelete = new JButton("Eliminar");
        btnRefresh = new JButton("Actualizar");

        // Configurar acciones
        btnNew.addActionListener(e -> new CrearBono(CUD.CREATE).setVisible(true));
        btnEdit.addActionListener(e -> editarBono());
        btnDelete.addActionListener(e -> eliminarBono());
        btnRefresh.addActionListener(e -> loadBonos());

        buttonPanel.add(btnNew);
        buttonPanel.add(btnEdit);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnRefresh);

        panel.add(buttonPanel, BorderLayout.SOUTH);
        add(panel);
    }

    private void loadBonos() {
        try {
            ArrayList<Bonos> bonos = bonoDAO.getAll();
            updateTableModel(bonos);
        } catch (SQLException ex) {
            showError("Error al cargar bonos: " + ex.getMessage());
        }
    }

    private void searchBonos() {
        String searchText = txtSearch.getText().trim();
        if (!searchText.isEmpty()) {
            try {
                ArrayList<Bonos> bonos = bonoDAO.search(searchText);
                updateTableModel(bonos);
            } catch (SQLException ex) {
                showError("Error al buscar bonos: " + ex.getMessage());
            }
        } else {
            loadBonos();
        }
    }

    private void updateTableModel(ArrayList<Bonos> bonos) {
        DefaultTableModel model = new DefaultTableModel(
                new Object[]{"ID", "Nombre", "Valor", "Estado", "Operación", "Planilla"},
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
                    bono.getEstado() == 1 ? "Activo" : "Inactivo",
                    bono.getOperacion() == 1 ? "Fijo" : "No Fijo",
                    bono.getPlanilla() == 1 ? "Mensual" : "Quincenal"
            });
        }

        table.setModel(model);
    }

    private void editarBono() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            int id = (int) table.getModel().getValueAt(selectedRow, 0);
            try {
                Bonos bono = bonoDAO.getById(id);
                if (bono != null) {
                    new CrearBono(CUD.UPDATE, bono).setVisible(true);
                }
            } catch (SQLException ex) {
                showError("Error al obtener bono: " + ex.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(this,
                    "Seleccione un bono para editar",
                    "Advertencia",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    private void eliminarBono() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            int id = (int) table.getModel().getValueAt(selectedRow, 0);
            try {
                Bonos bono = bonoDAO.getById(id);
                if (bono != null) {
                    int confirm = JOptionPane.showConfirmDialog(
                            this,
                            "¿Está seguro que desea eliminar este bono?",
                            "Confirmar eliminación",
                            JOptionPane.YES_NO_OPTION);

                    if (confirm == JOptionPane.YES_OPTION) {
                        boolean deleted = bonoDAO.delete(id);
                        if (deleted) {
                            JOptionPane.showMessageDialog(this,
                                    "Bono eliminado exitosamente",
                                    "Éxito",
                                    JOptionPane.INFORMATION_MESSAGE);
                            loadBonos();
                        } else {
                            showError("No se pudo eliminar el bono");
                        }
                    }
                }
            } catch (SQLException ex) {
                showError("Error al eliminar bono: " + ex.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(this,
                    "Seleccione un bono para eliminar",
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
        SwingUtilities.invokeLater(() -> new BonoListForm().setVisible(true));
    }
}