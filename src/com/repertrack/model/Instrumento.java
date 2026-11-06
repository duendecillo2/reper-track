package com.repertrack.model;

import com.repertrack.excepcion.LongitudExcedidaException;
import com.repertrack.util.Validador;

import java.util.Objects;

/**
 * Instrumento musical del músico. Entidad maestra del dominio.
 * Refactor TP3: asociación a Musico, tipo como enum, validación con excepciones.
 */
public class Instrumento {

    private int id;
    private Musico musico;
    private String nombre;
    private TipoInstrumento tipo;
    private String observaciones;
    private boolean activo = true;

    public Instrumento() {
        this.id = 0;
        this.tipo = TipoInstrumento.OTRO;
        this.observaciones = "";
    }

    public Instrumento(int id, Musico musico, String nombre, TipoInstrumento tipo, String observaciones)
            throws LongitudExcedidaException {
        this();
        this.id = id;
        setMusico(musico);
        setNombre(nombre);
        setTipo(tipo);
        setObservaciones(observaciones);
    }

    public Instrumento(int id, String nombre, String tipoStr) throws LongitudExcedidaException {
        this(id, null, nombre, TipoInstrumento.desde(tipoStr), "");
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Musico getMusico() { return musico; }
    public void setMusico(Musico musico) { this.musico = musico; }

    public String getNombre() { return nombre; }

    public void setNombre(String nombre) throws LongitudExcedidaException {
        Validador.exigirNoVacio("nombre", nombre);
        Validador.exigirLongitudMaxima("nombre", nombre, 100);
        this.nombre = nombre.trim();
    }

    public TipoInstrumento getTipo() { return tipo; }

    public void setTipo(TipoInstrumento tipo) {
        this.tipo = tipo == null ? TipoInstrumento.OTRO : tipo;
    }

    public String getObservaciones() { return observaciones; }

    public void setObservaciones(String observaciones) throws LongitudExcedidaException {
        Validador.exigirLongitudMaxima("observaciones", observaciones, 500);
        this.observaciones = observaciones == null ? "" : observaciones.trim();
    }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    /** Baja lógica: preserva historia sin romper FK. */
    public void darDeBaja() { this.activo = false; }
    public void activar() { this.activo = true; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Instrumento)) return false;
        Instrumento i = (Instrumento) o;
        return id == i.id;
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public String toString() {
        return "Instrumento{id=" + id + ", nombre='" + nombre + "', tipo=" + tipo + "}";
    }
}