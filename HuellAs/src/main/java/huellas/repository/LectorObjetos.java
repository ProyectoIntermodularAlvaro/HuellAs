package huellas.repository;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;

public class LectorObjetos<E extends Serializable> {

    private final File fichero;

    public LectorObjetos(String ruta) {
        this.fichero = new File(ruta);
    }

    public boolean existe() {
        return fichero.isFile();
    }

    @SuppressWarnings("unchecked")
    public ArrayList<E> lee() throws IOException {
        ArrayList<E> lista = new ArrayList<>();
        if (!existe()) {
            return lista;
        }
        try (ObjectInputStream entrada = new ObjectInputStream(new FileInputStream(fichero))) {
            while (true) {
                lista.add((E) entrada.readObject());
            }
        } catch (EOFException fin) {
            // Fin del fichero: ya se han leído todos los objetos.
            return lista;
        } catch (ClassNotFoundException e) {
            throw new IOException("Fichero no reconocido: " + fichero.getName(), e);
        }
    }
}
