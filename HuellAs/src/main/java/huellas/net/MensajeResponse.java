package huellas.net;

import java.io.Serializable;

public class MensajeResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private boolean correcto;
    private String mensaje;
    private Object datos;

    public MensajeResponse(boolean correcto, String mensaje, Object datos) {
        this.correcto = correcto;
        this.mensaje = mensaje;
        this.datos = datos;
    }

    public static MensajeResponse ok(String mensaje, Object datos) {
        return new MensajeResponse(true, mensaje, datos);
    }

    public static MensajeResponse error(String mensaje) {
        return new MensajeResponse(false, mensaje, null);
    }

    public boolean isCorrecto() {
        return correcto;
    }

    public void setCorrecto(boolean correcto) {
        this.correcto = correcto;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public Object getDatos() {
        return datos;
    }

    public void setDatos(Object datos) {
        this.datos = datos;
    }
}
