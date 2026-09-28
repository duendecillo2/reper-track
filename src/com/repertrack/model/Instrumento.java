package com.repertrack.model;

import java.util.Objects;

/** Instrumento musical del músico. Entidad maestra del dominio. */
public class Instrumento {
    private int id;
    private String nombre;
    private String tipo; // cuerda, viento, percusión, teclado u otro
    private String observaciones;
    private boolean activo = true; // baja lógica

    public Instrumento(int id, String nombre, String tipo) {
        if (nombre == null || nombre.isBlank()) throw new IllegalArgumentException("El nombre del instrumento es obligatorio.");
        if (tipo == null || tipo.isBlank()) throw new IllegalArgumentException("El tipo del instrumento es obligatorio.");
        this.id = id;
        this.nombre = nombre;
        this.tipo = tipo;
    }

    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = Objects.requireNonNull(nombre); }
    public String getTipo() { return tipo; }
    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
    public boolean isActivo() { return activo; }
    public void darDeBaja() { this.activo = false; }
}
