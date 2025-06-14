package Grupo05.Formularios;


import Grupo05.Persistencia.PagoEmpleadoDAO;
import Grupo05.Persistencia.EmpleadoDAO;
import Grupo05.dominio.Empleado;
import Grupo05.dominio.PagoEmpleado;
import Grupo05.Utils.CUD;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PagoEmpleadoReadingForm extends JDialog {
    private JTextField txtFechaInicio;
    private JTextField txtFechaFin;
    private JButton btnBuscar;

    private JButton btnCrear;
    private JButton btnModificar;
    private JButton btnEliminar;
    private JTable tablePagos;
    private JPanel mainPanel;
    private JLabel lbInicioFecha;
    private JLabel lbIFinFecha;

    private PagoEmpleadoDAO pagoEmpleadoDAO;
    private EmpleadoDAO empleadoDAO;
    private JFrame parentFrame;

    private Map<Integer, String> empleadoNamesCache;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public PagoEmpleadoReadingForm(JFrame parentFrame) {
        super(parentFrame, true);
        this.parentFrame = parentFrame;
        pagoEmpleadoDAO = new PagoEmpleadoDAO();
        empleadoDAO = new EmpleadoDAO();
        empleadoNamesCache = new HashMap<>();

        initComponentsManual();

        setTitle("Gestión de Pagos de Empleados");
        pack();
        setLocationRelativeTo(parentFrame);

        btnCrear.addActionListener(s -> {
            PagoEmpleadoForm pagoForm = new PagoEmpleadoForm(this.parentFrame, CUD.CREATE, null); // Ya corregido
            pagoForm.setVisible(true);
            pagoForm.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    loadPagosTable();
                }
            });
        });

        btnModificar.addActionListener(s -> {
            PagoEmpleado pago = getPagoFromTableRow();
            if (pago != null) {
                PagoEmpleadoForm pagoForm = new PagoEmpleadoForm(this.parentFrame, CUD.UPDATE, pago); // Ya corregido
                pagoForm.setVisible(true);
                pagoForm.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosed(WindowEvent e) {
                        loadPagosTable();
                    }
                });
            }
        });

        btnEliminar.addActionListener(s -> {
            PagoEmpleado pago = getPagoFromTableRow();
            if (pago != null) {
                PagoEmpleadoForm pagoForm = new PagoEmpleadoForm(this.parentFrame, CUD.DELETE, pago); // Ya corregido
                pagoForm.setVisible(true);
                pagoForm.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosed(WindowEvent e) {
                        loadPagosTable();
                    }
                });
            }
        });

        btnBuscar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchPagosByDateRange();
            }
        });

        loadPagosTable();
    }

    private void initComponentsManual() {
        mainPanel = new JPanel(new BorderLayout(10, 10));
        setContentPane(mainPanel);

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        topPanel.add(lbInicioFecha);
        topPanel.add(txtFechaInicio);
        topPanel.add(lbIFinFecha);
        topPanel.add(txtFechaFin);
        topPanel.add(btnBuscar);
        mainPanel.add(topPanel, BorderLayout.NORTH);

        tablePagos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        mainPanel.add(new JScrollPane(tablePagos), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        bottomPanel.add(btnCrear);
        bottomPanel.add(btnModificar);
        bottomPanel.add(btnEliminar);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
    }

    private void searchPagosByDateRange() {
        String fechaInicioStr = txtFechaInicio.getText().trim();
        String fechaFinStr = txtFechaFin.getText().trim();

        if (fechaInicioStr.isEmpty() || fechaFinStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, ingrese ambas fechas para la búsqueda.", "Campos Vacíos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            LocalDate fechaInicio = LocalDate.parse(fechaInicioStr, dateFormatter);
            LocalDate fechaFin = LocalDate.parse(fechaFinStr, dateFormatter);

            if (fechaFin.isBefore(fechaInicio)) {
                JOptionPane.showMessageDialog(this, "La fecha fin no puede ser anterior a la fecha inicio.", "Error de Fechas", JOptionPane.WARNING_MESSAGE);
                return;
            }

            tablePagos.setModel(new DefaultTableModel());
            empleadoNamesCache.clear();

            List<PagoEmpleado> pagos = pagoEmpleadoDAO.searchPagosByFechaRango(fechaInicio, fechaFin);

            if (pagos.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No se encontraron pagos en el rango de fechas especificado.", "Búsqueda", JOptionPane.INFORMATION_MESSAGE);
            }
            createTable((ArrayList<PagoEmpleado>) pagos);

        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Formato de fecha inválido. Use YYYY-MM-DD.", "Error de Formato", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al buscar pagos por fecha: " + ex.getMessage(), "Error de Base de Datos", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    public void loadPagosTable() {
        try {
            ArrayList<PagoEmpleado> pagos = (ArrayList<PagoEmpleado>) pagoEmpleadoDAO.getAllPagos();
            createTable(pagos);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al cargar todos los pagos: " + ex.getMessage(),
                    "ERROR", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    public void createTable(ArrayList<PagoEmpleado> pagos) {
        DefaultTableModel model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        model.addColumn("ID Pago");
        model.addColumn("ID Empleado");
        model.addColumn("Empleado");
        model.addColumn("Fecha Pago");
        model.addColumn("Horas");
        model.addColumn("Valor Hora");
        model.addColumn("Total Bonos");
        model.addColumn("Total Descuentos");
        model.addColumn("Pago Total");

        this.tablePagos.setModel(model);

        for (PagoEmpleado pago : pagos) {
            String empleadoNombre = "Cargando...";
            try {
                empleadoNombre = getEmpleadoFullName(pago.getEmpleadoId());
            } catch (SQLException e) {
                empleadoNombre = "Error al cargar";
                e.printStackTrace();
            }

            String fechaFormateada = pago.getFechaPago().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));

            model.addRow(new Object[]{
                    pago.getId(),
                    pago.getEmpleadoId(),
                    empleadoNombre,
                    fechaFormateada,
                    pago.getHorasTrabajadas(),
                    String.format("%.2f", pago.getValorHora()),
                    pago.getTotalBonosAplicados().toPlainString(),
                    pago.getTotalDescuentosAplicados().toPlainString(),
                    String.format("%.2f", pago.getTotalPago())
            });
        }

        hideCol(0);
        hideCol(1);
    }

    private void hideCol(int pColumna) {
        this.tablePagos.getColumnModel().getColumn(pColumna).setMaxWidth(0);
        this.tablePagos.getColumnModel().getColumn(pColumna).setMinWidth(0);
        this.tablePagos.getTableHeader().getColumnModel().getColumn(pColumna).setMaxWidth(0);
        this.tablePagos.getTableHeader().getColumnModel().getColumn(pColumna).setMinWidth(0);
    }

    private PagoEmpleado getPagoFromTableRow() {
        PagoEmpleado pago = null;
        try {
            int filaSelect = this.tablePagos.getSelectedRow();
            if (filaSelect == -1) {
                JOptionPane.showMessageDialog(this,
                        "Selecciona una fila de la tabla.",
                        "Validación", JOptionPane.WARNING_MESSAGE);
                return null;
            }

            int id = (int) this.tablePagos.getValueAt(filaSelect, 0);
            pago = pagoEmpleadoDAO.getPagoById(id);
            if (pago == null) {
                JOptionPane.showMessageDialog(this,
                        "No se encontró ningún pago con el ID seleccionado.",
                        "Validación", JOptionPane.WARNING_MESSAGE);
                return null;
            }
            return pago;
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al obtener el pago seleccionado: " + ex.getMessage(),
                    "ERROR", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
            return null;
        }
    }

    private String getEmpleadoFullName(int empleadoId) throws SQLException {
        if (empleadoNamesCache.containsKey(empleadoId)) {
            return empleadoNamesCache.get(empleadoId);
        } else {

            Empleado empleado = empleadoDAO.getById(empleadoId);
            if (empleado != null) {
                String fullName = empleado.getNombre() + " " + empleado.getApellido();
                empleadoNamesCache.put(empleadoId, fullName);
                return fullName;
            }
            return "Empleado Desconocido";
        }
    }


}
