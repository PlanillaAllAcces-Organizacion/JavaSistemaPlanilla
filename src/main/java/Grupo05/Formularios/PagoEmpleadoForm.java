package Grupo05.Formularios;

import Grupo05.Persistencia.PagoEmpleadoDAO;
import Grupo05.Persistencia.EmpleadoDAO;
import Grupo05.Persistencia.PuestoTrabajoDAO;
import Grupo05.dominio.*;
import Grupo05.Utils.CUD;
import Grupo05.dominio.PuestoTrabajo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PagoEmpleadoForm extends JDialog {

    private PagoEmpleadoDAO pagoEmpleadoDAO;
    private EmpleadoDAO empleadoDAO;
    private PuestoTrabajoDAO puestoTrabajoDAO;

    private JTextField txtNombreEmpleado;
    private JTextField txtApellidoEmpleado;
    private JTextField txtSalarioBase;
    private JTextField txtValorHoraPuesto;
    private JTextField txtHorasTrabajadas;
    private JTextField txtBonos;
    private JTextField txtDescuentos;
    private JTextField txtPagoTotal;
    private JComboBox<Empleado> cbxEmpleados;
    private JButton btnGuardarPago;
    private JButton btnCalcularPago;
    private JButton btnLimpiar;
    private JLabel lblBuscarEmpleado;
    private JLabel lblNombre;
    private JLabel lblApellido;
    private JLabel lblBase;
    private JLabel lblXHora;
    private JLabel lblBono;
    private JLabel lblDescuento;
    private JLabel lblPago;
    private JLabel lblFecha;
    private JLabel lblFechaPago;
    private JLabel lblHorasTrabajadas;

    private Empleado empleadoSeleccionado;
    private PuestoTrabajo puestoEmpleadoSeleccionado;

    private CUD currentCUDMode;
    private PagoEmpleado pagoActual;

    public PagoEmpleadoForm(JFrame parent, CUD mode, PagoEmpleado pago) {
        super(parent, true);
        this.currentCUDMode = mode;
        this.pagoActual = pago;

        pagoEmpleadoDAO = new PagoEmpleadoDAO();
        empleadoDAO = new EmpleadoDAO();
        puestoTrabajoDAO = new PuestoTrabajoDAO();

        initComponents();
        loadEmpleados();
        cbxEmpleados.setSelectedIndex(0);

        setupFormForCUDMode();
        pack();
        setLocationRelativeTo(parent);
    }

    public PagoEmpleadoForm() {
        this(null, CUD.CREATE, new PagoEmpleado());
    }

    private void initComponents() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));


        JPanel topPanel = new JPanel(new GridLayout(6, 2, 5, 5));
        topPanel.setBorder(BorderFactory.createTitledBorder("Información del Empleado y Puesto"));
        topPanel.add(lblBuscarEmpleado);
        topPanel.add(cbxEmpleados);
        topPanel.add(lblNombre);
        txtNombreEmpleado.setEditable(false);
        topPanel.add(txtNombreEmpleado);
        topPanel.add(lblApellido);
        txtApellidoEmpleado.setEditable(false);
        topPanel.add(txtApellidoEmpleado);
        topPanel.add(lblBase);
        txtSalarioBase.setEditable(false);
        topPanel.add(txtSalarioBase);
        topPanel.add(lblXHora);
        txtValorHoraPuesto.setEditable(false);
        topPanel.add(txtValorHoraPuesto);
        topPanel.add(new JLabel(""));
        topPanel.add(new JLabel(""));
        add(topPanel, BorderLayout.NORTH);


        JPanel centerPanel = new JPanel(new GridLayout(6, 2, 5, 5));
        centerPanel.setBorder(BorderFactory.createTitledBorder("Detalles del Pago"));

        centerPanel.add(lblHorasTrabajadas);
        centerPanel.add(txtHorasTrabajadas);

        centerPanel.add(lblBono);
        txtBonos.setEditable(false);
        centerPanel.add(txtBonos);

        centerPanel.add(lblDescuento);
        txtDescuentos.setEditable(false);
        centerPanel.add(txtDescuentos);

        centerPanel.add(lblPago);
        txtPagoTotal.setEditable(false);
        centerPanel.add(txtPagoTotal);

        centerPanel.add(lblFecha);
        centerPanel.add(lblFechaPago);

        centerPanel.add(btnCalcularPago);
        centerPanel.add(new JLabel(""));
        add(centerPanel, BorderLayout.CENTER);

        // --- Panel Inferior: Botones de Acción ---
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        bottomPanel.add(btnGuardarPago);
        bottomPanel.add(btnLimpiar);
        add(bottomPanel, BorderLayout.SOUTH);


        cbxEmpleados.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    empleadoSeleccionado = (Empleado) cbxEmpleados.getSelectedItem();
                    if (empleadoSeleccionado != null && empleadoSeleccionado.getId() != 0) {
                        try {

                            puestoEmpleadoSeleccionado = puestoTrabajoDAO.getById(empleadoSeleccionado.getPuestoTrabajoId());
                            displayEmpleadoInfo(empleadoSeleccionado, puestoEmpleadoSeleccionado);
                        } catch (SQLException ex) {
                            JOptionPane.showMessageDialog(PagoEmpleadoForm.this, "Error al cargar datos del puesto: " + ex.getMessage(), "Error de Base de Datos", JOptionPane.ERROR_MESSAGE);
                            ex.printStackTrace();
                            clearEmpleadoInfo();
                        }
                    } else {
                        clearEmpleadoInfo();
                        puestoEmpleadoSeleccionado = null;
                        clearPaymentFields();
                    }
                }
            }
        });

        btnCalcularPago.addActionListener(s -> calcularPago());
        btnGuardarPago.addActionListener(s -> guardarActualizarEliminarPago());
        btnLimpiar.addActionListener(s -> clearForm());

        cbxEmpleados.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value == null) {
                    label.setText("Seleccione un Empleado");
                } else {
                    label.setText(value.toString());
                    label.setForeground(list.getForeground());
                }
                return label;
            }
        });
    }

    private void setupFormForCUDMode() {
        switch (currentCUDMode) {
            case CREATE:
                setTitle("Registrar Nuevo Pago");
                btnGuardarPago.setText("Guardar Pago");
                btnLimpiar.setVisible(true);
                cbxEmpleados.setEnabled(true);
                lblFechaPago.setText("Se asignará automáticamente al guardar");
                break;
            case UPDATE:
                setTitle("Editar Pago");
                btnGuardarPago.setText("Actualizar Pago");
                btnLimpiar.setVisible(false);
                cbxEmpleados.setEnabled(false);
                loadPagoData(pagoActual);
                break;
            case DELETE:
                setTitle("Eliminar Pago");
                btnGuardarPago.setText("Confirmar Eliminación");
                btnLimpiar.setVisible(false);
                cbxEmpleados.setEnabled(false);
                setFormEnabled(false);
                loadPagoData(pagoActual);
                break;
        }
    }

    private void setFormEnabled(boolean enabled) {
        txtHorasTrabajadas.setEditable(enabled);
        btnCalcularPago.setEnabled(enabled);
    }

    private void loadEmpleados() {
        try {
            // ¡CORRECCIÓN! Usar EmpleadoDAO para obtener empleados
            List<Empleado> empleados = empleadoDAO.getAllActive();
            cbxEmpleados.removeAllItems();
            cbxEmpleados.addItem(null);
            for (Empleado emp : empleados) {
                cbxEmpleados.addItem(emp);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar empleados: " + ex.getMessage(), "Error de Base de Datos", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void loadPagoData(PagoEmpleado pago) {
        for (int i = 0; i < cbxEmpleados.getItemCount(); i++) {
            Empleado item = cbxEmpleados.getItemAt(i);
            if (item != null && item.getId() == pago.getEmpleadoId()) {
                cbxEmpleados.setSelectedItem(item);
                break;
            }
        }

        if (empleadoSeleccionado != null && empleadoSeleccionado.getId() != 0) {
            try {
                // ¡CORRECCIÓN! Obtener PuestoTrabajo usando PuestoTrabajoDAO
                puestoEmpleadoSeleccionado = puestoTrabajoDAO.getById(empleadoSeleccionado.getPuestoTrabajoId());
                displayEmpleadoInfo(empleadoSeleccionado, puestoEmpleadoSeleccionado);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error al cargar datos del puesto para edición: " + ex.getMessage(), "Error de Base de Datos", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }

        txtHorasTrabajadas.setText(String.valueOf(pago.getHorasTrabajadas()));
        txtBonos.setText(pago.getTotalBonosAplicados().toPlainString());
        txtDescuentos.setText(pago.getTotalDescuentosAplicados().toPlainString());
        txtPagoTotal.setText(String.format("%.2f", pago.getTotalPago()));
        lblFechaPago.setText(pago.getFechaPago().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
    }

    private void displayEmpleadoInfo(Empleado empleado, PuestoTrabajo puesto) {
        txtNombreEmpleado.setText(empleado.getNombre());
        txtApellidoEmpleado.setText(empleado.getApellido());
        if (puesto != null) {
            txtSalarioBase.setText(String.format("%.2f", puesto.getSalarioBase()));
            txtValorHoraPuesto.setText(String.format("%.2f", puesto.getValorxHora()));
        } else {
            txtSalarioBase.setText("N/A");
            txtValorHoraPuesto.setText("N/A");
        }
        clearPaymentFields();
    }

    private void clearEmpleadoInfo() {
        txtNombreEmpleado.setText("");
        txtApellidoEmpleado.setText("");
        txtSalarioBase.setText("");
        txtValorHoraPuesto.setText("");
        clearPaymentFields();
    }

    private void clearPaymentFields() {
        txtHorasTrabajadas.setText("");
        txtBonos.setText("");
        txtDescuentos.setText("");
        txtPagoTotal.setText("");
        lblFechaPago.setText("Se asignará automáticamente al guardar");
    }

    private void clearForm() {
        cbxEmpleados.setSelectedIndex(0);
        clearEmpleadoInfo();
    }

    private void calcularPago() {
        if (empleadoSeleccionado == null || empleadoSeleccionado.getId() == 0 || puestoEmpleadoSeleccionado == null) {
            JOptionPane.showMessageDialog(this,
                    "Por favor, seleccione un empleado válido",
                    "Error de Cálculo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (txtHorasTrabajadas.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Ingrese las horas trabajadas.",
                    "Error de Cálculo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int horasTrabajadas = Integer.parseInt(txtHorasTrabajadas.getText());
            if (horasTrabajadas < 0) {
                JOptionPane.showMessageDialog(this,
                        "Las horas trabajadas no pueden ser negativas.",
                        "Error de Cálculo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Obtener valor por hora (ya es BigDecimal)
            BigDecimal valorHora = puestoEmpleadoSeleccionado.getValorxHora();

            // Calcular pago bruto (horas * valor hora)
            BigDecimal pagoBruto = valorHora.multiply(new BigDecimal(horasTrabajadas));

            // Calcular total de bonos (fijos + porcentuales)
            BigDecimal totalBonos = pagoEmpleadoDAO.calcularTotalBonosParaEmpleado(
                    empleadoSeleccionado.getId(),
                    pagoBruto
            );

            // Calcular total de descuentos (fijos + porcentuales)
            BigDecimal totalDescuentos = pagoEmpleadoDAO.calcularTotalDescuentosParaEmpleado(
                    empleadoSeleccionado.getId(),
                    pagoBruto
            );

            // Calcular pago neto (pago bruto + bonos - descuentos)
            BigDecimal pagoNeto = pagoBruto
                    .add(totalBonos)
                    .subtract(totalDescuentos);

            // Mostrar resultados
            txtBonos.setText(totalBonos.setScale(2, BigDecimal.ROUND_HALF_UP).toString());
            txtDescuentos.setText(totalDescuentos.setScale(2, BigDecimal.ROUND_HALF_UP).toString());
            txtPagoTotal.setText(pagoNeto.setScale(2, BigDecimal.ROUND_HALF_UP).toString());

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Ingrese un número válido para las horas trabajadas.",
                    "Error de Entrada", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error de base de datos al calcular bonos/descuentos: " + ex.getMessage(),
                    "Error de DB", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error inesperado: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
    private void guardarActualizarEliminarPago() {
        if (currentCUDMode == CUD.DELETE) {
            performDelete();
        } else {
            performSaveOrUpdate();
        }
    }

    private void performSaveOrUpdate() {
        if (empleadoSeleccionado == null || empleadoSeleccionado.getId() == 0 || puestoEmpleadoSeleccionado == null) {
            JOptionPane.showMessageDialog(this, "Por favor, seleccione un empleado válido y asegúrese de que su puesto de trabajo está cargado.", "Error de Selección", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (txtPagoTotal.getText().isEmpty() || txtHorasTrabajadas.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Calcule el pago y asegúrese de que las horas trabajadas estén ingresadas antes de guardar.", "Falta Cálculo/Datos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int horasTrabajadas = Integer.parseInt(txtHorasTrabajadas.getText());
            if (horasTrabajadas < 0) {
                JOptionPane.showMessageDialog(this, "Las horas trabajadas no pueden ser negativas.", "Error de Entrada", JOptionPane.WARNING_MESSAGE);
                return;
            }

            BigDecimal valorHoraBD = puestoEmpleadoSeleccionado.getValorxHora();
            double valorHora = valorHoraBD.doubleValue();

            double totalPago = Double.parseDouble(txtPagoTotal.getText().replace(',', '.'));
            BigDecimal totalBonosAplicados = new BigDecimal(txtBonos.getText().replace(',', '.'));
            BigDecimal totalDescuentosAplicados = new BigDecimal(txtDescuentos.getText().replace(',', '.'));

            PagoEmpleado pago;
            if (currentCUDMode == CUD.UPDATE) {
                pago = this.pagoActual;
            } else {
                pago = new PagoEmpleado();
            }

            pago.setEmpleadoId(empleadoSeleccionado.getId());
            pago.setFechaPago(LocalDateTime.now());
            pago.setHorasTrabajadas(horasTrabajadas);
            pago.setValorHora(valorHora);
            pago.setTotalPago(totalPago);
            pago.setTotalBonosAplicados(totalBonosAplicados);
            pago.setTotalDescuentosAplicados(totalDescuentosAplicados);

            boolean success;
            if (currentCUDMode == CUD.UPDATE) {
                success = pagoEmpleadoDAO.update(pago);
            } else {
                success = (pagoEmpleadoDAO.create(pago) != null);
            }

            if (success) {
                String message = (currentCUDMode == CUD.UPDATE) ? "Pago actualizado con éxito." : "Pago guardado con éxito.";
                JOptionPane.showMessageDialog(this, message, "Éxito", JOptionPane.INFORMATION_MESSAGE);
                this.dispose();
            } else {
                String message = (currentCUDMode == CUD.UPDATE) ? "Error al actualizar el pago." : "Error al guardar el pago.";
                JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Datos numéricos inválidos. Revise Horas Trabajadas, Bonos, Descuentos o Pago Total.", "Error de Entrada", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error de base de datos al guardar/actualizar pago: " + ex.getMessage(), "Error de DB", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void performDelete() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de que desea eliminar permanentemente el pago con ID: " + pagoActual.getId() + "?",
                "Confirmar Eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                if (pagoEmpleadoDAO.delete(pagoActual.getId())) {
                    JOptionPane.showMessageDialog(this, "Pago eliminado con éxito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    this.dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "No se pudo eliminar el pago.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error de base de datos al eliminar pago: " + ex.getMessage(), "Error de DB", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }


}
