package Grupo05.Formularios;
import Grupo05.Persistencia.PuestoTrabajoDAO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.sql.SQLException;


public class PuestoTrabajo extends JDialog {
    private JTextField txtNombrePuesto;
    private JTextField txtSalarioBase;
    private JTextField txtValorxHora;
    private JTextField txtValorExtra;
    private JComboBox<String> cmbEstado;
    private JButton btnGuardar;
    private JPanel mainPanel;
    private JButton btnLimpiar;
    private JButton btnCancelar;
    private JLabel lblTitulo;

    private PuestoTrabajoDAO puestoTrabajoDAO;
    private Grupo05.dominio.PuestoTrabajo puestoActual;


    //cambio de prueba
    public PuestoTrabajo(JFrame parent) {
        this(parent, null);
    }

    public PuestoTrabajo(JFrame parent, Grupo05.dominio.PuestoTrabajo puestoEditar) {
        super(parent, puestoEditar == null ? "Nuevo Puesto de Trabajo" : "Editar Puesto de Trabajo", true);
        this.puestoActual = puestoEditar;

        puestoTrabajoDAO = new PuestoTrabajoDAO();

        initializeComponents();
        setupMainPanel();
        setupListeners();

        if (puestoEditar != null) {
            cargarDatosPuesto();
        }

        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setSize(450, 350);
        setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
        setLocationRelativeTo(parent);
    }

