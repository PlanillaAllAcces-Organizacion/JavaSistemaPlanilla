package Grupo05.Formularios;

import javax.swing.*;
import Grupo05.Persistencia.EmpleadoDAO;
import Grupo05.Persistencia.PuestoTrabajoDAO;
import Grupo05.Persistencia.TipoHorarioDAO;
import Grupo05.Utils.CUD;
import Grupo05.Utils.ComBo;
import Grupo05.dominio.Empleado;
import Grupo05.dominio.PuestoTrabajo;
import Grupo05.dominio.Horario;

import java.sql.SQLException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

public class EmpleadosForm extends JDialog{
    private JTextField txtDui;
    private JTextField txtNombre;
    private JTextField txtApellido;
    private JTextField txtCorreo;
    private JTextField txtTelefono;
    private JTextField txtSalario;
    private JTextField txtFecha;
    private JTextField txtUsuario;
    private JButton BtnCancelar;
    private JButton BtnGuardar;
    private JPanel mainPanel;
    private JComboBox cbEstado;
    private JComboBox cbPuesto;
    private JComboBox cbHorario;


    private EmpleadoDAO empleadoDAO; // Instancia de EmpleadoDAO
    private MainForm mainForm; // Referencia a la ventana principal
    private CUD cud; // Tipo de operación (CREATE, UPDATE, DELETE)
    private Empleado en; // Objeto Empleado que se está manipulando
    private TipoHorarioDAO tipoHorarioDAO;       // Declarado aquí
    private PuestoTrabajoDAO puestoTrabajoDAO; // Declarado aquí

    public EmpleadosForm(MainForm mainForm, CUD cud, Empleado empleado){
        this.cud = cud;
        this.en = empleado;
        this.mainForm = mainForm;

        empleadoDAO = new EmpleadoDAO();
        puestoTrabajoDAO = new PuestoTrabajoDAO();
        tipoHorarioDAO = new TipoHorarioDAO();
        setContentPane(mainPanel);
        setModal(true);
        init();
        pack();
        setLocationRelativeTo(mainForm);

        BtnCancelar.addActionListener(s -> this.dispose());
        BtnGuardar.addActionListener(s -> ok());
    }

    private void init() {
        initCBEstado(); // Inicializa el ComboBox de estado
        initCBsAdicionales(); // Inicializa los ComboBoxes adicionales

        switch (this.cud) {
            case CREATE:
                setTitle("Crear Empleado");
                BtnGuardar.setText("Guardar");
                break;
            case UPDATE:
                setTitle("Modificar Empleado");
                BtnGuardar.setText("Guardar");
                break;
            case DELETE:
                setTitle("Eliminar Empleado");
                BtnGuardar.setText("Eliminar");
                break;
        }

        setValuesControls(this.en); // Llena los campos con los valores del empleado
    }

