package huellas;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import huellas.model.Animal;
import huellas.model.Dueno;
import huellas.repository.AnimalRepository;
import huellas.repository.DuenoRepository;
import huellas.repository.EscritorObjetos;
import huellas.repository.LectorObjetos;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class RepositorioTest {

    @TempDir
    Path carpeta;

    private String ruta(String nombre) {
        return carpeta.resolve(nombre).toString();
    }

    @Test
    void loQueSeEscribeSeVuelveALeer() throws IOException {
        ArrayList<Dueno> lista = new ArrayList<>(List.of(
                new Dueno(1, "Alvaro", "alvaro@huellas.es", "Huellas2026", "926123456"),
                new Dueno(2, "Ana", "ana@huellas.es", "Clave1234", "600111222")));
        new EscritorObjetos<Dueno>(ruta("duenos.dat")).escribe(lista);

        ArrayList<Dueno> leidos = new LectorObjetos<Dueno>(ruta("duenos.dat")).lee();
        assertEquals(2, leidos.size());
        assertEquals("Alvaro", leidos.get(0).getNombre());
        assertTrue(leidos.get(0).login("alvaro@huellas.es", "Huellas2026"));
    }

    @Test
    void ficheroQueNoExisteDevuelveListaVacia() throws IOException {
        LectorObjetos<Dueno> lector = new LectorObjetos<>(ruta("nada.dat"));
        assertFalse(lector.existe());
        assertTrue(lector.lee().isEmpty());
    }

    @Test
    void listaVaciaSeGuardaYSeLeeVacia() throws IOException {
        new EscritorObjetos<Dueno>(ruta("duenos.dat")).escribe(new ArrayList<>());
        assertTrue(new LectorObjetos<Dueno>(ruta("duenos.dat")).lee().isEmpty());
    }

    @Test
    void escribirDosVecesSustituyeElContenido() throws IOException {
        EscritorObjetos<Dueno> escritor = new EscritorObjetos<>(ruta("duenos.dat"));
        escritor.escribe(new ArrayList<>(List.of(
                new Dueno(1, "Alvaro", "alvaro@huellas.es", "Huellas2026", "926123456"))));
        escritor.escribe(new ArrayList<>(List.of(
                new Dueno(2, "Ana", "ana@huellas.es", "Clave1234", "600111222"))));

        ArrayList<Dueno> leidos = new LectorObjetos<Dueno>(ruta("duenos.dat")).lee();
        assertEquals(1, leidos.size());
        assertEquals("Ana", leidos.get(0).getNombre());
    }

    @Test
    void noQuedaFicheroTemporal() throws IOException {
        new EscritorObjetos<Dueno>(ruta("duenos.dat")).escribe(new ArrayList<>(List.of(
                new Dueno(1, "Alvaro", "alvaro@huellas.es", "Huellas2026", "926123456"))));
        assertFalse(new File(ruta("duenos.dat.tmp")).exists());
    }

    @Test
    void ficheroDanadoLanzaExcepcion() throws IOException {
        File roto = carpeta.resolve("roto.dat").toFile();
        try (FileOutputStream fos = new FileOutputStream(roto)) {
            fos.write(new byte[] {1, 2, 3, 4});
        }
        assertThrows(IOException.class, () -> new LectorObjetos<Dueno>(roto.getPath()).lee());
    }

    @Test
    void elRepositorioRecuperaLosDatosAlVolverAAbrirse() throws IOException {
        DuenoRepository repo = new DuenoRepository(carpeta.toString());
        assertEquals(1, repo.siguienteId());
        repo.guardar(new Dueno(1, "Alvaro", "alvaro@huellas.es", "Huellas2026", "926123456"));

        DuenoRepository otro = new DuenoRepository(carpeta.toString());
        assertEquals(1, otro.listar().size());
        assertEquals(2, otro.siguienteId());
        assertEquals("Alvaro", otro.buscarPorEmail("ALVARO@HUELLAS.ES").getNombre());
        assertNull(otro.buscarPorEmail("nadie@huellas.es"));
    }

    @Test
    void elRepositorioDeAnimalesFiltraPorDueno() throws IOException {
        AnimalRepository repo = new AnimalRepository(carpeta.toString());
        repo.guardar(new Animal(1, "Toby", "Perro", "Mestizo", 3, 1));
        repo.guardar(new Animal(2, "Luna", "Gato", "", 2, 2));
        repo.guardar(new Animal(3, "Rex", "Perro", "", 5, 1));

        assertEquals(2, repo.listarPorDueno(1).size());
        assertEquals(1, repo.listarPorDueno(2).size());
        assertTrue(repo.listarPorDueno(99).isEmpty());
    }

    @Test
    void siFallaElDiscoNoSeQuedaElDatoEnMemoria() throws IOException {
        DuenoRepository repo = new DuenoRepository(carpeta.toString());
        // Un directorio con el nombre del temporal impide escribirlo.
        new File(ruta("duenos.dat.tmp")).mkdirs();

        assertThrows(IOException.class, () -> repo.guardar(
                new Dueno(1, "Alvaro", "alvaro@huellas.es", "Huellas2026", "926123456")));
        assertTrue(repo.listar().isEmpty());
        assertEquals(1, repo.siguienteId());
    }
}
