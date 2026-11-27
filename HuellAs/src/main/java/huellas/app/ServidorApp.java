package huellas.app;

import huellas.net.ConexionServidor;
import huellas.net.Servidor;
import huellas.repository.AnimalRepository;
import huellas.repository.DuenoRepository;
import huellas.service.HuellasService;
import java.io.IOException;

/**
 * Arranca solo el servidor, para probar la aplicación con el cliente en otra máquina.
 */
public class ServidorApp {

    public static void main(String[] args) throws IOException {
        int puerto = args.length > 0 ? Integer.parseInt(args[0]) : ConexionServidor.PUERTO;
        String carpeta = args.length > 1 ? args[1] : "datos";

        HuellasService servicio = new HuellasService(
                new DuenoRepository(carpeta), new AnimalRepository(carpeta));
        Servidor servidor = new Servidor(servicio);
        servidor.iniciar(puerto);
        System.out.println("Servidor HuellAs escuchando en el puerto " + servidor.getPuerto());
    }
}
