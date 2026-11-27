package huellas.repository;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;

public class EscritorObjetos<E extends Serializable> {

    private final File fichero;

    public EscritorObjetos(String ruta) {
        this.fichero = new File(ruta);
    }

    public void escribe(ArrayList<E> lista) throws IOException {
        File carpeta = fichero.getAbsoluteFile().getParentFile();
        if (carpeta != null) {
            carpeta.mkdirs();
        }

        // Se escribe en un temporal y se mueve al final, para no dejar el fichero
        // a medias si el programa se interrumpe durante el guardado.
        File temporal = new File(fichero.getAbsolutePath() + ".tmp");
        try (ObjectOutputStream salida = new ObjectOutputStream(new FileOutputStream(temporal))) {
            for (E objeto : lista) {
                salida.writeObject(objeto);
            }
        }
        Files.move(temporal.toPath(), fichero.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }
}
