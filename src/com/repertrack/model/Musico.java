package com.repertrack.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** Representa al músico usuario del sistema (monousuario en el prototipo). */
public class Musico {
    private int id;
    private String nombre;
    private String nivel; // principiante, intermedio, avanzado
    private String estilo;
    private List<Instrumento> instrumentos = new ArrayList<>();

    public Musico(int id, String nombre, String nivel) {
        if (nombre == null || nombre.isBlank()) throw new IllegalArgumentException("El nombre del músico es obligatorio.");
        this.id = id;
        this.nombre = nombre;
        this.nivel = nivel;
    }

    public void agregarInstrumento(Instrumento i) {
        Objects.requireNonNull(i, "Instrumento nulo.");
        instrumentos.add(i);
    }

    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public String getNivel() { return nivel; }
    public String getEstilo() { return estilo; }
    public void setEstilo(String estilo) { this.estilo = estilo; }
    public List<Instrumento> getInstrumentos() { return instrumentos; }
}
