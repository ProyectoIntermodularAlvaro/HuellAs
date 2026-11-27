package huellas.service;

import huellas.model.Animal;
import huellas.model.Dueno;
import huellas.repository.AnimalRepository;
import huellas.repository.DuenoRepository;
import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

public class HuellasService {

    private static final Pattern EMAIL = Pattern.compile("^[\\w.%+-]+@[\\w.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern TELEFONO = Pattern.compile("^\\d{9,15}$");

    private final DuenoRepository duenos;
    private final AnimalRepository animales;

    public HuellasService(DuenoRepository duenos, AnimalRepository animales) {
        this.duenos = duenos;
        this.animales = animales;
    }

    // Los metodos son synchronized porque el servidor atiende a varios clientes a la vez
    // y todos trabajan sobre los mismos repositorios.

    public synchronized Dueno registrar(String nombre, String email, String password, String telefono)
            throws IOException {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }
        if (email == null || !EMAIL.matcher(email.trim()).matches()) {
            throw new IllegalArgumentException("El email no es válido");
        }
        if (password == null || password.length() < 8) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 8 caracteres");
        }
        String tel = telefono == null ? "" : telefono.replace(" ", "");
        if (!TELEFONO.matcher(tel).matches()) {
            throw new IllegalArgumentException("El teléfono debe tener entre 9 y 15 dígitos");
        }
        if (duenos.buscarPorEmail(email) != null) {
            throw new IllegalArgumentException("Ya existe un usuario con ese email");
        }

        Dueno dueno = new Dueno(duenos.siguienteId(), nombre.trim(),
                email.trim().toLowerCase(), password, tel);
        duenos.guardar(dueno);
        return dueno;
    }

    public synchronized Dueno login(String email, String password) {
        Dueno dueno = duenos.buscarPorEmail(email);
        if (dueno == null || !dueno.login(email, password)) {
            throw new IllegalArgumentException("Email o contraseña incorrectos");
        }
        return dueno;
    }

    public synchronized Animal registrarMascota(int idDueno, String nombre, String especie,
            String raza, int edad) throws IOException {
        Dueno dueno = duenos.buscarPorId(idDueno);
        if (dueno == null) {
            throw new IllegalArgumentException("El dueño no existe");
        }
        Animal animal = dueno.registrarMascota(animales.siguienteId(), nombre, especie, raza, edad);
        animales.guardar(animal);
        return animal;
    }

    public synchronized List<Animal> listarMascotas(int idDueno) {
        return animales.listarPorDueno(idDueno);
    }
}
