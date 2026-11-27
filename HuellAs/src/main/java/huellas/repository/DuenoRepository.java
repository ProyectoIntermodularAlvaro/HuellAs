package huellas.repository;

import huellas.model.Dueno;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DuenoRepository {

    private final EscritorObjetos<Dueno> escritor;
    private final ArrayList<Dueno> duenos;

    public DuenoRepository(String carpeta) throws IOException {
        String ruta = carpeta + "/duenos.dat";
        this.escritor = new EscritorObjetos<>(ruta);
        this.duenos = new LectorObjetos<Dueno>(ruta).lee();
    }

    public void guardar(Dueno dueno) throws IOException {
        duenos.add(dueno);
        try {
            escritor.escribe(duenos);
        } catch (IOException e) {
            duenos.remove(dueno);
            throw e;
        }
    }

    public Dueno buscarPorEmail(String email) {
        if (email == null) {
            return null;
        }
        for (Dueno d : duenos) {
            if (d.getEmail().equalsIgnoreCase(email.trim())) {
                return d;
            }
        }
        return null;
    }

    public Dueno buscarPorId(int id) {
        for (Dueno d : duenos) {
            if (d.getId() == id) {
                return d;
            }
        }
        return null;
    }

    public List<Dueno> listar() {
        return new ArrayList<>(duenos);
    }

    public int siguienteId() {
        int max = 0;
        for (Dueno d : duenos) {
            max = Math.max(max, d.getId());
        }
        return max + 1;
    }
}