    private void initializeComponents() {
        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Configuración de componentes
        lblTitulo = new JLabel(puestoActual == null ? "CREAR NUEVO PUESTO" : "EDITAR PUESTO");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 16));
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        txtNombrePuesto = new JTextField(20);
        txtSalarioBase = new JTextField(20);
        txtValorxHora = new JTextField(20);
        txtValorExtra = new JTextField(20);
        cmbEstado = new JComboBox<>(new String[]{"ACTIVO", "INACTIVO"});

        btnGuardar = new JButton("Guardar");
        btnLimpiar = new JButton("Limpiar");
        btnCancelar = new JButton("Cancelar");

        // Configurar estilos de botones
        btnGuardar.setBackground(new Color(34, 139, 34));
        btnGuardar.setForeground(Color.WHITE);
        btnLimpiar.setBackground(new Color(0, 102, 204));
        btnLimpiar.setForeground(Color.WHITE);
        btnCancelar.setBackground(new Color(178, 34, 34));
        btnCancelar.setForeground(Color.WHITE);
    }

    private void setupMainPanel() {
        // Panel de título
        JPanel panelTitulo = new JPanel();
        panelTitulo.add(lblTitulo);
        mainPanel.add(panelTitulo);

        // Panel de formulario
        JPanel panelFormulario = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // Agregar campos al formulario
        agregarCampo(panelFormulario, gbc, 0, "Nombre del Puesto:", txtNombrePuesto);
        agregarCampo(panelFormulario, gbc, 1, "Salario Base:", txtSalarioBase);
        agregarCampo(panelFormulario, gbc, 2, "Valor por Hora:", txtValorxHora);
        agregarCampo(panelFormulario, gbc, 3, "Valor Hora Extra:", txtValorExtra);
        agregarCampo(panelFormulario, gbc, 4, "Estado:", cmbEstado);

        mainPanel.add(panelFormulario);

        // Panel de botones
        JPanel panelBotones = new JPanel();
        panelBotones.add(btnGuardar);
        panelBotones.add(btnLimpiar);
        panelBotones.add(btnCancelar);
        mainPanel.add(panelBotones);

        add(mainPanel);
    }

    private void agregarCampo(JPanel panel, GridBagConstraints gbc, int fila, String etiqueta, JComponent componente) {
        gbc.gridx = 0;
        gbc.gridy = fila;
        panel.add(new JLabel(etiqueta), gbc);

        gbc.gridx = 1;
        panel.add(componente, gbc);
    }

    private void setupListeners() {
        btnGuardar.addActionListener(e -> guardarPuesto());
        btnCancelar.addActionListener(e -> dispose());
        btnLimpiar.addActionListener(e -> limpiarFormulario());
    }

    private void cargarDatosPuesto() {
        if (puestoActual != null) {
            txtNombrePuesto.setText(puestoActual.getNombrePuesto());
            txtSalarioBase.setText(puestoActual.getSalarioBase().toString());
            txtValorxHora.setText(puestoActual.getValorxHora().toString());
            txtValorExtra.setText(puestoActual.getValorExtra().toString());
            cmbEstado.setSelectedIndex(puestoActual.getEstado() - 1); // 0=ACTIVO, 1=INACTIVO
        }
    }

    private void limpiarFormulario() {
        txtNombrePuesto.setText("");
        txtSalarioBase.setText("");
        txtValorxHora.setText("");
        txtValorExtra.setText("");
        cmbEstado.setSelectedIndex(0);
    }

    private void guardarPuesto() {
        try {
            if (!validarCampos()) {
                return;
            }

            Grupo05.dominio.PuestoTrabajo puesto = new Grupo05.dominio.PuestoTrabajo();

            // Si estamos editando, mantener el ID
            if (puestoActual != null) {
                puesto.setId(puestoActual.getId());
            }

            puesto.setNombrePuesto(txtNombrePuesto.getText().trim());
            puesto.setSalarioBase(new BigDecimal(txtSalarioBase.getText().trim()));
            puesto.setValorxHora(new BigDecimal(txtValorxHora.getText().trim()));
            puesto.setValorExtra(new BigDecimal(txtValorExtra.getText().trim()));
            puesto.setEstado((byte) (cmbEstado.getSelectedIndex() + 1)); // 1=ACTIVO, 2=INACTIVO

            boolean exito;
            String mensaje;

            if (puestoActual == null) {
                // Crear nuevo puesto
                Grupo05.dominio.PuestoTrabajo puestoCreado = puestoTrabajoDAO.create(puesto);
                exito = puestoCreado != null;
                mensaje = exito ? "Puesto creado exitosamente" : "Error al crear el puesto";
            } else {
                // Actualizar puesto existente
                exito = puestoTrabajoDAO.update(puesto);
                mensaje = exito ? "Puesto actualizado exitosamente" : "Error al actualizar el puesto";
            }

            if (exito) {
                JOptionPane.showMessageDialog(this, mensaje, "Éxito", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Los valores numéricos deben ser válidos y positivos",
                    "Error de formato", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error de base de datos: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private boolean validarCampos() {
        if (txtNombrePuesto.getText().trim().isEmpty()) {
            mostrarError("El nombre del puesto es obligatorio", txtNombrePuesto);
            return false;
        }

        try {
            BigDecimal salario = new BigDecimal(txtSalarioBase.getText().trim());
            if (salario.compareTo(BigDecimal.ZERO) <= 0) {
                mostrarError("El salario base debe ser mayor a cero", txtSalarioBase);
                return false;
            }
        } catch (NumberFormatException e) {
            mostrarError("El salario base debe ser un número válido", txtSalarioBase);
            return false;
        }

        try {
            BigDecimal valorHora = new BigDecimal(txtValorxHora.getText().trim());
            if (valorHora.compareTo(BigDecimal.ZERO) <= 0) {
                mostrarError("El valor por hora debe ser mayor a cero", txtValorxHora);
                return false;
            }
        } catch (NumberFormatException e) {
            mostrarError("El valor por hora debe ser un número válido", txtValorxHora);
            return false;
        }

        try {
            BigDecimal valorExtra = new BigDecimal(txtValorExtra.getText().trim());
            if (valorExtra.compareTo(BigDecimal.ZERO) <= 0) {
                mostrarError("El valor de hora extra debe ser mayor a cero", txtValorExtra);
                return false;
            }
        } catch (NumberFormatException e) {
            mostrarError("El valor de hora extra debe ser un número válido", txtValorExtra);
            return false;
        }

        return true;
    }

    private void mostrarError(String mensaje, JComponent componente) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
        componente.requestFocusInWindow();
    }

    public static void mostrarFormulario(JFrame parent, Grupo05.dominio.PuestoTrabajo puestoEditar) {
        SwingUtilities.invokeLater(() -> {
            PuestoTrabajo dialog = new PuestoTrabajo(parent, puestoEditar);
            dialog.setVisible(true);
        });
    }

    public static void main(String[] args) {
        mostrarFormulario(null, null);
    }
}