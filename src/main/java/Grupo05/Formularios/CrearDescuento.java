package Grupo05.Formularios;

import Grupo05.Persistencia.DescuentoDAO;
import Grupo05.Utils.CBOption;
import Grupo05.Utils.CUD;
import Grupo05.dominio.Descuentos;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

public class CrearDescuento extends JFrame {
    private JTextField txtNombre;
    private JTextField txtValor;
    private JComboBox<CBOption> cbEstado;
    private JComboBox<CBOption> cbOperacion;
    private JButton btnConfirmar;
    private JButton btnCancelar;
    private DescuentoDAO descuentoDAO;
    private CUD operacion;
    private Descuentos descuentoExistente;


    public CrearDescuento(CUD operacion) {
        this(operacion, null);
    }

    public CrearDescuento(CUD operacion, Descuentos descuentoExistente) {
        this.operacion = operacion;
        this.descuentoExistente = descuentoExistente;
        descuentoDAO = new DescuentoDAO();
        initComponents();
        setTitle(getTituloPorOperacion());
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        if (operacion == CUD.UPDATE && descuentoExistente != null) {
            cargarDatosExistente();
        } else if (operacion == CUD.DELETE) {
            prepararModoEliminacion();
        }
    }

    private String getTituloPorOperacion() {
        switch (operacion) {
            case CREATE: return "Crear Nuevo Descuento";
            case UPDATE: return "Editar Descuento";
            case DELETE: return "Eliminar Descuento";
            default: return "Gestión de Descuento";
        }
    }

    private void prepararModoEliminacion() {
        txtNombre.setEnabled(false);
        txtValor.setEnabled(false);
        cbEstado.setEnabled(false);
        cbOperacion.setEnabled(false);
        btnConfirmar.setText("Eliminar");
    }

    private void cargarDatosExistente() {
        txtNombre.setText(descuentoExistente.getNombre());
        txtValor.setText(String.valueOf(descuentoExistente.getValor()));
        setComboBoxValue(cbEstado, descuentoExistente.getEstado());
        setComboBoxValue(cbOperacion, descuentoExistente.getOperacion());
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

        // Nombre del Descuento
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Nombre del Descuento:"), gbc);

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
        cbOperacion.addItem(new CBOption("No fija", (byte)0));
        panel.add(cbOperacion, gbc);


        // Botones
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnConfirmar = new JButton(operacion == CUD.DELETE ? "Eliminar" : "Guardar");
        btnCancelar = new JButton("Cancelar");

        btnConfirmar.setBackground(new Color(34, 139, 34));
        btnConfirmar.setForeground(Color.WHITE);
        btnCancelar.setBackground(new Color(178, 34, 34));
        btnCancelar.setForeground(Color.WHITE);

        btnConfirmar.addActionListener(e -> confirmarOperacion());
        btnCancelar.addActionListener(e -> dispose());

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
                    JOptionPane.showMessageDialog(this,
                            "El nombre del descuento es requerido",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                double valor;
                try {
                    valor = Double.parseDouble(txtValor.getText());
                    if (valor <= 0) {
                        JOptionPane.showMessageDialog(this,
                                "El valor debe ser mayor que cero",
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this,
                            "El valor debe ser un número válido",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            CBOption estadoOption = (CBOption) cbEstado.getSelectedItem();
            CBOption operacionOption = (CBOption) cbOperacion.getSelectedItem();

            Descuentos descuento = new Descuentos(
                    (operacion == CUD.UPDATE || operacion == CUD.DELETE) && descuentoExistente != null ?
                            descuentoExistente.getId() : 0,
                    txtNombre.getText().trim(),
                    operacion == CUD.DELETE ? 0 : Double.parseDouble(txtValor.getText()),
                    (byte) estadoOption.getValue(),
                    (byte) operacionOption.getValue()
            );

            boolean resultado = false;
            String mensaje = "";

            switch (operacion) {
                case CREATE:
                    Descuentos descuentoCreado = descuentoDAO.create(descuento);
                    resultado = descuentoCreado != null;
                    mensaje = resultado ? "Descuento creado exitosamente" : "Error al crear el descuento";
                    break;

                case UPDATE:
                    resultado = descuentoDAO.update(descuento);
                    mensaje = resultado ? "Descuento actualizado exitosamente" : "Error al actualizar el descuento";
                    break;

                case DELETE:
                    if (JOptionPane.showConfirmDialog(this,
                            "¿Está seguro que desea eliminar este descuento?",
                            "Confirmar eliminación",
                            JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {

                        resultado = descuentoDAO.delete(descuento.getId());
                        mensaje = resultado ? "Descuento eliminado exitosamente" : "Error al eliminar el descuento";
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
            JOptionPane.showMessageDialog(this,
                    "Error de base de datos: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

}