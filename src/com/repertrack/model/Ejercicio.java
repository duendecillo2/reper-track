package com.repertrack.model;

import com.repertrack.excepcion.LongitudExcedidaException;
import com.repertrack.excepcion.RangoInvalidoException;

/**
 * Especialización: ejercicio técnico (escala, arpegio, étude, patrón rítmico).
 * Refactor TP3: asociación Instrumento + validaciones con Validador + checked.
 */
public class Ejercicio extends ElementoDeEstudio {

    private TipoEjercicio tipoEjercicio;
    private int bpmSugerido;

    public Ejercicio() { super(); this.tipoEjercicio = TipoEjercicio.ESCALA; this.bpmSugerido = 80; }

    public Ejercicio(int id, Instrumento instrumento, String nombre, int dificultad,
                     int tiempoEstimadoDominioMin, TipoEjercicio tipoEjercicio, int bpmSugerido)
            throws RangoInvalidoException, LongitudExcedidaException {
        super(id, instrumento, nombre, dificultad, tiempoEstimadoDominioMin);
        setTipoEjercicio(tipoEjercicio);
        setBpmSugerido(bpmSugerido);
    }

    /** Compatibilidad hacia atras (TestRunner original). */
    public Ejercicio(int id, String nombre, int dificultad, int instrumentoId,
                     String tipoEjercicio, int bpmSugerido)
            throws RangoInvalidoException, LongitudExcedidaException {
        super(id, nombre, dificultad, instrumentoId);
        setTipoEjercicio(TipoEjercicio.desde(tipoEjercicio));
        setBpmSugerido(bpmSugerido);
    }

    @Override
    public String getCategoria() { return CategoriaElemento.EJERCICIO.getValor(); }

    @Override
    public double calcularProgreso(int minutosAcumulados) {
        if (minutosAcumulados <= 0) return 0.0;
        int meta = getDificultad() * 60; // el ejercicio se domina con menos horas
        if (meta == 0) return 0.0;
        return Math.min(1.0, minutosAcumulados / (double) meta);
    }

    public TipoEjercicio getTipoEjercicioEnum() { return tipoEjercicio; }
    public String getTipoEjercicio() { return tipoEjercicio == null ? null : tipoEjercicio.getValor(); }

    public void setTipoEjercicio(TipoEjercicio t) {
        this.tipoEjercicio = t == null ? TipoEjercicio.ESCALA : t;
    }

    public int getBpmSugerido() { return bpmSugerido; }

    public void setBpmSugerido(int bpm) throws RangoInvalidoException {
        com.repertrack.util.Validador.exigirRangoEntero("bpmSugerido", bpm, 20, 300);
        this.bpmSugerido = bpm;
    }
}