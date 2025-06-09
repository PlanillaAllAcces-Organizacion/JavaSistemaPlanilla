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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

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
    private JLabel lbPassword;
    private JPasswordField txtPassword;

    private TipoHorarioDAO tipoHorarioDAO;
    private PuestoTrabajoDAO puestoTrabajoDAO;
    private EmpleadoDAO empleadoDAO;
    private Empleado em;
    private CUD cud;
    private MainForm mainForm;

    public EmpleadosForm(MainForm mainForm, CUD cud, Empleado empleado){
        this.cud = cud;
        this.em = empleado;
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
        initCBStatus();
        initCBPuesto();
        initCBHorario();

        switch (this.cud) {
            case CREATE:
                setTitle("Crear Usuario");
                BtnGuardar.setText("Guardar");
                break;
            case UPDATE:
                setTitle("Modificar Usuario");
                BtnGuardar.setText("Guardar");
                break;
            case DELETE:
                setTitle("Eliminar Usuario");
                BtnGuardar.setText("Eliminar");
                break;
        }

        setValuesControls(this.em);
    }

    private void initCBStatus() {
        DefaultComboBoxModel<ComBo> model = (DefaultComboBoxModel<ComBo>) cbEstado.getModel();
        model.addElement(new ComBo("ACTIVO", (byte)1));
        model.addElement(new ComBo("INACTIVO", (byte)2));
    }

    private void initCBPuesto() {
        DefaultComboBoxModel<ComBo> model = (DefaultComboBoxModel<ComBo>) cbPuesto.getModel();
        model.removeAllElements();
        model.addElement(new ComBo("Ninguno", 0));

        try {
            List<PuestoTrabajo> puestos = puestoTrabajoDAO.getAllActive();
            for (PuestoTrabajo p : puestos) {
                model.addElement(new ComBo(p.getNombrePuesto(), p.getId()));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar puestos: " + e.getMessage(), "Error de Base de Datos", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void initCBHorario() {
        DefaultComboBoxModel<ComBo> model = (DefaultComboBoxModel<ComBo>) cbHorario.getModel();
        model.removeAllElements();
        model.addElement(new ComBo("Ninguno", 0));

        try {
            List<Horario> horarios = tipoHorarioDAO.getAll();
            for (Horario h : horarios) {
                model.addElement(new ComBo(h.getNombreHorario(), h.getId()));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar horarios: " + e.getMessage(), "Error de Base de Datos", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void setValuesControls(Empleado empleado) {
        // Asegúrate de que 'empleado' no sea null antes de acceder a sus propiedades
        if (empleado != null) {
            txtDui.setText(empleado.getDui()); // <-- AÑADIDO PARA DEPURACIÓN, asegúrate de que se cargue si es UPDATE/DELETE
            txtNombre.setText(empleado.getNombre());
            txtApellido.setText(empleado.getApellido()); // <-- AÑADIDO PARA DEPURACIÓN
            txtCorreo.setText(empleado.getCorreo());
            txtTelefono.setText(String.valueOf(empleado.getTelefono())); // <-- AÑADIDO PARA DEPURACIÓN
            txtSalario.setText(String.valueOf(empleado.getSalario())); // <-- AÑADIDO PARA DEPURACIÓN
            // Formatear la fecha para mostrarla
            if (empleado.getFechacontra() != null) {
                txtFecha.setText(empleado.getFechacontra().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))); // <-- AÑADIDO PARA DEPURACIÓN
            }
            txtUsuario.setText(empleado.getUsuario()); // <-- AÑADIDO PARA DEPURACIÓN

            // Seleccionar el estatus en el ComboBox 'cbStatus'.
            // Recorre el modelo para encontrar el ComBo correcto por su valor.
            DefaultComboBoxModel<ComBo> estadoModel = (DefaultComboBoxModel<ComBo>) cbEstado.getModel();
            for (int i = 0; i < estadoModel.getSize(); i++) {
                ComBo item = estadoModel.getElementAt(i);
                if (item.getValue() instanceof Byte && item.getValue().equals(empleado.getEstado())) {
                    cbEstado.setSelectedItem(item);
                    break;
                }
            }

            // Seleccionar puesto
            DefaultComboBoxModel<ComBo> puestoModel = (DefaultComboBoxModel<ComBo>) cbPuesto.getModel();
            if (empleado.getPuestoTrabajoId() != null) {
                for (int i = 0; i < puestoModel.getSize(); i++) {
                    ComBo item = puestoModel.getElementAt(i);
                    if (item.getValue() instanceof Integer && item.getValue().equals(empleado.getPuestoTrabajoId())) {
                        cbPuesto.setSelectedItem(item);
                        break;
                    }
                }
            } else {
                cbPuesto.setSelectedItem(new ComBo("Ninguno", 0)); // Seleccionar "Ninguno" si es null
            }


            // Seleccionar horario
            DefaultComboBoxModel<ComBo> horarioModel = (DefaultComboBoxModel<ComBo>) cbHorario.getModel();
            if (empleado.getTipoDeHorarioId() != null) {
                for (int i = 0; i < horarioModel.getSize(); i++) {
                    ComBo item = horarioModel.getElementAt(i);
                    if (item.getValue() instanceof Integer && item.getValue().equals(empleado.getTipoDeHorarioId())) {
                        cbHorario.setSelectedItem(item);
                        break;
                    }
                }
            } else {
                cbHorario.setSelectedItem(new ComBo("Ninguno", 0)); // Seleccionar "Ninguno" si es null
            }
        }


        if (this.cud == CUD.CREATE) {
            cbEstado.setSelectedItem(new ComBo(null, (byte)1)); // Asegura que el estado por defecto sea Activo
            // Asegúrate de que los campos de puesto y horario también tengan valores por defecto si los deseas
            cbPuesto.setSelectedItem(new ComBo("Ninguno", 0));
            cbHorario.setSelectedItem(new ComBo("Ninguno", 0));
        }

        if (this.cud == CUD.DELETE) {
            txtDui.setEditable(false);
            txtNombre.setEditable(false);
            txtApellido.setEditable(false);
            txtCorreo.setEditable(false);
            txtTelefono.setEditable(false);
            txtSalario.setEditable(false);
            txtFecha.setEditable(false);
            txtUsuario.setEditable(false);
            cbEstado.setEnabled(false);
            cbPuesto.setEnabled(false);
            cbHorario.setEnabled(false);
            txtPassword.setEditable(false); // También deshabilitar la contraseña en DELETE
        }

        if (this.cud != CUD.CREATE) {
            txtPassword.setVisible(false);
            lbPassword.setVisible(false);
        }
    }

    private void ok() {
        System.out.println("Formulario - ok: Iniciando operación..."); // <-- AÑADIDO PARA DEPURACIÓN
        try {
            boolean res = getValuesControls();
            System.out.println("Formulario - ok: Validación de controles: " + res); // <-- AÑADIDO PARA DEPURACIÓN

            if (res) {
                boolean r = false;

                switch (this.cud) {
                    case CREATE:
                        System.out.println("Formulario - ok: Intentando crear empleado..."); // <-- AÑADIDO PARA DEPURACIÓN
                        Empleado empleado = empleadoDAO.create(this.em);
                        System.out.println("Formulario - ok: EmpleadoDAO.create() devolvió: " + (empleado != null ? "Objeto Empleado" : "null")); // <-- AÑADIDO PARA DEPURACIÓN
                        if (empleado != null) {
                            System.out.println("Formulario - ok: ID del empleado retornado: " + empleado.getId()); // <-- AÑADIDO PARA DEPURACIÓN
                            if (empleado.getId() > 0) {
                                r = true;
                            } else {
                                System.out.println("Formulario - ok: El ID del empleado retornado es 0 o negativo."); // <-- AÑADIDO PARA DEPURACIÓN
                            }
                        } else {
                            System.out.println("Formulario - ok: EmpleadoDAO.create() retornó null."); // <-- AÑADIDO PARA DEPURACIÓN
                        }
                        break;
                    // Los casos UPDATE y DELETE no están implementados en tu código original para el método ok()
                    // Si los vas a implementar, asegúrate de que también establezcan 'r = true;' bajo las condiciones correctas.
                }

                System.out.println("Formulario - ok: Resultado final 'r': " + r); // <-- AÑADIDO PARA DEPURACIÓN
                if (r) {
                    JOptionPane.showMessageDialog(null,
                            "Transacción realizada exitosamente",
                            "Información", JOptionPane.INFORMATION_MESSAGE);
                    this.dispose();
                    // Refrescar la tabla principal si tienes una (comentar si no aplica)
                    if (mainForm != null) {
                        // mainForm.loadEmpleados(); // Si tienes un método para recargar la tabla en MainForm
                    }
                } else {
                    JOptionPane.showMessageDialog(null,
                            "No se logró realizar ninguna acción",
                            "ERROR", JOptionPane.ERROR_MESSAGE);
                    // No hay 'return' aquí, así que el flujo continúa.
                    // Podrías poner un 'return;' aquí si quieres que la ejecución se detenga.
                }
            } else {
                JOptionPane.showMessageDialog(null,
                        "Los campos con * son obligatorios o tienen formato incorrecto", // Mensaje más específico
                        "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }
        } catch (Exception ex) {
            System.err.println("Formulario - ok: EXCEPCIÓN INESPERADA:"); // <-- AÑADIDO PARA DEPURACIÓN
            ex.printStackTrace(); // <-- AÑADIDO PARA DEPURACIÓN
            JOptionPane.showMessageDialog(null,
                    "Error inesperado: " + ex.getMessage(), // Añadir "Error inesperado"
                    "ERROR", JOptionPane.ERROR_MESSAGE);
            return;
        }
    }

    private boolean getValuesControls() {
        System.out.println("Formulario - getValuesControls: Validando campos..."); // <-- AÑADIDO PARA DEPURACIÓN

        // 1. DUI
        String dui = txtDui.getText().trim();
        if (dui.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El campo DUI es obligatorio.", "Validación", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        this.em.setDui(dui);

        // 2. Nombre
        String nombre = txtNombre.getText().trim();
        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El campo Nombre es obligatorio.", "Validación", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        this.em.setNombre(nombre);

        // 3. Apellido
        String apellido = txtApellido.getText().trim();
        if (apellido.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El campo Apellido es obligatorio.", "Validación", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        this.em.setApellido(apellido);

        // 4. Teléfono (asumiendo que es int)
        String telefonoStr = txtTelefono.getText().trim();
        if (telefonoStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El campo Teléfono es obligatorio.", "Validación", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        try {
            this.em.setTelefono(Integer.parseInt(telefonoStr));
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "El campo Teléfono debe ser un número válido.", "Validación", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        // 5. Correo
        String correo = txtCorreo.getText().trim();
        if (correo.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El campo Correo es obligatorio.", "Validación", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        this.em.setCorreo(correo);

        // 6. Salario Base (asumiendo que es double)
        String salarioStr = txtSalario.getText().trim();
        if (salarioStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El campo Salario es obligatorio.", "Validación", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        try {
            this.em.setSalario(Double.parseDouble(salarioStr));
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "El campo Salario debe ser un número válido.", "Validación", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        // 7. Fecha Contratación Inicial (LocalDateTime)
        String fechaStr = txtFecha.getText().trim();
        if (fechaStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El campo Fecha Contratación es obligatorio.", "Validación", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            this.em.setFechacontra(LocalDate.parse(fechaStr, formatter).atStartOfDay());
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "El formato de la Fecha de Contratación debe ser DD/MM/AAAA.", "Validación", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        // 8. Usuario
        String usuario = txtUsuario.getText().trim();
        if (usuario.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El campo Usuario es obligatorio.", "Validación", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        this.em.setUsuario(usuario);

        // 9. Estado (ComboBox)
        ComBo selectedEstado = (ComBo) cbEstado.getSelectedItem();
        if (selectedEstado == null || (selectedEstado.getValue() instanceof Byte && (byte) selectedEstado.getValue() == 0)) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un Estado.", "Validación", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        this.em.setEstado((byte) selectedEstado.getValue());

        // 10. Puesto de Trabajo (ComboBox)
        ComBo selectedPuesto = (ComBo) cbPuesto.getSelectedItem();
        // Asegúrate de que el valor sea un Integer antes de castear
        if (selectedPuesto != null && selectedPuesto.getValue() instanceof Integer) {
            int puestoId = (Integer) selectedPuesto.getValue();
            if (puestoId == 0) { // Si es "Ninguno" o 0
                this.em.setPuestoTrabajoId(null);
            } else {
                this.em.setPuestoTrabajoId(puestoId);
            }
        } else {
            this.em.setPuestoTrabajoId(null);
        }

        // 11. Horario (ComboBox)
        ComBo selectedHorario = (ComBo) cbHorario.getSelectedItem();
        // Asegúrate de que el valor sea un Integer antes de castear
        if (selectedHorario != null && selectedHorario.getValue() instanceof Integer) {
            int horarioId = (Integer) selectedHorario.getValue();
            if (horarioId == 0) { // Si es "Ninguno" o 0
                this.em.setTipoDeHorarioId(null);
            } else {
                this.em.setTipoDeHorarioId(horarioId);
            }
        } else {
            this.em.setTipoDeHorarioId(null);
        }

        // 12. Contraseña (solo para CREATE)
        if (this.cud == CUD.CREATE) {
            String password = new String(txtPassword.getPassword()).trim();
            if (password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "El campo Contraseña es obligatorio para crear un usuario.", "Validación", JOptionPane.WARNING_MESSAGE);
                return false;
            }
            this.em.setPasswordHash(password);
        }

        System.out.println("Formulario - getValuesControls: Todos los campos validados correctamente."); // <-- AÑADIDO PARA DEPURACIÓN
        return true; // Si todas las validaciones pasan, retorna true
    }
}
