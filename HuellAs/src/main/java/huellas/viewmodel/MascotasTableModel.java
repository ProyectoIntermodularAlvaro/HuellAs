package huellas.viewmodel;

import huellas.model.Animal;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

public class MascotasTableModel extends AbstractTableModel {

    private static final long serialVersionUID = 1L;

    private static final String[] COLUMNAS = {"Id", "Nombre", "Especie", "Raza", "Edad", "Estado"};

    private final List<Animal> mascotas = new ArrayList<>();

    public void setMascotas(List<Animal> nuevas) {
        mascotas.clear();
        if (nuevas != null) {
            mascotas.addAll(nuevas);
        }
        fireTableDataChanged();
    }

    @Override
    public int getRowCount() {
        return mascotas.size();
    }

    @Override
    public int getColumnCount() {
        return COLUMNAS.length;
    }

    @Override
    public String getColumnName(int columna) {
        return COLUMNAS[columna];
    }

    @Override
    public boolean isCellEditable(int fila, int columna) {
        return false;
    }

    @Override
    public Object getValueAt(int fila, int columna) {
        Animal animal = mascotas.get(fila);
        switch (columna) {
            case 0:
                return animal.getId();
            case 1:
                return animal.getNombre();
            case 2:
                return animal.getEspecie();
            case 3:
                return animal.getRaza().isBlank() ? "-" : animal.getRaza();
            case 4:
                return animal.getEdad();
            case 5:
                return animal.getEstado();
            default:
                return "";
        }
    }
}
