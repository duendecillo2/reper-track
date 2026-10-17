package com.repertrack.model;

import com.repertrack.excepcion.LongitudExcedidaException;
import com.repertrack.excepcion.RangoInvalidoException;
import com.repertrack.excepcion.SesionSinReferenciaException;
import com.repertrack.util.Validador;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * Sesión de práctica cronometrada. Entidad transaccional del sistema.
 * Refactor TP3: asociaciones a Instrumento y ElementoDeEstudio (no int).
 */
public class SesionDePractica {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private int id;
    private Instrumento instrumento;
    private ElementoDeEstudio elemento;
    private LocalDateTime fechaRegistro;
    private int tiempoNetoMin;
    private String comentario;

    public SesionDePractica() {
        this.id = 0;
        this.instrumento = null;
        this.elemento = null;
        this.fechaRegistro = LocalDateTime.now();
        this.tiempoNetoMin = 0;
        this.comentario = "";
    }

    public SesionDePractica(Instrumento instrumento, ElementoDeEstudio elemento,
                            int tiempoNetoMin, String comentario)
            throws SesionSinReferenciaException, RangoInvalidoException, LongitudExcedidaException {
        this();
        setInstrumento(instrumento);
        setElemento(elemento);
        setTiempoNetoMin(tiempoNetoMin);
        setComentario(comentario);
        validar();
    }

    public SesionDePractica(int id, Instrumento instrumento, ElementoDeEstudio elemento,
                            LocalDateTime fechaRegistro, int tiempoNetoMin, String comentario)
            throws SesionSinReferenciaException, RangoInvalidoException, LongitudExcedidaException {
        this(instrumento, elemento, tiempoNetoMin, comentario);
        this.id = id;
        if (fechaRegistro != null) this.fechaRegistro = fechaRegistro;
    }

    /** Compatibilidad hacia atras (TestRunner / SQL original). */
    public SesionDePractica(int id, int instrumentoId, int elementoId,
                            LocalDateTime fechaRegistro, int tiempoNetoMin, String comentario) {
        this.id = id;
        this.instrumento = new Instrumento();
        this.instrumento.setId(instrumentoId);
        this.elemento = new Cancion();
        this.elemento.setId(elementoId);
        this.fechaRegistro = fechaRegistro == null ? LocalDateTime.now() : fechaRegistro;
        this.tiempoNetoMin = tiempoNetoMin;
        this.comentario = comentario;
    }

    /** Tiempo en minutos derivado del cronómetro (para CU006). */
    public static int calcularTiempo(long segundosAcumulados) {
        return (int) Math.max(0, segundosAcumulados / 60);
    }

    /** Aplica RN-01: toda sesion debe tener instrumento y elemento. */
    public void validar() throws SesionSinReferenciaException {
        if (instrumento == null) {
            throw new SesionSinReferenciaException(
                "Debe seleccionar un instrumento antes de registrar la sesion (RN-01).");
        }
        if (elemento == null) {
            throw new SesionSinReferenciaException(
                "Debe seleccionar un elemento de estudio antes de registrar la sesion (RN-01).");
        }
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Instrumento getInstrumento() { return instrumento; }
    public void setInstrumento(Instrumento instrumento) { this.instrumento = instrumento; }

    public ElementoDeEstudio getElemento() { return elemento; }
    public void setElemento(ElementoDeEstudio elemento) { this.elemento = elemento; }

    public int getInstrumentoId() { return instrumento == null ? 0 : instrumento.getId(); }
    public int getElementoId() { return elemento == null ? 0 : elemento.getId(); }

    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDateTime f) { this.fechaRegistro = f == null ? LocalDateTime.now() : f; }

    public int getTiempoNetoMin() { return tiempoNetoMin; }

    public void setTiempoNetoMin(int t) throws RangoInvalidoException {
        Validador.exigirRangoEntero("tiempoNetoMin", t, 1, 100000);
        this.tiempoNetoMin = t;
    }

    public String getComentario() { return comentario; }

    public void setComentario(String comentario) throws LongitudExcedidaException {
        Validador.exigirLongitudMaxima("comentario", comentario, 500);
        this.comentario = comentario == null ? "" : comentario.trim();
    }

    public String getFechaFormateada() { return fechaRegistro.format(FMT); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SesionDePractica)) return false;
        SesionDePractica s = (SesionDePractica) o;
        return id == s.id;
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public String toString() {
        return "Sesion{id=" + id + ", inst=" + getInstrumentoId() + ", elem=" + getElementoId()
                + ", min=" + tiempoNetoMin + ", fecha=" + getFechaFormateada() + "}";
    }
}