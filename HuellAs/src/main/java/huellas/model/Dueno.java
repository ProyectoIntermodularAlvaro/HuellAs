package huellas.model;

public class Dueno extends Usuario {

    private static final long serialVersionUID = 1L;

    public Dueno() {
        super();
    }

    public Dueno(int id, String nombre, String email, String password, String telefono) {
        super(id, nombre, email, password, telefono);
    }

    public Animal registrarMascota(int idAnimal, String nombre, String especie, String raza, int edad) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del animal es obligatorio");
        }
        if (especie == null || especie.isBlank()) {
            throw new IllegalArgumentException("La especie del animal es obligatoria");
        }
        if (edad < 0 || edad > 40) {
            throw new IllegalArgumentException("La edad debe estar entre 0 y 40 años");
        }
        return new Animal(idAnimal, nombre.trim(), especie.trim(),
                raza == null ? "" : raza.trim(), edad, this.id);
    }
}
