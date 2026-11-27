package huellas.net;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ConexionServidor implements AutoCloseable {

    public static final String HOST = "localhost";
    public static final int PUERTO = 5000;

    private final Socket socket;
    private final ObjectOutputStream salida;
    private final ObjectInputStream entrada;

    public ConexionServidor(String host, int puerto) throws IOException {
        socket = new Socket(host, puerto);
        // El flujo de salida se crea y se vacia primero: si los dos extremos crearan antes
        // el de entrada, ambos quedarian esperando la cabecera del otro.
        salida = new ObjectOutputStream(socket.getOutputStream());
        salida.flush();
        entrada = new ObjectInputStream(socket.getInputStream());
    }

    public MensajeResponse enviar(MensajeRequest peticion) throws IOException {
        try {
            salida.writeObject(peticion);
            salida.reset();
            salida.flush();
            return (MensajeResponse) entrada.readObject();
        } catch (ClassNotFoundException e) {
            throw new IOException("Respuesta no válida del servidor", e);
        }
    }

    @Override
    public void close() throws IOException {
        socket.close();
    }
}
