package com.repertrack.model;

import com.repertrack.excepcion.LongitudExcedidaException;
import com.repertrack.excepcion.RangoInvalidoException;
import com.repertrack.util.Validador;

import java.util.Objects;

/**
 * Superclase abstracta del repertorio. Aplica herencia: una obra no se
 * estudia igual que un ejercicio técnico, y el modelo lo reconoce.
 *
 * Refactor TP3:
 *   - instrumentoId (int, atributo) -> asociacion Instrumento (rol: instrumento).
 *   - Validaciones via Validador + checked exceptions.
 *   - calcularProgreso() sigue abstract para garantizar polimorfismo (RN-POO-1).
 */
public abstract class ElementoDeEstudio {

    private int id;
    private String nombre;
    private int dificultad;       // 1 a 10
    private Instrumento instrumento; // ASOCIACION (no atributo int)
    private int tiempoEstimadoDominioMin;
    private boolean activo = true;

    protected ElementoDeEstudio() {
        this.id = 0;
        this.nombre = "";
        this.dificultad = 1;
        this.instrumento = null;
        this.tiempoEstimadoDominioMin = 60;
    }

    protected ElementoDeEstudio(int id, Instrumento instrumento, String nombre,
                                int dificultad, int tiempoEstimadoDominioMin)
            throws RangoInvalidoException, LongitudExcedidaException {
        this();
        this.id = id;
        setInstrumento(instrumento);
        setNombre(nombre);
        setDificultad(dificultad);
        setTiempoEstimadoDominioMin(tiempoEstimadoDominioMin);
    }

    /** Compatibilidad hacia atras (no recomendado, usar el otro constructor). */
    protected ElementoDeEstudio(int id, String nombre, int dificultad, int instrumentoId)
            throws RangoInvalidoException, LongitudExcedidaException {
        this(id, null, nombre, dificultad, 60);
    }

    /** Progreso estimado 0..1 según minutos acumulados vs. una meta. Polimórfico. */
    public abstract double calcularProgreso(int minutosAcumulados);

    public abstract String getCategoria();

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }

    public void setNombre(String nombre) throws LongitudExcedidaException {
        Validador.exigirNoVacio("nombre", nombre);
        Validador.exigirLongitudMaxima("nombre", nombre, 150);
        this.nombre = nombre.trim();
    }

    public int getDificultad() { return dificultad; }

    public void setDificultad(int dificultad) throws RangoInvalidoException {
        Validador.exigirRangoEntero("dificultad", dificultad, 1, 10);
        this.dificultad = dificultad;
    }

    public Instrumento getInstrumento() { return instrumento; }

    public void setInstrumento(Instrumento instrumento) {
        if (instrumento == null) {
            throw new IllegalArgumentException("El instrumento no puede ser nulo (FK obligatoria).");
        }
        this.instrumento = instrumento;
    }

    /** Mantener compatibilidad hacia atras (vistas que consultaban el id). */
    public int getInstrumentoId() {
        return instrumento == null ? 0 : instrumento.getId();
    }

    public int getTiempoEstimadoDominioMin() { return tiempoEstimadoDominioMin; }

    public void setTiempoEstimadoDominioMin(int min) throws RangoInvalidoException {
        Validador.exigirRangoEntero("tiempoEstimadoDominioMin", min, 1, 100000);
        this.tiempoEstimadoDominioMin = min;
    }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    public void darDeBaja() { this.activo = false; }
    public void activar() { this.activo = true; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ElementoDeEstudio)) return false;
        ElementoDeEstudio e = (ElementoDeEstudio) o;
        return id == e.id;
    }

    @Override
    public int hashCode() { return Objects.hash(id); }
}