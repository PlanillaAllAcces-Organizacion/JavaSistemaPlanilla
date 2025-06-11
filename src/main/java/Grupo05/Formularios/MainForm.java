package Grupo05.Formularios;

import javax.swing.*;

public class MainForm extends JFrame {

    public MainForm(){
        setTitle("Sistema en java de escritorio"); // Establece el título de la ventana principal (JFrame).
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Configura la operación por defecto al cerrar la ventana para que la aplicación se termine.
        setLocationRelativeTo(null); // Centra la ventana principal en la pantalla.
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Inicializa la ventana principal en estado maximizado, ocupando toda la pantalla.
        createMenu(); // Llama al método 'createMenu()' para crear y agregar la barra de menú a la ventana principal.
    }

    private void createMenu() {
        // Barra de menú
        JMenuBar menuBar = new JMenuBar(); // Crea una nueva barra de menú.
        setJMenuBar(menuBar); // Establece la barra de menú creada como la barra de menú de este JFrame (MainForm).

        JMenu menuPerfil = new JMenu("Perfil"); // Crea un nuevo menú llamado "Perfil".
        menuBar.add(menuPerfil); // Agrega el menú "Perfil" a la barra de menú.


        JMenuItem itemSalir = new JMenuItem("Salir"); // Crea un nuevo elemento de menú llamado "Salir".
        menuPerfil.add(itemSalir); // Agrega el elemento "Salir" al menú "Perfil".
        itemSalir.addActionListener(e -> System.exit(0)); // Agrega un ActionListener al elemento "Salir". Cuando se hace clic, termina la ejecución de la aplicación (cierra la JVM).


        // Menú "Matenimiento"
        JMenu menuMantenimiento = new JMenu("Mantenimientos"); // Crea un nuevo menú llamado "Mantenimientos".
        menuBar.add(menuMantenimiento); // Agrega el menú "Mantenimientos" a la barra de menú.

        JMenuItem itemUsers = new JMenuItem("Usuarios"); // Crea un nuevo elemento de menú llamado "Usuarios".
        menuMantenimiento.add(itemUsers); // Agrega el elemento "Usuarios" al menú "Mantenimientos".
        itemUsers.addActionListener(e -> { // Agrega un ActionListener al elemento "Usuarios".
            UserReadingForm userReadingForm = new UserReadingForm(this); // Cuando se hace clic, crea una nueva instancia de UserReadingForm (formulario para leer/listar usuarios), pasándole la instancia actual de MainForm como padre.
            userReadingForm.setVisible(true); // Hace visible el formulario de lectura de usuarios.
        });

        JMenuItem itemEmpleados = new JMenuItem("Empleados"); // Crea un nuevo elemento de menú llamado "Empleados".
        menuMantenimiento.add(itemEmpleados); // Agrega el elemento "Usuarios" al menú "Mantenimientos".
        itemEmpleados.addActionListener(e -> { // Agrega un ActionListener al elemento "Usuarios".
            EmpleadoReadingForm empleadoReadingForm = new EmpleadoReadingForm(this); // Cuando se hace clic, crea una nueva instancia de UserReadingForm (formulario para leer/listar usuarios), pasándole la instancia actual de MainForm como padre.
            empleadoReadingForm.setVisible(true); // Hace visible el formulario de lectura de usuarios.
        });



        // Nuevo ítem para Bonos
        JMenuItem itemBonos = new JMenuItem("Bonos"); // Crea un nuevo elemento de menú llamado "Bonos".
        menuMantenimiento.add(itemBonos); // Agrega el elemento "Bonos" al menú "Mantenimientos".
        itemBonos.addActionListener(e -> { // Agrega un ActionListener al elemento "Bonos".
            BonoListForm bonoListForm = new BonoListForm(); // Crea una nueva instancia de BonoListForm
            bonoListForm.setVisible(true); // Hace visible el formulario de gestión de bonos
        });


        JMenuItem ítemDescuentos = new JMenuItem("Descuentos");
        menuMantenimiento.add(ítemDescuentos);
        ítemDescuentos.addActionListener(e -> {
           DescuentoListForm descuentoListForm = new DescuentoListForm();
           descuentoListForm.setVisible(true);
        });

        JMenuItem ítemHorario = new JMenuItem("Horario");
        menuMantenimiento.add(ítemHorario);
        ítemHorario.addActionListener(e -> {
            TipoDeHorarioListForm descuentoListForm = new TipoDeHorarioListForm();
            descuentoListForm.setVisible(true);
        });


    }
}
