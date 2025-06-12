package Grupo05.Formularios;

import Grupo05.Persistencia.UserDAO;
import Grupo05.dominio.User;

import javax.swing.*;

public class PasswordForm extends JDialog{
    private JTextField txtUsuario;
    private JButton btnCambiar;
    private JPanel mainPanel;
    private JPasswordField txtPasswordH;

    private UserDAO userDAO;
    private MainForm mainForm;


    // Constructor de la clase ChangePasswordForm. Recibe una instancia de MainForm como parámetro.
    public PasswordForm(MainForm mainForm) {
        this.mainForm = mainForm;
        userDAO = new UserDAO();
        txtUsuario.setText(mainForm.getUserAutenticate().getName());
        setContentPane(mainPanel);
        setModal(true); // Hace que este diálogo sea modal, lo que significa que bloquea la interacción con la ventana principal hasta que se cierre.
        setTitle("Cambiar password"); // Establece el título de la ventana del diálogo.
        pack(); // Ajusta el tamaño de la ventana para que todos sus componentes se muestren correctamente.
        setLocationRelativeTo(mainForm); // Centra la ventana del diálogo relative a la ventana principal.

        // Agrega un ActionListener al botón btnChangePassword para que ejecute el método changePassword() cuando se haga clic.
        btnCambiar.addActionListener(e-> changePassword());

    }
    private void changePassword() {

        try {
            // Obtiene el usuario autenticado desde la ventana principal (MainForm).
            User userAut = mainForm.getUserAutenticate();
            // Crea un nuevo objeto User para almacenar los datos de actualización.
            User user = new User();
            // Establece el ID del usuario en el nuevo objeto User, utilizando el ID del usuario autenticado.
            user.setId(userAut.getId());
            // Establece la nueva contraseña en el objeto User, convirtiendo el array de caracteres del campo de contraseña a un String.
            user.setPasswordHash(new String(txtPasswordH.getPassword()));

            // Valida si la nueva contraseña está vacía.
            if (user.getPasswordHash().trim().isEmpty()) {
                JOptionPane.showMessageDialog(null,
                        "La contraseña es obligatoria",
                        "Validacion", JOptionPane.WARNING_MESSAGE);
                return; // Sale del método si la contraseña está vacía.
            }

            // Intenta actualizar la contraseña del usuario en la base de datos a través del UserDAO.
            boolean res = userDAO.updatePassword(user);

            // Verifica el resultado de la actualización.
            if (res) {
                // Si la actualización es exitosa, cierra la ventana actual (ChangePasswordForm).
                this.dispose();
                // Crea una nueva instancia de la ventana de inicio de sesión (LoginForm), pasando la ventana principal como parámetro.
                LoginForm loginForm = new LoginForm(this.mainForm);
                // Hace visible la ventana de inicio de sesión.
                loginForm.setVisible(true); // Muestra la ventana de inicio de sesión para que el usuario pueda ingresar con la nueva contraseña.
            } else {
                // Si la actualización falla, muestra un mensaje de advertencia.
                JOptionPane.showMessageDialog(null,
                        "No se logro cambiar la contraseña",
                        "Cambiar contraseña", JOptionPane.WARNING_MESSAGE);
            }
        } catch (Exception ex) {
            // Captura cualquier excepción que ocurra durante el proceso.
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "Sistema", JOptionPane.ERROR_MESSAGE); // Muestra un mensaje de error con la descripción de la excepción.
        }

    }

}
