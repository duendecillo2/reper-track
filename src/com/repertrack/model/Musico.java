package com.repertrack.model;

import com.repertrack.excepcion.LongitudExcedidaException;
import com.repertrack.util.Validador;

import java.util.Objects;

/**
 * Representa al músico usuario del sistema (monousuario en el prototipo).
 * Refactor TP3: nivel tipado con enum, validación en setters, throws checked.
 */
public class Musico {

    private int id;
    private String nombre;
    private Nivel nivel;
    private String estilo;

    public Musico() {
        this.id = 0;
        this.nivel = Nivel.INTERMEDIO;
        this.estilo = "";
    }

    public Musico(int id, String nombre, Nivel nivel) throws LongitudExcedidaException {
        this();
        this.id = id;
        setNombre(nombre);
        setNivel(nivel);
    }

    public Musico(int id, String nombre, String nivelStr) throws LongitudExcedidaException {
        this(id, nombre, Nivel.desde(nivelStr));
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }

    public void setNombre(String nombre) throws LongitudExcedidaException {
        Validador.exigirNoVacio("nombre", nombre);
        Validador.exigirLongitudMaxima("nombre", nombre, 100);
        this.nombre = nombre.trim();
    }

    public Nivel getNivel() { return nivel; }

    public void setNivel(Nivel nivel) {
        this.nivel = nivel == null ? Nivel.INTERMEDIO : nivel;
    }

    public String getEstilo() { return estilo; }

    public void setEstilo(String estilo) throws LongitudExcedidaException {
        Validador.exigirLongitudMaxima("estilo", estilo, 50);
        this.estilo = estilo == null ? "" : estilo.trim();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Musico)) return false;
        Musico m = (Musico) o;
        return id == m.id;
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public String toString() {
        return "Musico{id=" + id + ", nombre='" + nombre + "', nivel=" + nivel + "}";
    }
}