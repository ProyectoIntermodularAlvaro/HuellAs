package huellas.view;

import huellas.net.MensajeResponse;
import huellas.viewmodel.MascotasTableModel;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.io.IOException;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

public class PanelMascotas extends JPanel {

    private static final long serialVersionUID = 1L;

    private final VentanaPrincipal ventana;
    private final MascotasTableModel modelo = new MascotasTableModel();
    private final JLabel usuario = new JLabel();

    public PanelMascotas(VentanaPrincipal ventana) {
        this.ventana = ventana;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel cabecera = new JPanel(new BorderLayout());
        cabecera.add(usuario, BorderLayout.WEST);
        JButton salir = new JButton("Cerrar sesión");
        cabecera.add(salir, BorderLayout.EAST);
        add(cabecera, BorderLayout.NORTH);

        add(new JScrollPane(new JTable(modelo)), BorderLayout.CENTER);

        JButton nueva = new JButton("Registrar mascota");
        JButton actualizar = new JButton("Actualizar");
        JPanel botones = new JPanel();
        botones.add(nueva);
        botones.add(actualizar);
        add(botones, BorderLayout.SOUTH);

        nueva.addActionListener(e -> nuevaMascota());
        actualizar.addActionListener(e -> cargar());
        salir.addActionListener(e -> ventana.cerrarSesion());
    }

    public void cargar() {
        usuario.setText("Usuario: " + ventana.getControlador().getUsuario().getNombre());
        try {
            modelo.setMascotas(ventana.getControlador().listarMascotas());
        } catch (IOException e) {
            ventana.mostrarError(e.getMessage());
        }
    }

    private void nuevaMascota() {
        JTextField nombre = new JTextField();
        JTextField especie = new JTextField();
        JTextField raza = new JTextField();
        JTextField edad = new JTextField();

        JPanel formulario = new JPanel(new GridLayout(4, 2, 5, 5));
        formulario.add(new JLabel("Nombre:"));
        formulario.add(nombre);
        formulario.add(new JLabel("Especie:"));
        formulario.add(especie);
        formulario.add(new JLabel("Raza:"));
        formulario.add(raza);
        formulario.add(new JLabel("Edad:"));
        formulario.add(edad);

        int opcion = JOptionPane.showConfirmDialog(this, formulario, "Nueva mascota",
                JOptionPane.OK_CANCEL_OPTION);
        if (opcion != JOptionPane.OK_OPTION) {
            return;
        }

        int anios;
        try {
            anios = Integer.parseInt(edad.getText().trim());
        } catch (NumberFormatException e) {
            ventana.mostrarError("La edad debe ser un número");
            return;
        }

        try {
            MensajeResponse respuesta = ventana.getControlador().nuevaMascota(nombre.getText().trim(),
                    especie.getText().trim(), raza.getText().trim(), anios);
            if (respuesta.isCorrecto()) {
                cargar();
            } else {
                ventana.mostrarError(respuesta.getMensaje());
            }
        } catch (IOException e) {
            ventana.mostrarError(e.getMessage());
        }
    }
}
