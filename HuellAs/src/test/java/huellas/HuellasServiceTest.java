package huellas;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import huellas.model.Dueno;
import huellas.repository.AnimalRepository;
import huellas.repository.DuenoRepository;
import huellas.service.HuellasService;
import java.io.IOException;
import java.nio.file.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class HuellasServiceTest {

    @TempDir
    Path carpeta;

    private HuellasService servicio;

    @BeforeEach
    void preparar() throws IOException {
        servicio = new HuellasService(new DuenoRepository(carpeta.toString()),
                new AnimalRepository(carpeta.toString()));
    }

    private String errorAlRegistrar(String nombre, String email, String password, String telefono) {
        return assertThrows(IllegalArgumentException.class,
                () -> servicio.registrar(nombre, email, password, telefono)).getMessage();
    }

    @Test
    void registroCorrecto() throws IOException {
        Dueno dueno = servicio.registrar("Alvaro", "Alvaro@Huellas.ES", "Huellas2026", "926 123 456");
        assertEquals(1, dueno.getId());
        assertEquals("alvaro@huellas.es", dueno.getEmail());
        assertEquals("926123456", dueno.getTelefono());
    }

    @Test
    void losIdsSonCorrelativos() throws IOException {
        assertEquals(1, servicio.registrar("Alvaro", "a@huellas.es", "Clave1234", "926123456").getId());
        assertEquals(2, servicio.registrar("Ana", "b@huellas.es", "Clave1234", "926123456").getId());
    }

    @Test
    void noSeAdmitenDosCuentasConElMismoEmail() throws IOException {
        servicio.registrar("Alvaro", "a@huellas.es", "Clave1234", "926123456");
        assertEquals("Ya existe un usuario con ese email",
                errorAlRegistrar("Otro", "A@HUELLAS.ES", "Clave1234", "926123456"));
    }

    @Test
    void emailInvalido() {
        for (String malo : new String[] {"sin-arroba", "a@", "@huellas.es", "a@huellas"}) {
            assertEquals("El email no es válido",
                    errorAlRegistrar("Alvaro", malo, "Clave1234", "926123456"));
        }
    }

    @Test
    void contrasenaCorta() {
        assertEquals("La contraseña debe tener al menos 8 caracteres",
                errorAlRegistrar("Alvaro", "a@huellas.es", "corta", "926123456"));
    }

    @Test
    void telefonoInvalido() {
        assertEquals("El teléfono debe tener entre 9 y 15 dígitos",
                errorAlRegistrar("Alvaro", "a@huellas.es", "Clave1234", "12345678"));
        assertEquals("El teléfono debe tener entre 9 y 15 dígitos",
                errorAlRegistrar("Alvaro", "a@huellas.es", "Clave1234", "letras"));
    }

    @Test
    void nombreObligatorio() {
        assertEquals("El nombre es obligatorio",
                errorAlRegistrar("  ", "a@huellas.es", "Clave1234", "926123456"));
    }

    @Test
    void loginCorrecto() throws IOException {
        Dueno alta = servicio.registrar("Alvaro", "a@huellas.es", "Clave1234", "926123456");
        assertEquals(alta.getId(), servicio.login(" A@Huellas.ES ", "Clave1234").getId());
    }

    @Test
    void elErrorDeLoginEsIgualFalleElEmailOLaPassword() throws IOException {
        servicio.registrar("Alvaro", "a@huellas.es", "Clave1234", "926123456");
        String passwordMala = assertThrows(IllegalArgumentException.class,
                () -> servicio.login("a@huellas.es", "otra")).getMessage();
        String emailInexistente = assertThrows(IllegalArgumentException.class,
                () -> servicio.login("nadie@huellas.es", "Clave1234")).getMessage();
        assertEquals(passwordMala, emailInexistente);
    }

    @Test
    void altaYListadoDeMascotas() throws IOException {
        Dueno dueno = servicio.registrar("Alvaro", "a@huellas.es", "Clave1234", "926123456");
        servicio.registrarMascota(dueno.getId(), "Toby", "Perro", "Mestizo", 3);
        servicio.registrarMascota(dueno.getId(), "Luna", "Gato", "", 2);

        assertEquals(2, servicio.listarMascotas(dueno.getId()).size());
        assertEquals("Toby", servicio.listarMascotas(dueno.getId()).get(0).getNombre());
    }

    @Test
    void cadaDuenoVeSoloSusMascotas() throws IOException {
        Dueno alvaro = servicio.registrar("Alvaro", "a@huellas.es", "Clave1234", "926123456");
        Dueno ana = servicio.registrar("Ana", "b@huellas.es", "Clave1234", "926123456");
        servicio.registrarMascota(alvaro.getId(), "Toby", "Perro", "", 3);

        assertEquals(1, servicio.listarMascotas(alvaro.getId()).size());
        assertTrue(servicio.listarMascotas(ana.getId()).isEmpty());
    }

    @Test
    void mascotaDeUnDuenoQueNoExiste() {
        assertEquals("El dueño no existe",
                assertThrows(IllegalArgumentException.class,
                        () -> servicio.registrarMascota(99, "Toby", "Perro", "", 3)).getMessage());
    }

    @Test
    void unAltaRechazadaNoDejaRastro() throws IOException {
        assertThrows(IllegalArgumentException.class,
                () -> servicio.registrar("Alvaro", "mal-email", "Clave1234", "926123456"));
        assertEquals(1, servicio.registrar("Alvaro", "a@huellas.es", "Clave1234", "926123456").getId());
    }

    @Test
    void losDatosSobrevivenAlReinicio() throws IOException {
        Dueno dueno = servicio.registrar("Alvaro", "a@huellas.es", "Clave1234", "926123456");
        servicio.registrarMascota(dueno.getId(), "Toby", "Perro", "Mestizo", 3);

        HuellasService otro = new HuellasService(new DuenoRepository(carpeta.toString()),
                new AnimalRepository(carpeta.toString()));
        Dueno recuperado = otro.login("a@huellas.es", "Clave1234");
        assertEquals(1, otro.listarMascotas(recuperado.getId()).size());
        assertEquals("Toby", otro.listarMascotas(recuperado.getId()).get(0).getNombre());
    }
}
