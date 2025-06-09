package Grupo05.Formularios;

import Grupo05.Persistencia.EmpleadoDAO;
import Grupo05.dominio.Empleado;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import Grupo05.Utils.CUD;

public class EmpleadoReadingForm extends JDialog {
    private JTextField txtName;
    private JButton btnCrear;
    private JButton btnModificar;
    private JButton btnEliminar;
    private JTable tableEmpleados;
    private JPanel mainPanel;
    private JButton button1;
    private JButton btnsBono;
    private JButton btnsDescuento;

    private EmpleadoDAO empleadoDAO; // Instancia de UserDAO para realizar operaciones de base de datos de usuarios.
    private MainForm mainForm; //

    // Constructor de la clase UserReadingForm. Recibe una instancia de MainForm como parámetro.
    public EmpleadoReadingForm(MainForm mainForm) {
        this.mainForm = mainForm; // Asigna la instancia de MainForm recibida a la variable local.
        empleadoDAO = new EmpleadoDAO(); // Crea una nueva instancia de UserDAO al instanciar este formulario.
        setContentPane(mainPanel); // Establece el panel principal como el contenido de este diálogo.
        setModal(true); // Hace que este diálogo sea modal, bloqueando la interacción con la ventana principal hasta que se cierre.
        setTitle("Buscar Usuario"); // Establece el título de la ventana del diálogo.
        pack(); // Ajusta el tamaño de la ventana para que todos sus componentes se muestren correctamente.
        setLocationRelativeTo(mainForm); // Centra la ventana del diálogo relative a la ventana principal.

        // Agrega un ActionListener al botón btnCreate.
        btnCrear.addActionListener(s -> {
            // Crea una nueva instancia de UserWriteForm para la creación de un nuevo usuario, pasando la MainForm, la constante CREATE de CUD y un nuevo objeto User vacío.
            EmpleadosForm empleadosForm = new EmpleadosForm(this.mainForm, CUD.CREATE, new Empleado());
            // Hace visible el formulario de escritura de usuario.
            empleadosForm.setVisible(true);
            // Limpia la tabla de usuarios creando y asignando un modelo de tabla vacío  para refrescar la lista después de la creación.
            DefaultTableModel emptyModel = new DefaultTableModel();
            tableEmpleados.setModel(emptyModel);
        });

    }





}
