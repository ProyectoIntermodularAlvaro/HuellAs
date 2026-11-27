package huellas.net;

import huellas.model.Dueno;
import huellas.service.HuellasService;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

class ManejadorCliente implements Runnable {

    private final Socket socket;
    private final HuellasService servicio;
    private Dueno sesion;

    ManejadorCliente(Socket socket, HuellasService servicio) {
        this.socket = socket;
        this.servicio = servicio;
    }

    @Override
    public void run() {
        try (Socket s = socket;
                ObjectOutputStream salida = new ObjectOutputStream(s.getOutputStream())) {
            salida.flush();
            ObjectInputStream entrada = new ObjectInputStream(s.getInputStream());
            while (true) {
                MensajeRequest peticion = (MensajeRequest) entrada.readObject();
                salida.writeObject(procesar(peticion));
                salida.reset();
                salida.flush();
            }
        } catch (EOFException fin) {
            // El cliente ha cerrado la conexión.
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Conexión terminada: " + e.getMessage());
        }
    }

    MensajeResponse procesar(MensajeRequest peticion) {
        try {
            switch (peticion.getAccion()) {
                case MensajeRequest.REGISTRAR:
                    servicio.registrar(peticion.texto("nombre"), peticion.texto("email"),
                            peticion.texto("password"), peticion.texto("telefono"));
                    return MensajeResponse.ok("Cuenta creada correctamente", null);

                case MensajeRequest.LOGIN:
                    sesion = servicio.login(peticion.texto("email"), peticion.texto("password"));
                    return MensajeResponse.ok("Bienvenido", sinPassword(sesion));

                case MensajeRequest.LOGOUT:
                    sesion = null;
                    return MensajeResponse.ok("Sesión cerrada", null);

                case MensajeRequest.NUEVA_MASCOTA:
                    comprobarSesion();
                    servicio.registrarMascota(sesion.getId(), peticion.texto("nombre"),
                            peticion.texto("especie"), peticion.texto("raza"),
                            peticion.entero("edad"));
                    return MensajeResponse.ok("Mascota registrada", null);

                case MensajeRequest.LISTAR_MASCOTAS:
                    comprobarSesion();
                    return MensajeResponse.ok("OK",
                            new ArrayList<>(servicio.listarMascotas(sesion.getId())));

                default:
                    return MensajeResponse.error("Acción desconocida");
            }
        } catch (IllegalArgumentException e) {
            return MensajeResponse.error(e.getMessage());
        } catch (IOException e) {
            return MensajeResponse.error("No se pudieron guardar los datos");
        }
    }

    private void comprobarSesion() {
        if (sesion == null) {
            throw new IllegalArgumentException("Debes iniciar sesión");
        }
    }

    // La contraseña no sale del servidor.
    private static Dueno sinPassword(Dueno dueno) {
        return new Dueno(dueno.getId(), dueno.getNombre(), dueno.getEmail(), null, dueno.getTelefono());
    }
}
