package huellas.model;

import java.io.Serializable;

public class Animal implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id;
    private String nombre;
    private String especie;
    private String raza;
    private int edad;
    private int idDueno;
    private EstadoAnimal estado;

    public Animal() {
        this.estado = EstadoAnimal.EN_CASA;
    }

    public Animal(int id, String nombre, String especie, String raza, int edad, int idDueno) {
        this.id = id;
        this.nombre = nombre;
        this.especie = especie;
        this.raza = raza;
        this.edad = edad;
        this.idDueno = idDueno;
        this.estado = EstadoAnimal.EN_CASA;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEspecie() {
        return especie;
    }

    public void setEspecie(String especie) {
        this.especie = especie;
    }

    public String getRaza() {
        return raza;
    }

    public void setRaza(String raza) {
        this.raza = raza;
    }

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

    public int getIdDueno() {
        return idDueno;
    }

    public void setIdDueno(int idDueno) {
        this.idDueno = idDueno;
    }

    public EstadoAnimal getEstado() {
        return estado;
    }

    public void setEstado(EstadoAnimal estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return nombre + " - " + especie + " (" + edad + " años)";
    }
}
