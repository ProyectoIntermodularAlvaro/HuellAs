package huellas.view;

import huellas.net.MensajeResponse;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.io.IOException;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class PanelRegistro extends JPanel {

    private static final long serialVersionUID = 1L;

    private final VentanaPrincipal ventana;
    private final JTextField nombre = new JTextField(18);
    private final JTextField email = new JTextField(18);
    private final JPasswordField password = new JPasswordField(18);
    private final JTextField telefono = new JTextField(18);
    private final JLabel aviso = new JLabel(" ", SwingConstants.CENTER);

    public PanelRegistro(VentanaPrincipal ventana) {
        this.ventana = ventana;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        JLabel titulo = new JLabel("Crear cuenta", SwingConstants.CENTER);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 20));
        add(titulo, BorderLayout.NORTH);

        JPanel campos = new JPanel(new GridLayout(4, 2, 8, 8));
        campos.add(new JLabel("Nombre:"));
        campos.add(nombre);
        campos.add(new JLabel("Email:"));
        campos.add(email);
        campos.add(new JLabel("Contraseña:"));
        campos.add(password);
        campos.add(new JLabel("Teléfono:"));
        campos.add(telefono);

        JPanel formulario = new JPanel(new BorderLayout(5, 10));
        formulario.add(campos, BorderLayout.CENTER);
        formulario.add(aviso, BorderLayout.SOUTH);

        // GridBagLayout deja el formulario centrado y con su tamaño natural.
        JPanel centro = new JPanel(new GridBagLayout());
        centro.add(formulario);
        add(centro, BorderLayout.CENTER);

        JButton crear = new JButton("Registrarse");
        JButton volver = new JButton("Volver");
        JPanel botones = new JPanel();
        botones.add(crear);
        botones.add(volver);
        add(botones, BorderLayout.SOUTH);

        crear.addActionListener(e -> crearCuenta());
        volver.addActionListener(e -> ventana.mostrarLogin());
    }

    private void crearCuenta() {
        String correo = email.getText().trim();
        try {
            MensajeResponse respuesta = ventana.getControlador().registrar(nombre.getText().trim(),
                    correo, new String(password.getPassword()), telefono.getText().trim());
            if (respuesta.isCorrecto()) {
                limpiar();
                ventana.volverAlLogin(correo);
            } else {
                aviso.setText(respuesta.getMensaje());
            }
        } catch (IOException e) {
            ventana.mostrarError(e.getMessage());
        }
    }

    private void limpiar() {
        nombre.setText("");
        email.setText("");
        password.setText("");
        telefono.setText("");
        aviso.setText(" ");
    }
}
