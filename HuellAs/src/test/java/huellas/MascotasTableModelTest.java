package huellas;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import huellas.model.Animal;
import huellas.model.EstadoAnimal;
import huellas.viewmodel.MascotasTableModel;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MascotasTableModelTest {

    private MascotasTableModel modelo;

    @BeforeEach
    void preparar() {
        modelo = new MascotasTableModel();
    }

    @Test
    void modeloVacio() {
        assertEquals(0, modelo.getRowCount());
        assertEquals(6, modelo.getColumnCount());
        assertEquals("Id", modelo.getColumnName(0));
    }

    @Test
    void cadaAnimalEsUnaFila() {
        modelo.setMascotas(List.of(new Animal(7, "Toby", "Perro", "Mestizo", 3, 1)));

        assertEquals(1, modelo.getRowCount());
        assertEquals(7, modelo.getValueAt(0, 0));
        assertEquals("Toby", modelo.getValueAt(0, 1));
        assertEquals("Perro", modelo.getValueAt(0, 2));
        assertEquals("Mestizo", modelo.getValueAt(0, 3));
        assertEquals(3, modelo.getValueAt(0, 4));
        assertEquals(EstadoAnimal.EN_CASA, modelo.getValueAt(0, 5));
    }

    @Test
    void razaVaciaSeMuestraConUnGuion() {
        modelo.setMascotas(List.of(new Animal(1, "Toby", "Perro", "", 3, 1)));
        assertEquals("-", modelo.getValueAt(0, 3));
    }

    @Test
    void ponerUnaListaNuevaSustituyeLaAnterior() {
        modelo.setMascotas(List.of(new Animal(1, "Toby", "Perro", "", 3, 1)));
        modelo.setMascotas(List.of(new Animal(2, "Luna", "Gato", "", 2, 1),
                new Animal(3, "Rex", "Perro", "", 5, 1)));
        assertEquals(2, modelo.getRowCount());
        assertEquals("Luna", modelo.getValueAt(0, 1));
    }

    @Test
    void listaVaciaONula() {
        modelo.setMascotas(List.of(new Animal(1, "Toby", "Perro", "", 3, 1)));
        modelo.setMascotas(List.of());
        assertEquals(0, modelo.getRowCount());
        modelo.setMascotas(null);
        assertEquals(0, modelo.getRowCount());
    }

    @Test
    void lasCeldasNoSeEditan() {
        modelo.setMascotas(List.of(new Animal(1, "Toby", "Perro", "", 3, 1)));
        for (int columna = 0; columna < modelo.getColumnCount(); columna++) {
            assertFalse(modelo.isCellEditable(0, columna));
        }
    }
}
