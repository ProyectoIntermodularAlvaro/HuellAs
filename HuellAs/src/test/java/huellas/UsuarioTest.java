package huellas;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import huellas.model.Animal;
import huellas.model.Dueno;
import huellas.model.EstadoAnimal;
import huellas.model.Usuario;
import org.junit.jupiter.api.Test;

class UsuarioTest {

    private final Dueno dueno = new Dueno(1, "Alvaro", "alvaro@huellas.es", "Huellas2026", "926123456");

    @Test
    void loginCorrecto() {
        assertTrue(dueno.login("alvaro@huellas.es", "Huellas2026"));
    }

    @Test
    void loginIgnoraMayusculasEnElEmail() {
        assertTrue(dueno.login("ALVARO@HUELLAS.ES", "Huellas2026"));
        assertTrue(dueno.login("  alvaro@huellas.es ", "Huellas2026"));
    }

    @Test
    void loginFallaConDatosIncorrectos() {
        assertFalse(dueno.login("alvaro@huellas.es", "otra"));
        assertFalse(dueno.login("otro@huellas.es", "Huellas2026"));
    }

    @Test
    void loginConNulosDevuelveFalse() {
        assertFalse(dueno.login(null, "Huellas2026"));
        assertFalse(dueno.login("alvaro@huellas.es", null));
    }

    @Test
    void duenoHeredaDeUsuario() {
        assertTrue(dueno instanceof Usuario);
        assertEquals("Alvaro", dueno.getNombre());
    }

    @Test
    void registrarMascotaDevuelveAnimalEnCasa() {
        Animal toby = dueno.registrarMascota(1, "Toby", "Perro", "Mestizo", 3);
        assertEquals("Toby", toby.getNombre());
        assertEquals("Perro", toby.getEspecie());
        assertEquals(3, toby.getEdad());
        assertEquals(EstadoAnimal.EN_CASA, toby.getEstado());
        assertEquals(dueno.getId(), toby.getIdDueno());
    }

    @Test
    void registrarMascotaSinRazaLaDejaVacia() {
        assertEquals("", dueno.registrarMascota(1, "Toby", "Perro", null, 3).getRaza());
    }

    @Test
    void registrarMascotaExigeNombreYEspecie() {
        assertThrows(IllegalArgumentException.class,
                () -> dueno.registrarMascota(1, "  ", "Perro", "", 3));
        assertThrows(IllegalArgumentException.class,
                () -> dueno.registrarMascota(1, "Toby", null, "", 3));
    }

    @Test
    void laEdadDebeEstarEntreCeroYCuarenta() {
        assertEquals(0, dueno.registrarMascota(1, "Toby", "Perro", "", 0).getEdad());
        assertEquals(40, dueno.registrarMascota(2, "Tortuga", "Tortuga", "", 40).getEdad());
        assertThrows(IllegalArgumentException.class, () -> dueno.registrarMascota(3, "Toby", "Perro", "", -1));
        assertThrows(IllegalArgumentException.class, () -> dueno.registrarMascota(4, "Toby", "Perro", "", 41));
    }
}
