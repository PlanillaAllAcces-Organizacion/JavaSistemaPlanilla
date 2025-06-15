package Grupo05.Formularios;

import javax.swing.*;
import Grupo05.dominio.User;

import java.awt.event.ActionListener;

public class MainForm extends JFrame {

    private User userAutenticate;

    public User getUserAutenticate() {
        return userAutenticate;
    }

    public void setUserAutenticate(User userAutenticate) {
        this.userAutenticate = userAutenticate;
    }

    public MainForm(){
        setTitle("Sistema en java de escritorio"); // Establece el título de la ventana principal (JFrame).
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Configura la operación por defecto al cerrar la ventana para que la aplicación se termine.
        setLocationRelativeTo(null); // Centra la ventana principal en la pantalla.
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Inicializa la ventana principal en estado maximizado, ocupando toda la pantalla.
        createMenu(); // Llama al método 'createMenu()' para crear y agregar la barra de menú a la ventana principal.
    }

    private void createMenu() {
        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        // Build "Perfil" menu
        buildProfileMenu(menuBar);

        // Build "Mantenimientos" menu
        buildMaintenanceMenu(menuBar);

        // Build "Transacciones" menu (New, for payments and assignments)
        buildTransactionMenu(menuBar);
    }

    private void buildProfileMenu(JMenuBar menuBar) {
        JMenu menuPerfil = new JMenu("Perfil");
        menuBar.add(menuPerfil);

        JMenuItem itemChangePassword = new JMenuItem("Cambiar contraseña");
        menuPerfil.add(itemChangePassword);
        itemChangePassword.addActionListener(e -> {
            PasswordForm changePasswordForm = new PasswordForm(this);
            changePasswordForm.setVisible(true);
        });

        JMenuItem itemExit = new JMenuItem("Salir");
        menuPerfil.add(itemExit);
        itemExit.addActionListener(e -> System.exit(0));
    }

    private void buildMaintenanceMenu(JMenuBar menuBar) {
        JMenu menuMantenimiento = new JMenu("Mantenimientos");
        menuBar.add(menuMantenimiento);

        addMenuItem(menuMantenimiento, "Usuarios", e -> {
            UserReadingForm userReadingForm = new UserReadingForm(this);
            userReadingForm.setVisible(true);
        });

        addMenuItem(menuMantenimiento, "Empleados", e -> {
            EmpleadoReadingForm empleadoReadingForm = new EmpleadoReadingForm(this);
            empleadoReadingForm.setVisible(true);
        });

        menuMantenimiento.addSeparator(); // Separator for better grouping

        addMenuItem(menuMantenimiento, "Bonos", e -> {
            BonoListForm bonoListForm = new BonoListForm();
            bonoListForm.setVisible(true);
        });

        addMenuItem(menuMantenimiento, "Descuentos", e -> {
            DescuentoListForm descuentoListForm = new DescuentoListForm();
            descuentoListForm.setVisible(true);
        });

        menuMantenimiento.addSeparator(); // Separator for better grouping

        addMenuItem(menuMantenimiento, "Puesto de Trabajo", e -> {
            PuestosTrabajoListForm puestoTrabajoListForm = new PuestosTrabajoListForm();
            puestoTrabajoListForm.setVisible(true);
        });

        addMenuItem(menuMantenimiento, "Tipo de Horario", e -> {
            TipoDeHorarioListForm tipoDeHorarioListForm = new TipoDeHorarioListForm();
            tipoDeHorarioListForm.setVisible(true);
        });
    }

    private void buildTransactionMenu(JMenuBar menuBar) {
        JMenu menuTransacciones = new JMenu("Transacciones");
        menuBar.add(menuTransacciones);

        addMenuItem(menuTransacciones, "Asignar Bonos", e -> {
            AsignacionBonosForm asignacionBonosForm = new AsignacionBonosForm(this);
            asignacionBonosForm.setVisible(true);
        });

        addMenuItem(menuTransacciones, "Asignar Descuentos", e -> {
            AsignacionDescuentoForm asignacionDescuentoForm = new AsignacionDescuentoForm(this);
            asignacionDescuentoForm.setVisible(true);
        });

        menuTransacciones.addSeparator();

        addMenuItem(menuTransacciones, "Gestión de Pagos", e -> {
            // It's generally not good practice to create a dummy JFrame just for a parent.
            // If PagoEmpleadoReadingForm truly needs a JFrame parent, consider passing `this` (MainForm)
            // or refactor PagoEmpleadoReadingForm to not strictly require a JFrame parent
            // if it's meant to be a standalone dialog.
            // For now, I'm keeping your original logic with a comment.
            JFrame dummyParentFrame = new JFrame(); // Consider if this is the best approach
            PagoEmpleadoReadingForm pagoEmpleadoReadingForm = new PagoEmpleadoReadingForm(dummyParentFrame);
            dummyParentFrame.setSize(600, 400); // These settings apply to the dummy frame, not the dialog
            dummyParentFrame.setLocationRelativeTo(null);
            dummyParentFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Use DISPOSE_ON_CLOSE for temporary frames
            pagoEmpleadoReadingForm.setVisible(true);
        });
    }


    private void addMenuItem(JMenu menu, String text, ActionListener actionListener) {
        JMenuItem menuItem = new JMenuItem(text);
        menu.add(menuItem);
        menuItem.addActionListener(actionListener);
    }
}
