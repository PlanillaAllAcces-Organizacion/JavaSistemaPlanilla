package Grupo05.Formularios;

import Grupo05.Persistencia.BonoDAO;
import Grupo05.Utils.CBOption;
import Grupo05.Utils.CUD;
import Grupo05.dominio.Bonos;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

public class CrearBono extends JFrame {
    private JTextField txtNombre;
    private JTextField txtValor;
    private JComboBox<CBOption> cbEstado;
    private JComboBox<CBOption> cbOperacion;
    private JComboBox<CBOption> cbPlanilla;
    private JButton btnConfirmar;
    private JButton btnCancelar;
    private BonoDAO bonoDAO;
    private CUD operacion;
    private Bonos bonoExistente;

    public CrearBono() {
        this(CUD.CREATE, null);
    }

    public CrearBono(CUD operacion) {
        this(operacion, null);
    }

    public CrearBono(CUD operacion, Bonos bonoExistente) {
        this.operacion = operacion;
        this.bonoExistente = bonoExistente;
        bonoDAO = new BonoDAO();
        initComponents();
        setTitle(getTituloPorOperacion());
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        if (operacion == CUD.UPDATE && bonoExistente != null) {
            cargarDatosExistente();
        } else if (operacion == CUD.DELETE) {
            prepararModoEliminacion();
        }
    }

    private String getTituloPorOperacion() {
        switch (operacion) {
            case CREATE: return "Crear Nuevo Bono";
            case UPDATE: return "Editar Bono";
            case DELETE: return "Eliminar Bono";
            default: return "Gestión de Bono";
        }
    }

    private void prepararModoEliminacion() {
        txtNombre.setEnabled(false);
        txtValor.setEnabled(false);
        cbEstado.setEnabled(false);
        cbOperacion.setEnabled(false);
        cbPlanilla.setEnabled(false);
        btnConfirmar.setText("Eliminar");
    }

    private void cargarDatosExistente() {
        txtNombre.setText(bonoExistente.getNombreBono());
        txtValor.setText(String.valueOf(bonoExistente.getValor()));
        setComboBoxValue(cbEstado, bonoExistente.getEstado());
        setComboBoxValue(cbOperacion, bonoExistente.getOperacion());
        setComboBoxValue(cbPlanilla, bonoExistente.getPlanilla());
    }

    private void setComboBoxValue(JComboBox<CBOption> comboBox, byte value) {
        for (int i = 0; i < comboBox.getItemCount(); i++) {
            CBOption item = comboBox.getItemAt(i);
            if ((byte)item.getValue() == value) {
                comboBox.setSelectedIndex(i);
                break;
            }
        }
    }

    private void initComponents() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Nombre del Bono
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Nombre del Bono:"), gbc);

        gbc.gridx = 1;
        txtNombre = new JTextField(20);
        panel.add(txtNombre, gbc);

        // Valor
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Valor:"), gbc);

        gbc.gridx = 1;
        txtValor = new JTextField(20);
        panel.add(txtValor, gbc);

        // Estado
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Estado:"), gbc);

        gbc.gridx = 1;
        cbEstado = new JComboBox<>();
        cbEstado.addItem(new CBOption("Activo", (byte)1));
        cbEstado.addItem(new CBOption("Inactivo", (byte)0));
        panel.add(cbEstado, gbc);

        // Operación
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("Operación:"), gbc);

        gbc.gridx = 1;
        cbOperacion = new JComboBox<>();
        cbOperacion.addItem(new CBOption("Fija", (byte)1));
        cbOperacion.addItem(new CBOption("No Fija", (byte)0));
        panel.add(cbOperacion, gbc);

        // Planilla
        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(new JLabel("Planilla:"), gbc);

        gbc.gridx = 1;
        cbPlanilla = new JComboBox<>();
        cbPlanilla.addItem(new CBOption("Mensual", (byte)1));
        cbPlanilla.addItem(new CBOption("Quincenal", (byte)0));
        panel.add(cbPlanilla, gbc);

        // Botones
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnConfirmar = new JButton(operacion == CUD.DELETE ? "Eliminar" : "Guardar");
        btnCancelar = new JButton("Cancelar");

        btnConfirmar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                confirmarOperacion();
            }
        });

        btnCancelar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        buttonPanel.add(btnConfirmar);
        buttonPanel.add(btnCancelar);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.CENTER;
        panel.add(buttonPanel, gbc);

        add(panel);
    }

    private void confirmarOperacion() {
        try {
            if (operacion != CUD.DELETE) {
                String nombre = txtNombre.getText().trim();
                if (nombre.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "El nombre del bono es requerido", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                double valor;
                try {
                    valor = Double.parseDouble(txtValor.getText());
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "El valor debe ser un número válido", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            CBOption estadoOption = (CBOption) cbEstado.getSelectedItem();
            CBOption operacionOption = (CBOption) cbOperacion.getSelectedItem();
            CBOption planillaOption = (CBOption) cbPlanilla.getSelectedItem();

            Bonos bono = new Bonos(
                    (operacion == CUD.UPDATE || operacion == CUD.DELETE) && bonoExistente != null ? bonoExistente.getId() : 0,
                    txtNombre.getText().trim(),
                    operacion == CUD.DELETE ? 0 : Double.parseDouble(txtValor.getText()),
                    (byte) estadoOption.getValue(),
                    (byte) operacionOption.getValue(),
                    (byte) planillaOption.getValue()
            );

            boolean resultado = false;
            String mensaje = "";

            switch (operacion) {
                case CREATE:
                    Bonos bonoCreado = bonoDAO.create(bono);
                    resultado = bonoCreado != null;
                    mensaje = resultado ? "Bono creado exitosamente" : "Error al crear el bono";
                    break;

                case UPDATE:
                    resultado = bonoDAO.update(bono);
                    mensaje = resultado ? "Bono actualizado exitosamente" : "Error al actualizar el bono";
                    break;

                case DELETE:
                    if (JOptionPane.showConfirmDialog(this,
                            "¿Está seguro que desea eliminar este bono?",
                            "Confirmar eliminación",
                            JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {

                        resultado = bonoDAO.delete(bono.getId());
                        mensaje = resultado ? "Bono eliminado exitosamente" : "Error al eliminar el bono";
                    } else {
                        return;
                    }
                    break;
            }

            if (resultado) {
                JOptionPane.showMessageDialog(this, mensaje, "Éxito", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error de base de datos: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // Ejemplo de uso
                new CrearBono(CUD.CREATE).setVisible(true);
            }
        });
    }
}