package huellas.net;

import huellas.service.HuellasService;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Servidor {

    private final HuellasService servicio;
    private final ExecutorService hilos = Executors.newCachedThreadPool();
    private final List<Socket> clientes = new ArrayList<>();
    private ServerSocket serverSocket;

    public Servidor(HuellasService servicio) {
        this.servicio = servicio;
    }

    public void iniciar(int puerto) throws IOException {
        serverSocket = new ServerSocket(puerto);
        new Thread(this::aceptar).start();
    }

    public int getPuerto() {
        return serverSocket.getLocalPort();
    }

    private void aceptar() {
        while (!serverSocket.isClosed()) {
            try {
                Socket cliente = serverSocket.accept();
                synchronized (clientes) {
                    clientes.add(cliente);
                }
                hilos.execute(new ManejadorCliente(cliente, servicio));
            } catch (IOException e) {
                if (!serverSocket.isClosed()) {
                    System.err.println("Error al aceptar un cliente: " + e.getMessage());
                }
            }
        }
    }

    public void detener() {
        cerrar(serverSocket);
        synchronized (clientes) {
            // Hay que cerrar los sockets: un hilo bloqueado leyendo no atiende a la interrupción.
            for (Socket cliente : clientes) {
                cerrar(cliente);
            }
            clientes.clear();
        }
        hilos.shutdownNow();
    }

    private static void cerrar(java.io.Closeable recurso) {
        try {
            if (recurso != null) {
                recurso.close();
            }
        } catch (IOException e) {
            System.err.println("Error al cerrar: " + e.getMessage());
        }
    }
}
