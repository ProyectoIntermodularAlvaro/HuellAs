package huellas.controller;

import huellas.model.Animal;
import huellas.model.Dueno;
import huellas.net.ConexionServidor;
import huellas.net.MensajeRequest;
import huellas.net.MensajeResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/** Une la vista con el servidor: traduce las acciones del usuario en peticiones. */
public class HuellasController {

    private final ConexionServidor conexion;
    private Dueno usuario;

    public HuellasController(ConexionServidor conexion) {
        this.conexion = conexion;
    }

    public MensajeResponse registrar(String nombre, String email, String password, String telefono)
            throws IOException {
        return conexion.enviar(new MensajeRequest(MensajeRequest.REGISTRAR)
                .con("nombre", nombre)
                .con("email", email)
                .con("password", password)
                .con("telefono", telefono));
    }

    public MensajeResponse login(String email, String password) throws IOException {
        MensajeResponse respuesta = conexion.enviar(new MensajeRequest(MensajeRequest.LOGIN)
                .con("email", email)
                .con("password", password));
        if (respuesta.isCorrecto()) {
            usuario = (Dueno) respuesta.getDatos();
        }
        return respuesta;
    }

    public void logout() throws IOException {
        conexion.enviar(new MensajeRequest(MensajeRequest.LOGOUT));
        usuario = null;
    }

    public MensajeResponse nuevaMascota(String nombre, String especie, String raza, int edad)
            throws IOException {
        return conexion.enviar(new MensajeRequest(MensajeRequest.NUEVA_MASCOTA)
                .con("nombre", nombre)
                .con("especie", especie)
                .con("raza", raza)
                .con("edad", edad));
    }

    @SuppressWarnings("unchecked")
    public List<Animal> listarMascotas() throws IOException {
        MensajeResponse respuesta = conexion.enviar(new MensajeRequest(MensajeRequest.LISTAR_MASCOTAS));
        if (!respuesta.isCorrecto()) {
            throw new IOException(respuesta.getMensaje());
        }
        return respuesta.getDatos() == null ? new ArrayList<>() : (List<Animal>) respuesta.getDatos();
    }

    public Dueno getUsuario() {
        return usuario;
    }

    public void cerrar() throws IOException {
        conexion.close();
    }
}
