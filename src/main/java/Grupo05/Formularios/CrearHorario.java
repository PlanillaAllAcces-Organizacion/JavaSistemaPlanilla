package Grupo05.Formularios;

import Grupo05.Persistencia.TipoHorarioDAO;
import Grupo05.Utils.CUD;
import Grupo05.dominio.Horario;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

public class CrearHorario extends JDialog {
    private JPanel mainPanel;
    private JTextField txtNombreHorario;
    private JButton btnGuardar;
    private JButton btnCancelar;
    private JLabel lblTitulo;

    private TipoHorarioDAO horarioDAO;
    private Horario horarioActual;
    private CUD operacion;

    public CrearHorario(JFrame parent) {
        this(parent, null, CUD.CREATE);
    }

    public CrearHorario(JFrame parent, Horario horarioEditar, CUD operacion) {
        super(parent, operacion == CUD.CREATE ? "Nuevo Horario" : "Editar Horario", true);
        this.horarioActual = horarioEditar;
        this.operacion = operacion;
        this.horarioDAO = new TipoHorarioDAO();

        initializeComponents();
        setupMainPanel();
        setupListeners();

        if (horarioEditar != null) {
            cargarDatosHorario();
        }

        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setSize(400, 200);
        setLocationRelativeTo(parent);
    }

    private void initializeComponents() {
        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        lblTitulo = new JLabel(operacion == CUD.CREATE ? "CREAR NUEVO HORARIO" : "EDITAR HORARIO");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 16));
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        txtNombreHorario = new JTextField(20);

        btnGuardar = new JButton("Guardar");
        btnCancelar = new JButton("Cancelar");

        // Configurar estilos de botones
        btnGuardar.setBackground(new Color(34, 139, 34));
        btnGuardar.setForeground(Color.WHITE);
        btnCancelar.setBackground(new Color(178, 34, 34));
        btnCancelar.setForeground(Color.WHITE);
    }

    private void setupMainPanel() {
        // Panel de título
        JPanel panelTitulo = new JPanel();
        panelTitulo.add(lblTitulo);
        mainPanel.add(panelTitulo);

        // Panel de formulario
        JPanel panelFormulario = new JPanel(new GridLayout(1, 2, 5, 5));
        panelFormulario.add(new JLabel("Nombre del Horario:"));
        panelFormulario.add(txtNombreHorario);
        mainPanel.add(panelFormulario);

        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panelBotones.add(btnGuardar);
        panelBotones.add(btnCancelar);
        mainPanel.add(panelBotones);

        add(mainPanel);
    }

    private void setupListeners() {
        btnGuardar.addActionListener(e -> guardarHorario());
        btnCancelar.addActionListener(e -> dispose());
    }

    private void cargarDatosHorario() {
        if (horarioActual != null) {
            txtNombreHorario.setText(horarioActual.getNombreHorario());
        }
    }

    private void guardarHorario() {
        try {
            if (!validarCampos()) {
                return;
            }

            Horario horario = new Horario();

            // Si estamos editando, mantener el ID
            if (horarioActual != null) {
                horario.setId(horarioActual.getId());
            }

            horario.setNombreHorario(txtNombreHorario.getText().trim());

            boolean exito;
            String mensaje;

            if (operacion == CUD.CREATE) {
                Horario horarioCreado = horarioDAO.create(horario);
                exito = horarioCreado != null;
                mensaje = exito ? "Horario creado exitosamente" : "Error al crear el horario";
            } else {
                exito = horarioDAO.update(horario);
                mensaje = exito ? "Horario actualizado exitosamente" : "Error al actualizar el horario";
            }

            if (exito) {
                JOptionPane.showMessageDialog(this, mensaje, "Éxito", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error de base de datos: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private boolean validarCampos() {
        if (txtNombreHorario.getText().trim().isEmpty()) {
            mostrarError("El nombre del horario es obligatorio", txtNombreHorario);
            return false;
        }
        return true;
    }

    private void mostrarError(String mensaje, JComponent componente) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
        componente.requestFocusInWindow();
    }

    public static void mostrarFormulario(JFrame parent, Horario horarioEditar, CUD operacion) {
        SwingUtilities.invokeLater(() -> {
            CrearHorario dialog = new CrearHorario(parent, horarioEditar, operacion);
            dialog.setVisible(true);
        });
    }

    public static void main(String[] args) {
        mostrarFormulario(null, null, CUD.CREATE);
    }
}