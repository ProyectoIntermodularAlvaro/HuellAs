package huellas.app;

import huellas.controller.HuellasController;
import huellas.net.ConexionServidor;
import huellas.net.Servidor;
import huellas.repository.AnimalRepository;
import huellas.repository.DuenoRepository;
import huellas.service.HuellasService;
import huellas.view.VentanaPrincipal;
import java.io.IOException;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Arranca el servidor y abre la ventana. Es la clase que ejecuta NetBeans con Run Project.
 */
public class MainApp {

    private static final String CARPETA = "datos";

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Se usa el aspecto por defecto");
        }

        try {
            HuellasService servicio = new HuellasService(
                    new DuenoRepository(CARPETA), new AnimalRepository(CARPETA));
            Servidor servidor = new Servidor(servicio);
            servidor.iniciar(0);

            HuellasController controlador = new HuellasController(
                    new ConexionServidor(ConexionServidor.HOST, servidor.getPuerto()));

            SwingUtilities.invokeLater(() -> {
                VentanaPrincipal ventana = new VentanaPrincipal(controlador);
                ventana.setAlCerrar(servidor::detener);
                ventana.setVisible(true);
            });
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "No se pudo arrancar HuellAs: " + e.getMessage(),
                    "HuellAs", JOptionPane.ERROR_MESSAGE);
        }
    }
}
