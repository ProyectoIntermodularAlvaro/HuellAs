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

public class PanelLogin extends JPanel {

    private static final long serialVersionUID = 1L;

    private final VentanaPrincipal ventana;
    private final JTextField email = new JTextField(18);
    private final JPasswordField password = new JPasswordField(18);
    private final JLabel aviso = new JLabel(" ", SwingConstants.CENTER);

    public PanelLogin(VentanaPrincipal ventana) {
        this.ventana = ventana;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        JLabel titulo = new JLabel("HuellAs", SwingConstants.CENTER);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 24));
        add(titulo, BorderLayout.NORTH);

        JPanel campos = new JPanel(new GridLayout(2, 2, 8, 8));
        campos.add(new JLabel("Email:"));
        campos.add(email);
        campos.add(new JLabel("Contraseña:"));
        campos.add(password);

        JPanel formulario = new JPanel(new BorderLayout(5, 10));
        formulario.add(campos, BorderLayout.CENTER);
        formulario.add(aviso, BorderLayout.SOUTH);

        // GridBagLayout deja el formulario centrado y con su tamaño natural.
        JPanel centro = new JPanel(new GridBagLayout());
        centro.add(formulario);
        add(centro, BorderLayout.CENTER);

        JButton entrar = new JButton("Entrar");
        JButton registrarse = new JButton("Crear cuenta");
        JPanel botones = new JPanel();
        botones.add(entrar);
        botones.add(registrarse);
        add(botones, BorderLayout.SOUTH);

        entrar.addActionListener(e -> entrar());
        password.addActionListener(e -> entrar());
        registrarse.addActionListener(e -> ventana.mostrarRegistro());
    }

    private void entrar() {
        String correo = email.getText().trim();
        String clave = new String(password.getPassword());
        if (correo.isEmpty() || clave.isEmpty()) {
            aviso.setText("Rellena el email y la contraseña");
            return;
        }
        try {
            MensajeResponse respuesta = ventana.getControlador().login(correo, clave);
            if (respuesta.isCorrecto()) {
                password.setText("");
                aviso.setText(" ");
                ventana.mostrarMascotas();
            } else {
                aviso.setText(respuesta.getMensaje());
            }
        } catch (IOException e) {
            ventana.mostrarError(e.getMessage());
        }
    }

    public void prepararPara(String correo) {
        email.setText(correo);
        password.setText("");
        aviso.setText("Cuenta creada, ya puedes entrar");
    }
}
