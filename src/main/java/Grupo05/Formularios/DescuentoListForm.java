package Grupo05.Formularios;

import Grupo05.Persistencia.DescuentoDAO;
import Grupo05.Utils.CUD;
import Grupo05.dominio.Descuentos;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;

public class DescuentoListForm extends JFrame {
    private JTable table;
    private JButton btnRefresh;
    private JButton btnNew;
    private JButton btnEdit;
    private JButton btnDelete;
    private JButton btnSearch;
    private JTextField txtSearch;
    private DescuentoDAO descuentoDAO;

    public DescuentoListForm() {
        descuentoDAO = new DescuentoDAO();
        initComponents();
        setTitle("Gestión de Descuentos");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        loadDescuentos();
    }

    private void initComponents() {
        JPanel panel = new JPanel(new BorderLayout());

        // Panel de búsqueda
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        txtSearch = new JTextField(20);
        btnSearch = new JButton("Buscar");

        btnSearch.addActionListener(e -> searchDescuentos());
        txtSearch.addActionListener(e -> searchDescuentos());

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

        btnNew.setBackground(new Color(34, 139, 34));
        btnNew.setForeground(Color.WHITE);
        btnEdit.setBackground(new Color(153, 102, 255));
        btnEdit.setForeground(Color.WHITE);
        btnRefresh.setBackground(new Color(0, 102, 204));
        btnRefresh.setForeground(Color.WHITE);
        btnDelete.setBackground(new Color(178, 34, 34));
        btnDelete.setForeground(Color.WHITE);
        btnSearch.setForeground(Color.WHITE);
        btnSearch.setBackground(new Color(255, 153, 0));

        // Configurar acciones
        btnNew.addActionListener(e -> new CrearDescuento(CUD.CREATE).setVisible(true));
        btnEdit.addActionListener(e -> editarDescuento());
        btnDelete.addActionListener(e -> eliminarDescuento());
        btnRefresh.addActionListener(e -> loadDescuentos());

        buttonPanel.add(btnNew);
        buttonPanel.add(btnEdit);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnRefresh);

        panel.add(buttonPanel, BorderLayout.SOUTH);
        add(panel);
    }

    private void loadDescuentos() {
        try {
            ArrayList<Descuentos> descuentos = descuentoDAO.getAll();
            updateTableModel(descuentos);
        } catch (SQLException ex) {
            showError("Error al cargar descuentos: " + ex.getMessage());
        }
    }

    private void searchDescuentos() {
        String searchText = txtSearch.getText().trim();
        if (!searchText.isEmpty()) {
            try {
                ArrayList<Descuentos> descuentos = descuentoDAO.search(searchText);
                updateTableModel(descuentos);
            } catch (SQLException ex) {
                showError("Error al buscar descuentos: " + ex.getMessage());
            }
        } else {
            loadDescuentos();
        }
    }

    private void updateTableModel(ArrayList<Descuentos> descuentos) {
        DefaultTableModel model = new DefaultTableModel(
                new Object[]{"ID", "Nombre", "Valor", "Estado", "Operación"},
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        for (Descuentos descuento : descuentos) {
            model.addRow(new Object[]{
                    descuento.getId(),
                    descuento.getNombre(),
                    String.format("%,.2f", descuento.getValor()),
                    descuento.getEstado() == 1 ? "Activo" : "Inactivo",
                    descuento.getOperacion() == 1 ? "Fija" : "No fija"
            });
        }

        table.setModel(model);
    }

    private void editarDescuento() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            int id = (int) table.getModel().getValueAt(selectedRow, 0);
            try {
                Descuentos descuento = descuentoDAO.getById(id);
                if (descuento != null) {
                    new CrearDescuento(CUD.UPDATE, descuento).setVisible(true);
                }
            } catch (SQLException ex) {
                showError("Error al obtener descuento: " + ex.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(this,
                    "Seleccione un descuento para editar",
                    "Advertencia",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    private void eliminarDescuento() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            int id = (int) table.getModel().getValueAt(selectedRow, 0);
            try {
                Descuentos descuento = descuentoDAO.getById(id);
                if (descuento != null) {
                    int confirm = JOptionPane.showConfirmDialog(
                            this,
                            "¿Está seguro que desea eliminar este descuento?",
                            "Confirmar eliminación",
                            JOptionPane.YES_NO_OPTION);

                    if (confirm == JOptionPane.YES_OPTION) {
                        boolean deleted = descuentoDAO.delete(id);
                        if (deleted) {
                            JOptionPane.showMessageDialog(this,
                                    "Descuento eliminado exitosamente",
                                    "Éxito",
                                    JOptionPane.INFORMATION_MESSAGE);
                            loadDescuentos();
                        } else {
                            showError("No se pudo eliminar el descuento");
                        }
                    }
                }
            } catch (SQLException ex) {
                showError("Error al eliminar descuento: " + ex.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(this,
                    "Seleccione un descuento para eliminar",
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
        SwingUtilities.invokeLater(() -> new DescuentoListForm().setVisible(true));
    }
}