    private void initCBsAdicionales() {
        // --- ComboBox Tipo de Horario ---
        DefaultComboBoxModel<ComBo> modelHorario = (DefaultComboBoxModel<ComBo>) cbHorario.getModel();
        modelHorario.removeAllElements(); // Limpiar elementos previos por si acaso

        try {
            ArrayList<Horario> horarios = tipoHorarioDAO.getAll();
            for (Horario h : horarios) {
                modelHorario.addElement(new ComBo(h.getNombreHorario(), h.getId()));
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al cargar tipos de horario: " + ex.getMessage(),
                    "Error de Carga", JOptionPane.ERROR_MESSAGE);
        }

        // --- ComboBox Puesto de Trabajo ---
        DefaultComboBoxModel<ComBo> modelPuesto = (DefaultComboBoxModel<ComBo>) cbPuesto.getModel();
        modelPuesto.removeAllElements(); // Limpiar elementos previos

        try {
            ArrayList<PuestoTrabajo> puestos = puestoTrabajoDAO.getAllActive();
            for (PuestoTrabajo p : puestos) {
                modelPuesto.addElement(new ComBo(p.getNombrePuesto(), p.getId()));
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al cargar puestos de trabajo: " + ex.getMessage(),
                    "Error de Carga", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void initCBEstado() {
        DefaultComboBoxModel<ComBo> model = (DefaultComboBoxModel<ComBo>) cbEstado.getModel();
        model.addElement(new ComBo("ACTIVO", (byte)1));
        model.addElement(new ComBo("INACTIVO", (byte)2));
    }

    // Método para inicializar ComboBoxes adicionales (Tipo de Horario, Puesto de Trabajo)



    private void setValuesControls(Empleado empleado) {
        if (empleado == null) return; // Si el empleado es nulo, no hay nada que mostrar

        txtNombre.setText(empleado.getNombre());
        txtApellido.setText(empleado.getApellido());
        txtDui.setText(empleado.getDui());
        txtTelefono.setText(String.valueOf(empleado.getTelefono()));
        txtCorreo.setText(empleado.getCorreo());
        txtSalario.setText(String.valueOf(empleado.getSalario()));

        // Manejo de la fecha de contrato
        if (this.cud == CUD.CREATE) {
            // Para una nueva creación, obtener la fecha y hora actual de la computadora
            LocalDateTime now = LocalDateTime.now();
            // Formatear al String deseado para mostrar en el campo
            txtFecha.setText(now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")));
            // Y también establecerla en el objeto Empleado directamente para que getValuesControls la tenga
            this.en.setFechacontra(now);
        } else {
            // Para actualización o eliminación, mostrar la fecha de contrato existente
            txtFecha.setText(empleado.getFechacontra() != null ?
                    empleado.getFechacontra().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")) : "");
        }


        // Seleccionar estado en ComboBox
        cbEstado.setSelectedItem(new ComBo(null, empleado.getEstado()));

        // Seleccionar Tipo de Horario en ComboBox
        cbHorario.setSelectedItem(new ComBo(null, empleado.getTipoDeHorarioId()));

        // Seleccionar Puesto de Trabajo en ComboBox
        cbPuesto.setSelectedItem(new ComBo(null, empleado.getPuestoTrabajoId()));


        if (this.cud == CUD.CREATE) {
            cbEstado.setSelectedItem(new ComBo(null, (byte)1)); // Por defecto Activo
            // No se necesita cargar password, se ingresa uno nuevo
        }

        if (this.cud == CUD.DELETE) {
            // Deshabilitar todos los campos para la eliminación
            txtNombre.setEditable(false);
            txtApellido.setEditable(false);
            txtDui.setEditable(false);
            txtTelefono.setEditable(false);
            txtCorreo.setEditable(false);
            txtSalario.setEditable(false);
            cbEstado.setEnabled(false);
            cbHorario.setEnabled(false);
            cbPuesto.setEnabled(false);
        }

    }

    private boolean getValuesControls() {
        // Obtener valores de ComboBoxes
        ComBo selectedEstado = (ComBo) cbEstado.getSelectedItem();
        byte estado = selectedEstado != null ? (byte) (selectedEstado.getValue()) : (byte) 0;

        ComBo selectedHorario = (ComBo) cbHorario.getSelectedItem();
        int tipoDeHorarioId = selectedHorario != null ? (int) selectedHorario.getValue() : 0;

        ComBo selectedPuesto = (ComBo) cbPuesto.getSelectedItem();
        int puestoTrabajoId = selectedPuesto != null ? (int) selectedPuesto.getValue() : 0;

        // Validaciones de campos obligatorios
        if (txtNombre.getText().trim().isEmpty() ||
                txtApellido.getText().trim().isEmpty() ||
                txtDui.getText().trim().isEmpty() ||
                txtCorreo.getText().trim().isEmpty() ||
                estado == (byte) 0 || tipoDeHorarioId == 0 || puestoTrabajoId == 0) {
            JOptionPane.showMessageDialog(null,
                    "Los campos con * son obligatorios y los ComboBoxes deben tener una selección válida.",
                    "Validación", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        // Validación y parseo de Teléfono
        int telefono;
        try {
            telefono = Integer.parseInt(txtTelefono.getText().trim());
            if (telefono <= 0) {
                JOptionPane.showMessageDialog(null, "El teléfono debe ser un número válido y positivo.", "Validación", JOptionPane.WARNING_MESSAGE);
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "El teléfono debe ser un número entero válido.", "Validación", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        // Validación y parseo de Salario
        double salario;
        try {
            salario = Double.parseDouble(txtSalario.getText().trim());
            if (salario <= 0) {
                JOptionPane.showMessageDialog(null, "El salario debe ser un número válido y positivo.", "Validación", JOptionPane.WARNING_MESSAGE);
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "El salario debe ser un número decimal válido.", "Validación", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        // Validación y parseo de Fecha de Contrato (LocalDateTime)
        LocalDateTime fechaContrato;
        try {
            // Asegúrate de que el formato de entrada coincida con el formato de parseo.
            // Por ejemplo, "2025-06-08T10:30:00"
            fechaContrato = LocalDateTime.parse(txtFecha.getText().trim());
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(null, "Formato de fecha de contrato inválido. Use YYYY-MM-DDTHH:MM:SS (ej: 2025-06-08T10:30:00).", "Validación", JOptionPane.WARNING_MESSAGE);
            return false;
        }


        // Si todas las validaciones pasan, asignar valores al objeto Empleado
        this.en.setTipoDeHorarioId(tipoDeHorarioId);
        this.en.setPuestoTrabajoId(puestoTrabajoId);
        this.en.setDui(txtDui.getText().trim());
        this.en.setNombre(txtNombre.getText().trim());
        this.en.setApellido(txtApellido.getText().trim());
        this.en.setTelefono(telefono);
        this.en.setCorreo(txtCorreo.getText().trim());
        this.en.setEstado(estado);
        this.en.setSalario(salario);
        this.en.setFechacontra(fechaContrato);

        return true;
    }

    private void ok() {
        try {
            boolean res = getValuesControls(); // Obtener y validar los valores del formulario

            if (res) { // Si la validación fue exitosa
                boolean r = false; // Resultado de la operación de base de datos

                switch (this.cud) {
                    case CREATE:
                        Empleado nuevoEmpleado = empleadoDAO.create(this.en);
                        if (nuevoEmpleado != null && nuevoEmpleado.getId() > 0) {
                            r = true;
                        }
                        break;
                    case UPDATE:
                        r = empleadoDAO.update(this.en);
                        break;
                    case DELETE:
                        r = empleadoDAO.delete(this.en);
                        break;
                }

                if (r) {
                    JOptionPane.showMessageDialog(null,
                            "Transacción realizada exitosamente",
                            "Información", JOptionPane.INFORMATION_MESSAGE);
                    this.dispose(); // Cierra la ventana
                } else {
                    JOptionPane.showMessageDialog(null,
                            "No se logró realizar ninguna acción",
                            "ERROR", JOptionPane.ERROR_MESSAGE);
                }
            }
            // Si res es false, el mensaje de error ya se mostró en getValuesControls()
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null,
                    "Error en la operación: " + ex.getMessage(),
                    "ERROR", JOptionPane.ERROR_MESSAGE);
        }
    }
}


