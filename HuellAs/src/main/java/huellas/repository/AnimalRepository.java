package huellas.repository;

import huellas.model.Animal;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AnimalRepository {

    private final EscritorObjetos<Animal> escritor;
    private final ArrayList<Animal> animales;

    public AnimalRepository(String carpeta) throws IOException {
        String ruta = carpeta + "/animales.dat";
        this.escritor = new EscritorObjetos<>(ruta);
        this.animales = new LectorObjetos<Animal>(ruta).lee();
    }

    public void guardar(Animal animal) throws IOException {
        animales.add(animal);
        try {
            escritor.escribe(animales);
        } catch (IOException e) {
            animales.remove(animal);
            throw e;
        }
    }

    public List<Animal> listarPorDueno(int idDueno) {
        List<Animal> suyos = new ArrayList<>();
        for (Animal a : animales) {
            if (a.getIdDueno() == idDueno) {
                suyos.add(a);
            }
        }
        return suyos;
    }

    public List<Animal> listar() {
        return new ArrayList<>(animales);
    }

    public int siguienteId() {
        int max = 0;
        for (Animal a : animales) {
            max = Math.max(max, a.getId());
        }
        return max + 1;
    }
}
