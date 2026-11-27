package huellas.net;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class MensajeRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final String REGISTRAR = "REGISTRAR";
    public static final String LOGIN = "LOGIN";
    public static final String LOGOUT = "LOGOUT";
    public static final String NUEVA_MASCOTA = "NUEVA_MASCOTA";
    public static final String LISTAR_MASCOTAS = "LISTAR_MASCOTAS";

    private String accion;
    private Map<String, Object> parametros = new HashMap<>();

    public MensajeRequest(String accion) {
        this.accion = accion;
    }

    public MensajeRequest con(String nombre, Object valor) {
        parametros.put(nombre, valor);
        return this;
    }

    public String texto(String nombre) {
        Object valor = parametros.get(nombre);
        return valor == null ? null : valor.toString();
    }

    public int entero(String nombre) {
        Object valor = parametros.get(nombre);
        return valor instanceof Integer ? (Integer) valor : -1;
    }

    public String getAccion() {
        return accion;
    }

    public void setAccion(String accion) {
        this.accion = accion;
    }

    public Map<String, Object> getParametros() {
        return parametros;
    }

    public void setParametros(Map<String, Object> parametros) {
        this.parametros = parametros;
    }
}
