package huellas.view;

import huellas.controller.HuellasController;
import java.awt.CardLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class VentanaPrincipal extends JFrame {

    private static final long serialVersionUID = 1L;

    private final HuellasController controlador;
    private final CardLayout cartas = new CardLayout();
    private final JPanel paneles = new JPanel(cartas);
    private final PanelLogin panelLogin;
    private final PanelMascotas panelMascotas;
    private Runnable alCerrar = () -> { };

    public VentanaPrincipal(HuellasController controlador) {
        super("HuellAs");
        this.controlador = controlador;
        this.panelLogin = new PanelLogin(this);
        this.panelMascotas = new PanelMascotas(this);

        paneles.add(panelLogin, "login");
        paneles.add(new PanelRegistro(this), "registro");
        paneles.add(panelMascotas, "mascotas");
        add(paneles);

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                cerrar();
            }
        });
        setSize(600, 400);
        setLocationRelativeTo(null);
    }

    public void setAlCerrar(Runnable accion) {
        this.alCerrar = accion;
    }

    public HuellasController getControlador() {
        return controlador;
    }

    public void mostrarLogin() {
        cartas.show(paneles, "login");
    }

    public void mostrarRegistro() {
        cartas.show(paneles, "registro");
    }

    public void mostrarMascotas() {
        panelMascotas.cargar();
        cartas.show(paneles, "mascotas");
    }

    public void volverAlLogin(String email) {
        panelLogin.prepararPara(email);
        mostrarLogin();
    }

    public void cerrarSesion() {
        try {
            controlador.logout();
        } catch (IOException e) {
            mostrarError(e.getMessage());
        }
        mostrarLogin();
    }

    public void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "HuellAs", JOptionPane.ERROR_MESSAGE);
    }

    private void cerrar() {
        try {
            controlador.cerrar();
        } catch (IOException e) {
            System.err.println("Error al cerrar la conexión: " + e.getMessage());
        }
        dispose();
        alCerrar.run();
    }
}
